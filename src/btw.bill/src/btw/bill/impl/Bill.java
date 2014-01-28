package btw.bill.impl;

import com.jiuqi.dna.core.type.GUID;

public class Bill implements Comparable<Bill>{

	public GUID id;
	public long dt;
	public String sn;
	public int compareTo(Bill b) {
		return 0-this.sn.compareTo(b.sn);
	}
}
