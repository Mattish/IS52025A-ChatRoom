import java.io.Serializable;

public class Message implements Serializable{
	public enum Type{
		JOIN,DIRECT,MESSAGE,WHO,QUIT
	};

	public String value1,value2, sender;
	public Type type;

	public Message(){
		sender = "";
		value1 = "";
		value2 = "";

	}

	public Message(Message.Type type, String value1, String value2){
		this.type = type;
		sender = "";
		this.value1 = value1;
		this.value2 = value2;
	}

	public Message(Message inputMessage){
		type = inputMessage.type;
		value1 = inputMessage.value1;
		value2 = inputMessage.value2;
		sender = inputMessage.sender;
	}
}