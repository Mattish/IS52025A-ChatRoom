import java.io.Serializable;

public class Message implements Serializable{
	public enum Type{
		JOIN,MESSAGE,WHO,QUIT
	};

	public String value1,value2;
	public Type type;

	public Message(Message.Type type, String value1, String value2){
		this.type = type;
		//!join username
		//@user:message
		//!who
		//!quit
		this.value1 = value1;
		this.value2 = value2;
	}
}