package util;

public class Card {
	public final int s;
	public final int r;

	public Card(int s, int r) {
		this.s = s;
		this.r = r;
	}
	
	public Card(int n) {
		this.r = n / 4;
		this.s = n % 4;
	}

	public Card(String input) {
		switch (input.charAt(0)) {
		case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
			r = input.charAt(0) - '2';
			break;
		case 'T':
			r = 8;
			break;
		case 'J':
			r = 9;
			break;
		case 'Q':
			r = 10;
			break;
		case 'K':
			r = 11;
			break;
		case 'A':
			r = 12;
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
	
	public static int toValue(int s, int r) {
		return r*4 + s;
	}
	
	public static int valueToSuit(int n) {
		return n % 4;
	}
	
	public static int valueToRank(int n) {
		return n / 4;
	}

	public int toLibValue() {
		int nr = 12 - r;
		int ns;
		switch (s) {
		case 2:
			ns = 3;
			break;
		case 3:
			ns = 2;
			break;
		default:
			ns = s;
		}
		return r*4 + s;
	}
	
	public int toValue() {
		return r*4 + s;
	}

	public static int pairToValue(int c1, int c2) {
		assert c1 != c2;
		if (c1 > c2)
			return c2*52 + c1;
		else
			return c1*52 + c2;
	}
	
	@Override
	public String toString() {
		String result = "";
		switch (r) {
		case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
			result += r + 2;
			break;
		case 8:
			result += "T";
			break;
		case 9:
			result += "J";
			break;
		case 10:
			result += "Q";
			break;
		case 11:
			result += "K";
			break;
		case 12:
			result += "A";
			break;
		case -1:
			result += "_";
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
		case -1:
			result += "_";
		}
		return result;
	}
}
