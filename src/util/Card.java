package util;

public class Card {
	public final int s;
	public final int r;

	public Card(int s, int r) {
		this.s = s;
		this.r = r;
	}
	
	public Card(String input) {
		switch (input.charAt(0)) {
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			r = input.charAt(0) - '0';
			break;
		case 'T':
			r = 10;
			break;
		case 'J':
			r = 11;
			break;
		case 'Q':
			r = 12;
			break;
		case 'K':
			r = 13;
			break;
		case 'A':
			r = 14;
			break;
		default:
			System.out.println("Hole card rank parse error.");
			r = -1;
		}
		switch (input.charAt(1)) {
			case 's':
				s = 0;
				break;
			case 'h':
				s = 1;
				break;
			case 'c':
				s = 2;
				break;
			case 'd':
				s = 3;
				break;
			default:
				System.out.println("Hole card suit parse error.");
				s = -1;
		}
	}
	
	public int toValue() {
		return (r-2)*4 + s;
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
