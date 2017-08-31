package tool.audio.io;

import java.io.File;
import java.io.FileOutputStream;

public class FormatConverter {
	public static final String RIFF = "RIFF";
	public static final byte[] byte_RIFF = RIFF.getBytes();

	public static final String WAVE = "WAVE";
	public static final byte[] byte_WAVE = WAVE.getBytes();

	public static final String fmt = "fmt";
	public static final byte[] byte_fmt = fmt.getBytes();
	
	public static final String DATA = "data";
	public static final byte[] byte_data = DATA.getBytes();

	public static final byte[] signal = { 12, 0, 0, 0, 06, 0, 01, 0, 40, 0x1F, 0, 0, 40, 0x1F, 0, 0, 01, 0, 8, 0, 0, 0,
			66, 61, 63, 74, 04, 0, 0, 0, (byte) 0xF4, 66, 04, 0 };
	
	/**
	 * @deprecated
	 * @param outputFile
	 * @param input
	 * @throws Exception
	 */
	public static void convertAUtoWAVE(File outputFile,byte[] input) throws Exception{
		FileOutputStream fos=new FileOutputStream(outputFile,false);
		System.out.println("Output: "+outputFile.getAbsolutePath());
		fos.write(byte_RIFF);
		fos.write(input.length+41);
		fos.write(byte_WAVE);
		fos.write(byte_fmt);
		for(byte temp:signal)
			fos.write(temp);
		fos.write(byte_data);
		fos.write(input);
		fos.flush();
		fos.close();
	}
}
