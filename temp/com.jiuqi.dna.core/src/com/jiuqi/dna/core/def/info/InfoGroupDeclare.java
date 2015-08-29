package com.jiuqi.dna.core.def.info;

import com.jiuqi.dna.core.def.ModifiableNamedElementContainer;
import com.jiuqi.dna.core.def.NamedDeclare;

/**
 * 信息组声明
 * 
 * @author gaojingxin
 * 
 */
public interface InfoGroupDeclare extends InfoGroupDefine, NamedDeclare {
	/**
	 * 得到参数容器
	 * 
	 * @return 返回参数容器
	 */
	public ModifiableNamedElementContainer<? extends InfoDeclare> getInfos();

	public ErrorInfoDeclare newError(String name, String messageFrmt);

	public WarningInfoDeclare newWarning(String name, String messageFrmt);

	public HintInfoDeclare newHint(String name, String messageFrmt);

	/**
	 * 新建过程信息项
	 */
	public ProcessInfoDeclare newProcess(String name, String messageFrmt);
}
