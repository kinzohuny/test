package com.jiuqi.dna.core.spi.setl;

import java.util.ArrayList;

import com.jiuqi.dna.core.exception.NullArgumentException;
import com.jiuqi.dna.core.invoke.Task;
import com.jiuqi.dna.core.misc.SXElement;
import com.jiuqi.dna.core.type.GUID;

/**
 * 批量执行提取任务
 * 
 * @author gaojingxin
 * 
 */
public final class SETLBatchExecuteTask extends Task<SETLBatchExecuteTask.Mode> {
	public enum Mode {
		/**
		 * 做检查
		 */
		CHECK,
		/**
		 * 执行批量提取
		 */
		BATCH
	}

	/**
	 * 帮助器
	 */
	public final SETLExternalHelper helper;

	/**
	 * 获取统计对象，便于设置相关统计值
	 */
	public final SETLStatistic statistic = new SETLStatistic();

	public static class SETLProjectXML {
		public final SXElement xml;
		public final GUID rptSolutionID;

		private SETLProjectXML(SXElement xml, GUID rptSolutionID) {
			if (xml == null) {
				throw new NullArgumentException("xml");
			}
			if (rptSolutionID == null) {
				throw new NullArgumentException("rptSolutionID");
			}
			this.xml = xml;
			this.rptSolutionID = rptSolutionID;
		}
	}

	/**
	 * 提取方案列表
	 */
	private final ArrayList<SETLProjectXML> projectsXML = new ArrayList<SETLProjectXML>();

	public final void addProject(SXElement projectXml, GUID rptSolutionID) {
		this.projectsXML.add(new SETLProjectXML(projectXml, rptSolutionID));
	}

	public final int getProjectCount() {
		return this.projectsXML.size();
	}

	public final void clearProjects() {
		this.projectsXML.clear();
	}

	public final SETLProjectXML getProject(int index) {
		return this.projectsXML.get(index);
	}

	public SETLBatchExecuteTask(SETLExternalHelper helper) {
		if (helper == null) {
			throw new NullArgumentException("helper");
		}
		this.helper = helper;
	}
}
