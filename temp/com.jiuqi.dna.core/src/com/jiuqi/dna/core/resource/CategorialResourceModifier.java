package com.jiuqi.dna.core.resource;

/**
 * ������������Դ�޸���
 * 
 * @author gaojingxin
 * 
 * @param <TFacade>
 * @param <TImpl>
 * @param <TKeysHolder>
 */
public interface CategorialResourceModifier<TFacade, TImpl extends TFacade, TKeysHolder>
        extends ResourceModifier<TFacade, TImpl, TKeysHolder>,
        CategorialResourceQuerier {

}
