package tool.network;

import java.net.Socket;

/**Socket处理的接口类，包括处理与界面的输入输出，传入信息的处理等
 * @author Mr_Li
 *
 */
public interface SocketProcessor {

	/**用于向界面的控制台输出内容
	 * @param str
	 */
	public void printConsole(String str);
	
	/**处理接收到的数据，例如命令处理、转发、及执行等
	 * @param str
	 */
	public void processIncomeMsg(String str);
	

	/**
	 * 对新加入的连接进行相关操作的接口，不涉及
	 * 
	 * @param s 获取接入的socket
	 */
	public abstract void processIncomeSocket(Socket s);
	
	/**在开始接收之前，给socket的建立连接时的第一个消息
	 * @param str
	 */
	public abstract void initalProcess(Object ts);
	
	
}
