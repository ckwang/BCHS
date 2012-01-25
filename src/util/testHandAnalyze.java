package util;

public class testHandAnalyze {
	public static void main(String[] args) {
		Hand h = new Hand(new Card("As"), new Card("9c"));
		h.addCards(new Card("2c"));
		h.addCards(new Card("2d"));
		h.addCards(new Card("4c"));
		
		System.out.println(h.analyzePossibleFullHouse());
		
	}
}
