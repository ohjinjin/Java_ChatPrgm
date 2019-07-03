import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatServer
{
	private static ArrayList<PrintWriter> out = new ArrayList<PrintWriter>();
	private static ArrayList<String> nicknameList = new ArrayList<String>();
	private static int connectCount = 0;
	public static Date currentDate;
	
	public ChatServer()
	{
	}

	public static class getCurrTime extends Thread
	{
		public void run()
		{
			while(true)
			{
				currentDate = new Date();
			}
		}
	}
	
	
	private static class Ssth extends Thread
	{
		private PrintWriter printWriter;
		private Socket socket;
		private int myId;
		
		public Ssth(Socket socket, int clientid)
		{
			
			connectCount++;
			this.socket = socket;
			this.myId = clientid;
			try {
				printWriter = new PrintWriter(socket.getOutputStream(), true);
				out.add(printWriter);
				nicknameList.add("�����"+out.size());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void run()
		{
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader
						(socket.getInputStream()));
				printWriter.println(myId);
				String ib = "�����" + out.size() + " ���� �����ϼ̽��ϴ�.";
				
				String allUsers="";
				for (int i = 0; i<out.size();i++) {
					allUsers += nicknameList.get(i) + "&";			
				}
				
				for (int i = 0 ; i < out.size(); i++)
				{
					PrintWriter a = out.get(i);
					a.println("0+" + "+" + ib + allUsers);	
				}
				while (true)
				{
					String id = null, msg = null, nickName = null, res = null;
					res = in.readLine();
					id = res.split("\\+")[0];
					nickName = res.split("\\+")[1];
					msg = res.split("\\+")[2];

					allUsers = "";
					if (!(nicknameList.get(Integer.parseInt(id)-1).equals(nickName))) {	// Ȥ�� ���� ��� �г����� ����Ǿ��ٸ�
						nicknameList.set(Integer.parseInt(id)-1, nickName); // �����ؼ� �����ض�\
						
						allUsers += ".";
						for (int j = 0; j<out.size();j++) {
							allUsers += nicknameList.get(j) + "&";							
						}
					}
					
					for (int i = 0 ; i < out.size(); i++)
					{
						PrintWriter a = out.get(i);
						a.println(res + allUsers);
					}
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
}
	public static void main(String[] args) throws IOException {
		System.out.println("ä�� ���α׷� ���� ���� ��");
		ChatServer a = new ChatServer();
		ServerSocket ss = new ServerSocket(7777);
		int clientid = 0;
		try {
			getCurrTime gt = new getCurrTime();
			gt.start();
			while (true)
			{
				clientid++;
				Ssth th = new Ssth(ss.accept(), clientid);
				th.start();
			}
		} finally {
			ss.close();
		}
	}
	
}
