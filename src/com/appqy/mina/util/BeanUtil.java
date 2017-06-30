package com.appqy.mina.util;

import java.nio.charset.Charset;

/**
 * 存储常量类
 * */
public class BeanUtil {
	/**
	 * 字符串编码
	 * */
  public final static  Charset charset = Charset.forName("utf-8");
  public final static int UPLOAD_FILE = 1;	//传送文件
  public final static int UPLOAD_STR = 2;	//传送字符串
}
