package tool.data.processor;

import org.apache.poi.ss.usermodel.Row;

/**
 * 对Excel文档行进行处理的接口类
 * 
 * @author Mr_Li
 *
 */
public interface RowProcessable {
	/**
	 * 对Excel行进行处理的接口方法
	 * 
	 * @param r
	 *            当前所遍历到的行
	 * @param stNum
	 *            当前行所在的工作表的序号
	 */
	void processRow(Row r, int sheetNum) throws Exception;
}