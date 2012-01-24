package bot1;

public class Card {
	public final int s; //0-3
	public final int r; //0-12, 0->2, 12->A
	public final int hash;
	public Card(int ns, int nr){
		s=ns;
		r=nr;
		hash=r*4+s;
	}
	public Card(int value){
		s=value%4;
		r=value/4;
		hash=value;
	}
}
