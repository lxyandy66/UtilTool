package tool.mcu;

import java.io.IOException;
import java.net.SocketException;

import tool.network.SocketProcessor;

public class ThreadSerial implements Runnable {
	private SocketProcessor sp;
	private SerialMessenger sm;

	public ThreadSerial() {

	}

	public ThreadSerial(SerialMessenger sm, SocketProcessor sp) {
		this.sm = sm;
		this.sp = sp;
	}

	public ThreadSerial(String portDescr, int baudRate, SocketProcessor sp) throws Exception {

		this.sm = new SerialMessenger();
		sm.initSerialPort(portDescr, baudRate);// 这种方法可能抛异常
		this.sp = sp;
	}// 构造函数，从ServerManager中接收Socket

	public void run() {
		try {
			// for debug
			sp.printConsole("Serial Thread start!");

			sp.initalProcess(this);
			String str_msg = "";// 相当于一个buffer
			while (true) {
				try {
					str_msg = sm.receiveMessage();// 切记这里会有死锁
					if (str_msg != null) {
						sp.printConsole("\nFrom serial>> " + str_msg);
						sp.processIncomeMsg(str_msg);
					}
					str_msg = null;
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					sp.printConsole("\nFrom serial>> " + "socket closed");
					return;
				} catch (Exception e) {
					// TODO: handle exception
					sp.printConsole("\nFrom serial>> " + "Other excepetion detected: " + e.getMessage());
					e.printStackTrace();
					return;
				}
			}
		} catch (Exception e) {
			sp.printConsole("\nFrom serial>> " + "跳出while, 链接终止");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 发送消息给当前客户端，通过传入参数String发送
	 * 
	 * @param str_send
	 * @throws IOException
	 */
	public void sendMessage(String str_send) throws IOException {
		sp.printConsole("Send<< " + str_send);
		sm.sendMessage(str_send);
	}

	public boolean isAllocate() {
		return sm != null;
	}

	public void disconnect() {
		sm.disconnect();
	}

}
