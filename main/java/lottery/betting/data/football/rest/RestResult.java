package lottery.betting.data.football.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResult {

	private int pointsTeam1;
	private int pointsTeam2;

	public RestResult(int pointsTeam1, int pointsTeam2) {
		this.pointsTeam1 = pointsTeam1;
		this.pointsTeam2 = pointsTeam2;
	}

	public int getPointsTeam1() {
		return pointsTeam1;
	}

	public void setPointsTeam1(int pointsTeam1) {
		this.pointsTeam1 = pointsTeam1;
	}

	public int getPointsTeam2() {
		return pointsTeam2;
	}

	public void setPointsTeam2(int pointsTeam2) {
		this.pointsTeam2 = pointsTeam2;
	}
}
