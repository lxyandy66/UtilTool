package tool.mcu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;

/**
 * @author Mr_Li
 *
 */
public class SerialMessenger {

	/**
	 * 串口识别器
	 */
	private CommPortIdentifier portId;// CommPortIdentifier.getPortIdentifier("");

	/**
	 * 
	 */
	private SerialPort port;

	private static String portName;
	private static int openPortTimeOut;

	private boolean isInit = false;

	private BufferedReader mcuBr;
	private PrintWriter mcuPw;

	public void initSerialPort(String portDescr, int baudRate, int datebits, int stopbits, int parity)
			throws Exception {
		portId = CommPortIdentifier.getPortIdentifier(portDescr);
		/**
		 * open(String TheOwner, int i)：打开端口 TheOwner 自定义一个端口名称，随便自定义即可
		 * i：打开的端口的超时时间，单位毫秒，超时则抛出异常：PortInUseException if in use.
		 * 如果此时串口已经被占用，则抛出异常：gnu.io.PortInUseException: Unknown Application
		 */
		port = (SerialPort) portId.open(SerialMessenger.portName, SerialMessenger.openPortTimeOut);

		/**
		 * 设置串口参数：setSerialPortParams( int b, int d, int s, int p ) b：波特率（baudrate）
		 * d：数据位（datebits），SerialPort 支持 5,6,7,8 Arudino默认8位 s：停止位（stopbits），SerialPort
		 * 支持 1,2,3 Arudino默认1位停止位 p：校验位 (parity)，SerialPort 支持 0,1,2,3,4 Arudino默认无校验位
		 * 如果参数设置错误，则抛出异常：gnu.io.UnsupportedCommOperationException: Invalid Parameter
		 * 此时必须关闭串口，否则下次 portIdentifier.open 时会打不开串口，因为已经被占用
		 */
		port.setSerialPortParams(baudRate, datebits, stopbits, parity);
	}

	/**
	 * 根据Arduino设置默认的串口通信位，即8位数据位，1位停止位，无校验位
	 * 
	 * @param portDescr
	 * @param baudRate
	 * @throws Exception
	 */
	public void initSerialPort(String portDescr, int baudRate) throws Exception {
		initSerialPort(portDescr, baudRate, 8, 1, 0);
	}

	private void initOutputStream() throws IOException {
		if (!checkExisted())
			throw new IOException("Port not initialized!");
		this.mcuPw = new PrintWriter(port.getOutputStream(), true);
	}

	private void initInputStream() throws IOException {
		if (!checkExisted())
			throw new IOException("Port not initialized!");
		this.mcuBr = new BufferedReader(new InputStreamReader(port.getInputStream()));
	}

	public void sendMessage(String str) throws IOException {
		// TODO Auto-generated method stub
		if (!checkExisted())
			throw new IOException("Port not initialized!");
		if (mcuPw == null)
			initOutputStream();
		System.out.println(str + " in client");
		mcuPw.println(str);
		mcuPw.flush();
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
		synchronized (this.port) {
			if (this.port != null) {
				this.port.close();
			}
		}

	}

	private boolean checkConnected() {
		return checkExisted();
	}

	private boolean checkExisted() {
		return port != null;
	}

	public String receiveMessage() throws IOException {
		// TODO Auto-generated method stub
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		if (this.mcuBr == null) {
			initInputStream();
		}
		return mcuBr.readLine();
	}

}
