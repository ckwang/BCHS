package util;

import java.util.Random;


public class Equity {
	HandEval h;

	public Equity() {
		this.h = new HandEval();
	}

	public double[] calc(String[] hands) {
		int[] x = this.mapCards(hands);
		return h.computePreFlopEquityForSpecificHoleCards(this.mapCards(hands),
				hands.length);
	}

	public double[] calc(String[] hands, int iterations) {
		return null;
	}
	public double[] calc(String[] hands, String board) {
		int[] b = this.mapCards(board);
		int[] mapHands = this.mapCards(hands);
		double[] result = null;
		
		if (b.length == 3) {
			result = h.computeFlopEquityForSpecificCards(mapHands, b,
					hands.length);
		} else if (b.length == 4) {
			result = h.computeTurnEquityForSpecificCards(mapHands, b,
					hands.length);
		} else {
			result = h.computeRiverEquityForSpecificCards(mapHands,
					b, hands.length);
		}
		return result;
	}

	public double[] calc(String[] hands, String board, int iterations) {
		return null;
	}
	
	public double[] monteCarlo(String[] hands, String board) {
		
		int[] tempHole = this.mapCards(hands);
		int[] boardCards = this.mapCards(board);
		
		int[] holeCards = new int[4];
		holeCards[0] = tempHole[0];
		holeCards[1] = tempHole[1];
		
		Random rnd = new Random();
		
		double[] result = new double[8];
		for (int i = 0; i < 8; i++) {
			result[i] = 0;
		}
		
		for (int i = 0; i < 100; i++) {
			holeCards[2] = rnd.nextInt(52);
			holeCards[3] = rnd.nextInt(52);
//			holeCards[4] = rnd.nextInt(52);
//			holeCards[5] = rnd.nextInt(52);
			
			double[] temp = h.computeFlopEquityForSpecificCards(holeCards, boardCards, 2);
			for (int j = 0; j < 8; j++) {
				result[j] += temp[j];
			}
		}
		
		for (int i = 0; i < 8; i++) {
			result[i] /= 100;
		}
		
		return result;
	}
	
	public int[] mapCards(String hand) {
		hand = hand.replaceAll(",", "");
		hand = hand.replaceAll("(\\p{Ll})(\\p{Lu}|\\p{N})", "$1 $2");
		String[] hs = hand.split(" ");
		int[] mapped = new int[hs.length];
		int rank, suit;
		char c, s;
		for (int i = 0; i < hs.length; i++) {
			c = hs[i].charAt(0);
			switch (c) {
			case 'A':
				rank = 0;
				break;
			case 'K':
				rank = 1;
				break;
			case 'Q':
				rank = 2;
				break;
			case 'J':
				rank = 3;
				break;
			case 'T':
				rank = 4;
				break;
			default:
				rank = 14 - Character.getNumericValue(c);
			}

			s = hs[i].charAt(1);
			switch (s) {
			case 's':
				suit = 0;
				break;
			case 'h':
				suit = 1;
				break;
			case 'd':
				suit = 2;
				break;
			default:
				suit = 3;
				break;
			}
			mapped[i] = 4 * rank + suit;
		}
		return mapped;
	}

	private int[] mapCards(String[] hands) {
		int[] mapping = new int[2 * hands.length];
		for (int i = 0; i < hands.length; i++) {
			int[] h = this.mapCards(hands[i]);
			mapping[2 * i] = h[0];
			mapping[2 * i + 1] = h[1];
		}
		return mapping;
	}
}