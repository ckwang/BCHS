package pokerbot.util;

class Hand {
	public boolean complete = false;
	public boolean straight = false;
	public boolean flush = false;
	public int num = 0;
	public Category best = Category.Nothing;
	public Rank highest;
	public Card[] private;
	public Card[] community = new Card[5];
	public int[] suits = {0, 0, 0, 0};
	public int[] ranks = new int[13];
	
	public Hand() {}

	public Hand(Card c1, Card c2) {
		private = {c1, c2};
		Array.fill(ranks, 0);
		if (c1.r == c2.r)
			best = Category.Pair;
		highest = c1.r;
		if (c2.r > highest)
			highest = c2.r;
		ranks[c1.r.ordinal] += 1;
		ranks[c2.r.ordinal] += 1;
		suits[c1.s.ordinal] += 1;
		suits[c2.s.ordinal] += 1;
	}

	public addCommunity(int i, Card c) {
		assert i >= 0 && i < 5;
		community[i] = c;
		ranks[c.r.ordinal] += 1;
		suits[c.s.ordinal] += 1;
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
