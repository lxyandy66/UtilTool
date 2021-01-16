package tool.mcu;

import java.util.ArrayList;
import com.fazecast.jSerialComm.SerialPort;

/**
 * 串口的管理类，基于RXTX库实现
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
	public static String[] getAllComPort() {
//		/**
//		 * getPortIdentifiers：获得电脑主板当前所有可用串口
//		 */
//		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		
		
		SerialPort[] serialPortList=SerialPort.getCommPorts();
		String[] portNameList = new String[SerialPort.getCommPorts().length];
		/**
		 * 将可用串口名添加到 List 列表
		 */
		for(int i=0;i<SerialPort.getCommPorts().length;i++)
			portNameList[i]=serialPortList[i].getSystemPortName();
		
		return portNameList;
	}
}
