package com.appqy.mina.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileHelper {
	
	//TODO 必须改进的地方  采用以下发送来收发文件 可以实现分段发送
	//FileInputStream fis = new FileInputStream(file);
	//FileChannel chnl = fis.getChannel();
	//chnl.transferTo(0, chnl.size(), targetChannel);
	/**
	 * 直接根据文件路径返回byte数组
	 * */
	@SuppressWarnings("resource")
	public byte[] getContent(String filePath) throws IOException {  
        File file = new File(filePath);  
  
        long fileSize = file.length();
        
        if (fileSize > Integer.MAX_VALUE) {  
            System.out.println("file too big...");  
            return null;  
        }  
        
        FileInputStream fi = new FileInputStream(file);  
  
        byte[] buffer = new byte[(int) fileSize];  
  
        int offset = 0;  
  
        int numRead = 0;  
  
        while (offset < buffer.length  
        		
        && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
        	
            offset += numRead;  
        }  
        // 确保所有数据均被读取   
        if (offset != buffer.length) {  
  
            throw new IOException("Could not completely read file " + file.getName());  
  
        }  
        fi.close();  
        return buffer;  
    }  
	/**
	 * 根据文件返回byte数组
	 * */

	@SuppressWarnings("resource")
	public byte[] getContent(File file) throws IOException {  
	        long fileSize = file.length();  

	        FileInputStream fi = new FileInputStream(file);  
	        
	        byte[] buffer = new byte[(int) fileSize];  
	        
	        int offset = 0;  
	  
	        int numRead = 0;  
	        
	        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
	        	System.out.println(offset);
	            offset += numRead;  
	        }
	        
	        // 确保所有数据均被读取   
	        if (offset != buffer.length) {  
	  
	            throw new IOException("Could not completely read file " + file.getName());  
	  
	        }  
	        fi.close();
	        
	        return buffer;  
	}
}
