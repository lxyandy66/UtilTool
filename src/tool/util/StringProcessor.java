package tool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class StringProcessor {
	/**
	 * 将普通字符串用16进制描述 如"WAZX-B55SY6-S6DT5"
	 * 描述为："57415a582d4235355359362d5336445435"
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String strToHex(String str) throws UnsupportedEncodingException {
		byte[] bytes = str.getBytes("UTF-8");
		return bytesToHex(bytes);
	}

	/**
	 * 将16进制描述的字符串还原为普通字符串 如"57415a582d4235355359362d5336445435"
	 * 还原为："WAZX-B55SY6-S6DT5"
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String hexToStr(String hex) throws UnsupportedEncodingException {
		byte[] bytes = hexToBytes(hex);
		return new String(bytes, "UTF-8").toUpperCase();
	}

	/** 16进制转byte[] */
	public static byte[] hexToBytes(String hex) {
		int length = hex.length() / 2;
		byte[] bytes = new byte[length];
		for (int i = 0; i < length; i++) {
			String tempStr = hex.substring(2 * i, 2 * i + 2);// byte:8bit=4bit+4bit=十六进制位+十六进制位
			bytes[i] = (byte) Integer.parseInt(tempStr, 16);
		}
		return bytes;
	}

	/** byte[]转16进制 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int tempI = bytes[i] & 0xFF;// byte:8bit,int:32bit;高位相与.
			String str = Integer.toHexString(tempI);
			if (str.length() < 2) {
				sb.append(0).append(str);// 长度不足两位，补齐：如16进制的d,用0d表示。
			} else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}

	public static String[] stringToArray(String str) {
		String[] temp = str.split("", str.length() + 1);
		String[] result = new String[temp.length - 1];
		for (int i = 0; i < result.length; i++)
			result[i] = temp[i + 1];
		return result;
	}

	public static String objectToBase64(Object obj) {
		return Base64.encode(objectToByte(obj));

	}

	public static Object base64ToObject(String str) throws Exception {
		return byteToObject(Base64.decode(str));
	}
	
	public static String byteToBase64(byte[] b){
		return Base64.encode(b);
	}
	
	public static byte[] base64ToByte(String s) {
		return Base64.decode(s);
	}

	/**
	 * 对象转byte
	 * 
	 * @param obj
	 * @return
	 */
	private static byte[] objectToByte(Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
	
			bytes = bo.toByteArray();
	
			bo.close();
			oo.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return bytes;
	}

	/**
	 * byte转对象
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object byteToObject(byte[] bytes) throws Exception {
		Object obj = null;
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		obj = oi.readObject();
		bi.close();
		oi.close();
		return obj;
	}
	
	/**@author Li
	 * @param array 输入的字符串数组
	 * @return 转换完成的多行段落
	 */
	public static String stringArrayToDialog(String[] array){
		String result="";
		for(String temp:array)
			result+=temp+"\n";
		return result;
	} 
	
	/**提供将字符串数组以制定的分割符号隔开输出为一个完整的字符串的方法
	 * 
	 * @param array	输入的字符串数组
	 * @param interv 所分隔字符串的符号
	 * @return
	 */
	public static String stringArrayToDialog(String[] array,String interv){
		String result="";
		for(String temp:array)
			result+=temp+interv;
		return result;
	}
	

	
	@Deprecated
	public static String rsaPublicKeyToBase64(RSAPublicKey pubKey) {
		return Base64.encode(objectToByte(pubKey));
	}

	@Deprecated
	public static String rsaPrivateKeyToBase64(RSAPrivateKey priKey) {
		return Base64.encode(objectToByte(priKey));
	}

	@Deprecated
	public static RSAPrivateKey base64ToRSAPrivateKey(String str) throws Exception {
		return byteToRSAPrivateKey(Base64.decode(str));
	}

	@Deprecated
	public static RSAPublicKey base64ToRSAPublicKey(String str) throws Exception {
		return byteToRSAPublicKey(Base64.decode(str));
	}

	@Deprecated
	private static RSAPublicKey byteToRSAPublicKey(byte[] bytes) throws Exception {
		RSAPublicKey obj = null;
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		obj = (RSAPublicKey) oi.readObject();
		bi.close();
		oi.close();
		return obj;
	}

	@Deprecated
	private static RSAPrivateKey byteToRSAPrivateKey(byte[] bytes) throws Exception {
		RSAPrivateKey obj = null;
		ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		obj = (RSAPrivateKey) oi.readObject();
		bi.close();
		oi.close();
		return obj;
	}
}
