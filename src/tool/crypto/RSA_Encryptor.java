package tool.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import tool.util.StringProcessor;

public class RSA_Encryptor {
	/*
	 * 这个类用于实际按照业界标准的X509EncodedKeySpec以及PKCS8EncodedKeySpec作为公私钥的分发以及使用
	 */

	public static final BigInteger SHORT_POWER = new BigInteger("17");// 用短公开指数加速加密，就是e=17
	private static final int BIT_LENGTH = 1024;
	private static final int CERTAINTY = 200;
	private static final String RSA_ALGORITHM = "RSA";
	// 所以要不要成员变量???

	private static SecureRandom random = new SecureRandom("Powered by L' 201301110216".getBytes());//

	public static KeyPair generateKeyPair(byte[] seed) throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		generator.initialize(BIT_LENGTH, new SecureRandom(seed));
		return generator.generateKeyPair();
	}

	public static byte[] encryptData(byte[] data, String base64PubKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(base64PubKey));/*WTMSB这里直接用base64解出来了get啥DerInputStream.getLength(): lengthTag=109, too big.*/
		cipher.init(Cipher.ENCRYPT_MODE, (RSAPublicKey)StringProcessor.base64ToObject(base64PubKey));
		return cipher.doFinal(data);
	}

	public static byte[] decryptData(byte[] data, String base64PrvKey)
			throws Exception {
		Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//		cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(base64PrvKey));/*WTMSB这里直接用base64解出来了get啥DerInputStream.getLength(): lengthTag=109, too big.*/
		cipher.init(Cipher.DECRYPT_MODE, (RSAPrivateKey)StringProcessor.base64ToObject(base64PrvKey));
		return cipher.doFinal(data);
	}

	public static BigInteger genaratePrime() {// 给外面的直接调用生成
		return BigInteger.probablePrime(BIT_LENGTH, random);
	}

	public static RSAPublicKey getPublicKey(String base64PubKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec keySpace = new X509EncodedKeySpec(Base64.decode(base64PubKey));
		KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);
		return (RSAPublicKey) factory.generatePublic(keySpace);/*不是这里的问题...!!!报错：DerInputStream.getLength(): lengthTag=109, too big.!!!*/
		//看网上说是证书和私钥的问题，未解决
	}

	public static RSAPrivateKey getPrivateKey(String base64PrvKey)//所以这个base64给的到底是个啥??privateKey？
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		PKCS8EncodedKeySpec keySpace = new PKCS8EncodedKeySpec(Base64.decode(base64PrvKey));
		KeyFactory factory = KeyFactory.getInstance(RSA_ALGORITHM);/*也不是这里的问题...!!!同样解密也会报错DerInputStream.getLength(): lengthTag=109, too big.!!!*/
		return (RSAPrivateKey) factory.generatePrivate(keySpace);
	}

	/**
	 * 这个方法直接用来从已变为Base64格式的RSAPublicKey字符串恢复到对象
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static RSAPublicKey getPublicKeyFromBase64(String str) throws Exception {
		return (RSAPublicKey) StringProcessor.byteToObject(Base64.decode(str));
	}

	public static String Encrypt(String msg, String pubKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		RSAPublicKey publicKey = (RSAPublicKey) StringProcessor.base64ToObject(pubKey);// StringProcessor.base64ToRSAPublicKey(pubKey);
		return Encrypt(msg, publicKey);
	}

	public static String Encrypt(String msg, RSAPublicKey publicKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encode(c.doFinal(msg.getBytes("UTF-8")));
	}// 真正从publicKey来处理

	public static String Encrypt(String msg, RSAPrivateKey privateKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.ENCRYPT_MODE, privateKey);
		return Base64.encode(c.doFinal(msg.getBytes("UTF-8")));
	}// 仅用于签名

	public static String Decrypt(String msg, String prvKey) throws Exception {
		return Decrypt(msg, (RSAPrivateKey) StringProcessor.base64ToObject(prvKey));
	}

	public static String Decrypt(String msg, RSAPrivateKey prvKey) throws Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, prvKey);
		return new String(c.doFinal(Base64.decode(msg)), "UTF-8");
	}// 真正直接从privateKey处理

	public static String Decrypt(String msg, RSAPublicKey pubKey) throws Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, pubKey);
		return new String(c.doFinal(Base64.decode(msg)), "UTF-8");
	}// 仅用于验证签名

	public static boolean isTwoPrime(BigInteger a, BigInteger b) {
		return a.isProbablePrime(CERTAINTY) && b.isProbablePrime(CERTAINTY);
	}

	public static String intToBinary(BigInteger bigInt, int radix) {
		return new BigInteger(1, bigInt.toByteArray()).toString(radix);// 这里的1代表正数
	}

	public static BigInteger squareMultiply(BigInteger power, BigInteger base, BigInteger modBase) {
		BigInteger result = base;
		String powerBinString = intToBinary(power, 2);
		System.out.println(powerBinString);
		// 还有点问题
		for (int i = powerBinString.length() - 2; i >= 0; i--) {
			result = result.multiply(result);
			result = result.mod(modBase);
			if (powerBinString.charAt(i) == '1') {
				result = result.multiply(base);
				result = result.mod(modBase);
			}
		}
		return result.mod(modBase);
	}

}
