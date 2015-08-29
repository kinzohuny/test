package com.jiuqi.dna.core.impl;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.jiuqi.dna.core.def.DNASqlType;
import com.jiuqi.dna.core.def.info.InfoGroupDeclarator;
import com.jiuqi.dna.core.def.model.ModelDeclarator;
import com.jiuqi.dna.core.def.query.ModifyStatementDeclarator;
import com.jiuqi.dna.core.def.query.ORMDeclarator;
import com.jiuqi.dna.core.def.query.QueryStatementDeclarator;
import com.jiuqi.dna.core.def.query.StoredProcedureDeclarator;
import com.jiuqi.dna.core.def.query.UserFunctionDeclarator;
import com.jiuqi.dna.core.def.table.TableDeclarator;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.internal.db.sync.DbSync;
import com.jiuqi.dna.core.type.GUID;

/**
 * 发布的元数据定义声明器.
 * 
 */
final class PublishedDeclarator extends PublishedElement {

	final Class<? extends DeclaratorBase> clazz;
	DeclaratorBase ref;

	PublishedDeclarator(Class<? extends DeclaratorBase> clazz) {
		this.clazz = clazz;
	}

	@Override
	public String toString() {
		return this.clazz == null ? null : "[" + this.clazz.getName() + "]";
	}

	/**
	 * 元数据定义创建步骤
	 * 
	 */
	static class CreateStep extends StartupStepBase<PublishedDeclarator> {

		final Class<? extends DeclaratorBase> baseClass;

		private final boolean tryLoadScript;

		private final boolean regToRootSite;

		/**
		 * 尝试对各种依赖脚本声明的声明器装载脚本的URL，备随后解析使用
		 */
		final boolean tryLoadScript(PublishedDeclarator pe, ResolveHelper helper) {
			if (this.tryLoadScript) {
				DNASqlType et = DNASqlType.typeOfDeclaratorClass(pe.clazz);
				if (et == null) {
					return false;
				}
				final String postfix = et.declareScriptPostfix;
				if (postfix == null) {
					return false;
				}
				final int postfixL = postfix.length();
				if (postfixL == 0) {
					return false;
				}
				final String className = pe.clazz.getName();
				final int classNameL = className.length();
				final char[] resourceName = new char[classNameL + postfixL + 1];
				className.getChars(0, classNameL, resourceName, 0);
				int nameCharStart = 0;
				for (int i = 0; i < classNameL; i++) {
					if (resourceName[i] == '.') {
						resourceName[i] = '/';
						nameCharStart = i + 1;
					}
				}
				resourceName[classNameL] = '.';
				postfix.getChars(0, postfixL, resourceName, classNameL + 1);
				final ClassLoader cl = pe.clazz.getClassLoader();
				final URL url = cl.getResource(Utils.fastString(resourceName));
				if (url != null) {
					return pe.space.regDeclareScript(className.substring(nameCharStart, classNameL), et, url, pe.publishMode, helper.catcher);
				}
			}
			return false;
		}

		CreateStep(int priority, String description,
				Class<? extends DeclaratorBase> baseClass) {
			super(priority, description);
			this.baseClass = baseClass;
			this.tryLoadScript = DNASqlType.declareScirptSupported(baseClass);
			this.regToRootSite = false;
		}

		CreateStep(StartupStepBase<?> previous, int pd, String description,
				Class<? extends DeclaratorBase> baseClass, boolean regToRootSite) {
			super(previous, pd, description);
			this.baseClass = baseClass;
			this.tryLoadScript = DNASqlType.declareScirptSupported(baseClass);
			this.regToRootSite = regToRootSite;
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			synchronized (DeclaratorBase.class) {
				DeclaratorBase.newInstanceByCore = helper.context;
				try {
					target.ref = helper.newObject(target.clazz, target.space);
					target.ref.bundle = target.bundle;
				} finally {
					DeclaratorBase.newInstanceByCore = null;
				}
			}
			if (this.regToRootSite) {
				target.space.site.regDeclarator(target.ref, target.publishMode, helper.catcher);
			} else {
				target.space.regDeclarator(target.ref, target.publishMode, helper.catcher);
			}
			return this.nextStep(helper, target);
		}
	}

	static class RefStep extends StartupStepBase<PublishedDeclarator> {

		RefStep(StartupStepBase<?> previous, int pd, String discription) {
			super(previous, pd, discription);
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			target.ref.tryDeclareUseRef(helper.querier);
			return this.nextStep(helper, target);
		}
	}

	static class PrepareStep extends StartupStepBase<PublishedDeclarator> {

		PrepareStep(StartupStepBase<?> previous, int pd, String description) {
			super(previous, pd, description);
		}

		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return null;
		}

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			(helper).ensurePrepared(target.space, (Prepareble) target.ref.getDefine());
			return this.nextStep(helper, target);
		}
	}

	static final CreateStep information_create = new CreateStep(StartupStep.DECLARATOR_HIGHEST_PRI, "实例化信息定义", InfoGroupDeclarator.class) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			return information_prepare;
		}
	};

	static final PrepareStep information_prepare = new PrepareStep(information_create, 0x100, "装载多语言信息") {
	};

	static final CreateStep table_create = new CreateStep(information_prepare, 0x100, "实例化表定义", TableDeclarator.class, false) {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			if (target.clazz == TD_CoreMetaData.class) {
				TD_CoreMetaData coreMetadta = (TD_CoreMetaData) target.ref;
				helper.coreMetadta = coreMetadta;
				helper.tryInitCoreMetadata(target.space.site, coreMetadta);
				helper.context.occorAt.site.regNamedDefineToSpace(TableDefine.class, TableDefineImpl.DUMMY, helper.catcher);
			} else if (target.clazz == TD_CoreSiteInfo.class) {
				helper.tryInitCoreSiteInfo(target.space.site, (TD_CoreSiteInfo) target.ref);
			} else {
				return table_ref;
			}
			return null;
		}
	};

	static final int STEP_LOAD_CUSTOM_TABLES = table_create.getPriority() + 0x10;

	static final RefStep table_ref = new RefStep(table_create, 0x100, "确定表定义对其他元素的引用") {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			if (target.space.isDBValid() && helper.application().isFirstInCluster) {
				if (ContextVariableIntl.UNSAFE_FORCE_SKIP_DB_SYNC) {
					return null;
				}
				return table_sync;
			}
			return null;
		}
	};

	static final int STEP_RESOLVE_CUSTOM_TABLES_REF = table_ref.getPriority() + 0x10;

	static final StartupStepBase<PublishedDeclarator> table_sync = new StartupStepBase<PublishedDeclarator>(table_ref, 0x100, "同步数据库表结构") {

		@Override
		public StartupStep<PublishedDeclarator> doStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			helper.syncDbTable(target.space, (TableDefineImpl) target.ref.getDefine());
			return null;
		}
	};

	static final class SaveTableMetadataStep implements
			StartupStep<SaveTableMetadataEntry> {

		private final CoreMetadataUtl utl;

		SaveTableMetadataStep(TD_CoreMetaData coreMetadata) {
			this.utl = new CoreMetadataUtl(coreMetadata);
		}

		public StartupStep<SaveTableMetadataEntry> doStep(ResolveHelper helper,
				SaveTableMetadataEntry entry) throws Throwable {
			if (entry.delete != null) {
				this.utl.delete(helper.context, entry.delete);
			} else {
				this.utl.save(helper.context, entry.table);
			}
			return null;
		}

		public String getDescription() {
			return "保存逻辑表表元数据";
		}

		public int getPriority() {
			return PublishedDeclarator.STEP_SAVE_CUSTOM_TABLES_METADATA;
		}
	}

	static final class SaveTableMetadataEntry extends StartupEntry {

		final TableDefineImpl table;
		final GUID delete;

		SaveTableMetadataEntry(TableDefineImpl table, GUID delete) {
			this.table = table;
			this.delete = delete;
		}
	}

	static final int STEP_SAVE_CUSTOM_TABLES_METADATA = table_sync.getPriority() + 0x10;

	static final CreateStep orm_create = new CreateStep(table_sync, 0x100, "实例化ORM定义", ORMDeclarator.class, false) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return orm_ref;
		}

	};

	static final RefStep orm_ref = new RefStep(orm_create, 0x100, "确定ORM定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return orm_prepare;
		}

	};

	static final PrepareStep orm_prepare = new PrepareStep(orm_ref, 0x100, "初始化ORM定义") {
	};

	static final CreateStep query_create = new CreateStep(orm_prepare, 0x100, "实例化查询语句定义", QueryStatementDeclarator.class, false) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return query_ref;
		}
	};

	static final RefStep query_ref = new RefStep(query_create, 0x100, "确定查询语句定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return query_prepare;
		}

	};

	static final PrepareStep query_prepare = new PrepareStep(query_ref, 0x100, "初始化查询语句定义") {
	};

	static final CreateStep command_create = new CreateStep(query_prepare, 0x100, "实例化更新语句定义", ModifyStatementDeclarator.class, false) {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return command_ref;
		}
	};

	static final RefStep command_ref = new RefStep(command_create, 0x100, "确定更新语句对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return command_prepare;
		}

	};

	static final PrepareStep command_prepare = new PrepareStep(command_ref, 0x100, "初始更新语句定义") {
	};

	static final CreateStep user_function_create = new CreateStep(command_prepare, 0x100, "实例化用户定义函数", UserFunctionDeclarator.class, true) {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			return user_function_prepare;
		}
	};

	static final StartupStepBase<PublishedDeclarator> user_function_prepare = new StartupStepBase<PublishedDeclarator>(user_function_create, 0x100, "初始化用户定义函数") {

		@Override
		public StartupStep<PublishedDeclarator> doStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			final UserFunctionImpl function = (UserFunctionImpl) target.ref.getDefine();
			function.initPatterns();
			return user_function_sync;
		}

	};

	static final StartupStepBase<PublishedDeclarator> user_function_sync = new StartupStepBase<PublishedDeclarator>(user_function_prepare, 0x100, "同步用户定义函数到数据库") {

		@Override
		public final StartupStep<PublishedDeclarator> doStep(
				ResolveHelper helper, PublishedDeclarator target)
				throws Throwable {
			final UserFunctionImpl function = (UserFunctionImpl) target.ref.getDefine();
			final ContextImpl<?, ?, ?> context = helper.context;
			final SpaceNode save = context.occorAt.site.updateContextSpace(context);
			try {
				final DbSync refactor = helper.context.getDBAdapter().refactor();
				try {
					refactor.sync(function);
				} catch (SQLException e) {
					throw new CreateRoutineException(function, e);
				}
				function.valid = true;
			} finally {
				save.updateContextSpace(context);
			}
			return null;
		}

	};

	static final CreateStep procedure_create = new CreateStep(user_function_sync, 0x100, "实例化本地存储过程定义", StoredProcedureDeclarator.class, true) {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return procedure_prepare;
		}
	};

	static final PrepareStep procedure_prepare = new PrepareStep(procedure_create, 0x100, "初始化本地存储过程定义") {

		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return procedure_sync;
		}
	};

	static final StartupStepBase<PublishedDeclarator> procedure_sync = new StartupStepBase<PublishedDeclarator>(procedure_prepare, 0x100, "同步本地存储过程到数据库") {

		@Override
		public StartupStep<PublishedDeclarator> doStep(ResolveHelper helper,
				PublishedDeclarator target) throws Throwable {
			final StoredProcedureDefineImpl procedure = (StoredProcedureDefineImpl) ((StoredProcedureDeclarator) target.ref).getDefine();
			final ContextImpl<?, ?, ?> context = helper.context;
			final SpaceNode save = context.occorAt.site.updateContextSpace(context);
			try {
				final DbSync refactor = helper.context.getDBAdapter().refactor();
				try {
					refactor.sync(procedure);
				} catch (SQLException e) {
					throw new CreateRoutineException(procedure, e);
				}
				refactor.check(procedure);
				procedure.setValid(true);
				return null;
			} finally {
				save.updateContextSpace(context);
			}
		}
	};

	static final CreateStep model_create = new CreateStep(procedure_sync, 0x100, "实例化模型定义", ModelDeclarator.class, false) {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return model_ref;
		}
	};

	// 装载自定义model
	static final int STEP_LOAD_CUSTOM_MODELS = model_create.getPriority() + 0x10;

	// 明确model对其他表的引用
	static final RefStep model_ref = new RefStep(model_create, 0x100, "确定模型定义对其他元素的引用") {
		@Override
		StartupStep<PublishedDeclarator> nextStep(ResolveHelper helper,
				PublishedDeclarator target) {
			return model_prepare;
		}
	};

	// 明确自定义model对其他元数据的引用
	static final int STEP_RESOLVE_CUSTOM_MODELS_REF = model_ref.getPriority() + 1;
	// 准备model
	static final PrepareStep model_prepare = new PrepareStep(model_ref, 0x100, "初始化模型定义") {
	};

	final static String xml_element_info = "info-group";
	final static String xml_element_table = "table";
	final static String xml_element_orm = "orm";
	final static String xml_element_query = "query";
	final static String xml_element_command = "command";
	final static String xml_element_procedure = "procedure";
	final static String xml_element_model = "model";
	final static String xml_element_user_function = "user-function";

	static final Map<String, CreateStep> beginSteps = new HashMap<String, CreateStep>();
	static {
		beginSteps.put(xml_element_info, information_create);
		beginSteps.put(xml_element_table, table_create);
		beginSteps.put(xml_element_query, query_create);
		beginSteps.put(xml_element_command, command_create);
		beginSteps.put(xml_element_user_function, user_function_create);
		beginSteps.put(xml_element_procedure, procedure_create);
		beginSteps.put(xml_element_orm, orm_create);
		beginSteps.put(xml_element_model, model_create);
	}
}