package com.appqy.mina.server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import com.appqy.mina.util.DemuxingAppqyProtocolCodecFactory;
import com.appqy.tools.Language;
import com.appqy.tools.getConfig;
/**
 * @project_name    coderServer
 * @description     Mina框架主入口 TCP多线程管理器核心入口
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-9-6
 * @code name       coffee bean
 * TODO 考虑长连接和短连接  和 控制单用户连接数  加入访问验证机制   后面加入多线程传输
 */
public class MinaServer {
 
	private int PORT;
	private final static Logger log = Logger.getLogger(MinaServer.class);
	public MinaServer(){
			//自动加载配置文件服务器监听的端口号
			getConfig v_setting = new getConfig();
			this.PORT = Integer.parseInt(v_setting.get_setting("ServerPort"));
	}
	
    public void scoketMain(){
        //构造接收器  创建非阻塞的server端的Socket连接
    	
        IoAcceptor acceptor = new NioSocketAcceptor();
        //创建业务处理类
        //ServerHandler handler = new ServerHandler();
        DemuxingServerHandler handler = new DemuxingServerHandler();
        //设置业务处理类
        acceptor.setHandler(handler);
        SocketSessionConfig cfg = (SocketSessionConfig) acceptor.getSessionConfig(); 
        //设置读取数据的缓冲区大小 
        cfg.setReadBufferSize(1024);
        //MINA在调用了close()方法后，不会再进入TIME_WAIT状态了，而直接Close掉了
        cfg.setSoLinger(0);
        cfg.getBothIdleTimeInMillis();
        //读写通道30秒内无操作进入空闲状态   
        cfg.setIdleTime(IdleStatus.BOTH_IDLE, 220);
        //将NioProcessor的IO操作线程和TimeServerHandler的业务处理线程分开
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
        //过滤器 自定义混合解码器 采用DemuxingProtocolCodecFactory混合解码器协议
        acceptor.getFilterChain().addLast("codec",new ProtocolCodecFilter(new DemuxingAppqyProtocolCodecFactory(true,0)));
        //acceptor.getFilterChain().addLast("StringCodec", new ProtocolCodecFilter(new StringProtocalCodecFactory(Charset.forName("UTF-8"))));  
        //添加编码过滤器  这个过滤器的作用是将来自客户端输入的信息转换成一行行的文本后传递给 IoHandler ; ps:将指定名称的过滤器添加到过滤器链的末尾。
        //acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        //添加时间过滤器  默认间隔1秒同IP发送的连接请求将被拒绝(包括同IP下的其他端口,网上说不包含,测试之后是包含)
        //acceptor.getFilterChain().addFirst("tcpTime", new ConnectionThrottleFilter(5000));
         
        //添加日志组件
        acceptor.getFilterChain().addLast("logging", new LoggingFilter());
        //为NioSocketAcceptor增加一个线程的处理池 java运行分配少量内存的情况下  加了线程池容易内存溢出
        //acceptor.getFilterChain().addFirst("ThreadPool",new ExecutorFilter(Executors.newCachedThreadPool()));
        // 启动服务
        try {
			acceptor.bind(new InetSocketAddress(this.PORT));
			Date now = new Date();
			log.info(Language.eServerStar + this.PORT + " "+DateFormat.getDateTimeInstance().format(now));
		} catch (IOException e) {
		
			e.printStackTrace();
			log.error(Language.ePortUse);
			log.error(Language.eServerExit);
			log.error("StackTrace:" + e.getStackTrace().toString());
			//结束程序
			System.exit(-1);
		}
        
    }
}