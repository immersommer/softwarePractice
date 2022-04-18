package lottery.betting.data.number;

import lottery.betting.data.Result;

import java.util.Arrays;
import java.util.List;


public class SelectNumber implements Result<SelectNumber> {
	private final String numStr;
	private final int superNumber;

	public SelectNumber(String numStr, int superNumber){
	  this.numStr = numStr;
	  this.superNumber = superNumber;
	}

	public String getNumStr() {
		return numStr;
	}

	public int getSuperNumber() {
		return superNumber;
	}

	@Override
	public String toString(){
		return numStr + " // "+ superNumber;
	}

	private int countEquals(String otherNumStr){
		String[] strArr1 = numStr.split(",");
		List<String> list = Arrays.asList(strArr1);
		String[] strArr2 = otherNumStr.split(",");

		int equalCount = 0;
		for (String s : strArr2) {
			if (list.contains(s)) {
				equalCount++;
			}
		}
		return equalCount;
	}

	private int lotteryReturnWithSuperNumber(int count){
		int compared;
		switch(count){
			case 3:
				compared = 2;
				break;
			case 4:
				compared = 8;
				break;
			case 5:
				compared = 300;
				break;
			case 6:
				compared = 10000;
				break;
			default:
				compared = 0;
		}
		return compared;
	}

	private int lotteryReturnWithoutSuperNumber(int count){
		int compared;
		switch(count){
			case 3:
				compared = 1;
				break;
			case 4:
				compared = 4;
				break;
			case 5:
				compared = 120;
				break;
			case 6:
				compared = 800;
				break;
			default:
				compared = 0;
		}
		return compared;
	}

	@Override
	public int compareTo(SelectNumber o) {
		int equalCount = countEquals(o.numStr);

		if(superNumber == o.superNumber){
			return lotteryReturnWithSuperNumber(equalCount);
		} else {
			return lotteryReturnWithoutSuperNumber(equalCount);
		}
	}
}
