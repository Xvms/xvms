package com.appqy.mina.util;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;


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

public class StringProtocolDecoder implements MessageDecoder {
		
		private AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
		/*
		 * 判断是可被当前解码器解码
		 * @see org.apache.mina.filter.codec.demux.MessageDecoder#decodable(org.apache.mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer)
		 */
		@Override
		public MessageDecoderResult decodable(IoSession session, IoBuffer in)
		{
			
			Context context = (Context) session.getAttribute(CONTEXT);
			if(context!=null){
				if(context.limit_data){
					in.buf().position(0);
				}
			}
			//表示数据不够，需要读到新的数据后，再次调用decode()方法。
			if (in.remaining() < 2){
				return MessageDecoderResult.NEED_DATA;
			}
			else{
					context = new Context();
					//获取一个字符表示新的数据开始 用于判断异常数据
					in.skip(1);//这个数据是十六进制的 01 也就是1
					//获取第一个字符用于判断是否可以被当前解码器解码
					context.dataType = in.getInt();
					
					if(context.dataType == BeanUtil.UPLOAD_STR){
						//读取标题长度
						context.strLength = in.getInt();
						//声明数组长度
						context.byteStr = new byte[context.strLength];
						//System.out.println("我收到2了");
						session.setAttribute(CONTEXT, context);
						//表示可以解码
						return MessageDecoderResult.OK;
					}else{
						//System.out.println("服务端收到意外数据");
						return MessageDecoderResult.NOT_OK;
					}
				}
		}

		@Override
		public MessageDecoderResult decode(IoSession session, IoBuffer in,ProtocolDecoderOutput outPut) throws Exception {
			//将客户端发来的对象进行封装
			//TODO  等待测试超长数据是否能正常解码 
			Context context = (Context) session.getAttribute(CONTEXT);
			//跳过第一个字节
			if(!context.init){
				in.buf().position(0);//由于分包之后mina不能让没有改变的缓冲数据返回正常,于是先移动了下游标,这里给归0
				context.init = true;
				in.skip(1);
				in.getInt();
				in.getInt();
			}
 
			int count = context.count;
			//System.out.println("这里是第一次COnut："+context.count);
			//System.out.println("一共有"+in.remaining()+"数据");
			while (in.hasRemaining()) {
				//System.out.println("循环里面的Count："+count);
				byte b = in.get();
				if(b == 1){
					//收到下一条的起始数据了,证明此条数据已经残缺 重置缓冲区 作废此信息
					in.buf().position(1);//移动游标,mina不允许不使用数据就返回
					context.reset();//重置
					context.limit_data = true;//用于判断是否是前一次包破损后遗留下的新数据包  decodable中判断
					session.setAttribute(CONTEXT, context);
					return MessageDecoderResult.OK;//给他返回个正常解码,然后才能继续解码
				}
				new String(context.byteStr,BeanUtil.charset);
				//如果标题没读完 继续读
				if(!context.isReadName){
					context.byteStr[count] = b;
					if(count == context.strLength-1){
						//标题读完  byte[]转换为字符串
						context.fileName = new String(context.byteStr,BeanUtil.charset);
						//System.out.println(context.fileName);
						//count = -1;
						context.isReadName = true;
						//跳出程序
						break;
					}
				}
				/*
				if(context.isReadName && count != -1){
					//如果读取完了标题那么读取其他内容
					//byteFile[count] = b;
				}
				//byteFile[count] = b;
				 */
				//这里并未判断是否后面还有数据就加了1
				if(in.buf().position()<=in.buf().limit())
				count++;
            }
 
			context.count = count;
			session.setAttribute(CONTEXT, context);
			//如果内容全部读完  那么就存入并返回数据
			//System.out.println("Count:"+context.count+";----StrLen:"+context.strLength);
			if(context.count == context.strLength-1){
				//这里就代表数据全部接收完了 返回出去
				
				BaseMessage message = new BaseMessage();
				message.setDataType(context.dataType);
				StringBean bean = new StringBean();
				bean.setFileName(context.fileName);
				bean.setStrLength(context.strLength);
				message.setData(bean);
				outPut.write(message);
				//重置
				context.reset();
 
			}else{
				return  MessageDecoderResult.NEED_DATA;
			}
			//this.finishDecode(session, outPut);
			
			return MessageDecoderResult.OK;
		}

		/** 
		* 将IoBuffer转换成string   
		* @param str 
		*/  
		public IoBuffer byteToIoBuffer(byte [] bt,int length)  
		{  
		  
		       IoBuffer ioBuffer = IoBuffer.allocate(length);  
		       ioBuffer.put(bt, 0, length);  
		       ioBuffer.flip();  
		       return ioBuffer;  
		}
		
		@Override
		public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
				throws Exception {
			
			
		}
		
		private class Context{
			//供判断的数据类型
			public int dataType;
			//字符串长度
			public int strLength;
			//分页统计
			public int count;
			//是否已读完标题 PS这里指第一个单条指令
			public boolean isReadName;
			//第一个指令
			public String fileName;
			public byte[] byteStr;
			public boolean init = false;
			//是否是上传分包处理的数据
			public boolean limit_data = false;
			public void reset(){
				dataType = 0;
				strLength = 0;
				count = 0;
				isReadName = false;
				byteStr = null;
				fileName = null;
				limit_data = false;
			}
		}
}