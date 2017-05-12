/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved. 
 *====================================================
 * 文件名称: DESUtil.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2010-3-28			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.util;

import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @ClassName: DESUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenxy
 * @date 2013-8-26 上午11:14:08
 */
public class DESUtil {

	private final static String DES = "DES";

	private byte[] keyBytes;

	private Cipher ecipher;

	private Cipher dcipher;

	private Cipher initCipher(int opmode) {
		Cipher cipher = null;
		try {
			KeyGenerator generator = KeyGenerator.getInstance(DES);
			generator.init(new SecureRandom(keyBytes));
			Key key = generator.generateKey();
			cipher = Cipher.getInstance("DES");
			cipher.init(opmode, key);
			generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipher;
	}

	public DESUtil(String key) {
		keyBytes = ByteUtil.hexString2HexBytes(ByteUtil.bytes2HexString(key.getBytes()));
		ecipher = initCipher(Cipher.ENCRYPT_MODE);
		dcipher = initCipher(Cipher.DECRYPT_MODE);
	}

	/**
	 * 加密,将明文字符串加密转成HexString密文输出
	 * @param proclaim
	 * @return
	 * @author:chenxy
	 */
	public String encrypt(String proclaim) {
		try {
			byte[] encHexBytes = ByteUtil.hexString2HexBytes(ByteUtil.bytes2HexString(proclaim.getBytes()));
			byte[] enc = ecipher.doFinal(encHexBytes);
			return ByteUtil.bytes2HexString(enc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密,将明文Hex字节数组加密转成HexString密文输出
	 * @param encHexBytes
	 * @return
	 * @author:chenxy
	 */
	public String encryptHexBytes(byte[] encHexBytes) {
		try {
			byte[] enc = ecipher.doFinal(encHexBytes);
			return ByteUtil.bytes2HexString(enc);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密,将HexString密文解密转成明文字符串输出
	 * @param cryptogram
	 * @return
	 * @author:chenxy
	 */
	public String decrypt(String cryptogram) {
		try {
			byte[] decHexBytes = ByteUtil.hexString2HexBytes(cryptogram);
			byte[] dec = dcipher.doFinal(decHexBytes);
			return new String(dec);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密,将Hex字节数组密文解密转成明文字符串输出
	 * @param decHexBytes
	 * @return
	 * @author:chenxy
	 */
	public String decryptHexBytes(byte[] decHexBytes) {
		try {
			byte[] dec = dcipher.doFinal(decHexBytes);
			return new String(dec);
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description 根据键值进行加密
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) {
		try {
			if (data == null || key == null) {
				return null;
			}
			byte[] bt = encrypt(data.getBytes(), key.getBytes());
			String strs = new BASE64Encoder().encode(bt).replaceAll("\r\n", "");
			return strs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description 根据键值进行解密
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) {
		try {
			if (data == null || key == null) {
				return null;
			}
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] buf = decoder.decodeBuffer(data);
			byte[] bt = decrypt(buf, key.getBytes());
			return new String(bt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description 根据键值进行加密
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] data, byte[] key) {
		try {
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance(DES);
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description 根据键值进行解密
	 * @param data
	 * @param key 加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] data, byte[] key) {
		try {
			// 生成一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);
			// Cipher对象实际完成解密操作
			Cipher cipher = Cipher.getInstance(DES);
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			return cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public static void main(String[] args) {
//		String l = "{\"id\":265,\"username\":\"????\",\"timestamp\":\"20140814171938\",\"sign\":\"2104360a70790643b3bc8c2c0c49489d\"}";
//		System.out.println(DESUtil.encrypt(l, Constant.generateKey));
//	}

}
