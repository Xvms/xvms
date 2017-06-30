/**
 * @project_name    coderServer
 * @description     编码器核心编码库 执行编码命令
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-9-6
 * @code name       coffee bean
 */

package com.appqy.encoder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


public class coderCore{
	
		private final static Logger log = Logger.getLogger(coderCore.class);
		//调用命令行执行命令  某些命令需要加上 'cmd /c'+命令使用   
		//TODO这里可能会有权限和杀毒软件拦截问题!!!
		public int runCmd(String command){
			int exitVal = 9999;//exitVal 返回0表示正常  TODO 可以加一些判断作为转码器返回状态
		 
			  try
			        {
				  	   Process proc = Runtime.getRuntime().exec(command);
					   log.info("encoder ing...");
					            //命令行返回信息开始
					            //TODO 暂时去除 貌似会因为cmd buff 溢出不能正常返回
					   			//TDDO ffmpeg子线程超时  可以使用将proc传入子线程 等待30分钟如果还没转码完成就kill该线程
					            //如果不读取getErrorStream流 进程会被挂起假死 因为I/O Stream堵塞的原因 参照http://blog.sina.com.cn/s/blog_500d9d15010083oo.html
					            InputStream stderr = proc.getErrorStream();//这样就是只返回错误流 proc.getInputStream();
					            InputStreamReader isr = new InputStreamReader(stderr);
					            BufferedReader br = new BufferedReader(isr);
					            String line = null;
					            log.info("<CMD info>");
					            while ( (line = br.readLine()) != null){
					            	//System.out.println(line); //这里可以获取到ffmpeg输出的信息
					            }
					            log.info("</CMD info>");
					            exitVal = proc.waitFor();
					            log.debug("Process exitValue: " + exitVal);
					            return exitVal;
			        } catch (Throwable t)
						  {
			        	   log.warn("Process exitValue: " + exitVal);
			        	   log.warn("StackTrace: " + t.getStackTrace().toString());
						   t.printStackTrace();
						   t.getStackTrace();
						  }
			  return exitVal;
		}
		
 
 
	
}