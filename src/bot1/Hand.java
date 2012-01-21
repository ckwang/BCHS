package bot1;

import java.util.HashSet;
import java.util.Set;

public class Hand {
	public final Card[] cards = new Card[2];
	public final int hash;
	
	public int value;
	public Category best = Category.Nothing;
	public final int[] dups = {0, 0};
	
	public int num = 0;
	public final Card[] community = new Card[5];
	public final int[] suits = {0, 0, 0, 0};
	public final int[] ranks = new int[13];
	public final Set<Integer> used = new HashSet<Integer>();
	
	public Hand(Card c1, Card c2){
		for(int i=0;i<13;i++)ranks[i]=0;
		
		if(c1.hash>c2.hash){
			cards[0]=c1;
			cards[1]=c2;
		}else{
			cards[0]=c2;
			cards[1]=c1;
		}
		
		hash = cards[0].hash*52+cards[1].hash;
		
		suits[cards[0].s.ordinal()]++;
		suits[cards[1].s.ordinal()]++;
		ranks[cards[0].s.ordinal()]++;
		ranks[cards[1].s.ordinal()]++;
		used.add(c1.hash);
		used.add(c2.hash);
		
		if(c1.s==c2.s) {
			best = Category.Pair;
			dups[0] = c1.r.ordinal();
		}
		calcValue();
	}
	
	public void calcValue(){
		value = best.ordinal()*13*13*13*13 + 
				dups[0]*13*13*13 +
				dups[1]*13*13 +
				cards[0].r.ordinal()*13 +
				cards[1].r.ordinal();
		return;
	}
	
	public void calcConfig(){
		boolean flush = false;
		for(int i=0;i<4;i++){
			if(suits[i]>=5)flush=true;
		}
		
		int two = 0;
		int three = 0;
		int four = 0;
		
		int[] pos2 = {0,0,0};
		int[] pos3 = {0,0};
		int pos4 = 0;
		
		for(int i=12;i>=0;i--){
			if(ranks[i]==4){
				four++;
				pos4=i;
			}
			if(ranks[i]==3){
				pos3[three]=i;
				three++;
			}
			if(ranks[i]==2){
				pos2[two]=i;
				two++;
			}
		}
		
		boolean straight = false;
		int cnt = 0;
		for(int i=0;i<4;i++){
			if(ranks[i]>0)cnt++;
		}
		for(int i=0;i<13-4;i++){
			if(ranks[i+4]>0)cnt++;
			if(cnt==5){
				straight = true;
				dups[0]=i;
				dups[1]=0;
			}
			if(ranks[i]>0)cnt--;
		}
		
		if(four>0){
			best = Category.FourKind;
			dups[0]=pos4;
			dups[1]=0;
		}else if(three>0&&two>0){
			best = Category.FullHouse;
			dups[0]=pos3[0];
			dups[1]=pos2[0];
		}else if(three>=2){
			best = Category.FullHouse;
			dups[0]=pos3[0];
			dups[1]=pos3[1];
		}else if(flush){
			best = Category.Flush;
			dups[0]=0;
			dups[1]=0;
		}else if(three>0){
			best = Category.Set;
			dups[0]=pos3[0];
			dups[1]=0;
		}else if(straight){
			best = Category.Straight;
			dups[1]=0;
		}else if(two>=2){
			best = Category.TwoPair;
			dups[0]=pos2[0];
			dups[1]=pos2[1];
		}else if(two>0){
			best = Category.Pair;
			dups[0]=pos2[0];
			dups[1]=0;
		}else{
			best = Category.Nothing;
			dups[0]=0;
			dups[1]=1;
		}
		calcValue();
	}
	
	public void addCard(Card c){
		cards[num]=c;
		num++;
		suits[c.s.ordinal()]++;
		ranks[c.r.ordinal()]++;
		used.add(c.hash);
	}
	
	public int evalWithCard(Card c){
		suits[c.s.ordinal()]++;
		ranks[c.r.ordinal()]++;
		
		int lvalue = value;
		Category lbest = best;
		int[] ldups = {dups[0],dups[1]};
		
		calcConfig();
		int ret = value;
		
		value = lvalue;
		best = lbest;
		dups[0]=ldups[0];
		dups[1]=ldups[1];
		
		suits[c.s.ordinal()]--;
		ranks[c.r.ordinal()]--;
		return ret;
	}
}
