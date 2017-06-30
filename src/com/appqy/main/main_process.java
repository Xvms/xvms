/**
 * @project_name    coderServer
 * @description     编码服务端主线程
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-9-6
 * @code name       coffee bean
 */
package com.appqy.main;

 
import java.text.ParseException;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.SchedulerException;
import com.appqy.mina.server.MinaServer;
import com.appqy.tasks.serverTasks;


public class main_process{
	public static void main(String[] args) throws SchedulerException, ParseException{

				//读取日志系统配置
				PropertyConfigurator.configure("log4j.properties");
//				FtpClient FTPc = new FtpClient();
//				boolean send = FTPc.UpLoadFromDisk("www.scnj.tv", 9874, "videoserver", "videoservernpcxxx", "C:\\Users\\fy\\Desktop\\2014-04-28-17-26-03-499.mp4", "/02/", "test.mp4");
//				if(send)
//					System.out.println("ftp ok");
				//启动程序socket主服务
				MinaServer Mina = new MinaServer();
				Mina.scoketMain();
				//启动定时任务  定时进行任务扫描
				new serverTasks();
		 
				/*
				DataAES Ase = new DataAES();
				String a = Ase.encrypt("abc");
				System.out.println("加密后:"+a);
				String b = Ase.decrypt(a);
				System.out.println("解密后:"+b);
 				*/
// 反射
//				Class<?> c = Class.forName("com.appqy.mina.server.DemuxingServerHandler");
//				Field  f = c.getField("SessionMap");
//				Object obj = c.newInstance();
//				f.setAccessible(true);
//				ConcurrentHashMap<Long ,IoSession> map = (ConcurrentHashMap<Long, IoSession>) f.get(obj); //取到成员变量值
//				log.info("Session id 大小:"+map.size());
	}
}