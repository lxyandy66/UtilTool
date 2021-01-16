package tool.network;

import java.io.IOException;
import java.net.SocketException;

public class ThreadServer implements Runnable {// 觉得这个还可以再精简一点
//		线程池只能放入实现Runable或Callable类线程，不能直接放入继承Thread的类。
	private ClientSocket tr_socket;
	private String str_pub;

	private SocketProcessor sp;
	
	private int id;
	private String descr=null;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getFullDescr() {
		return this.id+this.descr;
	}
	public ThreadServer(ClientSocket csocket,SocketProcessor sp) throws IOException {
		this.tr_socket = csocket;
		this.sp=sp;
		this.id=hashCode();
	}// 构造函数，从ServerManager中接收Socket

	public ThreadServer(ClientSocket csocket,SocketProcessor sp,int id) throws IOException {
		this.tr_socket = csocket;
		this.sp=sp;
		this.id=id;
		sp.printConsole("ThreadServer consturcted! id: "+id);
	}// 构造函数，从ServerManager中接收Socket

	public ClientSocket getSocket() {
		return tr_socket;
	}

	public boolean checkConnected() {
		return tr_socket.checkConnected();
	}

	public void run() {
		try {
			sp.initalProcess(this);
			String str_msg = "";//相当于一个buffer
			while (true) {
				try {
					str_msg= tr_socket.receiveMessage();//切记这里会有死锁
					if (str_msg != null) {
						sp.printConsole("\nClient>> " + str_msg);
						sp.processIncomeMsg(str_msg);
					}
					str_msg=null;
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					sp.printConsole("\nClient>> " + "socket closed");
					return;
				}catch (Exception e) {
					// TODO: handle exception
					sp.printConsole("\nClient>> " + "Other excepetion detected: "+e.getMessage());
					e.printStackTrace();
					return;
				}
			}
		} catch (Exception e) {
			sp.printConsole("\nClient>> " + "跳出while, 链接终止");
			e.printStackTrace();
			return;
		}
	}


	/**
	 * 发送消息给当前客户端，通过公用String发送 !要抽象!
	 * 
	 * @throws IOException
	 */
	public void sendMessage() throws IOException {
		sp.printConsole("Send<< " + str_pub);
		tr_socket.sendMessage(str_pub);
	}

	/**
	 * 发送消息给当前客户端，通过传入参数String发送
	 * 
	 * @param str_send
	 * @throws IOException
	 */
	public void sendMessage(String str_send) throws IOException {
		sp.printConsole("Send<< " + str_send);
		tr_socket.sendMessage(str_send);
	}
	
	public String identify() {
		return "";
	}

	protected void disconnect() throws IOException {
//		ServerManager.list_client.remove(this);
		tr_socket.disconnect();
	}

}
