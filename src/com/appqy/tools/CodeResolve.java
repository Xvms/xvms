package com.appqy.tools;

/**
 * @project_name    coderServer
 * @description     对任务返回的状态码进行解析,并回传返回给客户端的信息
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-5-5
 * @file_name       CodeResolve.java
 */
public class CodeResolve {

	 /*
	  * 对代码进行解析
	  */
	 public String resolve(String[] rtCode){
		 
		 String rtMsg = "";
		 switch (rtCode[0]) {
		 	case "0":
				//成功发布
				rtMsg = "#008#"+rtCode[1];
				break;
		 	case "1":
				//转码成功并更新审核
				rtMsg = "#009#"+rtCode[1];
				break;
		 	case "2":
				//更新成功  返回所更新的文章ID
				rtMsg = "#010#"+rtCode[1];
				break;
		 	case "4":
				//查询转码状态成功  返回已经转码成功的ID
				rtMsg = "#011#"+rtCode[1];
				break;
		 	case "5":
				//删除文章成功  返回该文章ID
				rtMsg = "#012#"+rtCode[1];
				break;
		 	case "6":
				//删除文章失败
		 		rtMsg = "#013#1";
				break;
			case "-1":
				//数据发布错误,需要重新发布(可能是网络阻塞)
				rtMsg = "#005#1";
				break;
			case "-2":
				//无法对该视频进行转码
				rtMsg = "#007#"+rtCode[1];
				break;
			case "-3":
				//转码失败
				rtMsg = "#007#"+rtCode[1];
				break;
			case "997":
				//心跳包 默认什么都不执行
				break;
			case "999":
				//默认什么都不执行
				break;
			default:
				//未知命令
				rtMsg = "#999#1";
				break;
			}
		 return rtMsg;
	 }

}
