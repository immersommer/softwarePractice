package lottery.community;

import org.jetbrains.annotations.NotNull;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
@Transactional
public class CommunityManagement {

	private final CommunityRepository communities;

	public CommunityManagement(CommunityRepository communities){

		Assert.notNull(communities, "CommunityRepository must not be null!");

		this.communities=communities;
	}

	public void createCommunity(CreateForm form){

		Assert.notNull(form, "Registration form must not be null!");

		communities.save(new Community(form.getName(), form.getPassword()));

	}

	public void joinCommunity(Community community, UserAccount user){

		Assert.notNull(community, "community must not be null!");
		Assert.notNull(user, "user must not be null!");

		community.addUser(user);
	}

	public void removeFromCommunity(Community community,UserAccount user){
//		Assert.notNull(community, "community must not be null!");
		Assert.notNull(user, "user must not be null!");

		community.deleteUser(user);
	}

	public Community findCommunityByForm(CreateForm form){

		Community community = communities.findByName(form.getName());
		if(community!=null && community.getPassword().equals(form.getPassword())) {
			return community;
		}
		return null;
	}

	public Community findCommunityByName(String name) { return communities.findByName(name); }

	public Streamable<Community> findAll() { return communities.findAll(); }

	public Set<Community> findPersonalCommunities(UserAccount user) {
		Streamable<Community> allCommunities = communities.findAll();
		Set<Community> personalCommunities = new HashSet<>();
		for (Community community : allCommunities){
			if(community.userInCommunity(user)) {
				personalCommunities.add(community);
			}
		}
		return personalCommunities;
	}

	public Set<Community> findJoinableCommunities(UserAccount user) {
		Streamable<Community> allCommunities = communities.findAll();
		Set<Community> personalCommunities = new HashSet<>();
		for (Community community : allCommunities){
			if(!community.userInCommunity(user)) {
				personalCommunities.add(community);
			}
		}
		return personalCommunities;
	}

	public Community findByCommunityName(String name){
		return communities.findByName(name);
	}

	public void deleteCommunity(String name){communities.delete(communities.findByName(name));}

}

