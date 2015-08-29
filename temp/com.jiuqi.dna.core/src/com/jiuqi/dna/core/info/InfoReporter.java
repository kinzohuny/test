package com.jiuqi.dna.core.info;

import com.jiuqi.dna.core.def.info.ErrorInfoDefine;
import com.jiuqi.dna.core.def.info.HintInfoDefine;
import com.jiuqi.dna.core.def.info.ProcessInfoDefine;
import com.jiuqi.dna.core.def.info.WarningInfoDefine;

/**
 * 信息报告接口
 * 
 * @author gaojingxin
 * 
 */
public interface InfoReporter extends ProgressReporter {
	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * 报告提示信息
	 */
	public void reportHint(HintInfoDefine info, Object param1, Object param2,
			Object param3, Object... others);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * 报告错误信息
	 */
	public void reportError(ErrorInfoDefine info, Object param1, Object param2,
			Object param3, Object... others);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * 报告警告信息
	 */
	public void reportWarning(WarningInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * 报告Done信息
	 */
	public void reportWarning(WarningInfoDefine info, Object param1,
			Object param2, Object param3, Object... others);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3);

	/**
	 * 始某个过程，随后的信息报告都从属于该过程。<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void beginProcess(ProcessInfoDefine infoDefine, Object param1,
			Object param2, Object param3, Object... others);

	/**
	 * 完成报告信息<br>
	 * 过程的开始必须与结束配对使用即:
	 * 
	 * <pre>
	 * context.beginProcess(...);
	 * try{
	 *      XXXXX
	 * }finally{
	 *     context.endProcess();
	 * }
	 * </pre>
	 */
	public void endProcess();
}
