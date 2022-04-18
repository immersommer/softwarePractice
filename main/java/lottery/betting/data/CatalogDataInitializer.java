package lottery.betting.data;

import lottery.betting.data.football.FootballMatch;
import lottery.betting.data.football.rest.RestAPIParser;
import lottery.betting.data.number.LotteryEntity;
import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static org.salespointframework.core.Currencies.EURO;


@Component
@Order(20)
class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private final DataCatalog catalog;

	private final RestAPIParser parser;

	CatalogDataInitializer(DataCatalog catalog, RestAPIParser parser) {
		Assert.notNull(catalog, "DataCatalog must not be null!");
		this.catalog = catalog;
		this.parser = parser;
	}

	@Override
	public void initialize() {

		if (!catalog.findAll().iterator().hasNext()) {
			LOG.info("Creating default catalog entries.");

			//create FootballMatches
			for(FootballMatch match : parser.getNextMatchday()){
				catalog.save(match);
			}
			LOG.info("Retrieved data successfully from OpenLigaDB.");

			//create LotteryEntities
			LocalDateTime nextSunday = LocalDateTime.now();
			while(nextSunday.getDayOfWeek() != DayOfWeek.SUNDAY){
				nextSunday = nextSunday.plusDays(1);
			}

			LocalDateTime date = LocalDateTime.of(nextSunday.getYear(), nextSunday.getMonth(),
					nextSunday.getDayOfMonth(), 20,0);

			for(long i = 0; i < 69; i++){
				catalog.save(new LotteryEntity("Tippschein"+i, Money.of(10, EURO),
						date.plusWeeks(i), Category.LOTTERY, "Lotto 6 aus 49"));
			}
		}
	}
}
