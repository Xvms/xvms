package com.appqy.mina.util;
 
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * @project_name    coderServer
 * @description     服务器反还客户端消息使用的
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-11-30
 * @file_name       ResultStrMessageEncoder.java
 */

public class ResultStringMessageEncoder implements MessageEncoder<BaseMessage> {
	@Override
	public void encode(IoSession session, BaseMessage message,ProtocolEncoderOutput out) throws Exception {
		System.out.println("hahhahahahah");
		BaseMessage baseMessage = message;
		StringBean bean = (StringBean) baseMessage.getData();
		out.write(bean.getFileName());
		
	}
}
