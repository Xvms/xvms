package com.appqy.mina.util;

/**
 * 上传文件业务类
 * */
public class FileBean {
 private int fileSize;		//文件大小
 private String fileName;	//文件名称
 private byte[] fileContent;//文件byte数组

 
 
public int getFileSize() {
	return fileSize;
}
public void setFileSize(int fileSize) {
	this.fileSize = fileSize;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
}
public byte[] getFileContent() {
	return fileContent;
}
public void setFileContent(byte[] fileContent) {
	this.fileContent = fileContent;
}
 
 
}
