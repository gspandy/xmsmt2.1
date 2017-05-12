/**
 *====================================================
 * 文件名称: QRBarCodeUtil.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2014年9月10日			chenxy(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 */
package net.wit.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @ClassName: QRBarCodeUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2014年9月10日 上午9:25:34
 */
public class QRBarCodeUtil {

	private static final String FORMAT = "PNG";

	/**
	 * 生成条形码<br>
	 * <b>注意</b>条形码的宽度不能等于图片的宽度，否则解析不出来,如果解析不出来，请加大offset的值
	 * @param contents 内容
	 * @param dest 条形码图片地址
	 * @param width 宽度
	 * @param height 高度
	 * @param offset 偏移量
	 * @throws WriterException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void encodeBarCode(String contents, int width, int height, int offset, OutputStream stream) throws WriterException, FileNotFoundException, IOException {
		contents = new String(contents.getBytes("UTF-8"), "ISO-8859-1");
		BitMatrix matrix = new MultiFormatWriter().encode(contents, BarcodeFormat.CODE_128, width - offset, height);
		MatrixToImageWriter.writeToStream(matrix, FORMAT, stream);
	}

	public static void encodeBarCode(String contents, String dest, int width, int height, int offset) throws WriterException, FileNotFoundException, IOException {
		encodeBarCode(contents, width, height, offset, new FileOutputStream(new File(dest)));
	}

	/**
	 * 解析条形码
	 * @param dest 要解码的图片地址
	 * @return String 条形码内容
	 * @throws IOException
	 * @throws NotFoundException
	 */
	public static String decodeBarCode(String dest) throws IOException, NotFoundException {
		BufferedImage image = ImageIO.read(new File(dest));
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap imageBinaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result = new MultiFormatReader().decode(imageBinaryBitmap, null);
		return result.getText();
	}

	/**
	 * 以条形码 693 69838 0001 3 为例<br>
	 * 此条形码分为4个部分，从左到右分别为：<br>
	 * 1-3位：共3位，对应该条码的693，是中国的国家代码之一。（690--695都是中国大陆的代码，由国际上分配）<br>
	 * 4-8位：共5位，对应该条码的69838，代表着生产厂商代码，由厂商申请，国家分配<br>
	 * 9-12位：共4位，对应该条码的0001，代表着厂内商品代码，由厂商自行确定<br>
	 * 第13位：共1位，对应该条码的3，是校验码，依据一定的算法，由前面12位数字计算而得到。<br>
	 * （公式第13位算法<br>
	 * 1：取出该数的奇数位的和，c1=6+3+9+3+0+0=21；<br>
	 * 2：取出该数的偶数位的和，c2=9+6+8+8+0+1=32；<br>
	 * 3：将奇数位的和与“偶数位的和的三倍”相加。<br>
	 * 4：取出结果的个位数：117（117%10=7）；<br>
	 * 5：用10减去这个个位数：10-7=3；<br>
	 * 6：对得到的数再取个位数（对10去余）3%10=3；<br>
	 * 参考：<a href="http://baike.baidu.com/view/13740.htm?fr=aladdin">百度百科-条形码</a>
	 * @return String 校验码
	 * @throws Exception
	 */
	public static String checksumBarCode(String countryCode, String factoryCode, String productCode) throws Exception {
		String temp = countryCode + factoryCode + productCode;
		if (!(isNumber(countryCode) && isNumber(factoryCode) && isNumber(productCode))) {
			throw new Exception("不能含有非数字字符");
		}
		if (countryCode.length() != 3) {
			throw new Exception("国家地区代码不合规范,必须3位");
		}
		if (factoryCode.length() != 5) {
			throw new Exception("厂商代码不合规范,必须5位");
		}
		if (productCode.length() != 4) {
			throw new Exception("产品代码不合规范,必须4位");
		}
		char[] code = temp.toCharArray();

		int oddSum = 0;
		int evenSum = 0;
		for (int i = 0; i < code.length; i++) {
			if ((i + 1) % 2 == 1) {
				oddSum += Integer.valueOf(code[i] + "");
			} else {
				evenSum += Integer.valueOf(code[i] + "");
			}
		}
		int digit = (10 - ((oddSum + evenSum * 3) % 10)) % 10;

		return temp + digit;
	}

	/**
	 * 校验数字
	 * @param number 数字
	 * @return Boolean
	 */
	public static boolean isNumber(String number) {
		if (null == number || "".equals(number))
			return false;
		String regex = "[0-9]*";
		return number.matches(regex);
	}

	/**
	 * 生成二维码
	 * @param contents 内容，换行可以用\n
	 * @param dest 生成二维码图片地址
	 * @param width 宽度
	 * @param height 高度
	 * @throws WriterException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void encodeQRCode(String contents, String dest, int width, int height) throws WriterException, FileNotFoundException, IOException {
		contents = new String(contents.getBytes("UTF-8"), "ISO-8859-1");
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix matrix = writer.encode(contents, BarcodeFormat.QR_CODE, width, height);
		MatrixToImageWriter.writeToStream(matrix, FORMAT, new FileOutputStream(new File(dest)));
	}

	/**
	 * 从一张图片解析出二维码信息
	 * @param dest 目标地址
	 * @return String 二维码信息
	 * @throws IOException
	 * @throws NotFoundException
	 * @throws ChecksumException
	 * @throws FormatException
	 */
	public static String decodeQRCode(String dest) throws IOException, NotFoundException, ChecksumException, FormatException {
		QRCodeReader reader = new QRCodeReader();
		BufferedImage image = ImageIO.read(new File(dest));
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		Binarizer binarizer = new HybridBinarizer(source);
		BinaryBitmap imageBinaryBitmap = new BinaryBitmap(binarizer);
		Result result = reader.decode(imageBinaryBitmap);
		return result.getText();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("校验并写入：" + checksumBarCode("695", "32321", "2133"));
		QRBarCodeUtil.encodeBarCode(checksumBarCode("695", "32321", "2133"), "D:\\Logger_Files\\code_bar.png", 500, 50, 20);
		System.out.println("解析结果:" + QRBarCodeUtil.decodeBarCode("D:\\Logger_Files\\code_bar.png"));

		QRBarCodeUtil.encodeQRCode("http://www.baidu.com/", "D:\\Logger_Files\\code_qr.png", 200, 200);
		System.out.println(QRBarCodeUtil.decodeQRCode("D:\\Logger_Files\\code_qr.png"));
	}

}
