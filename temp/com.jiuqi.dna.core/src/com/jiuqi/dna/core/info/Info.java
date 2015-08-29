package com.jiuqi.dna.core.info;

import com.jiuqi.dna.core.def.info.InfoDefine;
import com.jiuqi.dna.core.type.GUID;

/**
 * 框架中的信息实例借口
 * 
 * @author gaojingxin
 * 
 */
public interface Info {
	/**
	 * 获得其所属的过程信息
	 */
	public ProcessInfo getProcess();

	/**
	 * 获得信息项的定义
	 */
	public InfoDefine getDefine();

	/**
	 * 获取开始时间
	 */
	public long getTime();

	/**
	 * 获取参数值
	 */
	public Object getParam(int index);

	/**
	 * 获得ID，作为日志保存时使用，该GUID随时间递增<br>
	 */
	public GUID getID();
}
