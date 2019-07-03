import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.*;

public class ChatClient extends JFrame implements KeyListener{
	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	private static JTextField j1 = new JTextField(40);
	private static JTextArea a1 = new JTextArea(10, 40);
	private static JTextArea a2 = new JTextArea(10, 10);
	private static JScrollPane chatScroll = new JScrollPane(a1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private static JScrollPane listScroll = new JScrollPane(a2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private static JMenuBar mb = new JMenuBar();
	private static JMenu menu = new JMenu("menu");
	
	public static Date currentDate;
	private static int myId = 0;
	private static String nickName = "";
	
	public ChatClient() throws IOException
	{
		super("채팅 프로그램 클라이언트");
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		j1.addKeyListener(this);
		a1.setEditable(false);
		a2.setEditable(false);
		add(j1, BorderLayout.SOUTH);
		add(chatScroll, BorderLayout.CENTER);
		add(listScroll, BorderLayout.EAST);
		JMenuItem item1 = new JMenuItem("대화명 변경");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//String old_nickName = nickName;
				nickName = JOptionPane.showInputDialog(null,"변경할 대화명 입력 : ");
				//out.println()
				//a2.getText().indexOf(nickName);
				//a2.getText().replace(old_nickName, nickName);
			}
		});
		
		menu.add(item1);
		/*menu.add(new JMenuItem("대화 내용 저장"));
		menu.addSeparator();
		menu.add(new JMenuItem("대화 나가기"));*/
		mb.add(menu);
		setJMenuBar(mb);

		setVisible(true);
		socket = new Socket("localhost", 7777);
		in = new BufferedReader(new InputStreamReader
				(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		myId = Integer.parseInt(in.readLine());
		nickName = "사용자" + myId;
	}
	
	public static class getCurrTime extends Thread
	{
		public void run()
		{
			while (true)
			{
				currentDate = new Date();
			}
		}
	}
	private static class ClTh extends Thread
	{
		public void run()
		{
			try {
				while (true)
				{
					String id = null, tmpMsg = null, msg = null, tmpUserList = null, res = null, who = null;
					res = in.readLine();
					id = res.split("\\+")[0];
					who = res.split("\\+")[1];
					tmpMsg = res.split("\\+")[2];

					
					if (Integer.parseInt(id) == 0) who = "\n서버 : ";
					else who = who + " : ";
					
					if (tmpMsg.contains("&")) {
						System.out.println("ccheck1" + tmpMsg);
						msg = tmpMsg.split("\\.")[0];
						tmpUserList = tmpMsg.split("\\.")[1];
						
						//a2.removeAll();
						a2.setText("");
						String userList[] = tmpUserList.split("\\&");
						
						for (int i = 0; i<userList.length; i++) {
							a2.append(userList[i]+"\n");
							System.out.println(userList[i]);
						}
					}
					else {
						System.out.println("ccheck2");
						msg = tmpMsg;
					}
					a1.append(who + msg + "   -" + currentDate + "\n");
					a1.setCaretPosition(a1.getDocument().getLength());
				}
			} catch (IOException e) {
				System.out.println("버퍼 읽기 오류");
			} finally {
				try {
					out.println(myId + "+" + nickName + "+" + "퇴장했습니다");
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void get_JT(KeyEvent e)
	{
		if (e.getKeyCode() == e.VK_ENTER)
		{
			String myans = j1.getText();
			out.println(myId + "+" + nickName + "+" + myans);
			a1.setCaretPosition(a1.getDocument().getLength());
			j1.setText("");
		}
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		get_JT(arg0);
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) throws IOException {
		ChatClient a = new ChatClient();
		getCurrTime gt = new getCurrTime();
		ClTh cl = new ClTh();
		cl.start();
		gt.start();
	}
}