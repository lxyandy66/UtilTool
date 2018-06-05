package tool.data.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelProcessor extends DataFileProcessor {

	/**
	 * POI中实际excel处理对象,需加载
	 * 
	 */
	private Workbook wb;
	private Sheet st;
	private int sheetCount;

	public ExcelProcessor() {
	}

	public void loadDataFile(String filePath) throws Exception {
		loadDataFile(new File(filePath));
	}

	/**
	 * 加载需要处理的excel文件
	 * 
	 * @param f
	 *            需要加载的excel文件
	 * @throws Exception
	 */
	public void loadDataFile(File f) throws Exception {
		if (!isLegal(f))
			throw new IOException(ERROR_GENERAL_ON_LOAD);
		String[] type = f.getName().split("\\.");
		dataFile = f;
		dataFis = new FileInputStream(dataFile);
		if (type[type.length - 1].equals("xls")) {
			wb = new HSSFWorkbook(dataFis);
		} else if (type[type.length - 1].equals("xlsx")) {
			wb = new XSSFWorkbook(dataFis);
		} else
			throw new IOException(ERROR_GENERAL_ON_LOAD);
		st = wb.getSheetAt(0);// 默认获取第1个sheet
		wb.setActiveSheet(0);// 默认第一个Sheet为活动
		sheetCount = wb.getNumberOfSheets();

		// on develop
		System.out.println("Load success : " + dataFile.getName());
	}

	public void loadDataFile(File f, boolean isCreatFile, boolean isXlsxFile) throws Exception {
		if (!isCreatFile)
			loadDataFile(f);
		f.createNewFile();
		this.dataFile = f;
		this.dataFis = new FileInputStream(f);
		this.wb = isXlsxFile ? new XSSFWorkbook() : new HSSFWorkbook();
		st = isCreatFile ? wb.createSheet() : this.wb.getSheetAt(0);
		sheetCount = wb.getNumberOfSheets();
	}

	public void closeDataFile() throws IOException {
		wb.close();
		wb = null;
		st = null;
		dataFis.close();
		dataFis = null;
		dataFile = null;
	}

	public int getSheetCount() {
		return sheetCount;
	}

	public Workbook getCurrentWorkBook() {
		return wb;
	}

	public Sheet getCurrentSheet() {
		return st;
	}

	public void setCurrentSheet(Sheet s) {
		this.st = s;
	}

	/**
	 * 获取序号对应的Sheet
	 * 
	 * @param stNum
	 *            Excel实际序号-1
	 * @return
	 * @throws Exception
	 */
	public Sheet getSheetAt(int stNum) throws Exception {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if (stNum > sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum);
	}

	/**
	 * 切换当前活动的工作表
	 * 
	 * @param stNum
	 * @throws Exception
	 */
	public void setCurrentSheet(int stNum) throws Exception {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if (stNum > sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		this.st = wb.getSheetAt(stNum);
		wb.setActiveSheet(stNum);
	}

	public static Cell getCell(Row r, int col) throws IOException {
		return getCell(r.getSheet(), r.getRowNum(), col);
	}

	public static Cell getCell(Sheet st, int row, int column) throws IOException {
		int sheetMergeCount = st.getNumMergedRegions();
		if (sheetMergeCount <= 0)
			return st.getRow(row).getCell(column);
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = st.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = st.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return fCell;
				}
			}
		}
		return st.getRow(row).getCell(column);
	}

	/**
	 * 获取当前工作表的单元格值，如参数为(1,1)，获取为B2单元格值
	 * 
	 * @param rowNum
	 *            行号，为excel中实际行号-1
	 * @param colNum
	 *            列号，为excel中实际列号-1
	 * @return
	 */
	public Cell getCell(int rowNum, int colNum) throws IOException {
		return st.getRow(rowNum).getCell(colNum);
	}

	/**
	 * 获取特定工作表的单元格值，如参数为(0,1,1)，获取为Sheet1中B2单元格值
	 * 
	 * @param stNum
	 *            工作表号，为excel中实际工作表号-1
	 * @param rowNum
	 *            行号，为excel中实际行号-1
	 * @param colNum
	 *            列号，为excel中实际列号-1
	 * @return
	 * @throws IOException
	 */
	public Cell getCell(int stNum, int rowNum, int colNum) throws IOException, IllegalArgumentException {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if (stNum > sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum).getRow(rowNum).getCell(colNum);
	}

	public Row getRow(int stNum, int rowNum) throws IOException {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if (stNum > sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum).getRow(rowNum);
	}

	public Row getRow(Sheet st, int rowNum) throws IOException {
		if (!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		return st.getRow(rowNum);
	}

	/**
	 * 将某一行所有单元格的数据转换为数组
	 * 
	 * @param stNum
	 * @param rowNum
	 * @return
	 */
	public String[] getRowValue(int stNum, int rowNum) {
		ArrayList<String> array = new ArrayList<String>();
		for (Cell c : wb.getSheetAt(stNum).getRow(rowNum))
			array.add(c.getStringCellValue());
		return (String[]) array.toArray();
	}

	public static String[] getRowValue(Row r) {
		ArrayList<String> array = new ArrayList<String>();
		r.forEach(c -> {
			try {
				array.add(c.getStringCellValue());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				array.add(String.format("%.4f", c.getNumericCellValue()));
			}
		});
		return array.toArray(new String[array.size()]);
	}

	/**
	 * 将数组按顺序逐个追加到行中
	 * 
	 * @param s
	 * @param st
	 *            追加到的Sheet
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void appendRow(String[] s, Sheet st) {
		if(s==null)
			return;
		if (st == null)
			st = this.getCurrentSheet();
		Row r = st.createRow(st.getLastRowNum() + 1);
		for (int i = 0; i < s.length; i++) {
			r.createCell(i);
			r.getCell(i).setCellValue(s[i]);
		}
	}

	
	/**增加标题栏，注意，工作表的首行若有内容将被覆盖
	 * @param title 标题栏内容
	 * @param st 增加标题栏的工作表，为空则为当前活动的工作表
	 */
	public void appendTitle(String[] title, Sheet st) {
		if(title==null)
			return;
		if (st == null)
			st = this.getCurrentSheet();
		Row r = st.createRow(0);
		for (int i = 0; i < title.length; i++) {
			r.createCell(i);
			r.getCell(i).setCellValue(title[i]);
		}
	}
	

	/**
	 * 通过接口类RowProcessor对源文件目录下所有的Excel进行处理 典型应用如多个原始Excel文件需要统一追加到一个新Excel文件并进行格式处理
	 * 
	 * @param dir
	 *            含多个原始Excel文件
	 * @param titleLength
	 *            原始文件标题栏长度
	 * @param rp 实现对数据源Excel文件行进行处理的接口
	 * @throws Exception
	 */
	public void appendFile(File[] dir, int titleLength, RowProcessable rp) throws Exception {
		ExcelProcessor epSource = new ExcelProcessor();
		Sheet st;
		for (File f : dir) {
			if (!isLegal(f))
				continue;
			epSource.loadDataFile(f);
			for (int i = 0; i < epSource.getSheetCount(); i++) {
				st = epSource.getSheetAt(i);
				for (int j = titleLength; j < st.getLastRowNum(); j++)
					rp.processRow(st.getRow(j), i);// 对单独的行进行处理
			}
			epSource.closeDataFile();
		}
	}

	/**对当前加载的Excel每个工作表每行进行遍历，可忽略指定行数的标题栏
	 * @param titleLength 标题栏行数
	 * @param rp
	 * @throws Exception
	 */
	public void iterateExcel(int titleLength, RowProcessable rp) throws Exception {
		int i = 0;
		for (Sheet st : this.getCurrentWorkBook()) {
			for (int j = titleLength; j < st.getLastRowNum(); j++)
				rp.processRow(st.getRow(j), i);
		}
	}

	public void saveChange() throws FileNotFoundException, IOException {
		wb.write(new FileOutputStream(dataFile));
	}

	public boolean isLoad() {
		return dataFis != null && wb != null;
	}

	protected boolean isLegal(File f) {
		String[] type = f.getName().split("\\.");
		if (f == null || !f.exists()) {
			System.out.println(ERROR_EMPTY);
			return false;
		}
		if (!f.isFile()) {
			System.out.println(f.getName() + ERROR_NOT_FILE);
			return false;
		}
		if (!type[type.length - 1].equals("xls") && !type[type.length - 1].equals("xlsx")) {
			System.out.println(f.getName() + ERROR_TYPE);
			return false;
		}
		return true;
	}

	/**
	 * 将本对象加载的Excel文件按最大行数maxRow分割为多个Excel小文件
	 * 
	 * @param maxRow
	 *            分割出的小Excel最大行数
	 * @param outputFiles
	 *            输出文件列表，本方法不处理其标题栏
	 * @param thisTitleLength
	 *            本文件的标题长度
	 * @param creator
	 *            需要对原始Excel文档进行处理实现的接口类
	 * @throws Exception
	 */
	public <E> void splitDataFile(int maxRow, File[] outputFiles, int thisTitleLength, EntityCreatable<E> creator)
			throws Exception {
		int[] ctrlCount=new int[] {0,0,0};//ctrlLine,ctrlOutput,totalProcess
		ExcelProcessor ep = new ExcelProcessor();
		ep.loadDataFile(outputFiles[ctrlCount[1]]);// 目标Excel
		this.iterateExcel(thisTitleLength, new RowProcessable() {
			
			@Override
			public void processRow(Row r, int sheetNum) throws Exception {
				// TODO Auto-generated method stub
				if (creator == null) {
					ep.appendRow(ExcelProcessor.getRowValue(r), null);
				} else
					ep.appendRow(creator.toStringArray(creator.getEntityFromObject(r)),
							ep.getCurrentSheet());
				ctrlCount[2]++;
				ctrlCount[0]++;
				if (ctrlCount[0] >= maxRow && ctrlCount[1] + 1 < outputFiles.length) {
					// 超过行数控制且还有输出时切换输出控制excel
					ep.saveChange();
					ep.closeDataFile();
					ep.loadDataFile(outputFiles[++ctrlCount[1]]);
					ctrlCount[0] = 0;
				}
			}
		});
		ep.saveChange();
		ep.closeDataFile();
		System.out.println("分割完毕，共分割 " + ctrlCount[2] + " 条");
	}

	/** 将本对象加载的Excel文件按最大行数maxRow分割为多个Excel小文件，自动生成分割后Excel小文件
	 * @param maxRow 单个文件的最大行数(含标题行)
	 * @param outputDir 输出的目录，需为文件夹
	 * @param isXlsxFile
	 * @param targetTitle
	 * @param thisTitleLength
	 * @param creator
	 * @throws Exception
	 */
	public <E> void splitDataFile(int maxRow, File outputDir, boolean isXlsxFile, String[] targetTitle,
			int thisTitleLength, EntityCreatable<E> creator) throws Exception {
		if(!outputDir.isDirectory())
			throw new IOException(ERROR_NOT_DIR);
		int[] ctrlCount = new int[] { 0, 0, 1 };//lineCtrl,totalCtrl,fileCtrl 实际上相当于单独三个变量，但是考虑到内部类的问题
		String[] targetFileName = { outputDir.getAbsolutePath() + "/" + this.dataFile.getName().split("\\.")[0] + "_",
				(isXlsxFile ? ".xlsx" : ".xls") };
		File targetFile = new File(targetFileName[0] + ctrlCount[2] + targetFileName[1]);
		ExcelProcessor epTarget = new ExcelProcessor();
		epTarget.loadDataFile(targetFile, true, isXlsxFile);
		epTarget.appendTitle(targetTitle, null);// 添加标题
		RowProcessable rp = new RowProcessable() {

			@Override
			public void processRow(Row r, int sheetNum) throws Exception {
				// TODO Auto-generated method stub
				if (creator == null)
					epTarget.appendRow(ExcelProcessor.getRowValue(r), null);
				else
					epTarget.appendRow(creator.toStringArray(creator.getEntityFromObject(r)), null);
				ctrlCount[1]++;
				if ((++ctrlCount[0]) >= maxRow && r.getRowNum() < r.getSheet().getLastRowNum()) {
					epTarget.saveChange();
					epTarget.closeDataFile();
					epTarget.loadDataFile(new File(targetFileName[0] + ++ctrlCount[2] + targetFileName[1]), true,
							isXlsxFile);
					epTarget.appendTitle(targetTitle, null);
					ctrlCount[0] = 0;
				}
			}
		};
		this.iterateExcel(thisTitleLength, rp);
		System.out.println("分割完毕，共分割  " + ctrlCount[1] + " 条数据");
		epTarget.saveChange();
		epTarget.closeDataFile();
	}

	
	/**
	 * 按Excel内容处理为HashMap格式,仅支持未合并的单元格
	 * 
	 * @param ep
	 *            当前加载了Excel的对象
	 * @param titleLength
	 *            当前Excel标题栏长度
	 * @param keyLine
	 *            key值所在列，0-base
	 * @param valueLine
	 *            值所在列，0-base
	 * @return
	 */
	public static Map<Object, Object> getHashMap(ExcelProcessor ep, int titleLength, int keyLine, int valueLine) {
		Map<Object, Object> hashMap = new HashMap<Object, Object>();
		for (Sheet st : ep.getCurrentWorkBook()) {
			for (int i = titleLength; i < st.getLastRowNum(); i++) {
				hashMap.put(ep.getSingleCellValue(st.getRow(i).getCell(keyLine)),
						ep.getSingleCellValue(st.getRow(i).getCell(valueLine)));
			}
		}
		return hashMap;
	}

	/**
	 * 返回单元格的值
	 * 
	 * @param c
	 * @param returnFormula
	 *            是否直接返回公式值，缺省则为返回实际值
	 * @return 返回值为字符串或数值
	 */
	public Object getSingleCellValue(Cell c, boolean returnFormula) {
		switch (c.getCellTypeEnum()) {
		case STRING:
			return c.getStringCellValue();
		case NUMERIC:
			return c.getNumericCellValue();
		case FORMULA:
			try {
				return returnFormula ? c.getCellFormula() : c.getStringCellValue();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return returnFormula ? c.getCellFormula() : c.getNumericCellValue();
			}
		default:
			break;
		}
		return c.getStringCellValue();
	}

	/**
	 * 返回单元格的值，不返回公式字符串
	 * 
	 * @param c
	 * @return
	 */
	public Object getSingleCellValue(Cell c) {
		return getSingleCellValue(c, false);
	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public Object getCellValue(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		System.out.println(sheetMergeCount);
		if (sheetMergeCount <= 0)
			return getSingleCellValue(st.getRow(row).getCell(column));
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress ca = sheet.getMergedRegion(i);
			int firstColumn = ca.getFirstColumn();
			int lastColumn = ca.getLastColumn();
			int firstRow = ca.getFirstRow();
			int lastRow = ca.getLastRow();

			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					Row fRow = sheet.getRow(firstRow);
					Cell fCell = fRow.getCell(firstColumn);
					return getSingleCellValue(fCell);
				}
			}
		}
		return getSingleCellValue(st.getRow(row).getCell(column));
	}

	/**
	 * 推荐直接使用该方法获取单元格值
	 * 
	 * 如果excel是wps格式，获取合并单元格的cell时，cell会是null，此时不能用该方法，请用getMergedRegionValue(Sheet
	 * sheet, int row, int column)
	 * 
	 * @description
	 * @author liuzhenpeng
	 * @date 2017年2月16日
	 * @param sheet
	 * @param cell
	 * @return
	 */
	public Object getCellValue(Sheet sheet, Cell cell) {
		return getCellValue(sheet, cell.getRowIndex(), cell.getColumnIndex());
	}

	/**
	 * 判断合并了行
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 */
	public static boolean isMergedRow(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row == firstRow && row == lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断指定的单元格是否是合并单元格
	 * 
	 * @param sheet
	 *            工作表
	 * @param row
	 *            行下标
	 * @param column
	 *            列下标
	 * @return
	 */
	public static boolean isMergedRegion(Sheet sheet, int row, int column) {
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if (row >= firstRow && row <= lastRow) {
				if (column >= firstColumn && column <= lastColumn) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 如果excel是wps格式，获取合并单元格的cell时，cell会是null，此时不能用该方法，请用isMergedRegion(Sheet sheet,
	 * int row, int column)
	 * 
	 * @description
	 * @author liuzhenpeng
	 * @date 2017年2月16日
	 * @param sheet
	 * @param cell
	 * @return
	 */
	public boolean isMergedRegion(Sheet sheet, Cell cell) {
		int row = cell.getRowIndex();
		int column = cell.getColumnIndex();
		return isMergedRegion(sheet, row, column);
	}

	/**
	 * 判断sheet页中是否含有合并单元格
	 * 
	 * @param sheet
	 * @return
	 */
	public static boolean hasMerged(Sheet sheet) {
		return sheet.getNumMergedRegions() > 0 ? true : false;
	}

	/**
	 * 合并单元格
	 * 
	 * @param sheet
	 * @param firstRow
	 *            开始行
	 * @param lastRow
	 *            结束行
	 * @param firstCol
	 *            开始列
	 * @param lastCol
	 *            结束列
	 */
	public static void mergeRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getStringCellValue(Cell cell) {
		if (cell == null)
			return "";
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
			return cell.getCellFormula();
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return "";
	}

	/**
	 * 判断Row(行)是否为空行(行本身为null或行中的单元格全部为null)
	 * 
	 * @param row
	 * @return
	 */
	public static boolean isEmptyRow(Row row) {
		if (row != null) {
			short lastCellNum = row.getLastCellNum();
			if (lastCellNum == 0) {// 如果不存在单元格则返回true
				return true;
			} else {
				// 空单元格的个数
				int emptyCellNum = 0;
				for (int i = 0; i < lastCellNum; i++) {
					Cell cell = row.getCell(i);
					if (isEmptyCell(cell)) {
						emptyCellNum++;
					}
				}
				if (emptyCellNum == lastCellNum) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 判断Row(行)是否存在空的单元格或者这行是否存在单元格
	 * 
	 * @param row
	 * @return
	 */
	public static boolean rowContianEmptyCell(Row row) {
		if (row != null) {
			short lastCellNum = row.getLastCellNum();
			if (lastCellNum == 0) {// 如果不存在单元格则返回true
				return true;
			} else {
				for (int i = 0; i < lastCellNum; i++) {
					Cell cell = row.getCell(i);
					if (isEmptyCell(cell)) {
						return true;
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 判断Sheet是否存在空的行或存在空数据的行
	 * 
	 * @param sheet
	 * @return
	 */
	public static boolean sheetContainEmptyRow(Sheet sheet) {
		if (sheet != null) {
			int lastRowNum = sheet.getLastRowNum();
			if (lastRowNum == 0) {// 如果不存在sheet则返回true
				return true;
			} else {
				for (int i = 0; i < lastRowNum; i++) {
					Row row = sheet.getRow(i);
					if (isEmptyRow(row)) {
						return true;
					}
				}
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 基于指定列数判断Sheet是否存在空的行或存在空数据的行
	 * 
	 * @param sheet
	 * @param columnNum
	 * @return
	 */
	public static boolean sheetContainEmptyRow(Sheet sheet, int columnNum) {
		if (sheet != null) {
			int lastRowNum = sheet.getLastRowNum();
			if (lastRowNum == 0) {// 如果不存在sheet则返回true
				return true;
			} else {
				if (lastRowNum >= columnNum) {
					for (int i = 0; i < columnNum; i++) {
						Row row = sheet.getRow(i);
						if (isEmptyRow(row)) {
							return true;
						}
					}
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 获取表格中空行的行号
	 * 
	 * @param sheet
	 * @return
	 */
	public static List<Integer> getEmptyRowNos(Sheet sheet) {
		List<Integer> list = new ArrayList<Integer>();
		if (sheet != null) {
			int lastRowNum = sheet.getLastRowNum();
			if (lastRowNum != 0) {// 如果不存在sheet则返回true
				for (int i = 0; i < lastRowNum; i++) {
					Row row = sheet.getRow(i);
					if (isEmptyRow(row)) {
						list.add(i);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 判断Cell(单元格)是否为空
	 * 
	 * @param cell
	 * @return
	 */
	public static boolean isEmptyCell(Cell cell) {
		String cellContent = getStringCellValue(cell);
		if (cellContent == null || cellContent.trim().equals("")) {
			return false;
		} else {
			return true;
		}
	}

}
