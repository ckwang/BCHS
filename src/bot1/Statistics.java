package bot1;

import java.util.Arrays;
import java.util.HashMap;

public class Statistics {
	public int [][][][][][] fold = new int [100][4][3][3][3][2]; //people, stage, seat, round, size, alive
	public int [][][][][][] chanceFold = new int [100][4][3][3][3][2];
	public int [][][][][][] raise = new int [100][4][3][3][3][2];
	public int [][][][][] chanceRaise = new int [100][4][3][3][2];
	public HashMap<String, Integer> names= new HashMap<String, Integer>();
	public HashMap<Integer, String> namelist= new HashMap<Integer, String>();
	public int players = 0;
	
	public Statistics(){
		players = 1;
		names.put("all", 0);
		namelist.put(0, "all");
		for(int i=0;i<100;i++){
			for(int j=0;j<4;j++){
				for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						for(int m=0;m<2;m++){
							chanceRaise[i][j][k][l][m]=0;
							for(int n=0;n<3;n++){
								fold[i][j][k][l][n][m]=0;
								chanceFold[i][j][k][l][n][m]=0;
								raise[i][j][k][l][n][m]=0;
							}
						}
					}
				}
			}
		}
		
	}
	public int nameToInt(String name1){
		String name = new String(name1);
		name = name.substring(0, name.length()-4);
		if(names.containsKey(name)){
			return names.get(name);
		}
		names.put(name, players);
		namelist.put(players, name);
		players++;
		return players-1;
	}
	public int commonToInt(int common){
		if(common == 0)return 0;
		if(common == 3)return 1;
		if(common == 4)return 2;
		return 3;		
	}
	public int timeToInt(int time){
		if(time <2)return time;
		return 2;
	}
	public int estimate(double ratio){
		if(ratio < 0.3)return 0;
		if(ratio < 1.2)return 1;
		return 2;
	}
	public int estimatecall(double ratio){
		if(ratio < 0.3)return 0;
		if(ratio < 0.6)return 1;
		return 2;
	}
	public int aliveToInt(int alive){
		if(alive==2)return 0;
		return 1;
	}
	public void fold(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time), size = estimatecall((double)toCall/stack);
		int num = aliveToInt(alive);
		chanceFold[player][stage][seat][round][size][num]++;
		chanceRaise[player][stage][seat][round][num]++;
		fold[player][stage][seat][round][size][num]++;
	}
	public void check(String name, int common, int seat, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int num = aliveToInt(alive);
		chanceRaise[player][stage][seat][0][num]++;
	}
	public void call(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time), size = estimatecall((double)toCall/stack);
		int num = aliveToInt(alive);
		chanceFold[player][stage][seat][round][size][num]++;
		chanceRaise[player][stage][seat][round][num]++;
	}
	public void raise(String name, int common, int time, int seat, int stack, int toCall, int amount, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time), size = estimatecall((double)toCall/stack);
		int ratio = estimate((double)amount/(stack+toCall));
		int num = aliveToInt(alive);
		chanceFold[player][stage][seat][round][size][num]++;
		chanceRaise[player][stage][seat][round][num]++;
		raise[player][stage][seat][round][ratio][num]++;
	}
	public void bet(String name, int common, int time, int seat, int stack, int amount, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time), ratio = estimate((double)amount/stack);
		int num = aliveToInt(alive);
		chanceRaise[player][stage][seat][round][num]++;
		raise[player][stage][seat][round][ratio][num]++;
	}
	public double getFoldProb(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time), size = estimatecall((double)toCall/stack);
		int num = aliveToInt(alive);
		if(chanceFold[player][stage][seat][round][size][num]>0){
			return (double)fold[player][stage][seat][round][size][num]/chanceFold[player][stage][seat][round][size][num];
		}
		return 0.2;
	}
}
