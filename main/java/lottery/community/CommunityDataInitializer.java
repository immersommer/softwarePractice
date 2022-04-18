package lottery.community;

import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommunityDataInitializer implements DataInitializer  {

	private static final Logger LOG = LoggerFactory.getLogger(CommunityDataInitializer.class);
	private final CommunityRepository communityRepository;

	CommunityDataInitializer(CommunityRepository communityRepository){

		this.communityRepository = communityRepository;
	}

	public void initialize() {

		LOG.info("Creating default communities.");

		if(communityRepository.findAll().isEmpty()) {

			var password = "123";

			communityRepository.save(new Community("gruppe", password));
			communityRepository.save(new Community("gewinner", password));
			communityRepository.save(new Community("loser", password));
		}
	}
}