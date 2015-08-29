package com.jiuqi.dna.core.impl;

import java.util.ArrayList;

import com.jiuqi.dna.core.auth.Authority;
import com.jiuqi.dna.core.spi.auth.callback.AccessControlEntry.AuthorityEntry.DataItem;

/**
 * 资源操作帮助类
 * 
 * @author LiuZhi 2009-12
 */
final class AccessControlHelper {

	static final long[] EMPTY_ACL;

	static {
		EMPTY_ACL = new long[0];
	}

	static final ArrayList<Long> scavengeAuthorityDataFrom(final long[] ACL,
			ArrayList<Long> longArrayList) {
		if (longArrayList == null) {
			longArrayList = new ArrayList<Long>();
		} else {
			longArrayList.clear();
		}
		if (ACL == null || ACL.length == 0) {
			return longArrayList;
		} else {
			for (int index = 0, capacity = (ACL.length / 3) * 2; index < capacity; index += 2) {
				int hashIndex = index;
				for (;;) {
					if (ACL[hashIndex] != 0L) {
						longArrayList.add(ACL[hashIndex]);
						longArrayList.add((ACL[hashIndex + 1] >>> 32) & 0x00000000FFFFFFFFL);
					}
					hashIndex = (int) (ACL[hashIndex + 1] & 0x00000000FFFFFFFFL);
					if (hashIndex <= 0) {
						break;
					}
				}
			}
			return longArrayList;
		}
	}

	static final long[] recoveACLWithAuthorityData(long[] ACL,
			final long[] authorityDatas) {
		if (authorityDatas == null) {
			return null;
		}
		if (authorityDatas.length == 0) {
			return EMPTY_ACL;
		}
		for (int index = 0, endIndex = authorityDatas.length; index < endIndex;) {
			ACL = setAuthority(ACL, authorityDatas[index++], (int) (authorityDatas[index++] & 0xFFFFFFFFL));
		}
		return ACL;
	}

	static final boolean equalACL(final long[] ACL1, final long[] ACL2) {
		if (ACL1 == null) {
			return ACL2 == null;
		} else {
			if (ACL2 != null && ACL1.length == ACL2.length) {
				for (int index = 0, endIndex = ACL1.length; index < endIndex; index++) {
					if (ACL1[index] != ACL2[index]) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 换算权限掩码<br>
	 * 一位的权限掩码将被换算成对应的两位权限掩码。
	 * 
	 * <pre>
	 * operationMask = 00000000000000000000000000011001B
	 * authorityMask = 00000000000000000000001111000011B
	 * </pre>
	 * 
	 * @param operationMask
	 *            操作定义掩码，后十六位有效
	 * @return 返回权限掩码
	 */
	static final int toAuthorityMask(final int operationMask) {
		int result = 0;
		int authorityMask = 0x3;
		int mark = 1;
		for (int i = 0; i < 16; i++) {
			if ((mark & operationMask) != 0) {
				result |= authorityMask;
			}
			authorityMask <<= 2;
			mark <<= 1;
		}
		return result;
	}

	/**
	 * 换算权限码<br>
	 * 
	 * <pre>
	 * operationMask = 00000000000000000000000000011001B
	 * authority     = 10B
	 * authorityCode = 00000000000000000000001010000010B
	 * </pre>
	 * 
	 * @param operationMask
	 *            操作定义掩码，后十六位有效
	 * @param authority
	 *            授权，后两位有效
	 * @return 返回权限码
	 */
	static final int toAuthorityCode(final int operationMask,
			final int authority) {
		int authorityMask;
		if ((authorityMask = authority & 3) == 0) {
			return 0;
		}
		int result = 0;
		int mark = 1;
		for (int i = 0; i < 16; i++) {
			if ((mark & operationMask) != 0) {
				result |= authorityMask;
			}
			authorityMask <<= 2;
			mark <<= 1;
		}
		return result;
	}

	static final long[] createACL(final int size) {
		if (size <= DEFAULT_ACL_SIZE) {
			return new long[DEFAULT_ACL_SIZE * 2 + 1];
		}
		int initializeSize = DEFAULT_ACL_SIZE * 2;
		while (initializeSize < size) {
			initializeSize *= 2;
		}
		return new long[initializeSize * 2 + 1];
	}

	static final boolean isEmpty(final long[] ACL) {
		if (ACL == null || ACL.length == 0) {
			return true;
		}
		for (int index = 0, capacity = (ACL.length / 3) * 2; index < capacity; index += 2) {
			int hashIndex = index;
			for (;;) {
				if (ACL[hashIndex] != 0L) {
					return false;
				}
				hashIndex = (int) (ACL[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (hashIndex <= 0) {
					break;
				}
			}
		}
		return true;
	}

	static final Authority getAuthority(final int authorityCode,
			final OperationEntry operation) {
		final int operationMask = toAuthorityMask(operation.getMask());
		final int operationCode = authorityCode & operationMask;
		if (operationCode == 0) {
			return Authority.UNDEFINE;
		} else if (operationCode == operation.allowAuthorityCode) {
			return Authority.ALLOW;
		} else {
			return Authority.DENY;
		}
	}

	static final int getAuthority(final long[] ACL, final long itemIdentifier) {
		if (ACL == null || ACL.length == 0) {
			return 0;
		}
		int hashIndex = (int) ((((ACL.length - 1) / 3 - 1) & ((itemIdentifier >>> 28) ^ (itemIdentifier >>> 4))) << 1);
		long ID = ACL[hashIndex];
		for (;;) {
			if (ID == itemIdentifier) {
				return (int) (ACL[hashIndex + 1] >>> 32);
			} else {
				hashIndex = (int) (ACL[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (hashIndex <= 0) {
					return 0;
				}
				ID = ACL[hashIndex];
			}
		}
	}

	static final long[] setAuthority(final long[] ACL,
			final long itemIdentifier, final ArrayList<DataItem> dataItemList) {
		int authorityCode = 0;
		for (DataItem dataItem : dataItemList) {
			if (dataItem == null) {
				continue;
			}
			authorityCode &= (~toAuthorityCode(dataItem.operation.getMask(), 3));
			authorityCode |= toAuthorityCode(dataItem.operation.getMask(), dataItem.authority.code);
		}
		return setAuthority(ACL, itemIdentifier, authorityCode);
	}

	static final long[] setAuthority(final long[] ACL,
			final long itemIdentifier, final int authorityCode) {
		return setAuthority(ACL, itemIdentifier, 0xFFFFFFFF00000000L, ((long) authorityCode) << 32);
	}

	/**
	 * 
	 * @param operationCode
	 *            32,34,36...62
	 * @param authority
	 *            0,1,2,3
	 * @return
	 */
	static final long[] setAuthority(final long[] ACL,
			final long holderIdentifier, final int operationCode,
			final int authority) {
		return setAuthority(ACL, holderIdentifier, OPERATION_MASK << operationCode, ((authority) & OPERATION_MASK) << operationCode);
	}

	private static final long[] setAuthority(long[] ACL,
			final long holderIdentifier, final long mask,
			final long authorityMask) {
		if (ACL == null || ACL.length == 0) {
			if (authorityMask == 0) {
				return EMPTY_ACL;
			}
			ACL = createACL(DEFAULT_ACL_SIZE);
			int hashIndex = (int) (((DEFAULT_ACL_CAPACITY - 1) & ((holderIdentifier >>> 28) ^ (holderIdentifier >>> 4))) << 1);
			ACL[hashIndex] = holderIdentifier;
			ACL[hashIndex + 1] = authorityMask;
			return ACL;
		}
		final int ACLSize = ACL.length - 1;
		final int hashCapacity = ACLSize / 3;
		int hashIndex = (int) (((hashCapacity - 1) & ((holderIdentifier >>> 28) ^ (holderIdentifier >>> 4))) << 1);
		if (ACL[hashIndex] == 0L) {
			// hash区对应位置为空
			if (authorityMask != 0L) {
				ACL[hashIndex++] = holderIdentifier;
				ACL[hashIndex] &= ~mask;
				ACL[hashIndex] |= authorityMask;
				return ACL;
			}
		} else if (ACL[hashIndex] == holderIdentifier) {
			// hash区对应位置存储了指定资源的授权信息
			if (authorityMask == 0L) {
				ACL[hashIndex++] = 0L;
				ACL[hashIndex] &= 0x00000000FFFFFFFFL;
			} else {
				ACL[hashIndex++] = holderIdentifier;
				ACL[hashIndex] &= ~mask;
				ACL[hashIndex] |= authorityMask;
			}
			return ACL;
		}
		// hash区对应位置不为空， 且不为指定资源，查找下一个
		if (authorityMask == 0L) {// 回收冲突空间的情况
			for (;;) {
				int nextIndex = (int) (ACL[++hashIndex] & 0x00000000FFFFFFFFL);
				if (nextIndex > 0) {// 有下一个
					if (ACL[nextIndex] == holderIdentifier) {// 下一个为要找的资源，
						// 回收下一个冲突空间
						ACL[nextIndex++] = 0L;
						ACL[hashIndex] = (ACL[hashIndex] & 0xFFFFFFFF00000000L) | ((int) (ACL[nextIndex] & 0x00000000FFFFFFFFL));
						ACL[nextIndex] = ((int) (ACL[ACLSize] & 0x00000000FFFFFFFFL)) + (nextIndex - 1) - ACLSize;
						ACL[ACLSize] = ACLSize - (nextIndex + 1);
						return ACL;
					}
					hashIndex = nextIndex;
				} else {
					return ACL;
				}
			}
		} else {
			for (;;) {
				int nextIndex = (int) (ACL[hashIndex + 1] & 0x00000000FFFFFFFFL);
				if (nextIndex > 0) {// 有下一个
					if (ACL[nextIndex] == holderIdentifier) {// 找到
						ACL[++nextIndex] &= ~mask;
						ACL[nextIndex] |= authorityMask;
						return ACL;
					}
					hashIndex = nextIndex;
				} else {// 没找到，需要添加
					final int firstFreeOffset = (int) (ACL[ACLSize] & 0x00000000FFFFFFFFL) + 2;
					if (firstFreeOffset <= hashCapacity) {
						final int firstFreeIndex = ACLSize - firstFreeOffset;
						hashIndex++;
						ACL[hashIndex] = (ACL[hashIndex] & 0xFFFFFFFF00000000L) | (firstFreeIndex & 0x00000000FFFFFFFFL);
						ACL[ACLSize] = ACL[firstFreeIndex + 1] + firstFreeOffset;
						ACL[firstFreeIndex] = holderIdentifier;
						ACL[firstFreeIndex + 1] = authorityMask;
						return ACL;
					} else {
						// 需重新hash
						int newACLSize = ACLSize << 1;
						int newHashCapacity = hashCapacity << 1;
						long[] newACL = new long[newACLSize + 1];
						int freeIndex = newACLSize - 1;
						int newHashIndex = (int) (((newHashCapacity - 1) & ((holderIdentifier >>> 28) ^ (holderIdentifier >>> 4))) << 1);
						newACL[newHashIndex++] = holderIdentifier;
						newACL[newHashIndex] = authorityMask;
						long oldResID, oldAuth, existID;
						for (int index = 0; index < ACLSize; index += 2) {
							oldResID = ACL[index];
							if (oldResID == 0L) {
								continue;
							}
							oldAuth = ACL[index + 1] & 0xFFFFFFFF00000000L;
							newHashIndex = (int) (((newHashCapacity - 1) & ((oldResID >>> 28) ^ (oldResID >>> 4))) << 1);
							existID = newACL[newHashIndex];
							newACL[newHashIndex++] = oldResID;
							if (existID == 0L) {
								newACL[newHashIndex] = oldAuth;
							} else {
								newACL[freeIndex--] = newACL[newHashIndex];
								newACL[newHashIndex] = oldAuth | freeIndex;
								newACL[freeIndex--] = existID;
							}
							if (freeIndex <= newHashCapacity) {
								newACLSize = newACLSize << 1;
								newHashCapacity = newHashCapacity << 1;
								newACL = new long[newACLSize + 1];
								freeIndex = newACLSize - 1;
								newHashIndex = (int) (((newHashCapacity - 1) & ((holderIdentifier >>> 28) ^ (holderIdentifier >>> 4))) << 1);
								newACL[newHashIndex++] = holderIdentifier;
								newACL[newHashIndex] = authorityMask;
								index = 0;
							}
						}
						newACL[newACLSize] = newACLSize - 1 - freeIndex;
						return newACL;
					}
				}
			}
		}
	}

	private static final int DEFAULT_ACL_CAPACITY = 1 << 2;

	private static final int DEFAULT_ACL_SIZE = DEFAULT_ACL_CAPACITY * 3 / 2;

	private static final long OPERATION_MASK = 0x3L;

	private AccessControlHelper() {
		// do nothing
	}

}
