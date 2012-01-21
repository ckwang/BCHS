package util;

public class Card {
	public final int s;
	public final int r;

	public Card(int s, int r) {
		this.s = s;
		this.r = r;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		switch (r) {
		case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
			result += r;
			break;
		case 10:
			result += "T";
			break;
		case 11:
			result += "J";
			break;
		case 12:
			result += "Q";
			break;
		case 13:
			result += "K";
			break;
		case 14:
			result += "A";
			break;
		}
		
		switch (s) {
		case 0:
			result += "s";
			break;
		case 1:
			result += "h";
			break;
		case 2:
			result += "c";
			break;
		case 3:
			result += "d";
			break;
		}
		
		return result;
	}
}
