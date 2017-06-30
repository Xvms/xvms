package com.appqy.mina.util;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import com.appqy.mina.server.MinaServer;

/**
 * @project_name    coderServer
 * @description     服务端字符串解码器
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-30
 * @file_name       StringDecoder.java
 */

public class StringProtocolEncoder implements MessageEncoder<BaseMessage> {
	private final static Logger log = Logger.getLogger(StringProtocolEncoder.class);
	/**
	 * 基本信息编码
	 * */
	@Override
	public void encode(IoSession session, BaseMessage message,ProtocolEncoderOutput outPut) throws Exception {
		
		
		IoBuffer buffer = IoBuffer.allocate(1024);
		//创建自动缩小的Buffer  Buffer会保持在上面设置的1024*1024*1,但是一旦需求超过会自动增加容量
		buffer.setAutoExpand(true);
		//传入数据类型
		buffer.putInt(message.getDataType());
		//存储的业务数据
		StringBean bean = (StringBean) message.getData();
		//文件名
		byte[] byteStr = bean.getFileName().getBytes(BeanUtil.charset);
		
		//传入文件名长度
		buffer.putInt(byteStr.length);
		//传入文件名
		//CharSequence charSequrnce = "你好";
		//CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
		//buffer.putString(charSequrnce, encoder);
		buffer.put(byteStr);
		//打包,归0数组指针
		buffer.flip();
		//发送
		outPut.write(buffer);
		log.debug("编码完成");
		//自动调整一下buffer的内存空间
		buffer.shrink();
	}

}