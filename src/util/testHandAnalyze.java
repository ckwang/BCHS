package util;

import java.util.List;

public class testHandAnalyze {
	public static void main(String[] args) {
		Hand h = new Hand(new Card("Qc"), new Card("Qd"));
		h.addCards(new Card("Ks"));
		h.addCards(new Card("Ts"));
		h.addCards(new Card("Jd"));
		
		PokerTable.makeNormTable();
		List<Card> cards = h.analyzePossibleFlush();
		PokerTable.assignProb2(cards, 0.5);
		
		long start = System.nanoTime();
//		for (int i = 0; i < 100; i++) {
		List<Card> quadHands = h.analyzePossibleQuads();
		List<Card> fullhouseHands = h.analyzePossibleFullHouse();
		List<Card> flushHands = h.analyzePossibleFlush();
		List<Card> straightHands = h.analyzePossibleStraight();
		List<Double> quadProbs = PokerTable.assignProb2(quadHands, 0.5);
		List<Double> fullhouseProbs = PokerTable.assignProb2(fullhouseHands, 0.5);
		List<Double> flushProbs = PokerTable.assignProb2(flushHands, 0.5);
		List<Double> straightProbs = PokerTable.assignProb2(straightHands, 0.5);
//		}
		long end = System.nanoTime();
		
//		// renormalize winning prob if opp check
//		for (int i = 0; i < quadProbs.size(); i++) {
//
//		}
//		for (int i = 0; i < fullhouseProbs.size(); i++) {
//
//		}
//		for (int i = 0; i < flushProbs.size(); i++) {
//
//		}
//		for (int i = 0; i < straightProbs.size(); i++) {
//			
//		}
//		
		
		
		System.out.println((end-start)/1000000.0);
		double sum = 0;
		for (int i = 0; i < quadProbs.size(); i++) {
			System.out.println(quadHands.get(2*i) + ", " + quadHands.get(2*i+1) + ": " + quadProbs.get(i));
			sum += quadProbs.get(i);
		}
		for (int i = 0; i < fullhouseProbs.size(); i++) {
			System.out.println(fullhouseHands.get(2*i) + ", " + fullhouseHands.get(2*i+1) + ": " + fullhouseProbs.get(i));
			sum += fullhouseProbs.get(i);
		}
		for (int i = 0; i < flushProbs.size(); i++) {
			System.out.println(flushHands.get(2*i) + ", " + flushHands.get(2*i+1) + ": " + flushProbs.get(i));
			sum += flushProbs.get(i);
		}
		for (int i = 0; i < straightProbs.size(); i++) {
			System.out.println(straightHands.get(2*i) + ", " + straightHands.get(2*i+1) + ": " + straightProbs.get(i));
			sum += straightProbs.get(i);
		}
		
		System.out.println(sum / 47 / 46 * 2);
	}
}
