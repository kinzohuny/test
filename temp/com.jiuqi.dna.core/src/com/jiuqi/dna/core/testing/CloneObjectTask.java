/**
 * 
 */
package com.jiuqi.dna.core.testing;

import com.jiuqi.dna.core.invoke.SimpleTask;

/**
 * @author hanfei
 * 
 */
public final class CloneObjectTask extends SimpleTask {

	private static final Object NONE_OBJECT = new Object();

	public final Object src;

	private Object dest;

	public final Object getCloneObject() {
		if (this.dest == NONE_OBJECT) {
			throw new IllegalStateException();
		}
		return dest;
	}

	public final void setCloneObject(final Object dest) {
		if (this.dest != NONE_OBJECT) {
			throw new IllegalStateException();
		}
		this.dest = dest;
	}

	public CloneObjectTask(final Object src) {
		this.src = src;
		this.dest = NONE_OBJECT;
	}

}
