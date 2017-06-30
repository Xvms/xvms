package com.appqy.tools;

 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


 
/**
 * @project_name    coderServer
 * @description     HTTP模拟请求工具
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-12-21
 * @file_name       HttpTookit.java
 */


	/** 
	* HTTP工具箱 
	*/ 
	public final class HttpTookit {
		
		//设置请求和传输超时时间  容易获取不到页面数据  30秒超时时间
		private RequestConfig requestConfig = RequestConfig.custom()
											 .setSocketTimeout(30000)
											 .setConnectTimeout(30000)
											 .build();
		private final static Logger log = Logger.getLogger(HttpTookit.class);
		/*
		 * 登陆
		 */
		public BasicCookieStore doLogin(String url,List <NameValuePair> nvps) throws Exception{
		 
			//CloseableHttpClient httpclient = HttpClients.createDefault();
			//创建Cookie存储对象 以备存储Cookie
			BasicCookieStore cookieStore = new BasicCookieStore();
	        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
	 
	        
			try {
				//1Post请求
	            HttpPost httpPost = new HttpPost(url);
	            	httpPost.setConfig(requestConfig);//设置超时配置
					httpPost.setEntity(new UrlEncodedFormEntity(nvps));
					CloseableHttpResponse response2;
					//2发送请求
					response2 = httpclient.execute(httpPost);
					
					//System.out.println(response2.getStatusLine());
	                //3处理响应结果
	                if (response2.getStatusLine().getStatusCode() == 200) {
	                	//4从输入流读取网页字符串内容
	                	HttpEntity entity2 = response2.getEntity();
	                  
	                    InputStream in = entity2.getContent();
	                    String r = readResponse(in);
	                    int msg;
	                    String uid ="";
	                    //分解数据   状态|UID
	                    if(r.length()>1){
	                    	String[] msgs = r.split("<#>");
	                    	msg = Integer.parseInt(msgs[0]);
	                    	uid = msgs[1];//登陆成功返回的用户的UID值
	                    }else{
	                    	msg = Integer.parseInt(r);
	                    }
	                    EntityUtils.consume(entity2);
	                    //通过返回数据 用|区分数据 在用cookei值回传
	                    BasicClientCookie cookie;
	                    if(msg==1){
	                    	//登陆成功 返回COOKIE
	                    	//System.out.println("登陆成功");
	                    	//Header[] headers = response2.getAllHeaders();
	                    	cookie = new BasicClientCookie("admin_uid", uid);//将UID返回出去
	                    	cookieStore.addCookie(cookie);
	                    	response2.close();
			                httpclient.close();	 
			                return cookieStore;
	                    }else{
	                    	cookie = new BasicClientCookie("msg", msg+"");
	        				cookieStore.addCookie(cookie);
	                    	response2.close();
			                httpclient.close();
	                    	return cookieStore;
	                    }
	                 
	                }
	                // and ensure it is fully consumed
	                response2.close();
			}catch(java.io.IOException e){
				e.getStackTrace();
				log.error("Web服务器端网络异常!info:"+e.getStackTrace());
				BasicClientCookie cookie = new BasicClientCookie("msg", "-1");
				cookieStore.addCookie(cookie);
				return cookieStore;
			} finally {
				httpclient.close();
			}
			return cookieStore;
		}
		public void doGet(String url) throws Exception {
			CloseableHttpClient httpclient = HttpClients.createDefault();
	        try {
	        	//http://www.scnj.tv/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=login&uid=server&pass=bkysbkys
	            HttpGet httpGet = new HttpGet(url);
	            httpGet.setConfig(requestConfig);//设置超时配置
	            CloseableHttpResponse response1 = httpclient.execute(httpGet);
	            // The underlying HTTP connection is still held by the response object
	            // to allow the response content to be streamed directly from the network socket.
	            // In order to ensure correct deallocation of system resources
	            // the user MUST either fully consume the response content  or abort request
	            // execution by calling CloseableHttpResponse#close().
	            
	            try {
	            	//返回登陆状态
	                //System.out.println(response1.getStatusLine());
	                HttpEntity entity1 = response1.getEntity();
	                // do something useful with the response body
	                if (response1.getStatusLine().getStatusCode() == 200) {  
	                    HttpEntity entity = response1.getEntity();  
	                  
	                    InputStream in = entity.getContent();  
	                    readResponse(in);
	                }
	                // and ensure it is fully consumed
	                EntityUtils.consume(entity1);
	                
	                // (3) 遍历返回头  
	                //Header[] headers = response1.getHeaders("PHPSESSION");   
	                
	                
	                 
	            } finally {
	                //response1.close();
	            }
 
	        } finally {
	            //httpclient.close();
	        }
	        
	        
	      
		}
		
		/*
         * POST提交数据
         */
		public String doPost(String url,List <NameValuePair> nvps,BasicCookieStore loginStatus) throws Exception{
			 
	        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(loginStatus).build();
	        String webStr = "";
			try {
				//1Post请求
	            HttpPost httpPost = new HttpPost(url);
	            httpPost.setConfig(requestConfig);//设置超时配置
	            
	            	/*
	            	 * nvps 内容
	            	 * List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				     * //网页post参数 http://www.scnj.tv/appqy_api/api.php?api_key=AaPpQqYyCcOoMmLl&opt=getCategory
				     * nvps.add(new BasicNameValuePair("api_key", WEB_KEY));
				     * nvps.add(new BasicNameValuePair("opt", "login"));
				     * nvps.add(new BasicNameValuePair("uid", uid));
				     * nvps.add(new BasicNameValuePair("pass", password));
	            	 * */
	            	
					httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));//需加上UTF8 不然提交出去到网站会变成乱码 TODO 以后需要提取配置编码
					 
					CloseableHttpResponse response2;
					//2发送请求
					response2 = httpclient.execute(httpPost);
					System.out.println(response2.getStatusLine());
	                //3处理响应结果
	                if (response2.getStatusLine().getStatusCode() == 200) {
	                	//4从输入流读取网页字符串内容
	                	HttpEntity entity2 = response2.getEntity();
	                    InputStream in = entity2.getContent();
	                    webStr = readResponse(in);
	                    //log.debug("网络请求接收到的返回数据"+webStr);
	                    EntityUtils.consume(entity2);
	                }
	                // and ensure it is fully consumed
	                response2.close();
			}catch(java.io.IOException e){
				e.getStackTrace();
				log.error("Web服务器端网络异常!info:"+e.getStackTrace().toString());
			}
			finally {
			//始终保持执行
            httpclient.close();
            
			}
			return webStr;
 
		}
		
		/*
		 * 读取网页返回信息
		 */
		public static String readResponse(InputStream in) throws Exception{  
			//HTTP获取状态 如需获取到字符串 需要获取网页编码 或者获取配置编码 否则会导致获取网页数据乱码
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));  
		    String line = null;
		    String content = "";
		    while ((line = reader.readLine()) != null) {  
		    	content = content + line;
		    }
		    return content;
		}
}
