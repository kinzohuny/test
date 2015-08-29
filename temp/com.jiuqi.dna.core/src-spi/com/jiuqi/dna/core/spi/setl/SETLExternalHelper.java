package com.jiuqi.dna.core.spi.setl;

import com.jiuqi.dna.core.Context;
import com.jiuqi.dna.core.def.table.TableDefine;
import com.jiuqi.dna.core.def.table.TableFieldDefine;
import com.jiuqi.dna.core.type.GUID;

/**
 * SETL外部帮助接口。<br>
 * 该接口有以下几个用途：
 * <ul>
 * <li>提供错误处理方法</li>
 * <li>提供目标表信息：是否浮动表、维度字段、维度值、转换器等</li>
 * <li>提供目标表记录的过滤器</li>
 * <li>提供写锁对象，可用该锁来控制指标提取执行过程的并发</li> 该接口应由指标提取功能/提取方案配置功能的开发人员实现。
 */
public interface SETLExternalHelper {
	public SETLSAXParseReporter getReporter();

	/**
	 * 获取目标表的维度信息
	 */
	public SETLTargetDimProvider[] getDimProviders(Context context,
			TableDefine table);

	/**
	 * 获取写锁
	 */
	public SETLWriteLock getWriteLock();

	/**
	 * 获取目标数据过滤器，可返回null<br>
	 * 注意，指标提取算法可能会使用多个线程对目标表table进行提取，每个线程都会调用该方法来获取一个过滤器。 所以在实现过滤器时应当注意线程安全问题。
	 */
	public SETLTargetFilter createTargetFilter(Context context,
			TableDefine table);

	/**
	 * 获取一个布尔值，指示目标表是否为浮动表
	 */
	public boolean isFloatTable(Context context, TableDefine table);

	/**
	 * 获取自定义的目标字段列表，这些字段不是指标提取映射的目标，但是可以在目标数据过滤器中修改。
	 */
	public TableFieldDefine[] getCustomTargetFields(TableDefine table);

	/**
	 * 实体标识容器
	 * 
	 * @author gaojingxin
	 * 
	 */
	public interface EntityPaths {
		public void put(String path);
	}

	/**
	 * 根据引用表获得对应的引用帮助器
	 */
	public SETLEntityRefHelper getEntityRefHelper(Context contex,
			TableDefine refTable);

	/**
	 * 用于提供维度值的接口
	 */
	public interface SETLTargetDimProvider {
		/**
		 * 获取维度字段
		 */
		public TableFieldDefine getField();

		/**
		 * 获取维度值的类型。如果该类型与维度字段类型不相同，getConverter方法必须返回一个能够将维度值转换成字段值的转换器实例。
		 */
		public SETLTargetDimValueType getValueType();

		/**
		 * 获取目标表维度值-字段值转换器。可以返回null。
		 */
		public SETLValueConverter getConverter();

		/**
		 * 根据业务方案的GUID获取维度值，并将维度值添加到集合中
		 */
		public void getValues(Context context, GUID slnID,
				SETLTargetDimValueSet set);

		/**
		 * 获取索引类型
		 */
		public SETLTargetIndexType getIndexType();
	}

	/**
	 * 目标表索引值的类型
	 */
	public enum SETLTargetDimValueType {
		/**
		 * 整型，或日期型
		 */
		LONG(Long.class, false),
		/**
		 * 字符串型
		 */
		STRING(String.class, false),
		/**
		 * GUID
		 */
		GUID(GUID.class, false),
		/**
		 * 整型区间，或日期区间
		 */
		LONG_RANGE(Long.class, true);

		private final Class<?> javaClass;
		private final boolean range;

		SETLTargetDimValueType(Class<?> javaClass, boolean range) {
			this.javaClass = javaClass;
			this.range = range;
		}

		public Class<?> getJavaClass() {
			return this.javaClass;
		}

		public boolean isRange() {
			return this.range;
		}
	}

	/**
	 * 目标表维度值的索引方式
	 */
	public enum SETLTargetIndexType {
		/**
		 * 哈希索引
		 */
		HASH,
		/**
		 * 有序索引
		 */
		ORDINAL
	}

	public interface SETLValueConverter {
		public Object convertFrom(GUID slnID, Object value);
	}

	/**
	 * 目标过滤器用于对目标数据维度和数据行进行过滤，以确定哪些行要被更新<br>
	 * 通常对于固定表来说确定维度值的情况下，只有一行；对于浮动表来说，确定维度上可能包含多行。<br/>
	 * 指标提取算法会在更新目标表时调用shouldUpdateDim方法来确定指定维度的数据是否应该更新：<br/>
	 * <li>如果该方法返回true，则指标提取算法调用shouldUpdateRow来确定指定维度下的每一行是否应该更新。</li> <li>
	 * 如果该方法返回false，则不更新指定维度下的行，并且不对指定维度下的行调用shouldUpdateRow方法。</li>
	 */
	public interface SETLTargetFilter {
		/**
		 * 对目标数据的维度值进行过滤，返回true表示对目标维度进行更新，返回false表示不更新。<br/>
		 * 参数r包含维度字段的值，可能不包含其他字段的值。不允许修改字段值。
		 */
		public boolean shouldUpdateDim(Context context, SETLTargetRecord r);

		/**
		 * 对目标数据行进行过滤，返回true表示更新该行，返回false表示不更新该行。<br/>
		 * 允许修改目标数据的非维度字段。
		 */
		public boolean shouldUpdateRow(Context context, SETLTargetRecord r);

		public void unuse();
	}

	public interface SETLTargetRecord {
		Object getField(String name);

		void setField(String name, Object value);
	}
}
