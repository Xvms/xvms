package com.appqy.mina.util;

/**
 * 发送指令业务类
 * */
public class StringBean {
	//任务类型
	private int dataType;
	//字符串长度
	private int strLength;
	//第一条指令
	private String fileName;
	 /**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the strLength
	 */
	public int getStrLength() {
		return strLength;
	}
	/**
	 * @param strLength the strLength to set
	 */
	public void setStrLength(int strLength) {
		this.strLength = strLength;
	}
 
}
