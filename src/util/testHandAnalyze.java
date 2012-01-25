package util;

import java.util.List;

public class testHandAnalyze {
	public static void main(String[] args) {
		Hand h = new Hand(new Card("Qc"), new Card("Qd"));
		h.addCards(new Card("Ks"));
		h.addCards(new Card("9s"));
		h.addCards(new Card("3s"));
		
		List<Card> cards = h.analyzePossibleFlush();
		PokerTable.assignProb2(cards, 0.5);
		
		long start = System.nanoTime();
//		for (int i = 0; i < 100; i++) {
//			h.analyzePossibleQuads();
//			h.analyzePossibleStraight();
		cards = h.analyzePossibleFlush();
		List<Double> probs = PokerTable.assignProb2(cards, 0.5);
//		}
		long end = System.nanoTime();
		
		System.out.println((end-start)/1000000.0);
		
		for (int i = 0; i < probs.size(); i++) {
			System.out.println(cards.get(2*i) + ", " + cards.get(2*i+1) + ": " + probs.get(i));
		}
		
	}
}
