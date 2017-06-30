package com.appqy.web;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.appqy.tools.MysqlBean;
import com.appqy.tools.MysqlTool;
import com.appqy.tools.getConfig;

/**
 * @project_name    coderServer
 * @description     转码服务器数据库操作类
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-3-1
 * @file_name       querySql.java
 */
public class querySql {
	//初始化获取一些基本配置
	private getConfig config = new getConfig();
	private String dataAddress = config.get_setting("dataAddress");
	private String dataPort = config.get_setting("dataPort");
	private String dataId = config.get_setting("dataId");
	private String dataPaw = config.get_setting("dataPaw");
	private String dataName = config.get_setting("dataName");
	
	private String dataAddress1 = config.get_setting("dataAddress1");
	private String dataPort1 = config.get_setting("dataPort1");
	private String dataId1 = config.get_setting("dataId1");
	private String dataPaw1 = config.get_setting("dataPaw1");
	private String dataName1 = config.get_setting("dataName1");
	
	/** 
	 * 将客户端传来的任务先存入MYSQL  TODO 后期这里应该加入一个判断 检测数据是否正确   TODO 有分解命令的地方
	 * @DataFormat 结构体数组
	 * @return id 返回新插入的数据ID
	 */
	public int putDatabase(DataFormat arrayData){
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址
		String video_path = arrayData.getPath();
		String content_id = arrayData.getContent_id();
		String catid = arrayData.getCatid();
		String content_status = arrayData.getContent_status();
		String author = arrayData.getAuthor();
		//String content = arrayData.getCatid()+"<#>"+arrayData.getTitle()+"<#>"+arrayData.getKeyword()+"<#>"+arrayData.getDescription()+"<#>"+arrayData.getStatus()+"<#>"+arrayData.getThumb()+"<#>"+arrayData.getPosid();
		String sql = "INSERT INTO `tasks` (`id`,`video`,`content_id`,`status`,`content_status`,`catid`,`error_code`,`video_id`,`author`)VALUES (NULL ,\""+video_path+"\","+content_id+", 0,"+content_status+","+catid+",0,0,"+author+");";
		int id = mysql.inUpdate(sql);
		mysql.close();
		return id;
	}
	
	/** 
	 * 成功转码的任务存储进视频库
	 * @String 分发后的视频网址+路径
	 * @return id 返回新插入的数据ID
	 */
	public int putVideoBase(String Rurl_path){
		MysqlTool mysql  = new MysqlTool(dataAddress1,dataName1,dataId1,dataPaw1,dataAddress1,dataPort1);
		//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址
		String sql = "INSERT INTO `video` (`id`,`video_path`)VALUES (NULL ,'"+Rurl_path+"');";
		int id = mysql.inUpdate(sql);
		mysql.close();
		return id;
	}
	
	/** 
	 * 更新任务数据库转码状态
	 * @String 任务ID号 和状态号
	 * @return id 返回影响行数  一般情况下是1 没有该ID就是0
	 */
	public int UpdataTaskStatus(int taskid,int status,int errorCode){
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		String sql = "UPDATE  `tasks` SET  `status` =  "+status+",`error_code` = "+errorCode+" WHERE  `id` ="+taskid;
		int id = mysql.Update(sql);
		mysql.close();
		return id;
	}
	
	/** 
	 * 更新任务数据库指定字段的值
	 * @String 任务ID号 和状态号
	 * @return id 返回影响行数  一般情况下是1 没有该ID就是0
	 */
	public int UpdataTaskVal(int taskid,String val,String field){
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		String sql = "UPDATE  `tasks` SET  `"+field+"` =  '"+val+"'  WHERE  `id` ="+taskid;
		int id = mysql.Update(sql);
		mysql.close();
		return id;
	}
	
	/** 
	 * 传入一个任务列表库ID号   获取相应数据
	 * @DataId 任务在数据库中的ID号
	 * @return data 返回数据内容
	 * @throws SQLException 
	 */
	public String[] queryDatabase(int DataId) throws SQLException{
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		String[] data = new String[4];
		//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址
		String sql = "SELECT * FROM `tasks` where id = "+DataId+" limit 0,1";;
		ResultSet rt = mysql.query(sql);
		rt.next();
		data[0] = rt.getString("content_id");//文章id
		data[1] = rt.getString("content_status");//文章状态
		data[2] = rt.getString("catid");//文章栏目 用于发布
		data[3] = rt.getString("author");//作者ID
		 
		rt.close();
		mysql.close();
		return data;
	}
	
	/** 
	 * 传入一个任务列表库ID号   获取相应数据
	 * @DataId 任务在数据库中的ID号
	 * @return data 返回数据内容
	 * @throws SQLException 
	 */
	public String[] queryTask(int DataId) throws SQLException{
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		String[] data = new String[6];
		String sql = "SELECT * FROM `tasks` where id = "+DataId+" limit 0,1";;
		ResultSet rt = mysql.query(sql);
		rt.next();
		
		data[0] = rt.getString("video");//视频文件路径
		data[1] = rt.getString("content_id");
		data[2] = rt.getString("content_status");
		data[3] = rt.getString("catid");
		data[4] = rt.getString("video_id");
		data[5] = rt.getString("author");
		
		rt.close();
		mysql.close();
		return data;
	}
	
	/** 
	 * 获取指定条数的待二次继续发布的任务
	 * @num    需要查询的数量
	 * @return list 返回数据内容
	 * @throws SQLException 
	 */
	public List<MysqlBean> queryTaskList(int num) throws SQLException{
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址
		String sql = "SELECT * FROM `tasks` where `status` = -1 ORDER BY id ASC limit 0,"+num;
		ResultSet rt = mysql.query(sql);
		List<MysqlBean> list = new ArrayList<MysqlBean>();
		 
		while(rt!=null && rt.next()){
			MysqlBean datas = new MysqlBean(rt.getInt("id"),
											rt.getString("video"),
											rt.getInt("content_id"),
											rt.getInt("status"),
											rt.getInt("content_status"),
											rt.getInt("catid"),
											rt.getInt("error_code"),
											rt.getInt("video_id"),
											rt.getInt("author"));
			list.add(datas);
            //列的记数是从1开始的，这个适配器和C#的不同
       }
		rt.close();
		mysql.close();
		return list;
	}
	
	/** 
	 * 传入ID数组字符串,返回已转码成功的ID字符串
	 * @return list 返回数据内容
	 * @throws SQLException 
	 */
	public String queryEncodeList(String ids) throws SQLException{
		MysqlTool mysql  = new MysqlTool(dataAddress,dataName,dataId,dataPaw,dataAddress,dataPort);
		//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址
		String sql = "SELECT `id` FROM `tasks` where id in("+ids+") and `status` = 1 ORDER BY id ASC limit 0,30";
		//System.out.println(sql);
		ResultSet rt = mysql.query(sql);
		String msg = "";
		while(rt!=null && rt.next()){
			
			msg += rt.getInt("id")+",";
            //列的记数是从1开始的，这个适配器和C#的不同
        }
		//删除最后一个字符 
		if(msg.length()>1){
			msg = msg.substring(0,msg.length()-1);
		}
		rt.close();
		mysql.close();
		return msg;
	}

}
