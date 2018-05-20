package tool.data.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelProcessor {

	private final String ERROR_TYPE = "文件格式错误";
	private final String ERROR_EMPTY = "文件为空";
	private final String ERROR_GENERAL_ON_LOAD = "文件错误";
	private final String ERROR_NOT_LOAD="尚未加载文件";
	private final String ERROR_EXCEED="超出范围";

	private File excelFile;
	private FileInputStream excelFis;
	
	
	/**POI中实际excel处理对象,需加载
	 * 
	 */
	private Workbook wb;
	private Sheet st;
	private int sheetCount;

	public ExcelProcessor() {
	}

	public void loadExcel(String filePath) throws Exception {
		loadExcel(new File(filePath));
	}

	/**
	 * 加载需要处理的excel文件
	 * 
	 * @param f
	 *            需要加载的excel文件
	 * @throws Exception
	 */
	public void loadExcel(File f) throws Exception {
		if(!isLegal(f))
			throw new IOException(ERROR_GENERAL_ON_LOAD);
		String[] type = f.getName().split("\\.");
		excelFile=f;
		excelFis=new FileInputStream(excelFile);
		if (type[type.length - 1].equals("xls")) {
			wb=new HSSFWorkbook(excelFis);
		}else if(type[type.length - 1].equals("xlsx")) {
			wb=new XSSFWorkbook(excelFis);
		}else
			throw new IOException(ERROR_GENERAL_ON_LOAD);
		st=wb.getSheetAt(0);//默认获取第1个sheet
		wb.setActiveSheet(0);//默认第一个Sheet为活动
		sheetCount=wb.getNumberOfSheets();
		
		//on develop
		System.out.println("Load success : "+excelFile.getName());
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
		this.st=s;
	}
	
	/**获取序号对应的Sheet
	 * @param stNum Excel实际序号-1
	 * @return
	 * @throws Exception
	 */
	public Sheet getSheetAt(int stNum) throws Exception{
		if(!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if(stNum>sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum);
	}
	
	/**切换当前活动的工作表
	 * @param stNum
	 * @throws Exception
	 */
	public void setCurrentSheet(int stNum) throws Exception {
		if(!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if(stNum>sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		this.st=wb.getSheetAt(stNum);
		wb.setActiveSheet(stNum);
	}
	
	/**获取当前工作表的单元格值，如参数为(1,1)，获取为B2单元格值
	 * @param rowNum 行号，为excel中实际行号-1
	 * @param colNum	 列号，为excel中实际列号-1
	 * @return
	 */
	public Cell getCell(int rowNum,int colNum) throws IOException{
		if(!isLoad())
			throw new IOException(ERROR_GENERAL_ON_LOAD);
		return st.getRow(rowNum).getCell(colNum);
	}
	
	/**获取特定工作表的单元格值，如参数为(0,1,1)，获取为Sheet1中B2单元格值
	 * @param stNum 工作表号，为excel中实际工作表号-1
	 * @param rowNum 行号，为excel中实际行号-1
	 * @param colNum	列号，为excel中实际列号-1
	 * @return
	 * @throws IOException
	 */
	public Cell getCell(int stNum,int rowNum,int colNum) throws IOException,IllegalArgumentException {
		if(!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if(stNum>sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum).getRow(rowNum).getCell(colNum);
	}
	
	public Row getRow(int stNum,int rowNum) throws IOException {
		if(!isLoad())
			throw new IOException(ERROR_NOT_LOAD);
		if(stNum>sheetCount)
			throw new IllegalArgumentException(ERROR_EXCEED);
		return wb.getSheetAt(stNum).getRow(rowNum);
	}
	
	/**将某一行所有单元格的数据转换为数组
	 * @param stNum
	 * @param rowNum
	 * @return
	 */
	public String[] getRowValue(int stNum,int rowNum) {
		ArrayList<String> array=new ArrayList<String>();
		for(Cell c:wb.getSheetAt(stNum).getRow(rowNum))
			array.add(c.getStringCellValue());
		return (String[]) array.toArray();
	}
	
	
	public void appendRow(String[]s,Sheet st) throws FileNotFoundException, IOException {
		Row r=st.createRow(st.getLastRowNum()+1);
		for(int i=0;i<s.length;i++) {
			r.createCell(i);
			r.getCell(i).setCellValue(s[i]);
		}
	}
	
	public void saveChange() throws FileNotFoundException, IOException {
		wb.write(new FileOutputStream(excelFile));
	}
	
	private boolean isLoad() {
		return excelFis!=null&&wb!=null;
	}

	protected boolean isLegal(File f) throws IOException {
		String[] type = f.getName().split("\\.");
		if (f == null || !f.isFile() || !f.exists())
			throw new IOException(ERROR_EMPTY);
		if (!type[type.length - 1].equals("xls") && !type[type.length - 1].equals("xlsx"))
			throw new IOException(ERROR_TYPE);
		return true;
	}

}
