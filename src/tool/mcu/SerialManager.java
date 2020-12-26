package tool.mcu;

import java.util.ArrayList;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;

/**
 * 串口的管理类
 * 
 * @author Mr_Li
 *
 */
public class SerialManager {
	
	/**
	 * 查找电脑上所有可用 com 端口
	 *
	 * @return 可用端口名称列表，没有时 列表为空
	 */
	public static ArrayList<String> getAllComPort() {
		/**
		 * getPortIdentifiers：获得电脑主板当前所有可用串口
		 */
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		ArrayList<String> portNameList = new ArrayList<>();

		/**
		 * 将可用串口名添加到 List 列表
		 */
		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();// 名称如 COM1、COM2....
			portNameList.add(portName);
		}
		return portNameList;
	}
}
