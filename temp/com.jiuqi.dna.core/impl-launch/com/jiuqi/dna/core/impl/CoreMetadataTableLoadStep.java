package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.da.IteratedRecord;
import com.jiuqi.dna.core.da.RecordIterateAction;
import com.jiuqi.dna.core.def.MetaElementType;
import com.jiuqi.dna.core.def.query.QueryColumnDefine;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableReferenceDeclare;
import com.jiuqi.dna.core.impl.CoreMetadataTableLoadStep.CoreMetadataTableLoadEntry;
import com.jiuqi.dna.core.impl.StringKeyMap.Visitor;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXMergeHelper;
import com.jiuqi.dna.core.type.GUID;

final class CoreMetadataTableLoadStep implements
		StartupStep<CoreMetadataTableLoadEntry> {

	final boolean sync;

	CoreMetadataTableLoadStep(boolean sync) {
		this.sync = sync;
	}

	public final String getDescription() {
		return "装载并同步自定义表定义";
	}

	public final int getPriority() {
		return PublishedDeclarator.STEP_LOAD_CUSTOM_TABLES;
	}

	public final StartupStep<CoreMetadataTableLoadEntry> doStep(
			final ResolveHelper helper, final CoreMetadataTableLoadEntry target)
			throws Throwable {
		final CoreMetadataMerge merge = helper.context.get(CoreMetadataMerge.class);
		final TD_CoreMetaDataH history = helper.context.get(TD_CoreMetaDataH.class);
		helper.syncDbTable(helper.context.occorAt.site, (TableDefineImpl) merge.getDefine()); //初始化CoreMetadataMerge表
		helper.syncDbTable(helper.context.occorAt.site, (TableDefineImpl) history.getDefine()); //初始化CoreMetaDataH表
		final MappingQueryStatementImpl orm = helper.context.newMappingQueryStatement(CoreMetadataMergeEntity.class);
		orm.newReference(merge, "t");
		orm.newColumn(merge.f_RECID, "id");
		orm.newColumn(merge.f_appid, "appid");
		orm.newColumn(merge.f_kind, "kind");
		orm.newColumn(merge.f_name, "name");
		orm.newColumn(merge.f_timestamp, "timestamp");
		orm.newColumn(merge.f_static_def, "staticDef");
		orm.newColumn(merge.f_dynamic_def, "dynamicDef");
		orm.newColumn(merge.f_merged, "merged");
		target.tables.visitAll(new Visitor<CoreMetadataTableLoadStep.TableMetadataEntry>() {
			public final void doVisit(String key, TableMetadataEntry cmt) {
				try {
					final ContextImpl<?, ?, ?> context = helper.context;
					final SXElement element = cmt.element;
					// cmt.name不是orgin,可能被大写化了,但find方法已经不区分大小写了.
					TableDefineImpl table = (TableDefineImpl) helper.querier.find(TableDefine.class, cmt.name);
					if (table == null) {
						final String orgin = TableXML.coalesceDisplayAndName(element);
						table = new TableDefineImpl(orgin, null);
						table.merge(element, target.merge);
						table.id = cmt.recid;
						if (CoreMetadataTableLoadStep.this.sync && !ContextVariableIntl.UNSAFE_FORCE_SKIP_DB_SYNC) {
							try {
								context.getDBAdapter().syncTable(table);
							} catch (Throwable e) {
								context.exception(e);
								throw Utils.tryThrowException(e);
							} finally {
								context.resolveTrans();
							}
						}
						context.occorAt.site.regNamedDefineToSpace(TableDefine.class, table, context.catcher);
					} else {
						// 静态TD表已经在“table_sync”步骤中注册了数据库同步的步骤。
						CoreMetadataMergeEntity merge = new CoreMetadataMergeEntity(helper.context);
						merge.setTable(cmt.name);
						merge.setStatic(table);
						merge.setDynamic(element);
						try {
							table.merge(element, target.merge);
						} catch (Throwable e) {
							DNALogManager.getLogger("core/db/sync").logError(null, "在合并混合模式的逻辑表[" + cmt.name + "]时发生异常：", e, false);
							throw e;
						}
						merge.setMerged(table);
						try {
							context.newORMAccessor(orm).insert(merge);
						} catch (Throwable e) {
							DNALogManager.getLogger("core/db/sync").logError(null, "在合并混合模式的逻辑表[" + cmt.name + "]时，保存合并历史发生异常：", e, false);
						}
						table.id = cmt.recid;
					}
					for (TableMetadataEntry next = cmt.duplicated; next != null && next.ver == cmt.ver; next = next.duplicated) {
						table.merge(next.element, target.merge);
					}
					for (TableMetadataEntry duplicated = cmt.duplicated; duplicated != null; duplicated = duplicated.duplicated) {
						DNALogManager.getLogger("core/db/sync").logInfo(null, "启动过程删除CORE_METADATA信息，表名："+duplicated.name+"，RECID："+duplicated.recid, null, false);
						helper.registerDeleteMetadataStep(duplicated.recid);
					}
					helper.registerSaveTableMetadataStep(table);
				} catch (Throwable e) {
					helper.catcher.catchException(e, this);
				}
			}
		});
		return nextStep;
	}

	private static final StartupStep<CoreMetadataTableLoadEntry> nextStep = new StartupStepBase<CoreMetadataTableLoadEntry>(PublishedDeclarator.STEP_RESOLVE_CUSTOM_TABLES_REF, "明确自定义表定义对其他元数据的引用") {
		@Override
		public StartupStep<CoreMetadataTableLoadEntry> doStep(
				ResolveHelper helper, CoreMetadataTableLoadEntry target)
				throws Throwable {
			target.merge.resolveDelayAction(CoreMetadataTableLoadStep.class, null);
			return null;
		}
	};

	static final class CoreMetadataTableLoadEntry extends StartupEntry {

		final SXMergeHelper merge;
		final StringKeyMap<TableMetadataEntry> tables = new StringKeyMap<TableMetadataEntry>(false);

		CoreMetadataTableLoadEntry(final SpaceNode space,
				final ResolveHelper helper, final TD_CoreMetaData coreMetadata) {
			SpaceNode nodeSave = space.updateContextSpace(helper.context);
			try {
				if (space.isDBValid()) {
					final QueryStatementImpl query = new QueryStatementImpl("s");
					final TableReferenceDeclare tr = query.newReference(coreMetadata);
					final QueryColumnDefine c_recid = query.newColumn(tr.expOf(coreMetadata.f_RECID));
					final QueryColumnDefine c_name = query.newColumn(tr.expOf(coreMetadata.f_name));
					final QueryColumnDefine c_xml = query.newColumn(tr.expOf(coreMetadata.f_xml));
					final QueryColumnDefine c_md5 = query.newColumn(tr.expOf(coreMetadata.f_md5));
					query.setCondition(tr.expOf(coreMetadata.f_kind).xEq(MetaElementType.TABLE.name()));
					helper.context.iterateQuery(query, new RecordIterateAction() {
						public final boolean iterate(Context context,
								IteratedRecord record, long recordIndex)
								throws Throwable {
							try {
								final GUID recid = record.getFields().get(c_recid).getGUID();
								final String name = record.getFields().get(c_name).getString();
								final String xml = record.getFields().get(c_xml).getString();
								GUID md5 = null;
								try {
									md5 = record.getFields().get(c_md5).getGUID();
								} catch (Throwable e) {
								}
								if (md5 != null && !md5.equals(GUID.MD5Of(xml))) {
									final StringBuilder s = new StringBuilder();
									s.append("逻辑表（" + name + "）元数据的MD5与期望值（" + md5.toString() + "）不匹配。\r\n");
									s.append(xml);
									DNALogManager.getLogger("core/db/sync").logFatal(null, s.toString(), null, false);
								}
								final TableMetadataEntry te = new TableMetadataEntry(helper, recid, name, xml);
								TableMetadataEntry find = CoreMetadataTableLoadEntry.this.tables.find(name);
								if (find == null) {
									CoreMetadataTableLoadEntry.this.tables.put(name, te);
								} else if (te.ver.ordinal() >= find.ver.ordinal()) {
									te.duplicated = find;
									CoreMetadataTableLoadEntry.this.tables.put(name, te);
								} else {
									for (; find != null; find = find.duplicated) {
										if (find.duplicated == null) {
											find.duplicated = te;
											break;
										} else if (te.ver.ordinal() >= find.duplicated.ver.ordinal()) {
											te.duplicated = find.duplicated;
											find.duplicated = te;
											break;
										}
									}
								}

							} catch (Throwable e) {
								helper.catcher.catchException(e, this);
							}
							return false;
						}
					});
				}
			} finally {
				nodeSave.updateContextSpace(helper.context);
			}
			this.merge = new SXMergeHelper(helper.querier, helper.catcher);
		}
	}

	static final class TableMetadataEntry {

		/**
		 * CORE_METADATA表中的NAME字段，可能被大写化过，不能作为创建表的名称！
		 */
		final String name;

		final GUID recid;

		final TableXML ver;

		final SXElement element;

		TableMetadataEntry duplicated;

		TableMetadataEntry(ResolveHelper helper, GUID recid, String name,
				String xml) {
			this.recid = recid;
			this.name = name;
			try {
				if (xml == null || xml.length() == 0) {
					throw defineEmpty(this.name);
				}
				SXElement element = helper.sxBuilder.build(xml);
				if (element == null) {
					throw defineEmpty(this.name);
				}
				element = element.firstChild();
				if (element == null) {
					throw defineEmpty(this.name);
				}
				this.element = element;
				this.ver = TableXML.detect(element);
			} catch (Throwable e) {
				throw Utils.tryThrowException(e);
			}
		}

		private static final IllegalArgumentException defineEmpty(String name) {
			return new IllegalArgumentException("逻辑表[" + name + "]元数据定义为空。");
		}
	}
}
