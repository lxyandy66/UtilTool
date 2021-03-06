package tool.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import tool.util.StringProcessor;

public class HashProcessor {

	public static final String[] method = { "MD5", "SHA-1", "SHA-256" };
	public static final int MD5 = 0;
	public static final int SHA1 = 1;
	public static final int SHA256 = 2;

	/**
	 * 计算文件的Hash值
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String getHash(File file, int methodSelect) throws Exception {
		if (!isLegalArg(methodSelect))
			throw new Exception("方法参数错误!");
		MessageDigest md = MessageDigest.getInstance(method[methodSelect]);
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int length = -1;
		while ((length = fis.read(buffer)) != -1) {
			md.update(buffer, 0, length);
		}
		fis.close();
		return StringProcessor.bytesToHex(md.digest());
	}

	/**
	 * 计算字符串的Hash值
	 * 
	 * @param str
	 *            method
	 * @return
	 * @throws Exception
	 */
	public static String getHash(String str, int methodSelect) throws Exception {
		if (!isLegalArg(methodSelect))
			throw new Exception("方法参数错误!");
		byte[] bt = str.getBytes();
		MessageDigest md = MessageDigest.getInstance(method[methodSelect]);
		md.update(bt);
		return StringProcessor.bytesToHex(md.digest());
	}

	private static boolean isLegalArg(int methodSelect) {
		return (methodSelect >= 0 && methodSelect <= method.length - 1);
	}

	/**
	 * 计算文件的Hash值,允许值为MD5，SHA-1，SHA-256。
	 * 
	 * @param file
	 * @param m
	 *            指定的哈希算法
	 * @return
	 * @throws Exception
	 */
	public static String getHash(File file, String m) throws Exception {
		if (getMethodNum(m) == -1)
			throw new Exception("方法参数错误!");
		return getHash(file, getMethodNum(m));
	}

	/**
	 * 计算字符串的Hash值,允许值为MD5，SHA-1，SHA-256。
	 * 
	 * @param str
	 *            method
	 * @param m
	 *            指定的哈希算法
	 * @return
	 * @throws Exception
	 */
	public static String getHash(String str, String m) throws Exception {
		if (getMethodNum(m) == -1)
			throw new Exception("方法参数错误!");
		return getHash(str, getMethodNum(m));
	}

	/**
	 * 用以检测输入的哈希算法是否被支持，允许值为MD5，SHA-1，SHA-256。
	 * 
	 * @param arg
	 *            输入的哈希算法
	 * 
	 * @return
	 */
	public static boolean isLegalArg(String arg) {
		for (String temp : method)
			if (!arg.equals(temp))
				return false;
		return true;
	}

	/**
	 * 返回哈希方法所对应的序号
	 * 
	 * @param m
	 * @return
	 */
	public static int getMethodNum(String m) {
		for (int i = 0; i < method.length; i++)
			if (m.equals(method[i]))
				return i;
		return -1;
	}

}
