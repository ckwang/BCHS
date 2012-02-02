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
	public static boolean trim = true;
	public int nameToInt(String name1){
		String name = new String(name1);
		if(trim&&name.length()>4){
			boolean cut = true;
			for(int i=0;i<4;i++){
				if(!Character.isDigit(name.charAt(name.length()-i-1))){
					cut = false;
					break;
				}
			}
			if(cut)name = name.substring(0, name.length()-4);
		}
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
	public double[] estimate(double ratio){
		if(ratio < 0.1) return new double[]{0, 0, 0};
		else if(ratio < 0.5)return new double[]{(ratio-0.1)/0.4, 0, 0};
		else if(ratio < 1)return new double[]{1-(ratio-0.5)/0.5, (ratio-0.5)/0.5, 0};
		else if(ratio < 2.5)return new double[]{0,1-(ratio-1)/1.5, (ratio-1)/1.5};
		else return new double[]{0,0,1};
	}
	public double[] estimatecall(double ratio){
		if(ratio < 0.1)return new double[]{1,0,0};
		else if(ratio < 0.35) return new double[]{1-(ratio-0.1)/0.25, (ratio-0.1)/0.25, 0};
		else if(ratio < 0.7) return new double[]{0, 1-(ratio-0.35)/0.35, (ratio-0.35)/0.35};
		else return new double[]{0,0,1};
	}
	public int aliveToInt(int alive){
		if(alive==2)return 0;
		return 1;
	}
	public void fold(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time); 
		int num = aliveToInt(alive);
		double[] size = estimatecall((double)toCall/stack);
		chanceRaise[player][stage][seat][round][num]++;
		for(int i=0;i<3;i++){
			chanceFold[player][stage][seat][round][i][num]+=size[i];
			fold[player][stage][seat][round][i][num]+=size[i];
		}
	}
	public void check(String name, int common, int seat, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int num = aliveToInt(alive);
		chanceRaise[player][stage][seat][0][num]++;
	}
	public void call(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		int num = aliveToInt(alive);
		double[] size = estimatecall((double)toCall/stack);
		chanceRaise[player][stage][seat][round][num]++;
		for(int i=0;i<3;i++){
			chanceFold[player][stage][seat][round][i][num]+=size[i];
		}
	}
	public void raise(String name, int common, int time, int seat, int stack, int toCall, int amount, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		double[] ratio = estimate((double)amount/(stack+toCall));
		int num = aliveToInt(alive);
		double[] size = estimatecall((double)toCall/stack);
		for(int i=0;i<3;i++){
			chanceFold[player][stage][seat][round][i][num]+=size[i];
		}
		chanceRaise[player][stage][seat][round][num]++;
		for(int i=0;i<3;i++){
			raise[player][stage][seat][round][i][num]+=ratio[i];
		}
	}
	public void bet(String name, int common, int time, int seat, int stack, int amount, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		int num = aliveToInt(alive);
		double[] ratio = estimate((double)amount/(stack));

		chanceRaise[player][stage][seat][round][num]++;
		for(int i=0;i<3;i++){
			raise[player][stage][seat][round][i][num]+=ratio[i];
		}
	}
	public double getFoldProb(String name, int common, int time, int seat, int stack, int toCall, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		int num = aliveToInt(alive);
		double[] size = estimatecall((double)toCall/stack);
		double ans = 0.0, wei = 0.0;
		for(int i=0;i<3;i++){
			if(chanceFold[player][stage][seat][round][i][num]>0.9){
				ans += size[i]*
					   fold[player][stage][seat][round][i][num]/chanceFold[player][stage][seat][round][i][num];
				wei += size[i];
			}
		}
		if(wei>1e-6){
			return ans/wei;
		}
		return 0.2;
	}
	public double getRaiseProb(String name, int common, int time, int seat, int alive){
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		int num = aliveToInt(alive);
		double ans = 0.0, wei = 0.0;
		if(chanceRaise[player][stage][seat][round][num]>0.9){
			for(int i=0;i<3;i++){
				ans += raise[player][stage][seat][round][i][num];
			}
			return ans/chanceRaise[player][stage][seat][round][num];
		}else{
			return 0.1;
		}
	}
	public double getRaiseAboveProb(String name, int common, int time, int seat, int alive, double potodd){
		double threshold;
		if(potodd>0.45)threshold = 9;
		else threshold = potodd/(1-2*potodd);
		int player = nameToInt(name), stage = commonToInt(common);
		int round = timeToInt(time);
		int num = aliveToInt(alive);
		double [] ratio = estimate(threshold);
		double ans = 0.0, wei = 1.0;
		if(chanceRaise[player][stage][seat][round][num]>0.9){
			for(int i=2;i>=0;i--){
				ans +=  (wei/2)*
						raise[player][stage][seat][round][i][num];
				wei -= ratio[i];
				ans +=  (wei/2)*
						raise[player][stage][seat][round][i][num];
			}
			return ans/chanceRaise[player][stage][seat][round][num];
		}else{
			return 0.1;
		}
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
							double norm = Math.sqrt(chanceFold[0][j][k][l][m][n]);
							if(norm > 0.9){
								if(norm < 3) norm = 3;
								fold[0][j][k][l][m][n]=(fold[0][j][k][l][m][n]/chanceFold[0][j][k][l][m][n])*norm;
								chanceFold[0][j][k][l][m][n] = norm;
							}else{
								if(n == 1){
									chanceFold[0][j][k][l][m][n] = chanceFold[0][j][k][l][m][0];
									fold[0][j][k][l][m][n] = fold[0][j][k][l][m][0];
								}else{
									chanceFold[0][j][k][l][m][n] = 1;
									fold[0][j][k][l][m][n] = 0.3;
								}
							}
						}
					}
					for(int n=0;n<2;n++){
						double norm = Math.sqrt(chanceRaise[0][j][k][l][n]);
						if(norm > 0.9){
							if(norm<3)norm = 3;
							for(int m=0;m<3;m++){
								raise[0][j][k][l][m][n] = (raise[0][j][k][l][m][n]/chanceRaise[0][j][k][l][n]) * norm; 
							}
							chanceRaise[0][j][k][l][n] = norm;
						}else{
							if(n==1){
								for(int m=0;m<3;m++){
									raise[0][j][k][l][m][n] = raise[0][j][k][l][m][0]; 
								}
								chanceRaise[0][j][k][l][n] = chanceRaise[0][j][k][l][0];
							}else{
								for(int m=0;m<3;m++){
									raise[0][j][k][l][m][n] = 0.03; 
								}
								chanceRaise[0][j][k][l][n] = 1;
							}
						}
					}
				}
			}
		}
	}
}
