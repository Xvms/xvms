package com.appqy.mina.server;


import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
 

import com.appqy.mina.util.BaseMessage;
import com.appqy.mina.util.FileBean;
import com.appqy.mina.util.StringBean;
import com.appqy.tasks.WorkQueue;
import com.appqy.tools.CodeResolve;
import com.appqy.tools.DataAES;
import com.appqy.tools.commandParsers;
import com.appqy.tools.getConfig;
import com.appqy.tools.tools;
import com.appqy.web.SiteActions;


/**
 * @project_name    coderServer
 * @description     服务端多路解码器助手类
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-30
 * @file_name       DemuxingServerHandler.java
 */
public class DemuxingServerHandler extends IoHandlerAdapter {
	//oncurrentHashMap是Java 5中支持高并发、高吞吐量的线程安全HashMap实现。内存使用是HashMap的16倍
	//用于管理session的
	public static ConcurrentHashMap<String ,IoSession> SessionMap = new ConcurrentHashMap<String ,IoSession>();
	//用于储存该session是否登陆web成功的值
	protected ConcurrentHashMap<Long ,String> maps = new ConcurrentHashMap<Long ,String>();
	//用于管理每次web登陆后的cookie使用
	protected HashMap<Long ,SiteActions> webCookie = new HashMap<Long ,SiteActions>();
 
	private final static Logger log = Logger.getLogger(DemuxingServerHandler.class);
	
	//获取线程池数量
	private getConfig config = new getConfig();
	private String encoder_pool_max_str = config.get_setting("encoder_pool_max");
	private int encoder_pool_max = Integer.parseInt(encoder_pool_max_str);
	//创建队列线程对象
	WorkQueue work = new WorkQueue(encoder_pool_max>0 ? encoder_pool_max : 2);//默认取2个线程同时工作  这里取决于服务器的同时转码能力
	//小工具类
	private tools tool = new tools();
	/**
     * 有新连接时触发
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    
    	log.debug("服务端, session open for " + session.getRemoteAddress());
        //TODO 在解码器处设置密码验证权限分配等信息  RSA 公私钥握手
        /*
        BaseMessage baseMessage = new BaseMessage();
		baseMessage.setDataType(BeanUtil.UPLOAD_STR);
		StringBean bean = new StringBean();
		String m = "<Item name=\"Download Speedlimit\" type=\"numeric\">10</Item><Item name=\"Upload Speedlimit\" type=\"numeric\">10</Item><Item name=\"Buffer Size\" type=\"numeric\">4096</Item><Item name=\"Admin port\" type=\"numeric\">14147</Item><Item name=\"Serverports\" type=\"string\">21</Item><Item name=\"Custom PASV IP type\" type=\"numeric\">0</Item><Item name=\"Custom PASV IP server\" type=\"string\"></Item><Item name=\"Use custom PASV ports\" type=\"numeric\">0</Item><Item name=\"Mode Z Use\" type=\"numeric\">0</Item><Item name=\"Mode Z min level\" type=\"numeric\">1</Item><Item name=\"Mode Z max level\" type=\"numeric\">9</Item><Item name=\"Mode Z allow local\" type=\"numeric\">0</Item><Item name=\"Mode Z disallowed IPs\" type=\"string\"/><Item name=\"IP Bindings\" type=\"string\">*</Item><Item name=\"IP Filter Allowed\" type=\"string\"/><Item name=\"IP Filter Disallowed\" type=\"string\"/><Item name=\"Hide Welcome Message\" type=\"numeric\">0</Item><Item name=\"Enable SSL\" type=\"numeric\">0</Item><Item name=\"Allow explicit SSL\" type=\"numeric\">1</Item><Item name=\"SSL Key file\" type=\"string\"/><Item name=\"SSL Certificate file\" type=\"string\"/><Item name=\"Implicit SSL ports\" type=\"string\">990</Item><Item name=\"Force explicit SSL\" type=\"numeric\">0</Item><Item name=\"Network Buffer Size\" type=\"numeric\">65536</Item><SpeedLimits><Download/><Upload/></SpeedLimits></Settings><Groups/><Users><User Name=\"anonymous\"><Option Name=\"Pass\"/><Option Name=\"Group\"/><Option Name=\"Bypass server userlimit\">0</Option><Option Name=\"User Limit\">0</Option><Option Name=\"IP Limit\">0</Option><Option Name=\"Enabled\">1</Option><Option Name=\"Comments\"/><IpFilter><Disallowed/><Allowed/></IpFilter><Permissions><Permission Dir=\"C:\\Users\\fy\\Desktop\\xampp-cmsware\\anonymous\\\"><Option Name=\"FileRead\">1</Option><Option Name=\"FileWrite\">0</Option><Option Name=\"FileDelete\">0</Option><Option Name=\"FileAppend\">0</Option>js";
		System.out.println("INT的长度是:"+m.getBytes().length);
		
		bean.setFileName(m);
		baseMessage.setData(bean);
		session.write(baseMessage); 
		*/
		//初始化Session密码验证
		maps.put(session.getId(),"");
	 
    }
    
    //空闲事件 可用于心跳
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		//session.close(true);
			this.resultMsg(session,"#997#1");
 
		//log.info("当前连接{"+session.getRemoteAddress()+"}处于空闲状态：{"+status+"}");
	}

	/**
     * 收到来自客户端的消息
     * TODO 当该连接的一个环节服务端未接收完,那么将导致该连接后面的全部错误,这里需要修正,考虑该连接超时后清空正在发送的数据
     */
	@Override
	public void messageReceived(IoSession session, Object message)throws Exception {
		//super.messageReceived(session, message);
 
		//收到消息
		long SessionId = session.getId();
		//登陆指令 0100000002000000137365727665727C7570777C626B7973626B7973  
		//命令测试 000000020000000A636D64202F6320646972
		
		BaseMessage baseMessage = (BaseMessage) message;

		if(maps.get(SessionId)!=null){
		//验证该session创建后的第一条登录指令
			if(maps.get(SessionId).equals("")){
				//未经过密码验证 则对传入第一条消息进行密码验证 
				log.debug("开始验证数据类型,for:"+session.getRemoteAddress());
				if(baseMessage.getDataType()!=2){
					//如果数据不为字符串类型 密码验证 直接关闭连接
					session.close(true);
					log.debug("登陆验证失败,关闭socket连接,for:"+session.getRemoteAddress());
				}else{
					//进行账号密码验证 验证之后获取权限等数据
					//对客户端发送过来的账号密码进行验证,成功后返回成功标识,以及账号权限以及栏目数据给客户端
					SiteActions getWeb = new SiteActions();
					
					StringBean bean = (StringBean) baseMessage.getData();
					//简单检测发来的账号是否为空(解码器收到异常数据或者未发送完的数据会出现该值为空)
					if(bean.getFileName()==null){
						log.warn("###账号数据为空,可能是解码器接收到异常数据###,for:"+session.getRemoteAddress());
						this.resultMsg(session,"#001#1");//提示重新登陆
						return;
					}
					//TODO 将登陆数据进行AES加密处理
					//DataAES Ase = new DataAES();
					String aseUserPaw = bean.getFileName();
					//System.out.println(Ase.encrypt("server|upw|bkysbkys"));
					//aseUserPaw = Ase.decrypt(aseUserPaw);
					//String[] userpaw = aseUserPaw.split("\\|upw\\|",2);
					String[] userpaw = aseUserPaw.split("\\|upw\\|",2);
					//System.out.println("账号是:"+userpaw[0]);
					//System.out.println("密码是:"+userpaw[1]);
					//简易判断账号数据是否正确 以防止不是登陆执行进入导致解码器异常
					if(aseUserPaw.equals("###997###1"))
						return;//未登陆状态下的心跳包
					else if(userpaw.length!=2){
						log.warn("###非法连接参数###,for:"+session.getRemoteAddress());
						this.resultMsg(session,"#001#1");//提示重新登陆
						return;
					}
					//执行登陆
					log.debug("执行登陆,for:"+session.getRemoteAddress());
					String rmsg = getWeb.loginWeb(userpaw[0],userpaw[1]);
					int msg;
	                String uid ="";
					//分解数据   状态|UID
	                if(rmsg.length()>2){
	                	String[] msgs = rmsg.split("<#>");
	                	msg = Integer.parseInt(msgs[0]);
	                	uid = msgs[1];//登陆成功返回的用户的UID值
	                }else{
	                	msg = Integer.parseInt(rmsg);
	                }
	                
					if(msg==1 && !uid.equals("")){
						//保存cookie到全局map
						webCookie.put(SessionId, getWeb);
						//登陆成功
						log.debug("成功登陆,for:"+session.getRemoteAddress());
						this.resultMsg(session,"#001#appqyLoginOk!,"+uid);
						maps.put(SessionId,uid);
						if(!SessionMap.containsKey(uid)){
							SessionMap.put(uid, session);
						}else{
							//后登陆的账号把前面一个登陆的T掉
							SessionMap.get(uid).close(true);
							SessionMap.put(uid, session);
						}
						//返回该账号栏目数据
						this.resultMsg(session,"#002#"+getWeb.getWebCategory());
						//返回网站推荐位数据
						this.resultMsg(session,"#003#"+getWeb.getWebPosition());
						return;
					}else if(msg==0){
						//密码错误 返回指令让他重新登陆
						this.resultMsg(session,"#001#1");
						//且设置maps里面的值为false 这样就不需要再创建新的连接
						maps.put(SessionId,"");
					}else if(msg==3){
						this.resultMsg(session,"#001#3");
						maps.put(SessionId,"");
					}else{
						//服务端与网站接口通信异常,可能是不能打开该网站或者超时
						this.resultMsg(session,"#004#1");
						maps.put(SessionId,"");
					}
					 
					
					//密码验证成功后加入 此值
					
					//再判断密码是否正确  错误即断开连接  如果连接断开后 也需要重置这个布尔值
				}
				
				
			}
		}
 
	    //字符串接收处理消息的地方
		CodeResolve codeResolve = new CodeResolve();
        //如果已经登陆 那么进入下一个流程
		if(!maps.get(SessionId).equals("")){
	        switch (baseMessage.getDataType()) {
			case 1:
				//收到文件的动作
				FileBean bean1 = (FileBean) baseMessage.getData();
				System.out.println(bean1.getFileName());
				FileOutputStream os = new FileOutputStream("e:\\"+bean1.getFileName());
				os.write(bean1.getFileContent());
				os.close();
				//存入文件之后返回地址路径 文件类型 等信息 然后给予转码操作
				break;
			case 2:
				//收到字符串消息的动作
				StringBean bean = (StringBean) baseMessage.getData();
				//执行命令 TODO CMD命令应该内置到系统里面,不然会有安全漏洞
				commandParsers cmdstr = new commandParsers(work);
				//传入有登陆状态的cookie和指令字符串  返回状态码
				
				String cmd = bean.getFileName();
				//先发布信息   阻塞方式执行  这个流程自动进行 由###001### 到路由里面区分
				
				//再进入转码流程  线程方式执行
				//TODO验证是否是指令字符串    正则验证 ###001###XXXXXX<#>XXXX<#>  x是字符串
				//过滤掉二次登陆
				String isSafe = cmd.substring(0, 9);
				String regex = "###\\d{3}###";
				//如果不符合规则 直接跳出去
				if(!isSafe.matches(regex))
					break;
				String[] rtCode = cmdstr.router(cmd,webCookie.get(SessionId));
				
				//判断状态码 给予客户端回复
				String msg = codeResolve.resolve(rtCode);
				if(msg.length()!=0)
					this.resultMsg(session,msg);
				break;
			default:
				log.error("接收到错误的消息类型!");
				break;
			}
		}
 
 
	}
	
	/**
     * 连接关闭时触发
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	log.debug("服务端, session closed from " + session.getRemoteAddress());
        //TODO 关闭时触发删除客户端的ID序列值
    	long id = session.getId();
    	if(maps.containsKey(id)){
	    	String uid = maps.get(id);
	    	if(SessionMap.containsKey(uid)){
	    		SessionMap.remove(uid);
	    	}
	    	maps.remove(id);
    	}

        if(webCookie.containsKey(session)){
        	webCookie.remove(session);
        }
        session.close(true);
    }
	
	/**
     * 当有异常发生时触发
     * 当 I/O 处理器的实现或是 Apache MINA 服务端自身中有异常抛出的时候，此方法被调用。
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
    	log.debug("服务端,发生异常:" + cause.getMessage() +" for:"+ session.getRemoteAddress());
        //TODO 如果客户端批量的 过快的断开登陆 那么会导致这里map清理不完 
    	long id = session.getId();
    	if(maps.containsKey(id)){
	    	String uid = maps.get(id);
	    	if(SessionMap.containsKey(uid)){
	    		SessionMap.remove(uid);
	    	}
	    	maps.remove(id);
    	}
        if(webCookie.containsKey(session)){
        	webCookie.remove(session);
        }
        this.resultMsg(session,"#998#1");//服务端发生异常,主动断开连接
        //TODO下面这个关闭很有可能导致中断连接
        //session.close(true);
        //TODO 发生异常时保存客户端正在做的事情，以便客户端再次连接上来的时候返回给它
        //删除该客户端id序列值
        
       
        
    }
    
    /**
     * 服务器回复消息用
     * 登陆成功                              #001#appqyLoginOk!  
     * 密码错误重新登陆          #001#1
     * 返回栏目数据                    #002#+str
     * 返回网站推荐位数据     #003#
     * 通信异常                              #004#1
     * 服务端发生异常,主动断开连接  #998#1
     * @throws InterruptedException 
     */
    public void resultMsg(IoSession session,String msg) throws InterruptedException{
    	Thread.sleep(400);
    	this.tool.resultMsg(session, msg);
    }
}