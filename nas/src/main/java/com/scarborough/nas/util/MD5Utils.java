package com.scarborough.nas.util;





import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用md5的方式对密码进行加密, 这是一个不可逆的加密算法
 */
public class MD5Utils {

	/**
	 * 私有构造方法
	 */
	private MD5Utils() {

	}

	public static String md5(String plainText) {
		byte[] secretBytes = null;

		try {
			secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		String md5Code = new BigInteger(1, secretBytes).toString(16);

		for (int i = 0; i < 32 - md5Code.length(); i++) {
			md5Code += "0";
		}

		return md5Code;
	}
}
