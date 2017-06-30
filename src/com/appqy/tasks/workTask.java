package com.appqy.tasks;

 
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import com.appqy.encoder.coderCore; 
import com.appqy.mina.server.DemuxingServerHandler;
import com.appqy.tools.CodeResolve;
import com.appqy.tools.FtpClient;
import com.appqy.tools.getConfig;
import com.appqy.tools.tools;
import com.appqy.web.SiteActions;
import com.appqy.web.querySql;

/**
 * @project_name    coderServer
 * @description     执行队列任务
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-2-28
 * @file_name       workTask.java
 */
public class workTask implements Runnable {// 任务接口
		private String[] cmd;//转码命令
		private String Encoder_path;//转码后存放的路径
		private String video;
		private getConfig config;
		private FtpClient FTPc = new FtpClient();//ftp分发文件 TODO以后得加入服务器状态检测后再做分发
		private SiteActions siteActions;
		private int taskid;
		private final static Logger log = Logger.getLogger(workTask.class);
		public workTask(String[] cmd,String Encoder_path,String video,getConfig config,SiteActions siteActions,int taskid){
			this.cmd = cmd;
			this.config = config;
			this.video = video;
			this.Encoder_path = Encoder_path;
			this.siteActions = siteActions;
			this.taskid = taskid;
		}
		@Override
		public void run() {
			String name = Thread.currentThread().getName();
		 
			try {
 
				//在任务列表中查询出当前任务的发布状态和新闻ID号和UID
				querySql querysql = new querySql();//实例化数据库类
				String[] taskData = new String[4];
				try {
					taskData = querysql.queryDatabase(taskid);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runRmsg(taskData[3],"-3",taskData[0]);
					return;//一旦失败,随即退出当前任务线程,防止任务错误代码被覆盖
				}
				
				//开始执行任务
				coderCore enCoder = new coderCore();
				//执行标清转码
				log.debug("文章内容ID为:"+taskData[0]+"开始PC转码!");
				int isOk = enCoder.runCmd(cmd[0]);
				//执行手机版转码
				log.debug("文章内容ID为:"+taskData[0]+"开始Phone转码!");
				int isOkm = enCoder.runCmd(cmd[1]);
				
				//FTP配置
				String ip = config.get_setting("video_ftp_ip");
				int port = Integer.parseInt(config.get_setting("video_ftp_port"));
				String uid = config.get_setting("video_ftp_id");
				String pwd = config.get_setting("video_ftp_pwd");
				//远程标准版目录配置 TODO 
				String rPath = (new java.text.SimpleDateFormat("yyyy/MM/dd/")).format(new Date());
				//手机版储存地址
				String rPathMini = rPath + "m/";
				//这里需要加配远程服务器LIST列表 包括域名等
				String Rurl = "rtmp://caches1v.scnj.tv/njtvlive|";
				boolean send = false;
				boolean send2 = false;
				
				
				
				
					if(isOk==0){
					log.info("PC encoder ok!");
					//FTP分发文件
						send = send(ip,port,uid,pwd,rPath,1);
					}else{
						log.warn("PC encoder err!");
						//querysql.UpdataTaskStatus(taskid, -1,3);//这个是转码失败重新任务修复尝试再次转码
						runRmsg(taskData[3],"-3",taskData[0]);
						return;//一旦失败,随即退出当前任务线程,防止任务错误代码被覆盖
					}
					
					if(isOkm==0){
						log.info("phone encoder ok!");
						send2 = send(ip,port,uid,pwd,rPathMini,2);	
					}else{
						log.warn("phone encoder err !");
						runRmsg(taskData[3],"-3",taskData[0]);
						//querysql.UpdataTaskStatus(taskid, -1,3);
						return;
					}
					
					//如果都分发成功了  那么进入发布更新流程
					if(send && send2){
						log.info("Sent successfully!");
						//更新到任务数据库 发布成功
						
						//将FTP分发后的地址存入视频库数据库
						String remoteFilePath = Rurl + rPath + video;//分发后的路径
						int autoVideoId = querysql.putVideoBase(remoteFilePath);
						//更新任务数据库的视频库视频ID
						querysql.UpdataTaskVal(taskid,autoVideoId+"","video_id");
						
						//判断是否需要让视频通过审核
						//将视频库的ID和文章状态POST到对应ID的文章去
						String httpCode = siteActions.postUpdataNews(taskData,autoVideoId);
						if(httpCode.equals("1")){
							//通过uid寻找session 然后回传信息
							runRmsg(taskData[3],"1",taskData[0]);
							querysql.UpdataTaskStatus(taskid, 1,0);//成功发布就更新为1 TODO 这里可以写日志
							log.info("content ID:"+taskData[0]+" is First ! updata WEB successfully!");
						}else{
							//失败后再次尝试发布
							httpCode = siteActions.postUpdataNews(taskData,autoVideoId);
							if(httpCode.equals("1")){
								//最终转码发布成功的地方2
								runRmsg(taskData[3],"1",taskData[0]);
								querysql.UpdataTaskStatus(taskid, 1,0);
								log.info("content ID:"+taskData[0]+" is Second! updata WEB successfully!");
							}else{
								log.warn("content ID:"+taskData[0]+" upData content err!");
								//如果更新失败的话,设置任务状态为-1 让循环执行任务来进行二次更新
								querysql.UpdataTaskStatus(taskid, -1,1);
								//TODO 失败之后提示手动更新  服务器仍然需要执行自动更新
								return;
							}
						}
					}else{
						log.info("send err 因为分发数据失败,未能成功更新发布数据");
						log.warn("Failed to send!!!可能因数:FTP使用模式,服务器IP设置,服务器路由器IP双线路由走向");
						querysql.UpdataTaskStatus(taskid, -1,2);
						return;
					}
	 
			} catch (InterruptedException e) {
			} catch (Exception e) {
				log.error(e.getStackTrace().toString());
			} catch (Throwable e) {
				log.error(e.getStackTrace().toString());
			}
			log.info(name + " executed OK");
		}
		
		public boolean send(String ip,int port,String uid,String pwd,String rPath,int taskType){
			//FTP分发文件 
			boolean send = false;
			//判断是什么版本的转换
			switch (taskType) {
			case 1:
				//PC版
				log.debug("send ftp pc ing...");
				send = FTPc.UpLoadFromDisk(ip,  port, uid, pwd, Encoder_path + video, rPath, video);
				break;
			case 2:
				//手机版分发
				log.debug("send ftp phone ing...");
				send = FTPc.UpLoadFromDisk(ip,port, uid, pwd, Encoder_path + "m_" +video, rPath, video);
				break;
			default:
				send = false;
				break;
			}
			
			return send;
		}
		
		/*
		 * 通过任务UID 寻找session 返回转码情况
		 * 找不到该session的时候说明他已下线,那么存入数据库
		 */
		private void runRmsg(String uid,String status,String contentId){
			
			if(DemuxingServerHandler.SessionMap.containsKey(uid)){
				IoSession session = DemuxingServerHandler.SessionMap.get(uid);//获取到该用户session
				tools tool = new tools();
				CodeResolve resolve = new CodeResolve();
				String rtCode[] = new String[2];
				rtCode[0] = status;
				rtCode[1] = contentId;
				//解析命令  并传回客户端
				tool.resultMsg(session,resolve.resolve(rtCode));
				log.debug("it's OK send client:"+uid);
			}else{
				//客户端已下线 存入数据库
				
			}
			
			/**
			 * PS:  数据库视频字段表
			 */
		}
 
	

}