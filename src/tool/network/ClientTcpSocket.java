package tool.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 继承socket包，可直接访问socket全部方法，提供一些更为详细的属性
 * 
 * @author Mr_Li
 *
 */
public class ClientTcpSocket implements ClientSocket {

	private Socket so;
	private BufferedReader tr_br;
	private PrintWriter tr_pw;

	private void initOutputStream() throws IOException {
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		this.tr_pw = new PrintWriter(so.getOutputStream(), true);
	}

	private void initInputStream() throws IOException {
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		this.tr_br = new BufferedReader(new InputStreamReader(so.getInputStream()));
	}

	/**
	 * 直接传入socket构造
	 * 
	 * @param so
	 */
	public ClientTcpSocket(Socket so) {
		this.so = so;
	}

	@Override
	public void sendMessage(String str) throws IOException {
		// TODO Auto-generated method stub
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		if (tr_pw == null)
			initOutputStream();
		System.out.println(str+" in client");
		tr_pw.println(str);
		tr_pw.flush();
	}

	@Override
	public void disconnect() throws IOException {
		// TODO Auto-generated method stub
//		if (tr_br != null) {
//			synchronized (tr_br) {
//				tr_br.close();
//				
//			}
//		}
//		if (tr_pw != null) {
//			synchronized (tr_pw) {
//				tr_pw.close();
//			}
//		}
		if(so!=null) {
			synchronized (so) {
				so.close();
			}
		}
	}

	@Override
	public String receiveMessage() throws IOException {
		// TODO Auto-generated method stub
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		if (this.tr_br == null) {
			initInputStream();
		}
		return tr_br.readLine();
	}

	@Override
	public void sendUrgentPacket(int data) throws IOException {
		// TODO Auto-generated method stub
		if (!checkExisted())
			throw new IOException("Socket is not exist!");
		so.sendUrgentData(data);
	}

	/**
	 * 通过发送紧急字节来检测连接是否可用
	 * 
	 * @param s 需要测试的socket链接
	 * @return
	 */
	public boolean checkConnected() {
		// 先检查连接是否已经关闭
		if (!checkExisted())
			return false;
		if(!so.isConnected() || so.isClosed())
			return false;
		// 若没有关闭，则尝试发送心跳包
		try {
			sendUrgentPacket(0xFF);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean checkExisted() {
		return so!= null;

	}
}
