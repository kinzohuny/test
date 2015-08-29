/**
 * 
 */
package com.jiuqi.dna.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.core.ObjectQuerier;
import com.jiuqi.dna.core.def.NamedDefine;
import com.jiuqi.dna.core.def.model.ModelScriptEngine;
import com.jiuqi.dna.core.impl.CoreMetadataTableLoadStep.CoreMetadataTableLoadEntry;
import com.jiuqi.dna.core.impl.PublishedDeclarator.SaveTableMetadataEntry;
import com.jiuqi.dna.core.impl.PublishedDeclarator.SaveTableMetadataStep;
import com.jiuqi.dna.core.log.DNALogManager;
import com.jiuqi.dna.core.misc.ExceptionCatcher;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.misc.SXElementBuilder;
import com.jiuqi.dna.core.type.GUID;

public final class ResolveHelper {

	public final ObjectQuerier querier;
	public final ExceptionCatcher catcher;
	public final SXElementBuilder sxBuilder;
	public final ContextImpl<?, ?, ?> context;
	private final Object[] adArgs;

	final <TObject> TObject newObject(Class<TObject> clazz, Space space) {
		final SpaceNode oldSave = space.updateContextSpace(this.context);
		try {
			return space.newObjectInNode(clazz, null, this.adArgs);
		} finally {
			oldSave.updateContextSpace(this.context);
		}
	}

	final void regInfoGroupLanguage(String infoGroupFullName, int localeKey,
			String[] infoNameMessages) {
		this.context.session.application.regInfoGroupLanguage(infoGroupFullName, localeKey, infoNameMessages);
	}

	final boolean tryInitService(ServiceBase<?> service) throws Throwable {
		return service.tryInit(this.context);
	}

	final void tryBuildResourceKeyPathInfos(ServiceBase<?> service) {
	}

	final void ensurePrepared(Space space, Prepareble toEnsure) {
		if (space.isDBValid() || !toEnsure.ignorePrepareIfDBInvalid()) {
			SpaceNode old = space.updateContextSpace(this.context);
			try {
				toEnsure.ensurePrepared(this.context, false);
			} finally {
				old.updateContextSpace(this.context);
			}
		}
	}

	final void tryBuildResourceRefInfos(ServiceBase<?> service) {
	}

	final void regModelScriptEngine(ModelScriptEngine<?> engine) {
		this.context.session.application.mseManager.regEngine(engine);
	}

	TD_CoreMetaData coreMetadta;

	private SaveTableMetadataStep saveTableMetadataStep;

	final void registerDeleteMetadataStep(GUID delete) {
		if (this.saveTableMetadataStep == null) {
			this.saveTableMetadataStep = new SaveTableMetadataStep(this.coreMetadta);
		}
		this.regStartupEntry(this.saveTableMetadataStep, new SaveTableMetadataEntry(null, delete));
	}

	final void registerSaveTableMetadataStep(TableDefineImpl table) {
		if (this.saveTableMetadataStep == null) {
			this.saveTableMetadataStep = new SaveTableMetadataStep(this.coreMetadta);
		}
		this.regStartupEntry(this.saveTableMetadataStep, new SaveTableMetadataEntry(table, null));
	}

	final void syncDbTable(Space space, TableDefineImpl table) throws Throwable {
		SpaceNode nodeSave = space.updateContextSpace(this.context);
		try {
			final SXElement before = SXElement.newDoc();
			table.renderInto(before);
			final String beforeStr = before.toString();
			try {
				this.context.getDBAdapter().syncTable(table);
			} catch (Throwable e) {
				this.context.exception(e);
				throw Utils.tryThrowException(e);
			} finally {
				this.context.resolveTrans();
			}
			final SXElement after = SXElement.newDoc();
			table.renderInto(after);
			final String afterStr = after.toString();
			if (!afterStr.equals(beforeStr)) {
				StringBuffer s = new StringBuffer();
				s.append("启动过程同步TableDefine表定义（"+table.getName()+"）的数据库结构后metadata信息发生变化：\r\n");
				s.append("同步之前为：\r\n");
				s.append(beforeStr);
				s.append("\r\n");
				s.append("同步之后为：\r\n");
				s.append(afterStr);
				DNALogManager.getLogger("core/db/sync").logInfo(null, s.toString(), null, false);
				this.registerSaveTableMetadataStep(table);
			}
		} finally {
			nodeSave.updateContextSpace(this.context);
		}
	}

	final void tryInitCoreMetadata(Site site, TD_CoreMetaData table)
			throws Throwable {
		if (site.isDBValid()) {
			this.syncDbTable(site, (TableDefineImpl) table.getDefine());
			this.regStartupEntry(new CoreMetadataTableLoadStep(this.application().isFirstInCluster), new CoreMetadataTableLoadEntry(site, this, table));
		}
	}

	final void tryInitCoreSiteInfo(Site site, TD_CoreSiteInfo table)
			throws Throwable {
		if (!site.isDBValid()) {
			return;
		}
		CoreSiteInfo siteInfo;
		SpaceNode nodeSave = site.updateContextSpace(this.context);
		try {
			try {
				final DBAdapterImpl adapter = this.context.getDBAdapter();
				adapter.syncTable((TableDefineImpl) table.getDefine());
				ORMAccessorProxy<CoreSiteInfo> orm = DBAdapterImpl.newORMAccessor(this.context, (MappingQueryStatementImpl) table.getMappingQueryDefine());
				try {
					siteInfo = orm.first();
					if (siteInfo == null) {
						siteInfo = new CoreSiteInfo();
						siteInfo.RECID = site.application.newRECID();
						siteInfo.RECVER = site.application.newRECVER();
						siteInfo.createTime = System.currentTimeMillis();
						orm.insert(siteInfo);
					}
				} finally {
					orm.unuse();
				}
			} catch (Throwable e) {
				this.context.exception(e);
				throw Utils.tryThrowException(e);
			} finally {
				this.context.resolveTrans();
			}
		} finally {
			nodeSave.updateContextSpace(this.context);
		}
		site.setSiteInfo(siteInfo);
	}

	private Map<StartupStep<StartupEntry>, StartupEntry> startupMap = new HashMap<StartupStep<StartupEntry>, StartupEntry>();

	@SuppressWarnings("unchecked")
	final void regStartupEntry(StartupStep<? extends StartupEntry> beginStep,
			StartupEntry entry) {
		StartupEntry oldTail = this.startupMap.put((StartupStep<StartupEntry>) beginStep, entry);
		if (oldTail != null) {
			float otP = oldTail.getPriority(beginStep);
			float eP = entry.getPriority(beginStep);
			if (eP < otP) {
				StartupEntry last = oldTail;
				StartupEntry one = last.nextInStep;
				while (one != oldTail && one.getPriority(beginStep) <= eP) {
					last = one;
					one = one.nextInStep;
				}
				entry.nextInStep = one;
				last.nextInStep = entry;
				this.startupMap.put((StartupStep<StartupEntry>) beginStep, oldTail);
			} else {
				entry.nextInStep = oldTail.nextInStep;
				oldTail.nextInStep = entry;
			}
		} else {
			entry.nextInStep = entry;
		}
	}

	public final static void logStartInfo(String info) {
		synchronized (System.out) {
			ApplicationImpl.printDateTime(System.out);
			System.out.print(": DNA 启动...");
			System.out.println(info);
		}
	}

	final void startup() {
		while (!this.startupMap.isEmpty()) {
			// 得到优先级最高的步骤
			StartupStep<StartupEntry> highest = null;
			int highestPRI = Integer.MAX_VALUE;
			for (StartupStep<StartupEntry> aStep : this.startupMap.keySet()) {
				int pri = aStep.getPriority();
				// 最小的,优先级最高
				if (highest == null || pri < highestPRI) {
					highest = aStep;
					highestPRI = pri;
				}
			}
			logStartInfo(highest.getDescription());
			// 移除
			StartupEntry tail = this.startupMap.remove(highest);
			StartupEntry head;
			do {
				head = tail.nextInStep;
				tail.nextInStep = head.nextInStep;
				StartupStep<StartupEntry> nextStep;
				try {
					nextStep = highest.doStep(this, head);
				} catch (Throwable e) {
					this.catcher.catchException(e, head);
					continue;
				}
				if (nextStep != null && nextStep.getPriority() > highestPRI) {
					this.regStartupEntry(nextStep, head);
				} else {
					head.nextInStep = null;// helpGC
				}
			} while (head != tail);
		}
		logStartInfo("完毕");
	}

	ResolveHelper(ContextImpl<?, ?, ?> context, SXElementBuilder sxBuilder) {
		if (context == null || sxBuilder == null) {
			throw new NullPointerException();
		}
		this.context = context;
		this.catcher = context.catcher;
		this.querier = new FilteredObjectQuerier(context) {
			@Override
			protected final boolean isValidFacadeClass(Class<?> facadeClass) {
				return facadeClass == Class.class || NamedDefine.class.isAssignableFrom(facadeClass) || DeclaratorBase.class.isAssignableFrom(facadeClass) || ServiceBase.class.isAssignableFrom(facadeClass);
			}
		};
		this.sxBuilder = sxBuilder;
		this.adArgs = new Object[] { this.querier };
	}

	public final ApplicationImpl application() {
		return this.context.occorAt.site.application;
	}

	private static final String PROP_JAVA_VERSION = "java.version";
	private static final String PROP_JAVA_RUNTIME_NAME = "java.runtime.name";
	private static final String PROP_JAVA_RUNTIME_VERSION = "java.runtime.version";
	private static final String PROP_JAVA_VM_NAME = "java.vm.name";
	private static final String PROP_JAVA_VM_VERSION = "java.vm.version";
	private static final String PROP_JAVA_VM_INFO = "java.vm.info";

	public static final void javaVersion() {
		logStartInfo("java version \"" + prop(PROP_JAVA_VERSION) + "\"");
		logStartInfo(prop(PROP_JAVA_RUNTIME_NAME) + " (build " + prop(PROP_JAVA_RUNTIME_VERSION) + ")");
		logStartInfo(prop(PROP_JAVA_VM_NAME) + " (build " + prop(PROP_JAVA_VM_VERSION) + ", " + prop(PROP_JAVA_VM_INFO) + ")");
	}

	private static final String prop(Object key) {
		Object value = System.getProperties().get(key);
		return value == null ? "" : value.toString();
	}
}