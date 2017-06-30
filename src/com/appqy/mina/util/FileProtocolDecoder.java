package com.appqy.mina.util;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @project_name    coderServer
 * @description     服务端文件解码器
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-30
 * @file_name       FileDecoder.java
 */

public class FileProtocolDecoder implements MessageDecoder {
		private final static Logger log = LoggerFactory.getLogger(FileProtocolDecoder.class);
		private AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
		/*
		 * 判断是可被当前解码器解码
		 * @see org.apache.mina.filter.codec.demux.MessageDecoder#decodable(org.apache.mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer)
		 */
		@Override
		public MessageDecoderResult decodable(IoSession session, IoBuffer in)
		{
			Context context = (Context) session.getAttribute(CONTEXT);
			
			
			//表示数据不够，需要读到新的数据后，再次调用decode()方法。
			if(context == null){
				context = new Context();
				// 跳过前4字节
				//in.skip(4); 
				//获取第一个字符用于判断是否可以被当前解码器解码
				context.dataType = in.getInt();
				if(context.dataType == BeanUtil.UPLOAD_FILE){
					System.out.println("我收到1了");
					context.strLength = in.getInt();
					context.byteStr = new byte[context.strLength];
					context.fileSize = in.getInt();
					context.byteFile = new byte[context.fileSize];
					session.setAttribute(CONTEXT, context);
					return MessageDecoderResult.OK;
				}else{
					return MessageDecoderResult.NOT_OK;
				}
			}else{
				if(context.dataType == BeanUtil.UPLOAD_FILE){
					//表示可以解码
					return MessageDecoderResult.OK;
				}else{
					//表示不能解码,会抛出异常
					return MessageDecoderResult.NOT_OK;
				}
			}
		}
			

		@Override
		public MessageDecoderResult decode(IoSession session, IoBuffer in,ProtocolDecoderOutput outPut) throws Exception {
			//将客户端发来的对象进行封装
			System.out.println("开始解码：");
			Context context = (Context) session.getAttribute(CONTEXT);
			if(!context.init){
				context.init = true;
				in.getInt();
				in.getInt();
				in.getInt();
			}
			byte[] byteFile = context.byteFile;
			int count = context.count;
			while(in.hasRemaining()){
				byte b = in.get();
				if(!context.isReadName){
					context.byteStr[count] = b;
					if(count == context.strLength-1){
						context.fileName = new String(context.byteStr,BeanUtil.charset);
						System.out.println(context.fileName);
						count = -1;
						context.isReadName = true;
					}
				}
				if(context.isReadName && count != -1){
					byteFile[count] = b;
				}
			//	byteFile[count] = b;
				count++;
 
			}
 
			context.count = count;
			System.out.println("count:"+count);
			System.out.println("context.fileSize:"+context.fileSize);
			session.setAttribute(CONTEXT, context);
			if(context.count == context.fileSize){
				BaseMessage message = new BaseMessage();
				message.setDataType(context.dataType);
				FileBean bean = new FileBean();
				bean.setFileName(context.fileName);
				bean.setFileSize(context.fileSize);
				bean.setFileContent(context.byteFile);
				message.setData(bean);
				outPut.write(message);
				context.reset();
			}
			return MessageDecoderResult.OK;
		}
		
		@Override
		public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
				throws Exception {
			
			
		}
		
		private class Context{
			//供判断的数据类型
			public int dataType;
			//文件字节
			public byte[] byteFile;
			//分页统计
			public int count;
			//标题字符串长度
			public int strLength;
			public boolean isReadName;
			//文件大小
			public int fileSize;
			public byte[] byteStr;
			public String fileName;
			public boolean init = false;
			
			public void reset(){
				dataType = 0;
				byteFile = null;
				count = 0;
				strLength = 0;
				isReadName = false;
				fileSize = 0;
				byteStr = null;
				fileName = null;
				
			}
		}
}