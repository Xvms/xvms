package com.appqy.mina.util;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * @project_name    coderServer
 * @description     组装这些编解码器的工厂
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-30
 * @file_name       MathProtocolCodecFactory.java
 * 
 * Demux 开发编解码器主要有如下几个步骤：
 * A. 定义Client 端、Server 端发送、接收的数据对象。
 * B. 使用Demux 编写编码器是实现MessageEncoder<T>接口，T 是你要编码的数据对象，这
 * 个MessageEncoder 会在DemuxingProtocolEncoder 中调用。
 * C. 使用Demux 编写编码器是实现MessageDecoder 接口，这个MessageDecoder 会在
 * DemuxingProtocolDecoder 中调用。
 * D. 在 DemuxingProtocolCodecFactory 中调用addMessageEncoder()、addMessageDecoder()
 * 方法组装编解码器。


 * 这个工厂类我们使用了构造方法的一个布尔类型的参数，以便其可以在Server 端、Client
 * 端同时使用。我们以Server 端为例，你可以看到调用两次addMessageDecoder()方法添加
 * 了1 号、2 号解码器，其实DemuxingProtocolDecoder 内部在维护了一个MessageDecoder
 * 数组，用于保存添加的所有的消息解码器，每次decode()的时候就调用每个MessageDecoder
 * 的decodable()方法逐个检查，只要发现一个MessageDecoder 不是对应的解码器，就从数
 * 组中移除，直到找到合适的MessageDecoder，如果最后发现数组为空，就表示没找到对应
 * 的MessageDecoder，最后抛出异常。
 */
public class DemuxingAppqyProtocolCodecFactory extends DemuxingProtocolCodecFactory {
	public DemuxingAppqyProtocolCodecFactory(boolean server , int type) {
		if (server) {
			//服务器回传消息编码用的
			super.addMessageEncoder(BaseMessage.class,StringProtocolEncoder.class);
			//服务器解码用
			super.addMessageDecoder(FileProtocolDecoder.class);
			super.addMessageDecoder(StringProtocolDecoder.class);
		} else {
			//解码就放解码器   编码就放编码器和文件返回类
			if(type == 1){
				super.addMessageEncoder(BaseMessage.class, FileProtocolEncoder.class);
				super.addMessageDecoder(FileProtocolDecoder.class);
			}else if(type == 2){
				super.addMessageEncoder(BaseMessage.class, StringProtocolEncoder.class);
				super.addMessageDecoder(StringProtocolDecoder.class);
			}
		}
	}
}