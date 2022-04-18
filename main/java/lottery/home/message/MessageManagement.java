package lottery.home.message;


import lottery.community.Community;
import lottery.community.CommunityManagement;
import lottery.user.User;
import lottery.user.UserManagement;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MessageManagement {

	private final MessageRepository entries;
	private final CommunityManagement communityManagement;
	private final UserManagement userManagement;

	MessageManagement(MessageRepository messageRepository, CommunityManagement communityManagement,
					  UserManagement userManagement) {

		Assert.notNull(messageRepository, "messageRepository must not be null!");

		this.entries = messageRepository;
		this.communityManagement = communityManagement;
		this.userManagement = userManagement;
	}

	public void save(Message entry){
		entries.save(entry);
		checkTenMessages(entry.getUser());
	}

	public void checkTenMessages(String user){
		if(fineMessageCount(user) < 10){
			return;
		} else {
			UserAccount userAccount = userManagement.findByUsername(user).getUserAccount();
			for (Community community : communityManagement.findPersonalCommunities(userAccount)) {
				communityManagement.removeFromCommunity(community, userAccount);
			}
		}
	}

	public int fineMessageCount(String user){
		Streamable<Message>allMessage = findEntriesByUser(user);
		Streamable<Message>fineMessage=allMessage.filter(message -> "2 Euro Bußgeld".equals(message.getTopic()));
		return (int)fineMessage.stream().count();
	}

	public int messageCount(String user){
		return (int)entries.findByUser(user).stream().count();
	}

	public boolean newMessages(String user){
		return (int) entries.findByUserNotRead(user).stream().count() != 0;
	}

	public Streamable<Message> findNotReadByUser(String user){
		return entries.findByUserNotRead(user);
	}

	public Streamable<Message> findReadByUser(String user){
		return entries.findByUserRead(user);
	}

	public Streamable<Message> findEntriesByUser(String user){
		return entries.findByUser(user);
	}

	public Streamable<Message> findAll() { return entries.findAll(); }

	public void deleteMessage(Long messageId){ entries.deleteById(messageId); }
}
