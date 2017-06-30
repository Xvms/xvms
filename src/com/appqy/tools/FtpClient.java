package com.appqy.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @project_name    coderServer
 * @description     FTP传输功能
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-3-5
 * @file_name       FtpClient.java
 */
public class FtpClient {

	/**
	 * Description: 向FTP服务器上传文件
	 * @param url FTP服务器hostname
	 * @param port FTP服务器端口
	 * @param username FTP登录账号
	 * @param password FTP登录密码
	 * @param path FTP服务器保存目录
	 * @param filename 上传到FTP服务器上的文件名
	 * @param input 输入流
	 * @return 成功返回true，否则返回false
	 */
	public static boolean uploadFile(String url,int port,String username, String password, String path, String filename, InputStream input) {
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect(url, port);//连接FTP服务器
			//如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);//登录
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			//设置FTP以2进制传输
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			//TODO 读取文件配置判断是否使用主动
			ftp.enterLocalPassiveMode();//被动
			//ftp.enterLocalActiveMode();//主动
			//创建目录
			mkDir(path,ftp);
			//改变目录
			ftp.changeWorkingDirectory(path);
			ftp.storeFile(filename, input);			
			input.close();
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
				}
			}
		}
		return success;
	}
	
 
	public boolean UpLoadFromDisk(String Ip,int Port,String Uid,String pwd, String localFile,String remoteDir,String FileName){
		boolean flag = false;
		try {
			FileInputStream in=new FileInputStream(new File(localFile));
			flag = uploadFile(Ip, Port, Uid, pwd, remoteDir, FileName, in);
			return flag;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return flag;
		}
	}

	/*
	public static void main(String[] args) {
		FtpClient FTPc = new FtpClient();
		FTPc.UpLoadFromDisk("www.appqy.com",  5047, "videoserver", "videoserverm,./", "C:\\Users\\fy\\Desktop\\20140304_132837.jpg", "m/d/s/3/", "DD.JPG");
	}
	*/
	
	/**
	 * 循环创建目录，并且创建完目录后，设置工作目录为当前创建的目录下
	 */
	 public static boolean mkDir(String ftpPath,FTPClient ftp2) {
	  if (!ftp2.isConnected()) {
	   return false;
	  }
	  try {
	   // 将路径中的斜杠统一
	   char[] chars = ftpPath.toCharArray();
	   StringBuffer sbStr = new StringBuffer(256);
	   for (int i = 0; i < chars.length; i++) {
	    if ('\\' == chars[i]) {
	    	sbStr.append('/');
	    }else{
	    	sbStr.append(chars[i]);
	    }
	   }
	   ftpPath = sbStr.toString();
	   //System.out.println("ftpPath" + ftpPath);
	   if (ftpPath.indexOf('/') == -1) {
	    // 只有一层目录
		   ftp2.makeDirectory(new String(ftpPath.getBytes(),"iso-8859-1"));
		   ftp2.changeWorkingDirectory(new String(ftpPath.getBytes(),"iso-8859-1"));
	   } else {
	    // 多层目录循环创建
	    String[] paths = ftpPath.split("/");
	    // String pathTemp = "";
	    for (int i = 0; i < paths.length; i++) {
	    	ftp2.makeDirectory(new String(paths[i].getBytes(),"iso-8859-1"));
	    	ftp2.changeWorkingDirectory(new String(paths[i].getBytes(), "iso-8859-1"));
	    }
	   }
	   return true;
	  } catch (Exception e) {
	   e.printStackTrace();
	   return false;
	  }
	 }

}
