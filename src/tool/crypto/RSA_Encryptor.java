package tool.crypto;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSA_Encryptor {

	private static final String RSA_FULL = "RSA/ECB/PKCS1Padding";
	private static final int BIT_LENGTH = 2048;
	private static final String RSA = "RSA";
	public static final String PUBLIC_KEY = "PUBLIC_KEY";
	public static final String PRIVATE_KEY = "PRIVATE_KEY";
	public static final BigInteger SHORT_POWER = new BigInteger("17");// 用短公开指数加速加密，就是e=17
	private static final int CERTAINTY = 200;
	private static SecureRandom random = new SecureRandom("Powered by L' 201301110216".getBytes());//



	/**
	 * 获取秘钥对，私钥关键字为PRIVATE_KEY，公钥关键字为PUBLIC_KEY
	 * 
	 * @author Mr_Li
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static Map<String, byte[]> generateKeyPair(byte[] seed) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
		generator.initialize(BIT_LENGTH, new SecureRandom(seed));
		KeyPair oriKeyPair = generator.generateKeyPair();

		RSAPublicKey pubKey = (RSAPublicKey) oriKeyPair.getPublic();
		RSAPrivateKey priKey = (RSAPrivateKey) oriKeyPair.getPrivate();
		
//		System.out.println("纯object:  "+StringProcessor.objectToBase64(pubKey));    这两个是不同的
//		System.out.println("encode:  "+StringProcessor.byteToBase64(pubKey.getEncoded()));
		
		// System.out.println(pubKey.getFormat()); X.509
		// System.out.println(priKey.getFormat()); PKCS#8
		Map<String, byte[]> keyPair = new HashMap<String, byte[]>();
		keyPair.put(PUBLIC_KEY, pubKey.getEncoded());
		keyPair.put(PRIVATE_KEY, priKey.getEncoded());

		// return getDistribKeyPair(pubKey, priKey);
		return keyPair;
	}
	
	

	private static byte[] encrypt(byte[] msg, PublicKey pubKey)
			throws IllegalBlockSizeException, BadPaddingException, Exception {
		Cipher c = Cipher.getInstance(RSA_FULL);
		c.init(Cipher.ENCRYPT_MODE, pubKey);
		return c.doFinal(msg);
	}

	private static byte[] decrypt(byte[] msg, PrivateKey prvKey)
			throws IllegalBlockSizeException, BadPaddingException, Exception {
		Cipher c = Cipher.getInstance(RSA_FULL);
		c.init(Cipher.DECRYPT_MODE, prvKey);
		return c.doFinal(msg);
	}

	public static byte[] encrypt(byte[] msg, byte[] pubKey) throws Exception {
		return encrypt(msg, restorePublicKey(pubKey));
	}

	public static byte[] decrypt(byte[] msg, byte[] prvKey) throws Exception {
		return decrypt(msg, restorePrivateKey(prvKey));
	}

	private static PublicKey restorePublicKey(byte[] pubKey) throws InvalidKeySpecException, Exception {
		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(pubKey);
		KeyFactory factory = KeyFactory.getInstance(RSA);
		PublicKey publicKey = factory.generatePublic(x509Spec);
		return publicKey;
	}

	private static PrivateKey restorePrivateKey(byte[] prvKey) throws Exception {
		PKCS8EncodedKeySpec pkcsSpec = new PKCS8EncodedKeySpec(prvKey);
		KeyFactory factory = KeyFactory.getInstance(RSA);
		PrivateKey privateKey = factory.generatePrivate(pkcsSpec);
		return privateKey;
	}

	public static BigInteger genaratePrime() {// 给外面的直接调用生成
		return BigInteger.probablePrime(BIT_LENGTH, random);
	}
	
	public static boolean isTwoPrime(BigInteger a, BigInteger b) {
		return a.isProbablePrime(CERTAINTY) && b.isProbablePrime(CERTAINTY);
	}
}
