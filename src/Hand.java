import java.util.Arrays;

public class Hand {

	public enum Category { Nothing, Pair, TwoPair, Triplets, Straight, Flush, FullHouse, Quads, StraightFlush }

	public boolean straight = false;
	public boolean flush = false;
	public int num = 0;
	public int highest;
	public Category best = Category.Nothing;
	public Card[] hole = new Card[2];
	public Card[] community = new Card[5];
	public int[] suits = {0, 0, 0, 0};
	public int[] ranks = new int[13];
	
	public Hand() {}

	public Hand(Card c1, Card c2) {
		hole[0] = c1;
		hole[1] = c2;
		Arrays.fill(ranks, 0);
		if (c1.r == c2.r)
			best = Category.Pair;
		highest = c1.r;
		if (c2.r > highest)
			highest = c2.r;
		ranks[c1.r] += 1;
		ranks[c2.r] += 1;
		suits[c1.s] += 1;
		suits[c2.s] += 1;
	}

	public void addCards(int i, Card c) {
		assert i >= 0 && i < 5;
		community[i] = c;
		ranks[c.r] += 1;
		suits[c.s - 2] += 1;
	}

	public void analyzeHand() {
		if (!flush) {
			for (int i = 0; i < 4; ++i) {
				if (suits[i] >= 5) {
					flush = true;
					break;
				}
			}
		}
	} 
}
