package com.appqy.tools;

import org.apache.log4j.Logger;
import com.appqy.tasks.WorkQueue;
import com.appqy.tasks.workTask;
import com.appqy.web.DataFormat;
import com.appqy.web.SiteActions;
import com.appqy.web.querySql;

/**
 * @project_name    coderServer
 * @description     解析客户端发送过来的命令
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-2-24
 * @file_name       commandParsers.java
 */
public class commandParsers {
	//用于管理每次web登陆后的cookie使用
	private querySql querysql = new querySql();
	private WorkQueue work;
	private getConfig config = new getConfig();
	private final static Logger log = Logger.getLogger(commandParsers.class);
	public commandParsers(WorkQueue work){
		this.work = work;
	}
	/**    
	 * 命令解析路由器  这里将为登录成功后的所有数据接入指令判断  判断后交付给执行类
	 * @param cmd 传入指令参数
	 * @param siteActions //带着登陆状态的cookie
	 * @return  code 
	 * @throws Exception
	 */
	  
	public String[] router(String cmd, SiteActions siteActions) throws Exception{
		//执行命令id号和指令集
		String id = cmd.substring(3,6);
		String str = cmd.substring(9);
		//TODO 这里需要检验命令合法性

		String[] code = new String[2];//0为成功发布  1为转码发布成功  -1为失败
		DataFormat arrayData = new DataFormat(stringToArray(str));//返回封装成结构体的数据
		String content_id = "-1";
		String statusCode = "";
		int rTaskId = 0;

		switch(id){
		case "001":
			/* 
			 * 转码发布流程
			 * 至于什么时候发布,由定时器查询数据库决定
			 * 接收到客户端上载完毕通知后进入转码流程,完成转码分发后通知WEB,根据该发布账号权限自动决定是否审核发布
			 */
			//根据任务ID 取出任务数据库内的值
			rTaskId = Integer.parseInt(arrayData.getTaskId());
			String[] tasks = querysql.queryTask(rTaskId);
			//重构数据
			String video = tasks[0];
			String Encoder_path = config.get_setting("Encoder_path");
			
			//判断这个视频是否能够解码 
			int isTrue = config.checkContentType(video);
					if(isTrue==0){
						//能进行解码 将解码参数组合成解码命令
						String[] cmdstr = config.video_properties(config.get_setting("coder_ftp_path")+video,Encoder_path,video);
						workTask wk = new workTask(cmdstr,Encoder_path,video,config,siteActions,rTaskId);
						work.execute(wk);//这里的线程不会阻塞,即使连接断开也会继续执行.但是不知道主服务终止会终止不.
						code[0] = "999";
					}else{
						code[0] = "-3";//无法转码
					}
			break;
		case "002":
			/*
			 * 将上传过来的任务数据发布并存入任务数据库
			 * 等待视频上载完毕之后 取出数据进入发布更新
			 */
			log.debug("fist post news _title:"+arrayData.getTitle());
			
			arrayData.setStatus("0");
			
			content_id = siteActions.postNews(arrayData);//以审核状态发布视频到网站
			if(content_id.equals("")){
				//重新尝试发布
				log.debug("Second post news _title:"+arrayData.getTitle());
				content_id = siteActions.postNews(arrayData);
			}
			if(content_id.equals("")){
				log.error("Post news err,title:"+arrayData.getTitle());
				code[0] = "-1";
 
			}else{
				arrayData.setContent_id(content_id);
				arrayData.setContent_status("0");
				code[0] = "0";//成功
				//存入任务数据库     下面这个发布环节会阻塞线程
				code[1] = querysql.putDatabase(arrayData)+"<#>"+content_id+"<#>"+arrayData.getPath();//插入任务队列数据库 并返回任务编号ID TODO 非常可能出现网站无法访问的情况
				//content_id rTaskId 返回回去
			}
			break;
		case "003":
			//更改文章信息指令
			statusCode = siteActions.postUpdataNewsInfo(arrayData);
			if(statusCode.equals("1")){
				log.info("content ID:"+arrayData.getContent_id()+" upData ok,code:"+statusCode);
				code[0] = "2";//更新成功
				code[1] = arrayData.getContent_id();
			}else{
				code[0] = "997";
			}
			break;
		case "004":
			//转码状态查询接口 客户端上传需要查询的ID号数组字符串 然后返回已转码成功的字符串id
			//嗯 这里借用的是taskid的值来用
			code[0] = "4";
			code[1] = querysql.queryEncodeList(arrayData.getTaskId());
			break;
		case "005":
			//删除文章信息指令
			statusCode = siteActions.postDelNews(arrayData.getCatid(),arrayData.getContent_id());
			log.info("content ID:"+arrayData.getContent_id()+" dell code"+statusCode);
			if(statusCode.equals("1")){
				code[0] = "5";//删除成功
				code[1] = arrayData.getContent_id();
			}else{
				code[0] = "6";//删除失败
			}
			
			break;
		
		case "997":
			//心跳包
			code[0] = "997";
			break;
		}
		return code;
	}
	
	

	//将字符串转换为数组  TODO 有分解命令的地方
	public String[] stringToArray(String str){
		String[] array =  str.split("<#>");
		return array;
	}
 
 
}
