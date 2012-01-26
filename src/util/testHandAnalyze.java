package util;

import java.util.List;

import lib.FiveEval;
import java.util.Random;

public class testHandAnalyze {
	public static void main(String[] args) {
		Hand h = new Hand(new Card("Ac"), new Card("Ad"));
		h.addCards(new Card("7s"));
		h.addCards(new Card("3s"));
		h.addCards(new Card("3d"));
		
		PokerTable.makeNormTable();
		List<Card> cards = h.analyzePossibleFlush();
		PokerTable.assignProb2(cards, 0.5);
		
		long start = System.nanoTime();
//		for (int i = 0; i < 100; i++) {
		List<Card> quadHands = h.analyzePossibleQuads();
		List<Card> fullhouseHands = h.analyzePossibleFullHouse();
		List<Card> flushHands = h.analyzePossibleFlush();
		List<Card> straightHands = h.analyzePossibleStraight();
		List<Card> tripletHands = h.analyzePossibleTriplet();
		List<Card> twoPairHands = h.analyzePossibleTwoPairs();
		List<Double> quadProbs = PokerTable.assignProb2(quadHands, 0.5);
		List<Double> fullhouseProbs = PokerTable.assignProb2(fullhouseHands, 0.5);
		List<Double> flushProbs = PokerTable.assignProb2(flushHands, 0.5);
		List<Double> straightProbs = PokerTable.assignProb2(straightHands, 0.5);
		List<Double> tripletProbs = PokerTable.assignProb2(tripletHands, 0.5);
		List<Double> twoPairProbs = PokerTable.assignProb2(twoPairHands, 0.5);
//		}
		long end = System.nanoTime();
		
		FiveEval e = new FiveEval();
		Random r = new Random();
		
		// Monte Carlo p1
		start = System.nanoTime();
		int rank_our = e.getRankOf(h.hole[0].toLibValue(), h.hole[1].toLibValue(),
				h.community.get(0).toLibValue(), h.community.get(1).toLibValue(), h.community.get(2).toLibValue());
		
		int win_count = 0;
		
		for (int i = 0; i < 1000; i++) {
			int c1, c2;
			do {
				c1 = r.nextInt(52);
			} while (h.usedCards.contains(c1));
			do {
				c2 = r.nextInt(52);
			} while (h.usedCards.contains(c2) || (c1 == c2));
			int c1_lib = new Card(c1).toLibValue();
			int c2_lib = new Card(c2).toLibValue();
			
			int rank_opp = e.getRankOf(c1_lib, c2_lib,
				h.community.get(0).toLibValue(), h.community.get(1).toLibValue(), h.community.get(2).toLibValue());
			
			if (rank_opp > rank_our)	win_count++;
		}
		end = System.nanoTime();
		System.out.println("win_prob: " + (win_count * 1.0 / 1000));
		
		// renormalize winning prob if opp check
//		for (int i = 0; i < quadProbs.size(); i++) {
//			for (int j = 0; j < quadProbs.size(); j++) {
//				if (i == j)	continue;
//				int ranki = e.getRankOf(quadHands.get(2*i).toLibValue(), quadHands.get(2*i+1).toLibValue(),
//						h.community.get(0).toLibValue(), h.community.get(1).toLibValue(), h.community.get(2).toLibValue());
//				int rankj = e.getRankOf(quadHands.get(2*j).toLibValue(), quadHands.get(2*j+1).toLibValue(),
//						h.community.get(0).toLibValue(), h.community.get(1).toLibValue(), h.community.get(2).toLibValue());
//				
//				
//			}
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

		
		
		System.out.println((end-start)/1000000.0);
//		double sum = 0;
//		for (int i = 0; i < quadProbs.size(); i++) {
//			System.out.println(quadHands.get(2*i) + ", " + quadHands.get(2*i+1) + ": " + quadProbs.get(i));
//			sum += quadProbs.get(i);
//		}
//		for (int i = 0; i < fullhouseProbs.size(); i++) {
//			System.out.println(fullhouseHands.get(2*i) + ", " + fullhouseHands.get(2*i+1) + ": " + fullhouseProbs.get(i));
//			sum += fullhouseProbs.get(i);
//		}
//		for (int i = 0; i < flushProbs.size(); i++) {
//			System.out.println(flushHands.get(2*i) + ", " + flushHands.get(2*i+1) + ": " + flushProbs.get(i));
//			sum += flushProbs.get(i);
//		}
//		for (int i = 0; i < straightProbs.size(); i++) {
//			System.out.println(straightHands.get(2*i) + ", " + straightHands.get(2*i+1) + ": " + straightProbs.get(i));
//			sum += straightProbs.get(i);
//		}
//		for (int i = 0; i < tripletProbs.size(); i++) {
//			System.out.println(/*tripletHands.get(2*i) + ", " + tripletHands.get(2*i+1) + ": " + */tripletProbs.get(i));
//			sum += tripletProbs.get(i);
//		}
//		for (int i = 0; i < twoPairProbs.size(); i++) {
//			System.out.println(/*tripletHands.get(2*i) + ", " + tripletHands.get(2*i+1) + ": " + */twoPairProbs.get(i));
//			sum += twoPairProbs.get(i);
//		}
		
//		for (int i = 0; i < twoPairHands.size() / 2; i++) {
//			System.out.println(twoPairHands.get(2*i) + ", " + twoPairHands.get(2*i+1) + ": ");
////			sum += twoPairProbs.get(i);
//		}
		
//		System.out.println(sum / 47 / 46 * 2);
	}
}
