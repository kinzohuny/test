package com.jiuqi.dna.core.spi.auth.callback;

import java.util.ArrayList;

import com.jiuqi.dna.core.auth.ActorState;
import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.auth.Operation;
import com.jiuqi.dna.core.type.GUID;

public abstract class AccessControlEntry {

	AccessControlEntry() {
		// do nothing
	}

	public Exception exception;

	public static abstract class ActorEntry extends AccessControlEntry {

		ActorEntry(final GUID identifier, final String name,
				final String title, final ActorState state,
				final String description) {
			this.identifier = identifier;
			this.name = name;
			this.title = title;
			this.state = state;
			this.description = description;
		}

		public final GUID identifier;

		public final String name;

		public final String title;

		public final ActorState state;

		public final String description;

	}

	public static final class AuthorityEntry extends AccessControlEntry {

		public AuthorityEntry(final GUID groupIdentifier,
				final GUID itemIdentifier) {
			this.groupIdentifier = groupIdentifier;
			this.itemIdentifier = itemIdentifier;
			this.dataItemList = new ArrayList<DataItem>();
		}

		public final void addDataItem(final Operation<?> operation,
				final Authority authority) {
			this.dataItemList.add(new DataItem(operation, authority));
		}

		public final GUID groupIdentifier;

		public final GUID itemIdentifier;

		public final ArrayList<DataItem> dataItemList;

		public static final class DataItem {

			private DataItem(final Operation<?> operation,
					final Authority authority) {
				this.operation = operation;
				this.authority = authority;
			}

			public final Operation<?> operation;

			public final Authority authority;

		}

	}

}
