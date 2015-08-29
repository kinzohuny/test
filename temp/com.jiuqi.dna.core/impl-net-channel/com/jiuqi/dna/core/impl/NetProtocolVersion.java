package com.jiuqi.dna.core.impl;

import com.jiuqi.dna.core.exception.NullArgumentException;

enum NetProtocolVersion {
	VER_2_5(0x02050000, "DNA/2.5"), VER_3_0(0x03000000, "DNA/3.0"), VER_3_0_1(
			0x03000100, "DNA/3.0.1");

	public final int ver;
	public final String tag;

	private NetProtocolVersion(int ver, String tag) {
		this.ver = ver;
		this.tag = tag;
	}

	public final static NetProtocolVersion getFromTag(String tag) {
		if (tag == null) {
			throw new NullArgumentException("tag");
		}
		for (NetProtocolVersion ver : NetProtocolVersion.values()) {
			if (ver.tag.equals(tag)) {
				return ver;
			}
		}
		return null;
	}

	public final static NetProtocolVersion maxVer() {
		NetProtocolVersion ver = null;
		for (NetProtocolVersion v : NetProtocolVersion.values()) {
			if (ver == null || ver.ver < v.ver) {
				ver = v;
			}
		}
		return ver;
	}
}
