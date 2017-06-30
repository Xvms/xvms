package com.appqy.tools;

import org.apache.mina.core.session.IoSession;

import com.appqy.mina.util.BaseMessage;
import com.appqy.mina.util.BeanUtil;
import com.appqy.mina.util.StringBean;

/**
 * @project_name    coderServer
 * @description     一些小工具类
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-5-28
 * @file_name       tools.java
 */
public class tools {

	/*
	 * 通过session回传信息
	 */
	public void resultMsg(IoSession session,String msg){
    	BaseMessage baseMsg = new BaseMessage();
    	baseMsg.setDataType(BeanUtil.UPLOAD_STR);
		StringBean bean = new StringBean();
		bean.setFileName(msg);
		baseMsg.setData(bean);
		//发送
		session.write(baseMsg);
    }

}
