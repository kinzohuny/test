package com.jiuqi.dna.core.situation;

/**
 * ÏûÏ¢¼àÌıÆ÷
 * 
 * @author gaojingxin
 * 
 */
public interface MessageListener<TMessage> {
	public void onMessage(Situation context, TMessage message,
			MessageTransmitter<TMessage> transmitter);
}
