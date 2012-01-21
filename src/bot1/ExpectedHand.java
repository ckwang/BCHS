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
	public double compare(Hand h){
		double result = 0.0;
		for(Integer i:hands.keySet()){
			if(h.used.contains(i))continue;
			
		}
		return 0.0;
	}
}
