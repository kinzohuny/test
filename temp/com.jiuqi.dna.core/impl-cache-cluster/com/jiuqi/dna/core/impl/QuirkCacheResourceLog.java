/**
 * 
 */
package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.type.GUID;

/**
 * 集群情况下资源修改日志
 * 
 * @author yangduanxue
 * 
 */
public final class QuirkCacheResourceLog {

	private GUID id;
	private String facade;
	private int modifyTimes;
	private boolean quirk;

	/**
	 * @return the id
	 */
	public final GUID getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(GUID id) {
		this.id = id;
	}

	/**
	 * @return the facade
	 */
	public final String getFacade() {
		return this.facade;
	}

	/**
	 * @param facade
	 *            the facade to set
	 */
	public final void setFacade(String facade) {
		this.facade = facade;
	}

	/**
	 * @return the modifyTime
	 */
	public final int getModifyTimes() {
		return this.modifyTimes;
	}

	/**
	 * @param modifyTimes
	 *            the modifyTime to set
	 */
	public final void setModifyTimes(int modifyTimes) {
		this.modifyTimes = modifyTimes;
	}

	/**
	 * @return the syncHandle
	 */
	public final boolean isQuirk() {
		return this.quirk;
	}

	/**
	 * @param quirk
	 *            the syncHandle to set
	 */
	public final void setQuirk(boolean quirk) {
		this.quirk = quirk;
	}

}
