package com.appqy.mina.server;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @project_name    coderServer
 * @description     ServerHandler 负责业务逻辑处理
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-22
 * @code name       coffee bean
 */

public class ServerHandler extends IoHandlerAdapter{
	private final static Logger log = Logger.getLogger(ServerHandler.class);
    protected HashMap<Long ,String> map = new HashMap<Long ,String>();;
    /**
     * 有新连接时触发  由I/O processor thread调用；
     * 当有新的连接打开的时候，该方法被调用。该方法在 sessionCreated之后被调用。
     * 这个方法在连接被打开时调用,它总是在 sessionCreated()方法之后被调用。对于 TCP 来
     * 说,它是在连接被建立之后调用,你可以在这里执行一些认证操作、发送数据等。 对于 UDP 来说,这个方法与
     * sessionCreated()没什么区别,但是紧跟其后执行。如果你每 隔一段时间,发送一些数据,那么
     * sessionCreated()方法只会在第一次调用,但是 sessionOpened()方法每次都会调用。
     * TODO 其实这里应该新开线程来进行查询操作,否则会造成堵塞,至于怎么新开线程还不知道啊
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	//调试信息
    	log.debug("会话创建 -> 服务端, session open for " + session.getRemoteAddress() + "客户标识" +session.getId());
        map.put(session.getId(), session.getRemoteAddress().toString());
    }
    
    /**
     * 连接关闭时触发
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	log.debug("会话关闭 -> 服务端, session closed from " + session.getRemoteAddress());
        //TODO 关闭时触发删除客户端的ID序列值
        map.remove(session.getId());
    }
    
    /**
     * 收到来自客户端的消息
     */
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
 
        String clientIP = session.getRemoteAddress().toString();
        log.debug("服务端接收到来自IP:"+clientIP+"的消息:"+message);
        //TODO 收到消息后调用对应解码器处理对应事件
        
        //TODO 用户验证 收到消息的时候判断是否是非法连接
        //###Key###MessageLength###Content###
        
        //TODO 指令验证
        
        //TODO 执行指令
        
        session.write("ok");
        //测试map内部值
        if(message.toString().equals("999")){
        	for (Entry<Long, String> entry : map.entrySet()) {  
                Long key = entry.getKey();  
                String value = entry.getValue().toString();  
                System.out.println("key =" + key + " value = " + value);  
            }
        }
    }
    
    /**
     * 当有异常发生时触发
     * 当 I/O 处理器的实现或是 Apache MINA 中有异常抛出的时候，此方法被调用。
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("服务端,发生异常" + cause.getMessage());
        map.remove(session.getId());
        session.close(true);
        //TODO 发生异常时保存客户端正在做的事情，以便客户端再次连接上来的时候返回给它
        //删除该客户端id序列值
        
    }
    
    /**
     * 当会话创建时被触发
     * 对于 TCP 连接来说,连接被接受的时候 调用,但要注意此时 TCP
     * 连接并未建立,此方法仅代表字面含义,也就是连接的对象 IoSession 被创建完毕的时候,回调这个方法。 对于 UDP
     * 来说,当有数据包收到的时候回调这个方法,因为 UDP 是无连接的。
     * 
     * TODO:这个方法是在I/O处理阶段连接建立完成时调用的,这个线程同时要处理很多其他的会话进程,所以这个方法的实现,调用成本会很高,
     * 尽量不要在这里做代码实现  sessionOpened则是在另一个线程调用的,不会影响主I/O线程的效率.如果没有绝对的需要,最好用sessionOpened
     * 来代替sessionCreated做一些处理代码.
     */
    /*
    @Override
    public void sessionCreated(IoSession session){
    	//TODO 保存客户端ID序列值，检测当前客户端是否有未完成处理事务
        
    }
    */
    /*
     * 当消息被成功发送出去的时候，此方法被调用。
     */
    @Override
    public void messageSent(IoSession session, Object message){
    	
    }
    
    /*
     * 当连接空闲时触发。
     * 对于 UDP 协议来说,这个方法始终不会 被调用。
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    	//实际上这里可以实现一个服务器心跳包的功能 有木有?  
        //log.info("当前连接{}处于空闲状态：{}", session.getRemoteAddress(), status);
    	//线程如果超过1个小时没反应,也应该做关闭处理 如下
    	//session.close(true);
    }
}