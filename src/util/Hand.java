package util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.FiveEval;

public class Hand {
	public boolean pocket = false;
	public int flushSuit = -1;
	public int straightLast = -1;
	public int quadRank = -1;
	public int tripRank = -1;
	public int pairRank1 = -1;
	public int pairRank2 = -1;
	public int singRank = -1;
	public Category bestCategory = Category.Nothing;
	public Card[] hole = new Card[2];
	public List<Card> community;
	public List<Card> bestHand;
	public int[] suitCount = {0, 0, 0, 0};
	public int[] rankCount = new int[13];
	public Set<Integer> usedCards = new HashSet<Integer>();

	public Hand(Card c1, Card c2) {
		hole[0] = c1;
		hole[1] = c2;
		Arrays.fill(rankCount, 0);
		if (c1.r == c2.r) {
			bestCategory = Category.Pair;
			pocket = true;
			pairRank1 = c1.r;
		}
		community = new ArrayList<Card>();
		usedCards.add(c1.toValue());
		usedCards.add(c2.toValue());
	}

	public void addCards(Card c) {
		community.add(c);
		rankCount[c.r] += 1;
		suitCount[c.s] += 1;
		usedCards.add(c.toValue());
	}

	public Category bestCategory() {
		int rank;
		switch (community.size()) {
			case 3:
				rank = FiveEval.getBestRankOf(hole[0].toValue(), hole[1].toValue(),
					community.get(0).toValue(), community.get(1).toValue(), community.get(2).toValue());
				return FiveEval.rankToCategory(rank);
			case 4:
				rank = FiveEval.getBestRankOf(hole[0].toValue(), hole[1].toValue(), community.get(0).toValue(),
					community.get(1).toValue(), community.get(2).toValue(), community.get(3).toValue());
				return FiveEval.rankToCategory(rank);
			case 5:
				rank = FiveEval.getBestRankOf(hole[0].toValue(), hole[1].toValue(), community.get(0).toValue(),
					community.get(1).toValue(), community.get(2).toValue(), community.get(3).toValue(), community.get(4).toValue());
				return FiveEval.rankToCategory(rank);
			default:
				return Category.Nothing;
		}
	}
	
	public List<Card> analyzePossibleQuads() {
		List<Card> result = new ArrayList<Card>();
		for (int i = 0; i < 13; i++) {
			boolean[] suit = new boolean[4];
			for (Card c : community) {
				if (c.r == i) {
					suit[c.s] = true;
				} 
			}
			
			switch (rankCount[i]) {
			case 4:
				// TODO: add the case for 4 cards with same rank
				break;
			case 3:
				for (int j = 0; j < 4; j++) {
					if (suit[j])	continue;
					int first = Card.toValue(j, i);
					if (usedCards.contains(first))	continue;
					
					for (int k = 0; k < 52; k++) {
						if (usedCards.contains(k) || k == first)	continue;
						result.add(new Card(j, i));
						result.add(new Card(k));
					}
				}
				break;
			case 2:
				if (hole[0].r == i || hole[1].r ==i)	break;
				
				for (int j = 0; j < 4; j++) {
					if (suit[j])	continue;
					result.add(new Card(j, i));
				} break;
			default:
				continue;
			}			
		}
		
		return result;
	}

	public List<Card> analyzePossibleFullHouse() {
		List<Card> result = new ArrayList<Card>();
		for (int i = 0; i < 13; i++) {	// long
			for (int j = 0; j < 13; j++) {	// short
				if (i == j)	continue;
				int necessaryLong = Math.max(3 - rankCount[i], 0);
				int necessaryShort = Math.max(2 - rankCount[j], 0);
				switch (necessaryLong + necessaryShort) {
				case 0:
					// TODO: what to do if already in this category
				case 1:
					if (necessaryLong == 1) {	// need 1 long
						boolean[] usedSuit = new boolean[4]; 
						
						for (Card c : community) {
							if (c.r == i)	usedSuit[c.s] = true;
						}
						
						for (int k = 0; k < 4; k++) {
							if (usedSuit[k])	continue;
							int first = Card.toValue(k, i);
							if (usedCards.contains(first))	continue;
							
							for (int w = 0; w < 52; w++) {
								if (usedCards.contains(w) || Card.valueToRank(w) == i)	continue;
								
								result.add(new Card(first));
								result.add(new Card(w));
							}
						}
					} else {	// need 1 short
						boolean[] usedSuit = new boolean[4]; 
						
						for (Card c : community) {
							if (c.r == j)	usedSuit[c.s] = true;
						}
						
						for (int k = 0; k < 4; k++) {
							if (usedSuit[k])	continue;
							int first = Card.toValue(k, j);
							if (usedCards.contains(first))	continue;
							
							for (int w = 0; w < 52; w++) {
								if (usedCards.contains(w) || Card.valueToRank(w) == j)	continue;
								
								result.add(new Card(first));
								result.add(new Card(w));
							}
						}
					}
					break;
				case 2:
					switch (necessaryLong) {
					case 0:
						boolean[] usedSuit = new boolean[4]; 
						
						for (Card c : community) {
							if (c.r == j)	usedSuit[c.s] = true;
						}
						
						for (int k = 0; k < 4; k++) {
							if (usedSuit[k])	continue;
							int first = Card.toValue(k, j);
							if (usedCards.contains(first))	continue;
							
							for (int w = k + 1; w < 4; w++) {
								int second = Card.toValue(w, j);
								if (usedCards.contains(second))	continue;
								
								result.add(new Card(first));
								result.add(new Card(second));
							}
						}
						
						break;
					case 1:
						boolean[] usedSuitLong = new boolean[4];
						boolean[] usedSuitShort = new boolean[4]; 
						
						for (Card c : community) {
							if (c.r == i)	usedSuitLong[c.s] = true;
							if (c.r == j)	usedSuitShort[c.s] = true;
						}
						
						for (int k = 0; k < 4; k++) {
							if (usedSuitLong[k])	continue;
							int first = Card.toValue(k, i);
							if (usedCards.contains(first))	continue;
							
							for (int w = 0; w < 4; w++) {
								if (usedSuitShort[w])	continue;
								int second = Card.toValue(w, j);
								if (usedCards.contains(second))	continue;
								
								result.add(new Card(first));
								result.add(new Card(second));
							}
						}
						
						break;
					case 2:
						usedSuit = new boolean[4]; 
						
						for (Card c : community) {
							if (c.r == i)	usedSuit[c.s] = true;
						}
						
						for (int k = 0; k < 4; k++) {
							if (usedSuit[k])	continue;
							int first = Card.toValue(k, i);
							if (usedCards.contains(first))	continue;
							
							for (int w = k + 1; w < 4; w++) {
								int second = Card.toValue(w, i);
								if (usedCards.contains(second))	continue;
								
								result.add(new Card(first));
								result.add(new Card(second));
							}
						}
						
						break;
					}
					break;
				default:
					continue;
				}
			}
		}
		return result;
	}

	public List<Card> analyzePossibleFlush() {
		List<Card> result = new ArrayList<Card>();
		for (int i = 0; i < 4; i++) {	
			switch (suitCount[i]) {
			case 5:
				// TODO: what to do if already in this category
				break;
			case 4:
				for (int j = 0; j < 13; j++) {
					if (usedCards.contains(Card.toValue(i, j)))	continue;
					for (int k = 0; k < 52; k++) {
						if (usedCards.contains(Card.toValue(i, k)) || k == Card.toValue(i, j))	continue;
						result.add(new Card(i, j));
						result.add(new Card(k));
					}
				}
				break;
			case 3:		
				for (int j = 0; j < 13; j++) {
					if (usedCards.contains(Card.toValue(i, j)))	continue;
					for (int k = j + 1; k < 13; k++) {
						if (usedCards.contains(Card.toValue(i, k)))	continue;
						result.add(new Card(i, j));
						result.add(new Card(i, k));
					}
				}
				break;
			default:
				continue;
			}
		}
		
		return result;
	}

	public List<Card> analyzePossibleStraight() {
		List<Card> result = new ArrayList<Card>();
		// A2345
		{
			boolean[] occupied = new boolean[5];
			int count = 0;
			
			for (Card c : community) {
				if (c.r == 12)	occupied[0] = true;
				if (c.r >= 0 && c.r < 4) {
					occupied[c.r + 1] = true;
				}
			}
			
			for (int j = 0; j < 5; j++) {
				if (occupied[j])	count++;
			}
			
			switch (count) {
			case 5:
				// TODO: what to do with same category
				break;
			case 4:
				int rank = 0;
				for (int j = 0; j < 5; j++) {
					if (occupied[j])	continue;
					rank = (j == 0) ? 12 : (j - 1);
				}
				
				for (int j = 0; j < 4; j++) {
					if (usedCards.contains(Card.toValue(j, rank)))	continue;	
					for (int k = 0; k < 52; k++) {
						if (usedCards.contains(k) || k == Card.toValue(j, rank))	continue;
						result.add(new Card(j, rank));
						result.add(new Card(k));
					}
				}
				
				break;
			case 3:
				int rank1 = 0, rank2 = 0;
				for (int j = 0; j < 5; j++) {
					if (!occupied[j]) {
						rank1 = (j == 0) ? 12 : (j - 1);
						for (int k = j + 1; k < 5; k++) {
							if (!occupied[k])	rank2 = (k == 0) ? 12 : (k - 1);
						}
						break;
					}
				}
				
				for (int j = 0; j < 4; j++) {
					if (usedCards.contains(Card.toValue(j, rank1)))	continue;
					for (int k = 0; k < 4; k++) {
						if (usedCards.contains(Card.toValue(k, rank2)))	continue;
						result.add(new Card(j, rank1));
						result.add(new Card(k, rank2));
					}
				}
				
				break;
			}
		}
		
		// others
		for (int i = 0; i <= 8; i++) {
			boolean[] occupied = new boolean[5];
			int count = 0;
			
			for (Card c : community) {
				if (c.r >= i && c.r < i + 5) {
					occupied[c.r - i] = true;
				}
			}
			
			for (int j = 0; j < 5; j++) {
				if (occupied[j])	count++;
			}
			
			switch (count) {
			case 5:
				// TODO: what to do with same category
				break;
			case 4:
				int rank = 0;
				for (int j = 0; j < 5; j++) {
					if (occupied[j])	continue;
					rank = i + j;
				}
				
				for (int j = 0; j < 4; j++) {
					if (usedCards.contains(Card.toValue(j, rank)))	continue;	
					for (int k = 0; k < 52; k++) {
						if (usedCards.contains(k) || k == Card.toValue(j, rank))	continue;
						result.add(new Card(j, rank));
						result.add(new Card(k));
					}
				}
				
				break;
			case 3:
				int rank1 = 0, rank2 = 0;
				for (int j = 0; j < 5; j++) {
					if (!occupied[j]) {
						rank1 = i + j;
						for (int k = j + 1; k < 5; k++) {
							if (!occupied[k])	rank2 = i + k;
						}
						break;
					}
				}
				
				for (int j = 0; j < 4; j++) {
					if (usedCards.contains(Card.toValue(j, rank1)))	continue;
					for (int k = 0; k < 4; k++) {
						if (usedCards.contains(Card.toValue(k, rank2)))	continue;
						result.add(new Card(j, rank1));
						result.add(new Card(k, rank2));
					}
				}
				
				break;
			default:
				continue;
			}
		}
		return result;
	}

//	public List<Card> analyzePossibleFlushDraw() {
//		assert community.size() < 5;
//		
//		List<Card> result = new ArrayList<Card>();
//		int[] suitCount = new int[4];
//		
//		for (Card c : community) {
//			suitCount[c.s]++;
//		}
//		
//		for (int i = 0; i < 4; i++) {
//			switch (suitCount[i]) {
//			case 4:
//				// TODO
//				break;
//			case 3:
//				
//				break;
//			case 2:
//				
//				break;
//			default:
//				continue;
//			}
//		}
//		
//	}

	public List<Card> analyzePossibleTriplet() {
		List<Card> result = new ArrayList<Card>();
		for (int i = 0; i < 13; ++i ) {
			if (rankCount[i] == 1) {
				result.add(new Card(-1, i));
				result.add(new Card(-1, i));
			}
			else if (rankCount[i] == 2) {
				result.add(new Card(-1, i));
				result.add(new Card(-1, -1));
			}
			else if (rankCount[i] >= 3) {
				return null;
			}
		}
		return result;
	}
	
	// have not eliminate possibilities of getting full house or triplets
	public List<Card> analyzePossibleTwoPairs() {
		List<Card> result = new ArrayList<Card>();
		List<Integer> pairs = new ArrayList<Integer>();
		List<Integer> singles = new ArrayList<Integer>();
		for (int i = 0; i < 13; ++i ) {
			if (rankCount[i] == 1) {
				singles.add(i);
			}
			else if (rankCount[i] >= 2) {
				pairs.add(i);
			}
		}
		if (pairs.size() >= 2)
			return null;
		if (singles.size() > 1) {
			for (int i = 1; i < singles.size(); ++i) {
				for (int j = 0; j < i; ++j) {
					result.add(new Card(-1, singles.get(i)));
					result.add(new Card(-1, singles.get(j)));
				}
			}
		}
		if (pairs.size() == 1) {
			for (int i: singles) {
				result.add(new Card(-1, i));
				result.add(new Card(-1, -1));
			}
		}
		return result;
	}

}
