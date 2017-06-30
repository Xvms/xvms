package com.appqy.tools;

/**
 * @project_name    coderServer
 * @description     任务数据库 数据对象封装 用于MYSQL存取数据
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-5-12
 * @file_name       MysqlBean.java
 */
public class MysqlBean {

	//任务数据库 字段
	private int id;//自增ID
	private String video;//视频文件全名
	private int content_id;//已发布的内容ID
	private int status;//任务的状态
	private int content_status = 0;//发布的文章状态
	private int catid;//文章栏目ID
	private int error_code;//异常错误代码
	private int video_id;//视频库视频ID
	private int author;//文章发布者ID
	public  MysqlBean(int id,String video,int content_id,int status,int content_status,int catid,int error_code,int video_id,int author){
		this.id = id;
		this.video = video;
		this.content_id = content_id;
		this.status = status;
		this.content_status = content_status;
		this.catid = catid;
		this.error_code = error_code;
		this.video_id = video_id;
		this.author = author;
	}
	/**
	 * @return the author
	 */
	public int getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(int author) {
		this.author = author;
	}
	/**
	 * @return the video_id
	 */
	public int getVideo_id() {
		return video_id;
	}
	/**
	 * @param video_id the video_id to set
	 */
	public void setVideo_id(int video_id) {
		this.video_id = video_id;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the video
	 */
	public String getVideo() {
		return video;
	}
	/**
	 * @param video the video to set
	 */
	public void setVideo(String video) {
		this.video = video;
	}
	/**
	 * @return the content_id
	 */
	public int getContent_id() {
		return content_id;
	}
	/**
	 * @param content_id the content_id to set
	 */
	public void setContent_id(int content_id) {
		this.content_id = content_id;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the content_status
	 */
	public int getContent_status() {
		return content_status;
	}
	/**
	 * @param content_status the content_status to set
	 */
	public void setContent_status(int content_status) {
		this.content_status = content_status;
	}
	/**
	 * @return the catid
	 */
	public int getCatid() {
		return catid;
	}
	/**
	 * @param catid the catid to set
	 */
	public void setCatid(int catid) {
		this.catid = catid;
	}
	/**
	 * @return the error_code
	 */
	public int getError_code() {
		return error_code;
	}
	/**
	 * @param error_code the error_code to set
	 */
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	
	

}
