package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DrawHand {
	private List<Card> communityCards = new ArrayList<Card>();
	private Set<Integer> usedCards = new HashSet<Integer>();
	
	public DrawHand(Card c1, Card c2, Card c3) {
		communityCards.add(c1);
		communityCards.add(c2);
		communityCards.add(c3);
		usedCards.add(c1.toValue());
		usedCards.add(c2.toValue());
		usedCards.add(c3.toValue());
	}
	
	public DrawHand(Card c1, Card c2, Card c3, Card c4) {
		this(c1, c2, c3);
		communityCards.add(c4);
		usedCards.add(c4.toValue());
	}
	
	public static int twoLibCardsToInt(int lc1, int lc2) {
		int c1 = Card.libValueToValue(lc1);
		int c2 = Card.libValueToValue(lc2);
		
		return (c2 > c1) ? (c1 * 52 + c2) : (c2 * 52 + c1);
	}
	
	public static int twoCardsToInt(int c1, int c2) {
		return (c2 > c1) ? (c1 * 52 + c2) : (c2 * 52 + c1);
	}
	
	
	public int[] analyzePossibleFlushDraw() {		
		int[] result = new int[52*52];
		int[] suitCount = new int[4];
		
		for (Card c : communityCards) {
			suitCount[c.s]++;
		}
		
		for (int i = 0; i < 4; i++) {
			switch (suitCount[i]) {
			case 4:
				// hold card: 2 different suit
				for (int h1 = 0; h1 < 52; h1++) {
					if (usedCards.contains(h1) || new Card(h1).s == i)	continue;
					for (int h2 = 0; h2 < 52; h2++) {
						if (usedCards.contains(h2) || new Card(h2).s == i)	continue;
						
						result[twoCardsToInt(h1, h2)] += 9;
					}
				}
				break;
			case 3:
				// hole card: 1 same suit, 1 different suit
				for (int hr1 = 0; hr1 < 13; hr1++) {
					if (usedCards.contains(Card.toValue(i, hr1)))	continue;
					for (int h2 = 0; h2 < 52; h2++) {
						if (usedCards.contains(h2) || new Card(h2).s == i)	continue;
						
						result[twoCardsToInt(Card.toValue(i, hr1), h2)] += 9;
					}
				}
				break;
			case 2:
				// hole card: 2 same suit
				for (int hr1 = 0; hr1 < 13; hr1++) {
					if (usedCards.contains(Card.toValue(i, hr1)))	continue;
					for (int hr2 = hr1 + 1; hr2 < 13; hr2++) {
						if (usedCards.contains(Card.toValue(i, hr2)))	continue;
						
						result[twoCardsToInt(Card.toValue(i, hr1), Card.toValue(i, hr2))] += 9;
					}
				}
				break;
			default:
				continue;
			}
		}
		
		return result;
	}
	
	public int[] analyzePossibleStraightDraw() {
		int[] result = new int[52*52];
		Map<Integer,boolean[]> flag = new HashMap<Integer,boolean[]>();
		
		// others
		for (int i = 0; i <= 8; i++) {
			boolean[] occupied = new boolean[5];
			int count = 0;
			
			for (Card c : communityCards) {
				if (i == -1) {	// A2345
					if (c.r == 12)	occupied[0] = true;
					if (c.r >= 0 && c.r < 4) {
						occupied[c.r + 1] = true;
					}
				} else {	// other cases
					if (c.r >= i && c.r < i + 5) {
						occupied[c.r - i] = true;
					}
				}

			}
			
			for (int j = 0; j < 5; j++) {
				if (occupied[j])	count++;
			}
			
			switch (count) {
			case 4:
				// hole card: 2 not in straight
				int rank = 0;
				for (int j = 0; j < 5; j++) {
					if (occupied[j])	continue;
					if (i == -1) {
						rank = (j == 0) ? 12 : (j - 1);
					} else {
						rank = i + j;
					}
				}
				
				for (int h1 = 0; h1 < 52; h1++) {
					if (usedCards.contains(h1) || new Card(h1).r == rank)	continue;	
					for (int h2 = 0; h2 < 52; h2++) {
						if (usedCards.contains(h2) || new Card(h2).r == rank)	continue;
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				break;
			case 3: {
				// hole card: 1 in straight, 1 not in straight
				int rank1 = 0, rank2 = 0;
				for (int j = 0; j < 5; j++) {
					if (!occupied[j]) {
						if (i == -1) {
							rank1 = (j == 0) ? 12 : (j - 1);
						} else {
							rank1 = i + j;
						}
						for (int k = j + 1; k < 5; k++) {
							if (!occupied[k]) {
								if (i == -1) {
									rank2 = (j == 0) ? 12 : (j - 1);
								} else {
									rank2 = i + j;
								}
							}
						}
					}
				}
				
				// rank1
				for (int h1s = 0; h1s < 4; h1s++) {
					int h1 = Card.toValue(h1s, rank1);
					for (int h2 = 0; h2 < 52; h2++) {
						if (usedCards.contains(h2) || (new Card(h2).r == rank2)
								|| ((new Card(h2).r == rank1) && (new Card(h2).s <= h1s)))	continue;
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank2] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				// rank2
				for (int h1s = 0; h1s < 4; h1s++) {
					int h1 = Card.toValue(h1s, rank2);
					for (int h2 = 0; h2 < 52; h2++) {
						if (usedCards.contains(h2) || (new Card(h2).r == rank1)
								|| ((new Card(h2).r == rank2) && (new Card(h2).s <= h1s)))	continue;
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank1] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				break;
			}
			case 2: {
				// hole card: 2 in straight
				int rank1 = 0, rank2 = 0, rank3 = 0;
				boolean flag1 = false, flag2 = false;
				for (int j = 0; j < 5; j++) {
					if (!occupied[j]) {
						if (!flag1) {
							if (i == -1) {
								rank1 = (j == 0) ? 12 : (j - 1);
							} else {
								rank1 = i + j;
							}
							flag1 = true;
						} else if (!flag2) {
							if (i == -1) {
								rank2 = (j == 0) ? 12 : (j - 1);
							} else {
								rank2 = i + j;
							}
							flag2 = true;
						} else {
							if (i == -1) {
								rank3 = (j == 0) ? 12 : (j - 1);
							} else {
								rank3 = i + j;
							}
						}
					}
				}
								
				// rank1, rank2
				for (int h1s = 0; h1s < 4; h1s++) {
					int h1 = Card.toValue(h1s, rank1);
					for (int h2s = 0; h2s < 4; h2s++) {
						int h2 = Card.toValue(h2s, rank2);
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank3] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				// rank1, rank3
				for (int h1s = 0; h1s < 4; h1s++) {
					int h1 = Card.toValue(h1s, rank1);
					for (int h2s = 0; h2s < 4; h2s++) {
						int h2 = Card.toValue(h2s, rank3);
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank2] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				// rank2, rank3
				for (int h1s = 0; h1s < 4; h1s++) {
					int h1 = Card.toValue(h1s, rank2);
					for (int h2s = 0; h2s < 4; h2s++) {
						int h2 = Card.toValue(h2s, rank3);
						
						int pairInt = twoCardsToInt(h1, h2);
						if (!flag.containsKey(pairInt)) {
							flag.put(pairInt, new boolean[13]);
						}
						flag.get(pairInt)[rank1] = true;
						//result[twoCardsToInt(h1, h2)] += 4;
					}
				}
				
				break;
			}
			default:
				continue;
			}
		}
		
		for (Entry<Integer, boolean[]> e : flag.entrySet()) {
			int count = 0;
			boolean[] f = e.getValue();
			for (int i = 0; i < 13; i++) {
				if (f[i])	count++;
			}
			
			result[e.getKey()] = count * 4;
		}
		
		return result;
	}

	public static void main(String[] args) {
		DrawHand dh = new DrawHand(new Card("3s"), new Card("7s"), new Card("8s"));
		
		int[] flush = dh.analyzePossibleFlushDraw();
		int[] straight = dh.analyzePossibleStraightDraw();
		
		int h1 = new Card("4s").toValue();
		int h2 = new Card("5h").toValue();
		
		System.out.println(flush[twoCardsToInt(h1, h2)]);
		System.out.println(straight[twoCardsToInt(h1, h2)]);
	}
}
