package util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import lib.HandEval;
import lib.FiveEval;
import lib.HandSixEval;

public class ExpectedHand {
	public static double drawTh = 0.1;
	public class PossibleHand implements Comparable{
		public double prob;
		public int c1,c2;
		public int rank;
		public double position; //small -> good
		public int draw;
		PossibleHand(int nc1,int nc2,double nprob){
			c1=nc1;
			c2=nc2;
			prob=nprob;
			rank=-1;
			position=-1;
			draw=-1;
			//odd=PokerTable.preflopWinningProb2(new Card(nc1%4,12-nc1/4), new Card(nc2%4,12-nc2/4));
		}
		PossibleHand(int nc1,int nc2,double nprob,int nrank,double nposition,int ndraw){
			c1=nc1;
			c2=nc2;
			prob=nprob;
			rank=nrank;
			position=nposition;
			draw=ndraw;
			//odd=PokerTable.preflopWinningProb2(new Card(nc1%4,12-nc1/4), new Card(nc2%4,12-nc2/4));
		}
		public int compareTo(Object obj) throws ClassCastException {
			if (!(obj instanceof PossibleHand))
				throw new ClassCastException("Can't cast to PossibleHand");
			int r2 = ((PossibleHand)obj).rank;
			return rank>r2?-1:(rank<r2?1:0);
		}
		public PossibleHand clone(){
			return new PossibleHand(c1,c2,prob,rank,position,draw);
		}
	}
	private class PossibleHandWithNewCard implements Comparable{
		public PossibleHand ph;
		public int rank;
		@SuppressWarnings("unused")
		public int compareTo(Object obj) throws ClassCastException {
			if (!(obj instanceof PossibleHandWithNewCard))
				throw new ClassCastException("Can't cast to PossibleHandWithNewCard");
			int r2 = ((PossibleHandWithNewCard)obj).rank;
			return rank>r2?-1:(rank<r2?1:0);
		}
	}
	
	public static final double EPS = 1e-8;
	public final PossibleHand[] hand = new PossibleHand[52*52];
	public final PossibleHand[] sample = new PossibleHand[52*52];
	public int sampleSize = 0;
	private final PossibleHandWithNewCard[] tmphand = new PossibleHandWithNewCard[52*52]; 
	public int len = 0;
	public int common = 0;
	public final int[] comCard = new int [5];
	public final int[] shuffle = new int [52*52];
	public static final Random generator = new Random(12345);
	
	public ExpectedHand(){
		for(int i=51;i>=0;i--){
			for(int j=i-1;j>=0;j--){
				hand[len] = new PossibleHand(i,j,1.0/(26*51));
				tmphand[len] = new PossibleHandWithNewCard();
				len++;
			}
		}
	}	
	public ExpectedHand(int c1, int c2){
		for(int i=51;i>=0;i--){
			if(i==c1||i==c2)continue;
			for(int j=i-1;j>=0;j--){
				if(j==c1||j==c2)continue;
				hand[len] = new PossibleHand(i,j,1.0/(25*49));
				tmphand[len] = new PossibleHandWithNewCard();
				len++;
			}
		}
	}
	public void normalize(){
		double sum = 0.0;
		for(int i=0;i<len;i++)sum+=hand[i].prob;
		for(int i=0;i<len;i++)hand[i].prob/=sum;
	}
	public void updateRank(){
		if(common == 3){
			for(int i=0;i<len;i++){
				hand[i].rank = FiveEval.getBestRankOf(
						hand[i].c1, hand[i].c2, comCard[0], comCard[1], comCard[2]);
			}
		}else if(common == 4){
			for(int i=0;i<len;i++){
				hand[i].rank = FiveEval.getBestRankOf(
						hand[i].c1, hand[i].c2, comCard[0], comCard[1], comCard[2], comCard[3]);
			}
		}else if(common == 5){
			for(int i=0;i<len;i++){
				hand[i].rank = FiveEval.getBestRankOf(
						hand[i].c1, hand[i].c2, comCard[0], comCard[1], comCard[2], comCard[3], comCard[4]);
			}
		}
		return;
	}
	public void updatePosition(){
		int start = 0;
		double current = 0.0;
		double step = 0.0;
		for(int i=0;i<len;i++){
			step+=hand[i].prob;
			if(i==len-1||hand[i].rank!=hand[i+1].rank){
				for(int j=start;j<=i;j++){
					hand[i].position = current+step/2;
				}
				current+=step;
				step = 0.0;
				start = i+1;
			}
		}
	}
	public void updateDraw(){
		int goodpos = 0;
		for(int i=0;i<len;i++){
			hand[i].draw = 0;
		}
		for(int i=0;i<len-1;i++){
			if(hand[i].position<drawTh&&hand[i+1].position+EPS>=drawTh){
				goodpos = i;
				break;
			}
		}
		for(int c=0;c<51;c++){
			boolean flag=false;
			for(int i=0;i<common;i++){
				if(comCard[i]==c)flag=true;
			}
			if(flag)continue;
			
			PossibleHand good = hand[0];
			for(int i=goodpos;i<len;i++){
				if(hand[i].c1==c||hand[i].c2==c||hand[i].prob<EPS)continue;
				good=hand[i];
			}
			int goodrank = FiveEval.getBestRankOf(good.c1, good.c2,
					comCard[0], comCard[1], comCard[2], c);
			for(int i=0;i<len;i++){
				int drawrank = 0;
				if(hand[i].c1==c||hand[i].c2==c||hand[i].prob<EPS){
					drawrank = -1;
				}else{
					if(common==3){
						drawrank = FiveEval.getBestRankOf(hand[i].c1, hand[i].c2,
								comCard[0], comCard[1], comCard[2], c);
					}else if(common==4){
						drawrank = FiveEval.getBestRankOf(hand[i].c1, hand[i].c2,
								comCard[0], comCard[1], comCard[2], comCard[3], c);
					}
				}
				if(drawrank>goodrank){
					hand[i].draw++;
				}
			}
		}
	}
	public void sort(){
		Arrays.sort(hand,0,len);
	}
	public void sample(int siz){
		if(siz>len)return;
		double[] array = new double[siz];
		for(int i=0;i<siz;i++){
			array[i] = generator.nextDouble();
		}
		Arrays.sort(array);
		int index = 0;
		double aggr = 0.0;
		for(int i=0;i<len;i++){
			aggr+=hand[i].prob;
			while(index<siz && array[index]<aggr){
				sample[index] = hand[i];
				index++;
			}
		}
		while(index<siz){
			sample[index]=hand[len-1];
			index++;
		}
		sampleSize = siz;
		return;
	}
	public void reduce(int siz){
		if(siz>len)return;
		double[] array = new double[siz];
		PossibleHand[] result = new PossibleHand[siz];
		for(int i=0;i<siz;i++){
			array[i] = generator.nextDouble();
		}
		Arrays.sort(array);
		int index = 0;
		double aggr = 0.0;
		for(int i=0;i<len;i++){
			aggr+=hand[i].prob;
			while(index<siz && array[index]<aggr){
				result[index] = hand[i];
				index++;
			}
		}
		while(index<siz){
			result[index]=hand[len-1];
			index++;
		}
		for(int i=0;i<siz;i++){
			hand[i]=result[i].clone();
			hand[i].prob=1.0/siz;
		}
		len = siz;
		return;
	}
	public void addCard(int c1){
		comCard[common++]=c1;
		for(int i=0;i<len;i++){
			if(hand[i].c2==c1||hand[i].c1==c1)hand[i].prob=0;
		}
		normalize();
	}
	private double internalComputeOdds(int c1,int c2, PossibleHand[] phs,int plen, boolean six){
		double result = 0.0, aggr = 0.0;
		for(int i=0;i<plen;i++){
			if(phs[i].c2 == c1 || phs[i].c2 == c2)continue;
			if(phs[i].c1 == c1 || phs[i].c1 == c2)continue;
			aggr += phs[i].prob;
			if(common == 3){
				if(!six){
					result += HandEval.computeFlopEquityForSpecificCards(
							new int[]{c1,c2,phs[i].c1,phs[i].c2}, 
							new int[]{comCard[0],comCard[1],comCard[2]}, 2)[0] * phs[i].prob;
				}else{
					result += HandSixEval.computeEstimatedFlopEquityForSpecificCards(
							new int[]{c1,c2,phs[i].c1,phs[i].c2}, 
							new int[]{comCard[0],comCard[1],comCard[2]}, 2)[0] * phs[i].prob;
				}
			}else if(common == 4){
				result += HandEval.computeTurnEquityForSpecificCards(
						new int[]{c1,c2,phs[i].c1,phs[i].c2}, 
						new int[]{comCard[0],comCard[1],comCard[2],comCard[3]}, 2)[0] * phs[i].prob;
			}else if(common == 5){
				result += HandEval.computeRiverEquityForSpecificCards(
						new int[]{c1,c2,phs[i].c1,phs[i].c2}, 
						comCard, 2)[0] * phs[i].prob;
			}else{
				/*result += phsEval.computePreFlopEquityForSpecificHoleCards(
						new int[]{c1,c2,phs[i].c1,phs[i].c2}, 2)[0] * phs[i].prob;*/
				result += PreflopTable.getProb(c1, c2, phs[i].c1,phs[i].c2)*100*phs[i].prob;
			}
		}		
		return result/aggr;
	}
	public double computeOdds(int c1,int c2){
		return internalComputeOdds(c1,c2,hand,len,false);		
	}
	public double computeOddsBySample(int c1,int c2,int iter){ //return 0-100
		if(iter>=len)return computeOdds(c1,c2);
		sample(iter);
		return internalComputeOdds(c1,c2,sample,iter,false);
	}
	public void multiply(HandsProbability hp){
		for(int i=0;i<len;i++){
			hand[i].prob*=hp.getProb(hand[i].c1,hand[i].c2);
		}
		normalize();
	}
	public double computeSixCardOdds(int c1,int c2){ //return 0-100
		return internalComputeOdds(c1,c2,hand,len,true);
	}
	public void shuffleGen(int iter){
		for(int i=0;i<iter;i++)shuffle[i]=i;
		for(int i=0;i<iter;i++){
			int nxt = generator.nextInt(iter),tmp=shuffle[i];
			 shuffle[i]=shuffle[nxt];
			 shuffle[nxt]=tmp;
		}
	}
	public double computeSixCardOdds3(int c1,int c2,ExpectedHand eh,int iter){
		double result = 0.0, aggr = 0.0;
		int size;
		for(int round = 0; round<=(iter-1)/len; round++){
			if(round!=(iter-1)/len)size = len;
			else size = iter%len;
			
			sample(size);
			eh.sample(size);
			shuffleGen(size);
			
			for(int i=0;i<size;i++){
				int s1 = sample[i].c1, s2 = sample[i].c2;
				int e1 = eh.sample[shuffle[i]].c1, e2 = eh.sample[shuffle[i]].c2;
				if( s2 == c1 || s2 == c2)continue;
				if( s1 == c1 || s1 == c2)continue;
				if( e2 == c1 || e2 == c2 || 
						s1 == e2 || 
						s2 == e2 )continue;
				if(e1 == c1 || e1 == c2 || 
						s1 == e1 || 
						s2 == e1 )continue;
				if(sample[i].prob<EPS)continue;
				if(eh.sample[shuffle[i]].prob<EPS)continue;
				
				double prob = sample[i].prob*eh.sample[shuffle[i]].prob;
				
				aggr += prob;
				if(common == 3){
					result += HandSixEval.computeEstimatedFlopEquityForSpecificCards(
							new int[]{c1,c2,s1,s2,e1,e2}, 
							new int[]{comCard[0],comCard[1],comCard[2]}, 3)[0] * prob;
				}else if(common == 4){
					result += HandEval.computeTurnEquityForSpecificCards(
							new int[]{c1,c2,s1,s2,e1,e2}, 
							new int[]{comCard[0],comCard[1],comCard[2],comCard[3]}, 3)[0] * prob;
				}else if(common == 5){
					result += HandEval.computeRiverEquityForSpecificCards(
							new int[]{c1,c2,s1,s2,e1,e2}, 
							comCard, 3)[0] * prob;
				}else{
					/*result += handEval.computePreFlopEquityForSpecificHoleCards(
							new int[]{c1,c2,hand[i].c1,hand[i].c2}, 2)[0] * hand[i].prob;*/
					//result += PreflopTable.getProb(c1, c2, hand[i].c1,hand[i].c2)*100*hand[i].prob;
				}
			}		
		}		
		return result/aggr;		
	}
	public static void main(String[] args){
		
		long start = System.nanoTime();
		for(int i=0;i<100;i++){
			ExpectedHand eh = new ExpectedHand();
			ExpectedHand eh2 = new ExpectedHand();
			
			eh.addCard(7);
			eh.addCard(13);
			eh.addCard(51);
			//eh.addCard(29);			
			eh.updateRank();
			//eh.updateDraw();
			eh.sort();
			//for(int j=0;j<10;j++)System.out.println(eh.hand[j].c1+" "+eh.hand[j].c2);
			eh.updatePosition();
			//eh.reduce(100);
			//System.out.println(eh.computeSixCardOdds(19,31));
			System.out.println(eh.computeSixCardOdds3(12, 31, eh2, 200));
			//System.out.println(eh.computeSixCardOdds(19, 31));
		}
		long end = System.nanoTime();
		System.out.println((end-start)/1000000.0/100);
		//System.out.println((totalTime/1000000.0)/100);
		
	}
}
