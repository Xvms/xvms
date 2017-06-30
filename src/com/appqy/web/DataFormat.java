package com.appqy.web;

/**
 * @project_name    coderServer
 * @description     对待发布到网站的数据进行格式化处理
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-4-28
 * @file_name       DataFormat.java
 */
public class DataFormat {

	private String catid ="";
	private String title="";
	private String keyword="";
	private String description="";
	private String status="";//审核状态
	private String thumb="";
	private String posid="";
	private String path="";
	private String content_id="";
	private String content_status="";//0代表文章审核发布  99代表直接发布
	private String author="";//文章作者ID
	private String siteid="";//站点ID 暂未启用
	private String taskId="";//任务数据的任务ID
	//栏目ID<#>这里是标题<#>关键词<#>简介<#>状态<#>缩略图<#>推荐ID<#>转码服务器内视频地址


//TODO 将客户端发上来的字段统一
	//初始化信息
	public DataFormat(String[] data){
		//进来的数据 顺序一定要和下面一致
		switch (data.length) {
		case 1:
			this.taskId = data[0];
			break;
		case 2:
			this.catid = data[0];
			this.content_id = data[1];
			break;
		case 9:
			this.catid = data[0];//发布时候的栏目ID
			this.title = data[1];//发布的标题
			this.keyword = data[2];//发布的关键字
			this.description = data[3];//发布的简介
			this.status = data[4];//发布的文章状态
			this.thumb = data[5];//发布的缩略图
			this.posid = data[6];//发布的推荐位
			this.path = data[7];//视频路径用于任务数据库使用
			this.author = data[8];//作者ID 用于定时任务更新文章使用的ID值
			break;
		case 8:
			this.catid = data[0];//发布时候的栏目ID
			this.content_id = data[1];//文章ID
			this.title = data[2];//发布的标题
			this.keyword = data[3];//发布的关键字
			this.description = data[4];//发布的简介
			this.thumb = data[5];//发布的缩略图
			this.posid = data[6];//发布的推荐位
			break;
		default:
			break;
		}
	}
	
	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	/**
	 * @return the content_id
	 */
	public String getContent_id() {
		return content_id;
	}


	/**
	 * @param content_id the content_id to set
	 */
	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}


	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}


	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return the content_status
	 */
	public String getContent_status() {
		return content_status;
	}

	/**
	 * @param content_status the content_status to set
	 */
	public void setContent_status(String content_status) {
		this.content_status = content_status;
	}
	
 
	
	/**
	 * @return the catid
	 */
	public String getCatid() {
		return catid;
	}

	/**
	 * @param catid the catid to set
	 */
	public void setCatid(String catid) {
		this.catid = catid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the thumb
	 */
	public String getThumb() {
		return thumb;
	}

	/**
	 * @param thumb the thumb to set
	 */
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	/**
	 * @return the posid
	 */
	public String getPosid() {
		return posid;
	}

	/**
	 * @param posid the posid to set
	 */
	public void setPosid(String posid) {
		this.posid = posid;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the siteid
	 */
	public String getSiteid() {
		return siteid;
	}

	/**
	 * @param siteid the siteid to set
	 */
	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

}
