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
				nicknameList.add("사용자"+out.size());
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
				String ib = "사용자" + out.size() + " 님이 입장하셨습니다.";
				
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
					if (!(nicknameList.get(Integer.parseInt(id)-1).equals(nickName))) {	// 혹시 보낸 사람 닉네임이 변경되었다면
						nicknameList.set(Integer.parseInt(id)-1, nickName); // 갱신해서 저장해라\
						
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
		System.out.println("채팅 프로그램 서버 실행 중");
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
