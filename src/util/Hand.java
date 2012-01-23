package util;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
	public int[] suits = {0, 0, 0, 0};
	public int[] ranks = new int[13];

	public Hand(Card c1, Card c2) {
		hole[0] = c1;
		hole[1] = c2;
		Arrays.fill(ranks, 0);
		if (c1.r == c2.r) {
			bestCategory = Category.Pair;
			pocket = true;
			pairRank1 = c1.r;
		}
		community = new ArrayList<Card>();
		ranks[c1.r-2] += 1;
		ranks[c2.r-2] += 1;
		suits[c1.s] += 1;
		suits[c2.s] += 1;
	}

	public void addCards(Card c) {
		community.add(c);
		assert c.r >= 0 && c.r <= 14;
		ranks[c.r-2] += 1;
		suits[c.s] += 1;
	}

	public void getBestHand() {
		analyzeHand();
		if (quadRank != -1) {
			int highest = -1;
			Card single = community.get(0);
			for (Card c: community) {
				if (c.r == quadRank) {
					bestHand.add(c);
				}
				else if (c.r > highest) {
					single = c;
					highest = c.r;
				}
			}
			bestHand.add(single);
			bestCategory = Category.Quads;
			return;
		}
		if (tripRank != -1 && pairRank1 != -1) {
			for (Card c: community) {
				if (c.r == tripRank || c.r == pairRank1) {
					bestHand.add(c);
				}
			}
			bestCategory = Category.FullHouse;
			return;
		}
		if (flushSuit != -1) {
			int highest = -1;
			Card highCard;
			for (Card c: community) {
				if (c.s == flushSuit) {
					bestHand.add(c);
					if (c.r > highest) {
						highCard = c;
						highest = c.r;
					}
				}
			}
			int last = bestHand.size()-1;
			while (bestHand.size() > 5) {
				if (bestHand.get(last).r != highest)
					bestHand.remove(last);
				--last;
			}
			bestCategory = Category.Flush;
			return;
		}
		if (straightLast != -1) {
			for (Card c: community) {
				if (c.r <= straightLast && c.r > straightLast - 5) {
					bestHand.set(c.r, c);
				}
			}
			bestCategory = Category.Straight;
			return;
		}
		if (tripRank != -1) {
			int h1 = -1, h2 = -1;
			Card hc1, hc2;
			for (Card c: community) {
				if (c.r == tripRank) {
					bestHand.add(c);
				}
				else {

				}
			}
			bestCategory = Category.Triplets;
			return;
		}
		if (pairRank1 != -1) {
			if (pairRank2 != -1) {
				bestCategory = Category.TwoPair;
			} else {
				bestCategory = Category.Pair;
			}
			return;
		}
	}

	public void analyzeHand() {
		if (flushSuit != -1) {
			for (int i = 0; i < 4; ++i) {
				if (suits[i] >= 5) {
					flushSuit = i;
					break;
				}
			}
		}
		int c = 0;
		for (int i = 0; i < 13; ++i) {
			if (ranks[i] == 0)
				c = 0;
			else {
				++c;
				switch (ranks[i]) {
					case 4:
						quadRank = i;
						bestCategory = Category.Quads;
						break;
					case 3:
						tripRank = i;
						break;
					case 2:
						pairRank2 = pairRank1;
						pairRank1 = i;
						break;
					case 1:
						singRank = i;
						break;
				}
			}
			if (c >= 5) {
				straightLast = i;
				bestCategory = Category.Straight;
			}
		}
	} 
}
