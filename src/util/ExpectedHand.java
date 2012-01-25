package util;

import java.util.Arrays;
import java.util.Random;

public class ExpectedHand {
	public static final double EPS = 1e-8;
	public static final int BASE = 64;
	public final double[] prob = new double[52*52];
	public final int[] hand = new int[52*52];
	public int len = 0;
	public int common = 0;
	public final int[] comCard = new int [5];
	public static final HandEval handeval = new HandEval();
	public static final Random generator = new Random(12345);
	
	public ExpectedHand(){
		for(int i=51;i>=0;i--){
			for(int j=i-1;j>=0;j--){
				hand[len++] = i*BASE+j;
			}
		}
		for(int i=0;i<len;i++)prob[i]=1.0/len;
	}	
	public ExpectedHand(int c1, int c2){
		for(int i=51;i>=0;i--){
			if(i==c1||i==c2)continue;
			for(int j=i-1;j>=0;j--){
				if(j==c1||j==c2)continue;
				hand[len++] = i*BASE+j;
			}
		}
		for(int i=0;i<len;i++)prob[i]=1.0/len;
	}
	public void normalize(){
		double sum = 0.0;
		for(int i=0;i<len;i++)sum+=prob[i];
		for(int i=0;i<len;i++)prob[i]/=sum;
	}
	public void reduce(int siz){
		if(siz>len)return;
		double[] array = new double[siz];
		int[] result = new int[siz];
		for(int i=0;i<siz;i++){
			array[i] = generator.nextDouble();
		}
		Arrays.sort(array);
		int index = 0;
		double aggr = 0.0;
		for(int i=0;i<len;i++){
			aggr+=prob[i];
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
			hand[i]=result[i];
			prob[i]=1.0/siz;
		}
		len = siz;
		return;
	}
	public void addCard(int c1){
		comCard[common++]=c1;
		for(int i=0;i<len;i++){
			if(hand[i]%BASE==c1||hand[i]/BASE==c1)prob[i]=0;
		}
		normalize();
	}
	public double computeOdds(int c1,int c2){
		double result = 0.0, aggr = 0.0;
		for(int i=0;i<len;i++){
			if(hand[i]%BASE == c1 || hand[i]%BASE == c2)continue;
			if(hand[i]/BASE == c1 || hand[i]/BASE == c2)continue;
			aggr += prob[i];
			if(common == 3){
				result += handeval.computeFlopEquityForSpecificCards(
						new int[]{c1,c2,hand[i]/BASE,hand[i]%BASE}, 
						new int[]{comCard[0],comCard[1],comCard[2]}, 2)[0] * prob[i];
			}else if(common == 4){
				result += handeval.computeTurnEquityForSpecificCards(
						new int[]{c1,c2,hand[i]/BASE,hand[i]%BASE}, 
						new int[]{comCard[0],comCard[1],comCard[2],comCard[3]}, 2)[0] * prob[i];
			}else if(common == 5){
				result += handeval.computeRiverEquityForSpecificCards(
						new int[]{c1,c2,hand[i]/BASE,hand[i]%BASE}, 
						comCard, 2)[0] * prob[i];
			}else{
				result += handeval.computePreFlopEquityForSpecificHoleCards(
						new int[]{c1,c2,hand[i]/BASE,hand[i]%BASE}, 2)[0] * prob[i];
			}
		}		
		return result/aggr;
	}
	public void multiply(HandsProbability hp){
		for(int i=0;i<len;i++){
			prob[i]*=hp.getProb(hand[i]/BASE, hand[i]%BASE);
		}
		normalize();
	}
	public static void main(String[] args){
		
		long start = System.nanoTime();
		for(int i=0;i<100;i++){
			ExpectedHand eh = new ExpectedHand();
			eh.addCard(5);
			eh.addCard(10);
			eh.addCard(28);
			eh.reduce(100);
			System.out.println(eh.computeOdds(0,17));
		}
		long end = System.nanoTime();
		System.out.println((end-start)/1000000.0/100);
		//System.out.println((totalTime/1000000.0)/100);
		
	}
}
