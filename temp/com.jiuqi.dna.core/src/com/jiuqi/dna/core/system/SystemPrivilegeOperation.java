package com.jiuqi.dna.core.system;

import com.jiuqi.dna.core.auth.Operation;

public enum SystemPrivilegeOperation implements Operation<SystemPrivilege> {

	EXECUTE {

		public String getTitle() {
			return "Ö´ÐÐ";
		}

		public int getMask() {
			return 1 << 0;
		}
	};

}
