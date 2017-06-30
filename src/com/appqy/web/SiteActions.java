package com.appqy.web;

 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
 
import com.appqy.tools.HttpTookit;
import com.appqy.tools.getConfig;

/**
 * @project_name    coderServer
 * @description     获取网页参数的参数预定义类
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-12-22
 * @file_name       getData.java
 */
public class SiteActions {
	
	
		//初始化获取一些基本配置
		getConfig config = new getConfig();
		String WEB_PATH = config.get_setting("WEB_PATH");
		String WEB_KEY = config.get_setting("WEB_KEY");

		HttpTookit ht = new HttpTookit();
		
		BasicCookieStore CookieStatus = null;

	/**
	 * 执行登陆
	 * 返回是否登录成功
	 */
	public String loginWeb(String uid,String password) throws Exception{
 
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=getCategory
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "login"));
        nvps.add(new BasicNameValuePair("uid", uid));
        nvps.add(new BasicNameValuePair("pass", password));
        //这个很重要,是当前连接的session和cookie 没有他就不能执行其他需要登录的了
        CookieStatus = ht.doLogin(WEB_PATH,nvps);
        //System.out.println(CookieStatus.toString());
        List<Cookie> cookies = CookieStatus.getCookies();
        //获取cookie数组
        HashMap<String, String> webCookie = new HashMap<String, String>();
        for(Cookie h : cookies){  
        	webCookie.put(h.getName(), h.getValue());
        	//System.out.println(h.getName() + " : " + h.getValue());
        }
        String msg = "";
        if(webCookie.containsKey("msg")){
        	msg = webCookie.get("msg");
        }
        String uuid = "";
        if(webCookie.containsKey("admin_uid"))
        uuid = cookies.get(1).getValue();
        
        if(msg.equals("0")){
        	//System.out.println("登陆失败!");
        	return "0";
        }else if(msg.equals("-1")){
        	//服务器与网站接口通信异常
        	return "-1";
        }else if(msg.equals("3")){
        	return "3";//账号锁定一小时
        }else{
        	//登陆成功
        	return "1<#>"+uuid;
        }
        
        //下面是把Cookie字段获取出来
        /*
        for(Cookie h : CookieStatus.getCookies()){  
        	System.out.println(h.getName() + " : " + h.getValue());   
        }
        
        
        List <NameValuePair> nvps2 = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=getCategory
        nvps2.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps2.add(new BasicNameValuePair("opt", "getCategory"));
        ht.doPost(WEB_PATH, nvps2, CookieStatus);
         */
        
        
	}
 
	/**
	 * 获取当前登陆账号后台权限所有栏目列表
	 *
	 */
	public String getWebCategory() throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=getCategory
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "getCategory"));
        
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}
	/**
	 * 获取当前登陆账号后台权限所有栏目列表
	 *
	 */
	public String getWebPosition() throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=getCategory
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "getPosition"));
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}

	/**
	 * 发布新闻
	 * 并返回发布状态
	 */
	public String postNews(DataFormat arrayData) throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		//栏目ID 类别ID 标题  关键字 简介 状态 缩略图
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=postVideo&title=标题&cid=63&keyword=关键词&description=简介&typeid=类别ID&status=99&thumb=http://www.xxx.com/uploadfile
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "postVideo"));
        //分解命令  TODO 有分解命令的地方
        nvps.add(new BasicNameValuePair("title", arrayData.getTitle()));
        nvps.add(new BasicNameValuePair("cid", arrayData.getCatid()));
        nvps.add(new BasicNameValuePair("description", arrayData.getDescription()));
        nvps.add(new BasicNameValuePair("keywords", arrayData.getKeyword()));
        nvps.add(new BasicNameValuePair("video_id", "0"));//这里就是视频库的ID 先给他为0 分发成功了再更新
        nvps.add(new BasicNameValuePair("posids", arrayData.getPosid()));
        nvps.add(new BasicNameValuePair("status", arrayData.getStatus()));
        nvps.add(new BasicNameValuePair("thumb", arrayData.getThumb()));
       
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}
	
	/**
	 * 更新新闻状态以及ID
	 * 并返回发布状态
	 */
	public String postUpdataNews(String[] Data,int videoDataID) throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=postVideo&title=标题&cid=63&keyword=关键词&description=简介&typeid=类别ID&status=99&thumb=http://www.xxx.com/uploadfile
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "updataVideo"));
        
        //分解命令  TODO 有分解命令的地方
        nvps.add(new BasicNameValuePair("id", Data[0].toString()));//文章ID
        nvps.add(new BasicNameValuePair("video_id", videoDataID+""));//视频库ID
        //nvps.add(new BasicNameValuePair("status", Data[1].toString()));//文章状态
        nvps.add(new BasicNameValuePair("cid", Data[2].toString()));//文章栏目ID
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}
	
	/**
	 * 删除文章
	 * 并返回状态
	 */
	public String postDelNews(String catid,String contentId) throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=postVideo&title=标题&cid=63&keyword=关键词&description=简介&typeid=类别ID&status=99&thumb=http://www.xxx.com/uploadfile
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "delVideo"));
        
        //分解命令  TODO 有分解命令的地方
        nvps.add(new BasicNameValuePair("id", contentId));//文章ID
        nvps.add(new BasicNameValuePair("cid", catid));//栏目ID
        
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}
	
	/**
	 * 更新新闻字段信息
	 * 并返回发布状态
	 */
	public String postUpdataNewsInfo(DataFormat Data) throws Exception{
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        //网页post参数 http://www.xxx.com/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=postVideo&title=标题&cid=63&keyword=关键词&description=简介&typeid=类别ID&status=99&thumb=http://www.xxx.com/uploadfile
        nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
        nvps.add(new BasicNameValuePair("opt", "updataVideoInfo"));
        
        //分解命令  TODO 有分解命令的地方
        nvps.add(new BasicNameValuePair("id", Data.getContent_id()));//文章ID
        nvps.add(new BasicNameValuePair("cid", Data.getCatid()));//文章栏目ID
        nvps.add(new BasicNameValuePair("title",Data.getTitle()));//标题
        nvps.add(new BasicNameValuePair("keywords",Data.getKeyword()));//关键词
        nvps.add(new BasicNameValuePair("description",Data.getDescription()));//简介
        nvps.add(new BasicNameValuePair("thumb",Data.getThumb()));//图片
        nvps.add(new BasicNameValuePair("posid",Data.getPosid()));//推荐位
        return ht.doPost(WEB_PATH,nvps,CookieStatus);
	}
	
}
