package tool.network;

import java.io.IOException;

public interface ClientSocket {

	/**
	 * 发送信息的接口，由子类的UDP或TCP实现
	 * 
	 * @param str
	 * @throws IOException 
	 */
	public void sendMessage(String str) throws IOException;

	/**
	 * 关闭socket连接
	 * @throws IOException 
	 * 
	 */
	public void disconnect() throws IOException;
	
	/**取代ThreadServer中的主循环部分
	 * @return 返回接收到的信息
	 */
	public String receiveMessage ()throws Exception;
	
	/**发送紧急数据，可用于检查是否客户端保持连接
	 * @param data
	 * @throws IOException 若客户端断开则会报此错误，可用来检测连接情况
	 */
	public void sendUrgentPacket(int data) throws IOException;
	
	/**检测连接是否仍保持
	 * @return
	 */
	public boolean checkConnected();
}
