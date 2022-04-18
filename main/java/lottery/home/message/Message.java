package lottery.home.message;

import org.salespointframework.useraccount.UserAccount;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String user;
	private String topic;
	private String detail;
	private LocalDateTime date;
	private static DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
	private boolean read = false;
	@SuppressWarnings("unused")
	protected Message() {}

	public Message(UserAccount user, String topic, String detail, LocalDateTime date){
		this.user = user.getUsername();
		this.topic = topic;
		this.detail= detail;
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public String getUser() {
		return user;
	}

	public String getTopic(){
		return topic;
	}

	public String getDetail(){
		return detail;
	}

	public String getDate() { return date.format(formatDateTime); }

	public boolean getRead() { return read; }

	public void setRead() { read = true; }

}
