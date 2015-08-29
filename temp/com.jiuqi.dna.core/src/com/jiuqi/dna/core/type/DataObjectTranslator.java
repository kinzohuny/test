package com.jiuqi.dna.core.type;

/**
 * 对象转化器
 * 
 * <p>
 * 用于序列化和克隆数据对象， 当某些对象无法达到DNA序列化要求时，注册专有的对象转化器辅助序列化动作。
 * 
 * @author gaojingxin
 * 
 */
public interface DataObjectTranslator<TSourceObject, TDelegateObject> {

	/**
	 * 获取当前自定义序列化器版本
	 */
	public short getVersion();

	/**
	 * 最小支持的序列化版本
	 */
	public short supportedVerionMin();

	/**
	 * 是否支持复制对象
	 */
	public boolean supportAssign();

	/**
	 * 获取源对象对应的数据代理对象
	 * 
	 * <p>
	 * 即可被系统接受的对象（各种装箱类型、枚举、数组、String、GUID、Class、
	 * DataType以及实现了DataObjectTraslator 的各种类型，包括绝大部分的java.util下的容器类型）
	 */
	public TDelegateObject toDelegateObject(TSourceObject obj);

	/**
	 * 确定还原后的对象实例，还原工作分为确定实例以及还原数据两部分。
	 * 
	 * @param destHint
	 *            目标对象提示，实现者应该考虑在可能的情况下重用该对象作为目标对象
	 * @param delegate
	 *            对应的数据代理对象
	 * @param version
	 *            转换器版本号
	 * @param forSerial
	 *            是否被序列化过程调用
	 * @return 返回原是对象的实例，已被后续还原数据使用
	 */
	public TSourceObject resolveInstance(TSourceObject destHint,
			TDelegateObject delegate, short version, boolean forSerial);

	/**
	 * 还原制定目标对象的数据
	 * 
	 * @param dest
	 *            制定目标对象
	 * @param delegate
	 *            对应的数据代理对象
	 * @param version
	 *            转换器版本号
	 * @param forSerial
	 *            是否被序列化过程调用
	 */
	public void recoverData(TSourceObject dest, TDelegateObject delegate,
			short version, boolean forSerial);
}