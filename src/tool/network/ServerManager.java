package tool.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ServerManager implements Runnable {
	private ServerSocket server;
	private int portNo = 5230;

	private SocketProcessor sp;

	/**
	 * 设置socket的处理对象，包括显示交互、接收信息处理方法等
	 * 
	 * @param sp the sp to set
	 */
	public void setSp(SocketProcessor sp) {
		this.sp = sp;
	}

	// 存储所有客户端Socket连接对象
	protected static List<ThreadServer> list_client = new ArrayList<ThreadServer>();
	// 线程池
	private ExecutorService es;

	/**
	 * @return the server
	 */
	public ServerSocket getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public void setPort(int port) {
		this.portNo = port;
	}

	// 用以覆盖跟Swing空间交互

	/**
	 * 获取当前连接的socket列表
	 * 
	 * @return
	 */
	public List<ThreadServer> getClientList() {
		return list_client;
	}

	public List<String> getClientListName() {
		return getClientList().stream().map(ThreadServer::getFullDescr).collect(Collectors.toList());
	}

	public ServerManager() {
	}

	public ServerManager(SocketProcessor sp) {
		this.sp = sp;
	}

	public void run() {
		try {
			// 设置服务器端口
			if (server == null)
				server = new ServerSocket(portNo);// 默认情况最多50个socket
			// 创建一个线程池
			es = Executors.newCachedThreadPool();
			sp.printConsole("start...");
			// 用来临时保存客户端连接的Socket对象
			Socket client = null;
			while (true) {
				// 接收客户连接并添加到list中
				client = server.accept();
				sp.processIncomeSocket(client);// 对加入的socket处理，可将其关闭
				if (client.isClosed())
					continue;
				// 开启一个客户端线程

				list_client.add(new ThreadServer(new ClientTcpSocket(client), sp));
				es.execute(list_client.get(list_client.size() - 1));// 就这里一句话特么我就要一个类啊!
//				这里的执行很奇怪，new了thread之后后面一句就暂时不执行了，直到下一个socket来
//				printConsole("In thread size is "+list_client.size());
				freshClientList();// 清理list
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对ServerManager下所有连接广播信息 ！要修改！
	 * 
	 * @param msg
	 */
	public void sendBroardcast(String msg) {
		// 这个发送信息对于整个list中的socket
		sp.printConsole("size is " + list_client.size());
		for (ThreadServer s : list_client) {
			try {
				s.sendMessage(msg);
				sp.printConsole("Send<<  " + msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				sp.printConsole(s.identify() + "   " + "Fail to send\n");
			}
		}
	}

	public void sendMessage(String msg, int id) {
		// 这个发送信息对于整个list中的socket
		list_client.stream().filter(o -> o.getId() == id).forEach(o -> {
			try {
				o.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sp.printConsole(e.getMessage());
				e.printStackTrace();
			}
		});
	}

	public void sendMessage(String msg, String descr) {
		// 这个发送信息对于整个list中的socket

		list_client.stream().filter(o -> o.getDescr().equals(descr)).forEach(o -> {
			try {
				o.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sp.printConsole(e.getMessage());
				e.printStackTrace();
			}
		});
	}

	/**
	 * 用于结束服务器使用，所有连接将安全断开
	 * 
	 * @throws Exception
	 */
	public void shutdown() {//
		if (list_client.isEmpty()) {
			sp.printConsole("当前无连接");
			return;
		}
		sp.printConsole("正在断开");
		// for (Socket s : list_client)
		// {用增强for循环对list进行遍历来删除会抛异常，因为remove之后list的数量不同
		// if (s != null && s.isConnected()) {
		// printConsole(s.getInetAddress() + s.getInetAddress().getHostName() +
		// " will close.\n");
		// s.close();
		// list_client.remove(s);
		// }
		// }用增强for循环对list进行遍历来删除会抛异常，因为remove之后list的数量不同
//		for(int i=0;i<list_client.size();i++) {
//			list_client.get(i).disconnect();
//			list_client.remove(i);
//		}
		Iterator<ThreadServer> i = list_client.iterator();
		while (i.hasNext()) {
			try {
				i.next().disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i.remove();
		}
		sp.printConsole("连接已断开");
		es.shutdownNow();
		
	}

	/**
	 * 更新ServerManager中Socket列表的线程
	 * 
	 */
	public void freshClientList() {
		// 更新list_client数据，删除断开的连接
		for (int j = 0; j < list_client.size(); j++) {
//			printConsole("this is " + j);
			if (!list_client.get(j).checkConnected()) {
				list_client.remove(j);
				sp.printConsole(j + " removed");
			} else
				sp.printConsole(j + " doesn't remove");
		}
//		printConsole("After fresh size is " + list_client.size());
	}

}
