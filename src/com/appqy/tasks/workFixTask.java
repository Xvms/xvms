package com.appqy.tasks;


import java.util.Date;
import org.apache.log4j.Logger;
import com.appqy.encoder.coderCore;
import com.appqy.tools.FtpClient;
import com.appqy.tools.MysqlBean;
import com.appqy.tools.getConfig;
import com.appqy.web.SiteActions;
import com.appqy.web.querySql;

/**
 * @project_name    coderServer
 * @description     重新运行遇到异常未成功的任务一次
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-2-28
 * @file_name       workTask.java
 */
public class workFixTask implements Runnable {// 任务接口
		private String[] cmd;//转码命令
		private String Encoder_path;//转码后存放的路径
		private String video;
		private getConfig config;
		private FtpClient FTPc = new FtpClient();//ftp分发文件 TODO以后得加入服务器状态检测后再做分发
		private MysqlBean Mbean;
		private int taskid,isOk = 1,isOkm = 1;;
		//FTP配置
		private String ip;
		private int port;
		private String uid;
		private String pwd;
		private String rPath;
		private String rPathMini;
		private String RserverUrl;
		//数据库配置
		private querySql querysql = new querySql();
		private final static Logger log = Logger.getLogger(workFixTask.class);
		public workFixTask(MysqlBean Mbean){
			//初始化各种变量
			this.config = new getConfig();
			this.Mbean = Mbean;//视频配置
			this.taskid = Mbean.getId();
			//FTP配置
			this.ip = config.get_setting("video_ftp_ip");
			this.port = Integer.parseInt(config.get_setting("video_ftp_port"));
			this.uid = config.get_setting("video_ftp_id");
			this.pwd = config.get_setting("video_ftp_pwd");
			//视频配置
			this.Encoder_path = config.get_setting("Encoder_path");//转码存放路径
			this.video = Mbean.getVideo();//待重新发布视频名称
			//远程标准版目录配置 TODO 
			this.rPath = (new java.text.SimpleDateFormat("yyyy/MM/dd/")).format(new Date());
			//手机版储存地址
			this.rPathMini = rPath + "m/";
			//这里需要加配远程视频服务器LIST列表 包括域名等
			this.RserverUrl = "rtmp://caches1v.scnj.tv/njtvlive|";
			//视频转码命令
			this.cmd = config.video_properties(config.get_setting("coder_ftp_path")+video,Encoder_path,video);
		}
		@Override
		public void run() {
			int error_code = Mbean.getError_code();
			boolean send1 = false,send = false; 
			//分析任务
			switch (error_code) {
			case 1:
				//更新文章ID
				try {
					updateContent();
				} catch (Exception e) {
					log.error("定时任务->(更新任务)失败,任务id:"+Mbean.getId());
				}
				break;
			case 2:
				//分发视频
				
				send = sendVideo(ip,port,uid,pwd,rPath,1);
				send1 = sendVideo(ip,port,uid,pwd,rPathMini,2);
				//更新库中状态
				if(send && send1){
					//将FTP分发后的地址存入视频库数据库
					String remoteFilePath = RserverUrl + rPath + video;//分发后的路径
					int autoVideoId = querysql.putVideoBase(remoteFilePath);
					Mbean.setVideo_id(autoVideoId);
					//更新文章ID
					try {
						updateContent();
					} catch (Exception e) {
						log.error("定时任务->(分发视频)失败,任务id:"+Mbean.getId());
					}	
				}		
				break;
			case 3:
				
				
				//对视频转码
		
				encoder();

				if(isOk==0 && isOkm==0){
					log.info("定时任务转码成功!");
					//分发视频
					send = sendVideo(ip,port,uid,pwd,rPath,1);
					send1 = sendVideo(ip,port,uid,pwd,rPathMini,2);
				}else{
						log.error("PC版转码失败!");
						querysql.UpdataTaskStatus(taskid, -2,3);
						return;//一旦失败,随即退出当前任务线程,防止任务错误代码被覆盖
				}
 
				//更新库中状态
				if(send && send1){
					//将FTP分发后的地址存入视频库数据库
					String remoteFilePath = RserverUrl + rPath + video;//分发后的路径
					int autoVideoId = querysql.putVideoBase(remoteFilePath);
					Mbean.setVideo_id(autoVideoId);
					//更新文章ID
					try {
						updateContent();
					} catch (Exception e) {
						log.error("定时任务->(视频转码 for 分发视频)失败,任务id:"+Mbean.getId());
						log.error("异常信息:"+e.getMessage()+";"+e.getStackTrace().toString());
						e.printStackTrace();
					}
				}	
				break;
			}
		}
		

		
		//对视频转码
		private void encoder(){
			//开始执行任务
			coderCore enCoder = new coderCore();
			//执行标清转码
			isOk = enCoder.runCmd(cmd[0]);
			//执行手机版转码
			isOkm = enCoder.runCmd(cmd[1]);
		}
		
		//重新分发视频
		private boolean sendVideo(String ip,int port,String uid,String pwd,String rPath,int taskType){
			//FTP分发文件 
			boolean send = false;
			//判断是什么版本的转换
			switch (taskType) {
			case 1:
				//PC版
				log.debug("进入PC版分发流程");
				send = FTPc.UpLoadFromDisk(ip,  port, uid, pwd, Encoder_path + video, rPath, video);
				break;
			case 2:
				//手机版分发
				log.debug("进入手机版分发流程");
				send = FTPc.UpLoadFromDisk(ip,port, uid, pwd, Encoder_path + "m_" +video, rPath, video);
				break;
			default:
				send = false;
				break;
			}
			
			return send;
		}
		
		//更新文章状态和视频ID
		private void updateContent() throws Exception{
			
			//仍然失败的话 设置错误状态为-2 不再进入查询
			SiteActions siteAction = new SiteActions();
			//在任务列表中查询出当前任务的发布状态和新闻ID号
			String[] taskData = new String[3];
			taskData[0] = Mbean.getContent_id()+"";
			taskData[2] = Mbean.getCatid()+"";
			int autoVideoId = Mbean.getVideo_id();
			//将视频库的ID和文章状态POST到对应ID的文章去
			String httpCode = siteAction.postUpdataNews(taskData,autoVideoId);
			if(httpCode.equals("1")){
				querysql.UpdataTaskStatus(taskid, 1,0);//成功发布就更新为1 TODO 这里可以写日志
				log.info("On fix! is one ! updata WEB successfully!");
			}else{
				//失败后再次尝试发布
				httpCode = siteAction.postUpdataNews(taskData,autoVideoId);//这里需要特殊方式更新 否则没有session
				if(httpCode.equals("1")){
					querysql.UpdataTaskStatus(taskid, 1,0);
					log.info("On fix! is two! updata WEB successfully!");
				}else{
					log.warn("更新失败!");
					//如果更新失败的话,设置任务状态为-2 抛弃任务
					querysql.UpdataTaskStatus(taskid, -2,1);
					return;
				}
			}
		}
}