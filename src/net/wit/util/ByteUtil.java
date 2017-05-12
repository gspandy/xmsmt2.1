/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2010 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved. 
 *====================================================
 * 文件名称: ByteUtil.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2010-3-12			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 * 
 */
package net.wit.util;

/**
 * @ClassName: ByteUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author chenxy
 * @date 2013-8-26 上午11:14:51
 */
public class ByteUtil {

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * @param src0
	 * @param src1
	 * @return
	 * @author:chenxy
	 */
	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
	 * @param src String
	 * @return
	 * @author:chenxy
	 */
	public static byte[] hexString2HexBytes(String src) {
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < src.length() / 2; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * 将16进制的字节数组转成HexStr输出，如byte[]{0x51, (byte) 0xCB, 0x36, (byte) 0xE2 } --> "51CB36E2"
	 * @param b
	 * @return
	 * @author:chenxy
	 */
	public static String hexBytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/** bytes转换成十六进制字符串 */
	public static String bytes2HexString(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static void main(String[] args) {
		String encryptStr = "00008000#snosBPjPiG4y66LIoqj0wn0F";
		hexString2HexBytes(encryptStr);
	}

}
