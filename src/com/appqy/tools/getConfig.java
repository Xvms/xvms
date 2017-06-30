
package com.appqy.tools;
/**
 * @project_name    coderServer
 * @description     获取编码配置文件
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-9-6
 * @code name       coffee bean
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
 

public class getConfig {

	private final static Logger log = Logger.getLogger(getConfig.class);
	public String get_setting(String key){
			//使用键值读取配置文件数据   使用方法 String str = get_setting("video_size_max");
	        Properties params = new  Properties();  
	        FileInputStream in = null;  
	        String value =null;
	        
	        try  
	        {  
	            in = new FileInputStream(new File("setting.xml"));
	            params.loadFromXML(in);  
	            value = params.getProperty(key);  
	        }   
	        catch (java.io.IOException e)  
	        {
	            log.error("\r\n setting.xml not found! \r\n");
	        }finally {  
	            if(in!=null) {  
	                try {  
	                    in.close();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	        return value;  
	 
	}
	
	//组合视频转码参数为视频转码命令
	public String[] video_properties(String input_file,String output_path,String output_file){
		String video_size_max,video_size_normal,video_size_min,video_audio_max,video_audio_normal,
			   video_audio_min,video_rate_max,video_rate_normal,video_rate_min;
		String[] cmd = new String[3];
		video_size_max = get_setting("video_size_max");
		video_size_normal = get_setting("video_size_normal");
		video_size_min = get_setting("video_size_min");
		video_audio_max = get_setting("video_audio_max");
		video_audio_normal = get_setting("video_audio_normal");
		video_audio_min = get_setting("video_audio_min");
		video_rate_max = get_setting("video_rate_max");
		video_rate_normal = get_setting("video_rate_normal");
		video_rate_min = get_setting("video_rate_min");
		//video_fps = get_setting("video_fps");
		//video_spect = get_setting("video_spect");
		
		//转换动态码率中的 最小码率 最大码率使用设置码率
		int minrate = Integer.parseInt(video_rate_normal) - 30;
		int maxrate = Integer.parseInt(video_rate_normal) + 30;
		int Mminrate = Integer.parseInt(video_rate_min) - 30;
		int Mmaxrate = Integer.parseInt(video_rate_min) + 30;
		//设置视频大小帧中的宽高为双数,单数转码器会报错
		video_size_normal = double_wh(video_size_normal);
		
		//TODO 还需设置三种格式参数  和  多线程的转码,非JAVA多线程,是ffmpeg内置的多线程命令  以下命令是视频生成后流媒体不卡的关键
		// ffmpeg  -i /encode/video/20140605/20140605b.mp4  -acodec libfaac -y -b 400k -vcodec libx264  -ab 48k -ar 44100 -s 570x456 -threads 4 -subq  6 -refs 6 -bf 0  -trellis 0 -8x8dct 0  -keyint_min 7 -qdiff 4  -maxrate 450k -minrate 380k  test.mp4
		cmd[0] = "ffmpeg -threads 16 -i "+input_file+" -acodec libfaac -y -b "+video_rate_normal+"k  -vcodec libx264  -ab "+video_audio_normal+"k -ar 44100 -maxrate "+maxrate+"k -minrate "+minrate+"k -s "+video_size_normal+" -subq  6 -refs 6 -bf 0  -trellis 0 -8x8dct 0  -keyint_min 7 -qdiff 4 "+output_path+output_file;
		cmd[1] = "ffmpeg -threads 16 -i "+input_file+" -acodec libfaac -y -b "+video_rate_min+"k  -vcodec libx264  -ab "+video_audio_min+"k -ar 44100 -maxrate "+Mmaxrate+"k -minrate "+Mminrate+"k -s "+video_size_min+" -subq  6 -refs 6 -bf 0  -trellis 0 -8x8dct 0  -keyint_min 7 -qdiff 4 "+output_path+"m_"+output_file;
		return cmd;
	}
	
	//图片截取
	public String getVideoImage(String input){
		String cmd = "ffmpeg -i "+input+" -y -f image2 -ss 10 -t 0.001 -s 350x240 test.jpg";
		return cmd;
	}
	
	//高宽比单数判断
	public String double_wh(String wh){
		String a[] = wh.split("\\*");
		int w = Integer.parseInt(a[0]);
		int h = Integer.parseInt(a[1]);
		if(w%2!=0){
			w++;
		}
		if(h%2!=0){
			h++;
		}
		return w+"*"+h;
	}
	
	/**
	 * 判断视频格式
	 * TODO 以后需要加入wmv的转码方式 以及判断这个视频是否是标准视频
	 */
	public int checkContentType(String file) {
        String type = file.substring(file.lastIndexOf(".") + 1,file.length()).toLowerCase();
        //ffmpeg能解析的格式：（mpg,wmv,3gp,mp4,mov,avi,flv等）
        int rt;
        switch (type) {
		case "avi":
		case "webm":
		case "mpg":
		case "mpeg":
		case "mov":
		case "3gp":
		case "mp4":
		case "asf":
		case "asx":
		case "mp3":
		case "flv":
		case "mkv":
		case "mtv":
		case "vob":
			rt = 0;
			break;
		
		case "wmv9":
		case "wmv":
		case "rm":
		case "rmvb":
			//对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
			rt = 1;
			break;
		default:
			rt = 1;
			break;
		}
 
        return rt;
    }

}
