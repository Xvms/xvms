package com.appqy.tools;
/**
 * @project_name    coderServer
 * @description     列文件目录工具
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-22
 * @code name       coffee bean
 */
import java.io.File;

public class listsDirectory {
	
	
	//调用方式
	//File f=new File("test");  test为输入目录
    //printDirectory(f,0);
	//递归的方式遍历目录
	public static void printDirectory(File f,int depth){
        if(!f.isDirectory()){//如果不是目录，则打印输出
            System.out.println(getTap(depth)+f.getName());
        }else{
            File[] fs=f.listFiles();
            System.out.println(getTap(depth)+f.getName());
            depth++;
            for(int i=0;i<fs.length;++i){
                File file=fs[i];
                printDirectory(file,depth);
            }
        }
	}
	//显示层次关系
	private static String getTap(int depth){
        StringBuffer tap=new StringBuffer();
        for(int i=0;i<depth;i++){
            tap.append("------");
        }
        return tap.toString();
	}
	
}
