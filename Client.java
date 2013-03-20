import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.lang.Thread;
import java.awt.*;
import java.awt.event.*;

public class Client implements ActionListener{
	private JTextField user = new JTextField("user",20);
	private JTextArea server = new JTextArea("",5,20);
	private JScrollPane sp = new JScrollPane(server); 
	private JFrame window = new JFrame("client");
	private JButton button = new JButton("Send");
	ConnectionToServer cts;
	public Client(String[] args){
		window.setSize(300,300);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new FlowLayout());
		window.add(sp);
		window.add(user);
		window.add(button);
		button.addActionListener(this);
		window.setVisible(true);
		cts = new ConnectionToServer(args[0], Integer.parseInt(args[1]), server);
		cts.start();
	}
	
	public void actionPerformed(ActionEvent a){
		String str = user.getText();
		cts.parseText(str);
	}
	
	public static void main(String[] args) throws Exception{
		if (args.length > 1)
			new Client(args);
		else
			System.out.println("Please start with a correct host and port number");
	}
}

class ConnectionToServer extends Thread{
	Socket socket;
	String message;
	JTextArea panel;
	public ConnectionToServer(String host, int port, JTextArea inputPanel){
		panel = inputPanel;
		message = "";
		try{
			socket = new Socket(host, port);
		}
		catch(Exception e){
			System.out.println("Unable to connection to server");
		}
	}
	synchronized public void setMessageString(String s){
		message = s;
	}

	synchronized public void parseText(String s){
		setMessageString(s);
	}

	synchronized public String getMessageString(){
		return message;
	}

	synchronized public void append(String s){
		panel.append(s);
	}

	public void run(){
		boolean quit = false;
		try{
			Scanner scanner = new Scanner(socket.getInputStream());
			while(!quit){
				try{
					String messageStr = getMessageString();				
					if (!messageStr.equals("")){	
						setMessageString("");
						Message messageObj = new Message();
						if (messageStr.startsWith("!join")){
							messageObj.type = Message.Type.JOIN;
							messageObj.value1 = messageStr.split(" ")[1];
						}
						else if (messageStr.startsWith("@") && messageStr.contains(":")){
							messageObj.type = Message.Type.DIRECT;
							String[] messageTmp = messageStr.split(":");
							messageTmp[0] = messageTmp[0].substring(1);
							messageObj.value1 = messageTmp[0];
							messageObj.value2 = messageTmp[1];
						}
						else if (messageStr.equals ("!who")){
							messageObj.type = Message.Type.WHO;
						}
						else if (messageStr.equals("!quit")){
							messageObj.type = Message.Type.QUIT;
						}
						else{
							messageObj.type = Message.Type.MESSAGE;
							messageObj.value2 = messageStr;
						}
						SendMessageObject(messageObj);
					}
				}
				catch(Exception e){
					System.out.println("Error parsing text string");
				}
				try{
					if (socket.getInputStream().available() > 0){
						byte[] buffer = new byte[socket.getInputStream().available()];
						int amountRead = socket.getInputStream().read(buffer,0,buffer.length);
						String tmpString = new String(buffer,0,amountRead);
						append(tmpString + "\n");

					}
				}
				catch(Exception e){
					System.out.println("Scanner next error");
				}
			}
		}
		catch(Exception e){
			System.out.println("Error setting up inputstream into scanner");
		}
	}

	public void SendMessageObject(Message obj){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(obj);
			oos.flush();
		}
		catch(Exception e){
			System.out.println("SendMessageObject method call failed");
		}
	}
}