package lottery.betting.data;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public abstract class Data extends Product {

	private LocalDateTime date;
	private static DateTimeFormatter formatDateTime = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

	private Category category;

	private Result result;

	private Boolean set = false;

	@SuppressWarnings({ "unused", "deprecation" })
	protected Data() {}

	public Data(String id, Money price, LocalDateTime date, Category category) {

		super(id, price);

		this.date = date;
		this.category = category;
	}

	public String getDate() { return date.format(formatDateTime); }

	public Category getCategory() {
		return category;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		if (!isSet()) {
			this.result = result;
			set();
		}
	}

	public Boolean isSet() {
		return set;
	}

	public void set() { set = true; }

	public abstract String toString();
}
