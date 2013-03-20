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
					System.out.println("Got new User connection");
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

	public synchronized String getUserList(){
		checkForDeadThreads();
		String userlist = "";
		for(int i = 0; i < userConnections.size(); i++){
			userlist += userConnections.get(i).getSender();
			if (i < userConnections.size()-1)
				userlist += ",";
		}
		return userlist;
	}

	public synchronized void AddGlobalMessage(Message message){
		switch(message.type){
			case MESSAGE:
				for(int i = 0; i < userConnections.size(); i++){
					if (userConnections.get(i).isAlive()){
						userConnections.get(i).sendMessageToUser(message);
					}
				}
			break;
			case DIRECT:
				for(int i = 0; i < userConnections.size(); i++){
					if (userConnections.get(i).isAlive()){
						if (userConnections.get(i).getSender().equals(message.value1))
							userConnections.get(i).sendMessageToUser(message);
					}
				}
			break;
		}
	}

	public synchronized void checkForDeadThreads(){
		for(int i = 0; i < userConnections.size(); i++){
			System.out.println(userConnections.get(i).isAlive());
			if (!userConnections.get(i).isAlive()){
				userConnections.remove(i);
				i--;
			}
		}
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
	boolean hasQuit;
	ArrayDeque<Message> messagesForUser;

	public UserConnection(Server parent, Socket socket){
		this.socket = socket;
		this.parent = parent;
		messagesForUser = new ArrayDeque<Message>();
		sender = "";
	}

	public synchronized String getSender(){
		return sender;
	}

	public synchronized void setSender(String s){
		sender = s;
	}

	public synchronized void sendMessageToUser(Message message){
		messagesForUser.push(new Message(message));
	}

	public synchronized void sendMessageGlobal(Message message){
		parent.AddGlobalMessage(message);
	}

	public void run(){
		ObjectInputStream ois;
		hasQuit = false;
		while(!hasQuit){
			try{
				Thread.sleep(1);
				if (socket.getInputStream().available() > 0){
					ois = new ObjectInputStream(socket.getInputStream());
					Object obj = ois.readObject();
					if(obj instanceof Message){
						Message messageObj = (Message)obj;
						switch(messageObj.type){
							case JOIN:
								setSender(messageObj.value1);
							break;
							case MESSAGE:
								messageObj.sender = sender;
								sendMessageGlobal(messageObj);
							break;
							case DIRECT:
								messageObj.sender = sender;
								sendMessageGlobal(messageObj);
							break;
							case WHO:
								String users = parent.getUserList();
								socket.getOutputStream().write(users.getBytes("UTF-8"));
								socket.getOutputStream().flush();
							break;
							case QUIT:
								hasQuit = true;
								sendMessageGlobal(messageObj);
							break;
						}
					}
				}

				if (messagesForUser.size() > 0){
					Message tmpMessage = messagesForUser.pop();
					socket.getOutputStream().write((tmpMessage.sender + ":" + tmpMessage.value2).getBytes("UTF-8"));
					//socket.getOutputStream().flush();
				}

			}
			catch(Exception e){
			}
		}
	}

}