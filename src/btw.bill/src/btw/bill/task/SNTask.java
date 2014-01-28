package btw.bill.task;

import com.jiuqi.dna.core.invoke.SimpleTask;

public class SNTask extends SimpleTask {

	public SNTask(long time){
		this.time = time;
	}
	
	public long time;
	public String dt;
	public int sn;
}
