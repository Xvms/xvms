package com.appqy.tools;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * @project_name    coderServer
 * @description     通信数据加解密
 * @author 			fy
 * @copyright		Appqy Team
 * @license			http://www.appqy.com/
 * @email			fy@appqy.com
 * @lastmodify		2013-10-22
 * @code name       coffee bean
 */

	/**
	 * This program generates a AES key, retrieves its raw bytes, and then
	 * reinstantiates a AES key from the key bytes. The reinstantiated key is used
	 * to initialize a AES cipher for encryption and decryption.
	 */

	public class DataAES {
		
		private static final String AES = "AES";
		//加解密密钥 16位
		public static String CRYPT_KEY;
		
		public DataAES(){
			//初始化类的时候加载KEY
			getConfig v_setting = new getConfig();
			CRYPT_KEY = v_setting.get_setting("ServerKey");
			System.out.println(CRYPT_KEY);
		}
	 
		/*例子
		public void main(String args) {
			String ID = "aaa";//待加密内容
			String idEncrypt = encrypt(ID);//加密
			System.out.println(idEncrypt);
			String idDecrypt = decrypt(idEncrypt);//解密
			System.out.println(idDecrypt);
		}
		*/

		/**
		 * 加密
		 * 
		 * @param encryptStr
		 * @return
		 */
		public static byte[] encrypt(byte[] src, String key) throws Exception {
			Cipher cipher = Cipher.getInstance(AES);
			SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);
			cipher.init(Cipher.ENCRYPT_MODE, securekey);//设置密钥和加密形式
			return cipher.doFinal(src);
		}

		/**
		 * 解密
		 * 
		 * @param decryptStr
		 * @return
		 * @throws Exception
		 */
		public static byte[] decrypt(byte[] src, String key)  throws Exception  {
			Cipher cipher = Cipher.getInstance(AES);
			SecretKeySpec securekey = new SecretKeySpec(key.getBytes(), AES);//设置加密Key
			cipher.init(Cipher.DECRYPT_MODE, securekey);//设置密钥和解密形式
			return cipher.doFinal(src);
		}
		
		/**
		 * 二进制转十六进制字符串
		 * 
		 * @param b
		 * @return
		 */
		public static String byte2hex(byte[] b) {
			String hs = "";
			String stmp = "";
			for (int n = 0; n < b.length; n++) {
				stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
				if (stmp.length() == 1)
					hs = hs + "0" + stmp;
				else
					hs = hs + stmp;
			}
			return hs.toUpperCase();
		}

		public static byte[] hex2byte(byte[] b) {
			if ((b.length % 2) != 0)
				throw new IllegalArgumentException("长度不是偶数");
			byte[] b2 = new byte[b.length / 2];
			for (int n = 0; n < b.length; n += 2) {
				String item = new String(b, n, 2);
				b2[n / 2] = (byte) Integer.parseInt(item, 16);
			}
			return b2;
		}
		
		/**
		 * 解密
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 */
		public final String decrypt(String data) {
			try {
				return new String(decrypt(hex2byte(data.getBytes()),CRYPT_KEY));
			} catch (Exception e) {
			}
			return null;
		}

		/**
		 * 加密
		 * 
		 * @param data
		 * @return
		 * @throws Exception
		 */
		public final String encrypt(String data) {
			try {
				return byte2hex(encrypt(data.getBytes(), CRYPT_KEY));
			} catch (Exception e) {
			}
			return null;
		}
		

		
		
	 

}
