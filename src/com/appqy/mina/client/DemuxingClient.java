package com.appqy.mina.client;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.appqy.mina.util.DemuxingAppqyProtocolCodecFactory;

/**
 * @project_name coderServer
 * @description 用于测试混合解码器
 * @code name Higgs boson
 * @author fy
 * @copyright Appqy Team
 * @license http://www.appqy.com/
 * @email fy@appqy.com
 * @lastmodify 2013-10-30
 * @file_name MathClient.java
 */
public class DemuxingClient {

	/*
	public static void main(String[] args) throws Throwable {
		IoConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new DemuxingAppqyProtocolCodecFactory(
						false, 1)));
		connector.setHandler(new ClientHandler());
		connector.connect(new InetSocketAddress("192.168.1.44", 8889));
	}
	*/
}
