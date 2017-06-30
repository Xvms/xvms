package com.appqy.tasks;

import java.text.ParseException;
import java.util.Date;
 
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


/**
 * @project_name    coderServer
 * @description     任务定时器服务
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2014-2-26
 * @code name       coffee bean
 * TODO 后续当然要加入固定时间之后删除数据库里面的往期过期任务,提供配置开关
 */
public class serverTasks {
 
		public serverTasks() throws SchedulerException, ParseException{
			
			/* 
			 * 循环式的
			 */
			SchedulerFactory factory = new StdSchedulerFactory();
			Scheduler scheduler = factory.getScheduler();
			//设置启动时间
			DateBuilder.evenMinuteDate(new Date());
			JobDetail job = JobBuilder.newJob(job.class).withIdentity("job1", "group1").build();
		 
			//job.getJobDataMap().put("a", true);//实现传参
			// @NOTICE
			// 与SimpleTrigger对比：类不同了，现在的是Trigger的子类CronTrigger；withSchedule中的参数变为CronScheduleBuilder了
			// CronScheduleBuilder可以通过类似"0/13 * * * * ?"这种表达式来创建定时任务
			// 当前这个表达式的定义是30分钟执行一次
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
					.withSchedule(CronScheduleBuilder.cronSchedule("1 0/30 * * * ?")).build();

			scheduler.scheduleJob(job, trigger);

			scheduler.start();
		 
			//scheduler.shutdown(true);
			
		}
		
		
		/*
		 * 指定时间版
		 */
		/*
		SchedulerFactory factory = new StdSchedulerFactory();
		// 从工厂里面拿到一个scheduler实例
		Scheduler scheduler = factory.getScheduler();
 
		// 计算任务的开始时间
		//DateBuilder.evenMinuteDate(new Date()); 方法是取下一个整数分钟  下面采用直接给出时间
		Date  runTime = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("28/02/2014 11:01:00");
		Date  runTime2 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("28/02/2014 11:01:00");
		// 真正执行的任务并不是Job接口的实例，而是用反射的方式实例化的一个JobDetail实例
		JobDetail job = JobBuilder.newJob(job.class).
						withIdentity("job1", "group1").
						build();
		job.getJobDataMap().put("a", true);//实现传参
		// 定义一个触发器，startAt方法定义了任务应当开始的时间   执行5次 (其实加上本身的执行是执行了6次)每次间隔3秒withRepeatCount(5).withIntervalInSeconds(3)
		Trigger trigger = TriggerBuilder.newTrigger().
						  withSchedule(SimpleScheduleBuilder.simpleSchedule())
						  .startAt(runTime).build();
		// 将任务和Trigger放入scheduler
		

		// 定义一个触发器，startAt方法定义了任务应当开始的时间   执行5次 (其实加上本身的执行是执行了6次)每次间隔3秒withRepeatCount(5).withIntervalInSeconds(3)
		Trigger trigger2 = TriggerBuilder.newTrigger().
						  withSchedule(SimpleScheduleBuilder.simpleSchedule())
						  .startAt(runTime2).build();

		scheduler.scheduleJob(job, trigger);
		scheduler.start();
		try {
			// 等待65秒，保证下一个整数分钟出现，这里注意，如果主线程停止，任务是不会执行的
			Thread.sleep(65L * 1000L);
		} catch (Exception e) {

		}
		// scheduler结束
		//scheduler.shutdown(true);

		 */
		
		
		
		
		/* 循环式的
		SchedulerFactory factory = new StdSchedulerFactory();
		Scheduler scheduler = factory.getScheduler();

		JobDetail job = JobBuilder.newJob(MyJob.class).withIdentity("job1", "group1").build();
		// @NOTICE
		// 与SimpleTrigger对比：类不同了，现在的是Trigger的子类CronTrigger；withSchedule中的参数变为CronScheduleBuilder了
		// CronScheduleBuilder可以通过类似"0/13 * * * * ?"这种表达式来创建定时任务
		// 当前这个表达式的定义是每个秒是13的倍数，或者是0的时候，都触发任务
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0/13 * * * * ?")).build();

		scheduler.scheduleJob(job, trigger);

		scheduler.start();
		try {
			// 等待60秒查看效果
			Thread.sleep(60L * 1000L);
		} catch (Exception e) {
		}
		scheduler.shutdown(true);
 
 

--------------------------------------
附：cronExpression配置说明  
  
字段   允许值   允许的特殊字符   
秒    0-59    , - * /   
分    0-59    , - * /   
小时    0-23    , - * /   
日期    1-31    , - * ? / L W C   
月份    1-12 或者 JAN-DEC    , - * /   
星期    1-7 或者 SUN-SAT    , - * ? / L C #   
年（可选）    留空, 1970-2099    , - * /

	0 0 12 * * ?			每天12点触发
	0 15 10 ? * *			每天10点15分触发
	0 15 10 * * ?			每天10点15分触发
	0 15 10 * * ? *			每天10点15分触发
	0 15 10 * * ? 2005		2005年每天10点15分触发
	0 * 14 * * ?			每天下午的 2点到2点59分每分触发
	0 0/5 14 * * ?			每天下午的 2点到2点59分(整点开始，每隔5分触发)
	0 0/5 14,18 * * ?		每天下午的 2点到2点59分(整点开始，每隔5分触发) 每天下午的 18点到18点59分(整点开始，每隔5分触发)
	0 0-5 14 * * ?			每天下午的 2点到2点05分每分触发
	0 10,44 14 ? 3 WED		3月分每周三下午的 2点10分和2点44分触发
	0 15 10 ? * MON-FRI		从周一到周五每天上午的10点15分触发
	0 15 10 15 * ?			每月15号上午10点15分触发
	0 15 10 L * ?			每月最后一天的10点15分触发
	0 15 10 ? * 6L			每月最后一周的星期五的10点15分触发
	0 15 10 ? * 6L 2002-2005	从2002年到2005年每月最后一周的星期五的10点15分触发
	0 15 10 ? * 6#3			每月的第三周的星期五开始触发
	0 0 12 1/5 * ?			每月的第一个中午开始每隔5天触发一次
	0 11 11 11 11 ?			每年的11月11号 11点11分触发(光棍节)
--------------------------------------
 */

}




