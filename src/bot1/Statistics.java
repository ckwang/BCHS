package bot1;

import java.util.Arrays;
import java.util.HashMap;

public class Statistics {
	public double [][][][][][] fold = new double [100][4][3][3][3][2]; //people, stage, seat, round, size, alive
	public double [][][][][][] chanceFold = new double [100][4][3][3][3][2];
	public double [][][][][][] raise = new double [100][4][3][3][3][2];
	public double [][][][][] chanceRaise = new double [100][4][3][3][2];
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
	public Statistics(String init){
		String[] fields = init.split(" ");
		players = 1;
		names.put("all", 0);
		namelist.put(0, "all");
		
		int count = 0;
		for(int j=0;j<4;j++){
			for(int k=0;k<3;k++){
				for(int l=0;l<3;l++){
					for(int n=0;n<2;n++){
						chanceRaise[0][j][k][l][n] = Double.parseDouble(fields[count++]);	
					}
					for(int m=0;m<3;m++){
						for(int n=0;n<2;n++){
							fold[0][j][k][l][m][n] = Double.parseDouble(fields[count++]);	
							chanceFold[0][j][k][l][m][n] = Double.parseDouble(fields[count++]);	
							raise[0][j][k][l][m][n] = Double.parseDouble(fields[count++]);									
						}
					}
				}
			}
		}
		
		for(int i=1;i<100;i++){
			for(int j=0;j<4;j++){
				for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						for(int m=0;m<2;m++){
							chanceRaise[i][j][k][l][m]=chanceRaise[0][j][k][l][m];
							for(int n=0;n<3;n++){
								fold[i][j][k][l][n][m]=fold[0][j][k][l][n][m];
								chanceFold[i][j][k][l][n][m]=chanceFold[0][j][k][l][n][m];
								raise[i][j][k][l][n][m]=raise[0][j][k][l][n][m];
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
	public String toInitString(){
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<1;i++){
			for(int j=0;j<4;j++){
				for(int k=0;k<3;k++){
					for(int l=0;l<3;l++){
						for(int n=0;n<2;n++){
							sb.append(String.format("%.3f ", chanceRaise[i][j][k][l][n]));	
						}
						for(int m=0;m<3;m++){
							for(int n=0;n<2;n++){
								sb.append(String.format("%.3f ", fold[i][j][k][l][m][n]));
								sb.append(String.format("%.3f ", chanceFold[i][j][k][l][m][n]));
								sb.append(String.format("%.3f ", raise[i][j][k][l][m][n]));								
							}
						}
					}
				}
			}
		}
		return sb.toString();
		
	}
	public void reduce(){
		for(int j=0;j<4;j++){
			for(int k=0;k<3;k++){
				for(int l=0;l<3;l++){					
					for(int m=0;m<3;m++){
						for(int n=0;n<2;n++){
							if(chanceFold[0][j][k][l][m][n]>50){
								fold[0][j][k][l][m][n]=(fold[0][j][k][l][m][n]/chanceFold[0][j][k][l][m][n])*50;
								chanceFold[0][j][k][l][m][n] = 50;
							}
							if(chanceFold[0][j][k][l][m][n]<5){
								if(n==1){
									chanceFold[0][j][k][l][m][n] = chanceFold[0][j][k][l][m][0];
									fold[0][j][k][l][m][n] = fold[0][j][k][l][m][0];
								}else{
									chanceFold[0][j][k][l][m][n] = 5;
									fold[0][j][k][l][m][n] = 1.5;
								}
							}
							if(chanceRaise[0][j][k][l][n]>50){
								raise[0][j][k][l][m][n]=(raise[0][j][k][l][m][n]/chanceRaise[0][j][k][l][n])*50;	
							}	
							if(chanceRaise[0][j][k][l][n]<5){
								if(n==1){
									raise[0][j][k][l][m][n] = raise[0][j][k][l][m][0];
								}else{
									raise[0][j][k][l][m][n] = 0;
								}
							}
						}
					}
					for(int n=0;n<2;n++){
						if(chanceRaise[0][j][k][l][n]>50){
							chanceRaise[0][j][k][l][n]=50;
						}
						if(chanceRaise[0][j][k][l][n]<5){
							if(n==1){
								chanceRaise[0][j][k][l][n] = chanceRaise[0][j][k][l][0];
							}else{
								chanceRaise[0][j][k][l][n] = 5;
							}
						}
					}
				}
			}
		}
	}
}
