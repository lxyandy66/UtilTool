package tool.data.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class DataFileProcessor {
	
	protected final String ERROR_TYPE = "文件格式错误";
	protected final String ERROR_EMPTY = "文件为空";
	protected final String ERROR_GENERAL_ON_LOAD = "文件错误";
	protected final String ERROR_NOT_LOAD = "尚未加载文件";
	protected final String ERROR_EXCEED = "超出范围";
	protected final String ERROR_NOT_FILE="不是文件";
	
	protected File dataFile;
	protected FileInputStream dataFis;
	
	abstract void loadDataFile(File f) throws Exception;

	abstract void loadDataFile(String path) throws Exception;

	abstract void closeDataFile() throws Exception;

	abstract boolean isLoad();

	abstract void saveChange() throws FileNotFoundException, IOException;
	
	abstract boolean isLegal(File f) throws IOException;
}
