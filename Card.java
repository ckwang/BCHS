package pokerbots.util;

public class Card {

	public enum Suit { Spade, Heart, Club, Diamond };

	public final int s;
	public final int r;

	public Card(int s, int r) {
		this.s = s;
		this.r = r;
	}
}
