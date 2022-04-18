package lottery.betting.data.football;

import lottery.betting.data.Result;

public class Score implements Result<Score> {
	private final int home, guest, difference;

	public Score(int home, int guest){
		this.home = home;
		this.guest = guest;
		this.difference = home - guest;
	}

	public int getHome() {
		return home;
	}

	public int getGuest() {
		return guest;
	}

	public int getDifference() {return difference;}

	@Override
	public String toString(){
		return home + " : " + guest;
	}

	@Override
	public int compareTo(Score o) {
		int compared = 0;
		int otherDifference = o.getDifference();
		if(o.getHome() == home && o.getGuest() == guest) {
			compared = 3;
		} else if(difference == otherDifference) {
			if(difference != 0){
				compared = 2;
			} else{
				compared = 1;
			}
		} else if((difference > 0 && otherDifference > 0) || (difference < 0 && otherDifference < 0)) {
			compared = 1;
		}
		return compared;
	}
}
