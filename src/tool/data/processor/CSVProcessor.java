package tool.data.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 使用apache common实现
 * 
 * @author Mr_Li
 *
 */
public class CSVProcessor extends DataFileProcessor {

	// CSV文件分隔符
	// private final static String NEW_LINE_SEPARATOR = "\n";
	// 初始化csvformat
	private CSVFormat formator;
	private CSVParser parser;
	private FileReader fr;

	public void initFormator(String[] header) throws FileNotFoundException, IOException, Exception {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		formator = CSVFormat.DEFAULT.withHeader(header);
		parser = new CSVParser(fr, formator);
	}

	public Iterator<CSVRecord> getIterator() {
		return parser.iterator();
	}

	/**
	 * @param maxRow
	 * @param outputFiles
	 * @param thisTitleLength
	 * @param creator
	 * @throws Exception
	 */
	public <E> void splitDataFile(int maxRow, File[] outputFiles, int thisTitleLength, EntityCreator<E> creator)
			throws Exception {
		int ctrlLine = 0;
		// 控制输出的excel
		int ctrlOutput = 0;
		int totalProcess = 0;
		ExcelProcessor ep = new ExcelProcessor();
		ep.loadDataFile(outputFiles[ctrlOutput]);
		int titleControl = thisTitleLength;// 这里应该怎么优化一下
		for (CSVRecord r : parser.getRecords()) {
			if (titleControl-- > 0)
				continue;
			if (creator == null) {
//				ep.appendRow(r.toMap().values(), null);
			} else {
				ep.appendRow(creator.toStringArray(creator.getEntityFromObject(r)), ep.getCurrentSheet());
			}
			totalProcess++;
			ctrlLine++;
			if (ctrlLine >= maxRow && ctrlOutput + 1 < outputFiles.length) {
				// 超过行数控制且还有输出时切换输出控制excel
				ep.saveChange();
				ep.closeDataFile();
				ep.loadDataFile(outputFiles[++ctrlOutput]);
				ctrlLine = 0;
			}

		}
		ep.saveChange();
		ep.closeDataFile();
		System.out.println("分割完毕，共分割 " + totalProcess + " 条");
	}

	@Override
	public void loadDataFile(File f) throws Exception {
		// TODO Auto-generated method stub
		if (!isLegal(f))
			throw new IOException(this.ERROR_TYPE);
		this.dataFile = f;
		fr = new FileReader(f);

		// for dev
		System.out.println("Load CSV success: " + dataFile.getName());
	}

	@Override
	public void loadDataFile(String path) throws Exception {
		// TODO Auto-generated method stub
		loadDataFile(new File(path));
	}

	@Override
	public void closeDataFile() throws Exception {
		// TODO Auto-generated method stub
		parser.close();
		fr.close();
	}

	@Override
	public boolean isLoad() {
		// TODO Auto-generated method stub
		return fr != null;
	}

	@Override
	public void saveChange() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	boolean isLegal(File f) throws IOException {
		// TODO Auto-generated method stub
		return f.getName().toLowerCase().endsWith(".csv") && f.exists() && f.isFile();
	}

}
