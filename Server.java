import java.net.*;
import java.io.*;
import java.lang.Thread;
import java.util.*;

public class Server{
	ArrayList<UserConnection> userConnections;
	ArrayDeque<Message> globalMessages;

	public Server(int port){
		userConnections = new ArrayList<UserConnection>(10);
		globalMessages = new ArrayDeque<Message>();
		ServerSocket ss;
		try{
			ss = new ServerSocket(port);

			while(true){
				try{
					Socket connection = ss.accept();
					UserConnection tmpUserConnection = new UserConnection(this, connection);
					userConnections.add(tmpUserConnection);
					tmpUserConnection.start();
				}
				catch(Exception e){
					System.out.println("Error Getting connection");		
				}
			}
		}
		catch(Exception e){
			System.out.println("Error setting up ServerSocket, is the port free?");
		}

	}

	public synchronized void AddGlobalMessage(Message message){
		globalMessages.push(message);
	}

	public static void main(String[] args){
		try{
			int port = Integer.parseInt(args[0]);
			if (port > 0 && port < 65535)
				new Server(port);
		}
		catch(Exception e){	
			System.out.println("Please make sure to enter a valid port number as the 1st argument");
		}
	}

}

class UserConnection extends Thread{
	Server parent;
	Socket socket;
	String sender;
	ArrayDeque<Message> messagesForUser;

	public UserConnection(Server parent, Socket socket){
		this.socket = socket;
		messagesForUser = new ArrayDeque<Message>();
	}

	public synchronized void sendMessageToUser(Message message){
		messagesForUser.push(message);
	}

	public void sendMessageGlobal(Message message){

	}

	public void run(){
		boolean hasQuit = false;
		while(!hasQuit){
			try{
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Object obj = ois.readObject();
				if(obj instanceof Message){

				}
			}
			catch(Exception e){
				System.out.println("Issue with Client Connection");
				e.printStackTrace();
			}
		}
	}

}