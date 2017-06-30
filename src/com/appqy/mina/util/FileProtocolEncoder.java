package com.appqy.mina.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
 
/**
 * @project_name    coderServer
 * @description     用于客户端编码
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-11-1
 * @file_name       FileProtocolEncoder.java
 */
public class FileProtocolEncoder implements MessageEncoder<BaseMessage> {

		/**
		 * 基本信息编码
		 * */
		@Override
		public void encode(IoSession session, BaseMessage message,ProtocolEncoderOutput outPut) throws Exception {
			
			IoBuffer buffer = IoBuffer.allocate(1024*1024*1);
			//创建自动缩小的Buffer  Buffer会保持在上面设置的1024*1024*1,但是一旦需求超过会自动增加容量
			buffer.setAutoExpand(true);
			//传入数据类型
			buffer.putInt(message.getDataType());
			//存储的业务数据
			FileBean bean = (FileBean) message.getData();
			//文件名
			byte[] byteStr = bean.getFileName().getBytes(BeanUtil.charset);
			
			//传入文件名长度
			buffer.putInt(byteStr.length);
			//文件大小
			
			buffer.putInt(bean.getFileSize());
			//传入文件名
			buffer.put(byteStr);
			//传入文件内容
			buffer.put(bean.getFileContent());
			//打包,归0数组指针
			buffer.flip();
			//发送
			outPut.write(buffer);
			System.out.println("编码完成！");
			buffer.shrink();
		}

	}