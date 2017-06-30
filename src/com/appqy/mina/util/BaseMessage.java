package com.appqy.mina.util;

/**
 * @project_name 	coderServer
 * @description	 	业务依据  储存数据
 * @code name 		Higgs boson
 * @author 			fy
 * @copyright 		Appqy Team
 * @license 		http://www.appqy.com/
 * @email 			fy@appqy.com
 * @lastmodify 		2013-10-30
 * @file_name 		SendMessage.java
 */
public class BaseMessage {
		private int dataType; // 作为业务判断依据
		private Object data; // 存储业务数据

		public BaseMessage(int dataType, Object data) {
			this.dataType = dataType;
			this.data = data;
		}

		public BaseMessage() {

		}

		public int getDataType() {
			return dataType;
		}

		public void setDataType(int dataType) {
			this.dataType = dataType;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

	}