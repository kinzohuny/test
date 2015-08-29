package com.jiuqi.dna.core.impl;

interface INetPackageSign {
	/**
	 * 远程调用包
	 */
	final static byte REQUEST_PACKAGE = 0x01;
	/**
	 * 类型信息包
	 */
	final static byte TYPE_PACKAGE = 0x02;
	/**
	 * 站点包
	 */
	final static byte SITE_PACKAGE = 0x03;
	/**
	 * 集群/远程调用事务包
	 */
	final static byte TRANSACTION_PACKAGE = 0x05;
}