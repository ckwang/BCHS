package bot1;

public class Card {
	public final Suit s; //0-3
	public final Rank r; //0-12, 0->2, 12->A
	public final int hash;
	public Card(Suit ns, Rank nr){
		s=ns;
		r=nr;
		hash=r.ordinal()*4+s.ordinal();
	}
	public Card(int value){
		s=Suit.values()[value%4];
		r=Rank.values()[value/4];
		hash=value;
	}
}
