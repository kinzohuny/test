package com.jiuqi.dna.core.spi.publish;

/**
 * �ռ��ʶ
 * 
 * @author gaojingxin
 * 
 */
public interface SpaceToken {
	public String getName();

	public SpaceToken getParent();

	public SpaceToken getSibling();

	public SpaceToken getFirstChild();

	public SpaceToken getSite();
}
