package com.appqy.tasks;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.appqy.tools.FtpClient;
import com.appqy.tools.MysqlBean;
import com.appqy.tools.getConfig;
import com.appqy.web.querySql;

/**
 * @project_name    coderServer
 * @description     执行定时任务的执行接口
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-9-6
 * @code name       coffee bean
 */

public class job implements Job {
	private final static Logger log = Logger.getLogger(workTask.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//下面的A是一个传参
		//JobDataMap jobName = context.getJobDetail().getJobDataMap();
		//jobName.getBooleanValue("a");
		log.info("任务正在执行，执行时间: " + Calendar.getInstance().getTime());
		/*
		 * 下面开始正式执行检测任务 
		 * 检测未被正确完成的任务 尝试重试
		 * 
		 * 只需要查询任务状态为-1的任务 不能抢在未执行之前去执行
		 * 执行任务失败的流程代码:
		 * 1:转码和分发都成功了 更新文章视频ID和状态这个流程失败
		 * 2:转码成功 分发流程失败
		 * 3:转码任务失败
		 */
		
		//取回10个异常任务 
		 
		querySql querysql = new querySql();//实例化数据库类
		List<MysqlBean> list = null;//存储返回数据的LIST
		try {
			list = querysql.queryTaskList(10);
		} catch (SQLException e) {
			log.error("定时任务循环数据库意外,详细:"+e.getStackTrace().toString());
		}
		//有任务执行 没有任务跳出此线程继续等待
		int listCount = list.size();
		if(listCount>1){
			//创建2个线程的进程池
			WorkQueue work = new WorkQueue(2);

			//开始遍历任务
			for(int i=0;i<listCount;i++){
				
				MysqlBean Mbean = list.get(i);
				//创建修复任务线程
				workFixTask fixTask = new workFixTask(Mbean);
				//使用线程执行
				work.execute(fixTask);
			}
 
		}
		 
	}
 
	
	 
}
