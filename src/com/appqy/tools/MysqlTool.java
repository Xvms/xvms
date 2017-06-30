package com.appqy.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * @project_name    coderServer
 * @description     mysql操作连接类
 * @code name       Higgs boson
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-2-24
 * @file_name       MysqlTool.java
 */
public class MysqlTool {

	   private final static Logger log = Logger.getLogger(MysqlTool.class);
	   private Connection conn=null;
	   private Statement st=null;
	   private ResultSet rs=null;
	   
	   public MysqlTool(String host,String databaseName,String userName,String password,String address,String port){
	       try{
	           //写入驱动所在处，打开驱动
	       Class.forName("com.mysql.jdbc.Driver").newInstance();
		   //数据库，用户，密码，创建与具体数据库的连接
 
	       conn=DriverManager.getConnection("jdbc:mysql://"+ host +":"+port+"/"+databaseName+"?useUnicode=true&characterEncoding=UTF-8&user="+userName+"&password="+password);
		   //创建执行sql语句的对象
		   st=conn.createStatement();
		   log.debug("连接数据库成功");
	       }catch(Exception e){
	    	   log.error("连接数据库失败"+e.toString());
	       }
	       
	   }
	   
	   /**
	    * 查询单条数据的一个列的值
	    * @param sqlStatement
	    * @param n
	    * @return
	    */
	   public String query(String sqlStatement,String n){
	       String result="";
	       try{
	           rs=st.executeQuery(sqlStatement);
	           while(rs!=null && rs.next()){
	                result=rs.getString(n);
	                //列的记数是从1开始的，这个适配器和C#的不同
	           }
	           rs.close();
	           return result;
	       }catch(Exception e){
	    	   log.error("查询失败"+e.toString());
	           return "";
	       }
	   }
	   /**
	    * 返回数据资源句柄 要记得关闭  用于多条数据的提取
	    * @param sqlStatement
	    * @return
	    */
	   public ResultSet query(String sqlStatement){
	       try{
	           rs = st.executeQuery(sqlStatement);
	           if(rs!=null){
	        	   return rs;
	           }else{
	        	   return null;
	           }
	       }catch(Exception e){
	    	   log.error("查询失败"+e.toString());
	           return null;
	       }
	   }
	   
	   /**
	    * 用于更新或者插入 
	    * @param sqlStatement sql语句
	    * @return 成功返回影响行数
	    */
	   public int Update(String sqlStatement){
	       int row=0;
	       try{
	           row=st.executeUpdate(sqlStatement);
	           st.close();
	           conn.close();
	           //this.close();
	           return row;
	       }catch(Exception e){
	    	   log.warn("执行sql语句失败"+e.toString());
	           this.close();
	           return row;
	       }
	   }
	   
	 /**
	  * 用于插入 成功
	  * @param sqlStatement SQL语句
	  * @return 返回自增ID
	  */
	   public int inUpdate(String sqlStatement){
		   ResultSet res = null;
	       int row=0;
	       int id = 0;
	       try{
	           row=st.executeUpdate(sqlStatement,Statement.RETURN_GENERATED_KEYS);
	           if(row>0){
	        	   //写入成功
	        	   res=st.getGeneratedKeys();  
	               if(res.next())  id=res.getInt(1);//返回自增ID号
	           }
	           st.close();
	           conn.close();
	           //this.close();
	           return id;
	       }catch(Exception e){
	    	   log.warn("执行sql语句失败"+e.toString());
	           this.close();
	           return id;
	       }
	   }
	   
	   //关闭各种连接
	   public void close(){
	      try{
	          if(rs!=null){
	        	  this.rs.close();
	          }
	          if(st!=null){
	        	  this.st.close();
	          }
	          if(conn!=null){
	        	  this.conn.close();
	          } 
	      }catch(Exception e){
	          log.error("关闭数据库连接失败"+e.toString());
	      }       
	   }
 

}
