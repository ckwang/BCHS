package util;

public class testHandAnalyze {
	public static void main(String[] args) {
		Hand h = new Hand(new Card("As"), new Card("9c"));
		h.addCards(new Card("Kc"));
		h.addCards(new Card("Qc"));
		h.addCards(new Card("Ac"));
		
		System.out.println(h.analyzePossibleFlush().size());
		
	}
}
