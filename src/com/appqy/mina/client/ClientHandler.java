package com.appqy.mina.client;

import java.io.File;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.appqy.mina.util.BaseMessage;
import com.appqy.mina.util.BeanUtil;
import com.appqy.mina.util.FileBean;
import com.appqy.mina.util.FileHelper;
/**
 * @project_name    coderServer
 * @description     Client端的IoHandler
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-30
 * @file_name       ClientHandler.java
 */
public class ClientHandler extends IoHandlerAdapter {

	@Override
	public void sessionOpened(IoSession session){
		BaseMessage baseMessage = new BaseMessage();
		baseMessage.setDataType(BeanUtil.UPLOAD_FILE);
		FileBean bean = new FileBean();
 
		File file = new File("D:\\照片\\2013年\\松风寨\\IMG_9214.jpg");
 
		bean.setFileName(file.getName());
		bean.setFileSize((int)file.length());
		try {
			FileHelper helper =new FileHelper();
			bean.setFileContent(helper.getContent(file));
		} catch (Exception e) {

			e.printStackTrace();
		}
		baseMessage.setData(bean);
		session.write(baseMessage); 
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
	}
	
	/**
     * 当有异常发生时触发
     * 当 I/O 处理器的实现或是 Apache MINA 中有异常抛出的时候，此方法被调用。
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("客户端,发生异常" + cause.getMessage());
        //session.close(true);
        //TODO 发生异常时保存客户端正在做的事情，以便客户端再次连接上来的时候返回给它
        //删除该客户端id序列值
        
    }
}