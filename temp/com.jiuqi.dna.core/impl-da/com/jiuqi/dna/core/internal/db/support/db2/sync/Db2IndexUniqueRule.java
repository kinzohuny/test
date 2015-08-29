package com.jiuqi.dna.core.internal.db.support.db2.sync;

enum Db2IndexUniqueRule {

	/**
	 * permits duplicates
	 */
	P(true, true) {
	},

	/**
	 * unique
	 */
	U(true, false) {
	},

	/**
	 * implements primary key
	 */
	D(false, false) {
	};

	final boolean unqiue;
	final boolean primaryKey;

	Db2IndexUniqueRule(boolean unique, boolean primaryKey) {
		this.unqiue = unique;
		this.primaryKey = primaryKey;
	}
}