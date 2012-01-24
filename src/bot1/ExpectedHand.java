package bot1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpectedHand {
	public static final double EPS = 1e-8;
	public final Map<Integer,Hand> hands = new HashMap<Integer, Hand>();
	public final Map<Integer,Double> prob = new HashMap<Integer, Double>();
	
	public ExpectedHand(){
		for(int i=51;i>=0;i--){
			for(int j=i-1;j>=0;j--){
				hands.put(Integer.valueOf(i*52+j), new Hand(new Card(i), new Card(j)));
				prob.put(Integer.valueOf(i*52+j), Double.valueOf(1.0/(51*26)));
			}
		}
	}	
	public ExpectedHand(Card c1, Card c2){
		for(int i=51;i>=0;i--){
			if(i==c1.hash||i==c2.hash)continue;
			for(int j=i-1;j>=0;j--){
				if(j==c1.hash||j==c2.hash)continue;
				hands.put(Integer.valueOf(i*52+j), new Hand(new Card(i), new Card(j)));
				prob.put(Integer.valueOf(i*52+j), Double.valueOf(1.0/(49*25)));
			}
		}
	}
	public void normalize(){
		double sum = 0.0;
		Set<Integer> remove = new HashSet<Integer>();
		for(Integer i:hands.keySet()){
			if(prob.get(i)<EPS){
				remove.add(i);
			}
			sum+=prob.get(i);
		}
		for(Integer i: remove){
			hands.remove(i);
			prob.remove(i);
		}
		for(Integer i:hands.keySet()){
			prob.put(i,Double.valueOf(prob.get(i)/sum));
		}
	}
	public static long totalTime = 0; 
	public double compare(Hand h){
		double result = 0.0, aggr = 0.0;
		h.calcConfig();
		h.calcValue();
		
		for(Integer i:hands.keySet()){
			
			if(h.used.contains(i/52))continue;
			if(h.used.contains(i%52))continue;
			
			int total=0,win=0,loss=0;
			Hand opp = hands.get(i);
			totalTime-=System.nanoTime();
			
			opp.calcConfig();
			opp.calcValue();
			for(Integer j=51;j>=0;j--){
				if(i%52==j)continue;
				if(i/52==j)continue;
				if(h.used.contains(j))continue;
				total++;
				int v1=h.evalWithCard(new Card(j)),v2=opp.evalWithCard(new Card(j));
				if(v1>v2)win++;
				if(v2>v1)loss++;
			}
			totalTime+=System.nanoTime();
			
			aggr+=prob.get(i);
			double w=(1-(double)loss/total)*(1-(double)loss/total),
				   l=(1-(double)win/total)*(1-(double)win/total);
			if(h.value>opp.value){
				result+=w*prob.get(i);
			}else if(h.value<opp.value){
				result+=(1-l)*prob.get(i);
			}else{
				result+=(w+1-l)/2*prob.get(i);
			}
			
		}
		
		return result/aggr;
	}
	public static void main(String[] args){
		Hand h1 = new Hand(new Card(0),new Card(1));
		
		long start = System.nanoTime();
		for(int i=0;i<100;i++){
			ExpectedHand eh = new ExpectedHand();
			eh.compare(h1);
		}
		long end = System.nanoTime();
		System.out.println((end-start)/1000000.0/100);
		System.out.println((totalTime/1000000.0)/100);
		
	}
}
