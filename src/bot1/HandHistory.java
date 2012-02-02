package bot1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import util.*;

public class HandHistory {

	public List<List<String>> player1 = new ArrayList<List<String>>();
	public List<List<String>> player2 = new ArrayList<List<String>>();;
	public List<List<String>> player3 = new ArrayList<List<String>>();;
	public List<List<String>> all = new ArrayList<List<String>>();;
	public String[] names = null;

	public static int cardToInt(String card){
		int s=0,r=0;
		switch(card.charAt(0)){
		case 'A':	r=0;break;
		case 'K':	r=1;break;
		case 'Q':	r=2;break;
		case 'J':	r=3;break;
		case 'T':	r=4;break;
		default:	r=12-(card.charAt(0)-'2');
		}
		switch(card.charAt(1)){
		case 's':	s=0;break;
		case 'h':	s=1;break;
		case 'd':	s=2;break;
		case 'c':	s=3;break;
		}
		return s+r*4;
	}
	
	private int [] playerStack = new int[3];
	private int stack;
	private int [] seat = new int[3];
	private boolean [] folded = new boolean [3];
	private int common;
	private int [] tablecards = new int[5];
	private int [][] holecards = new int [3][2];
	private int [] time = new int [3];
	public Statistics statistic = new Statistics();	
	
	public void parseHand(BufferedReader br) throws IOException {
		String[] dealer = br.readLine().split(" ");
		String[] sb = br.readLine().split(" ");
		String[] bb = br.readLine().split(" ");
		assert dealer.length == 5;
		if (names == null) {
			names = new String[3];
			names[0] = new String(dealer[3]);
			names[1] = new String(sb[3]);
			names[2] = new String(bb[3]);
		}
		for(int i=0;i<3;i++)seat[i]=0;

		//System.out.println("---");
		for(int i=0;i<3;i++){
			playerStack[i]=0;
			folded[i] = false;
			time[i] = 0;
		}
		stack = 0;
		common = 0;
		
		String[] tokens;
		String line, name;
		int player,raise,alive = 3;
		while (!(line = br.readLine()).equals("")) {
			tokens = line.split(" ");
			name = tokens[0];
			if (tokens[0].equals("Dealt")) {
				name = tokens[2];
				player = nameToInt(name);
				holecards[player][0] = cardToInt(tokens[3].substring(1));
				holecards[player][1] = cardToInt(tokens[4]);
			}
			player = nameToInt(name);
			if (tokens[1].equals("posts")) {
				seat[player] = Integer.parseInt(tokens[5]) == 1?1:2;
				playerStack[player]+=Integer.parseInt(tokens[5]);
				stack += playerStack[player];
			}
			else if (tokens[1].equals("raises")) {
				raise = Integer.parseInt(tokens[3]) - playerStack[player];
				int max = 0;
				for(int i=0;i<3;i++){
					if(max<playerStack[i])max = playerStack[i];
				}
				statistic.raise(name, common, time[player], seat[player], stack, max-playerStack[player], raise - max, alive);
				if(!name.startsWith("OWWW"))
					statistic.raise("all0000", common, time[player], seat[player], stack, max-playerStack[player], raise - max, alive);
				time[player]++;
				playerStack[player]+=raise;
				stack += raise;
			}
			else if (tokens[1].equals("folds")) {
				int max = 0;
				for(int i=0;i<3;i++){
					if(max<playerStack[i])max = playerStack[i];
				}
				//if(seat[player]==0&&common==0&&time[player]==0){System.out.print("**");
				//	System.out.println(name+" "+stack +" "+ max);}
				
				statistic.fold(name, common, time[player], seat[player], stack, max-playerStack[player],alive);
				if(!name.startsWith("OWWW"))
					statistic.fold("all0000", common, time[player], seat[player], stack, max-playerStack[player],alive);
				alive--;
				folded [player] = true;
			}
			else if (tokens[1].equals("bets")) {
				raise = Integer.parseInt(tokens[2]);
				int max = 0;
				for(int i=0;i<3;i++){
					if(max<playerStack[i])max = playerStack[i];
				}
				statistic.bet(name, common, time[player], seat[player], stack, raise, alive);
				if(!name.startsWith("OWWW"))
					statistic.bet("all0000", common, time[player], seat[player], stack, raise, alive);
				
				time[player]++;
				stack+=max-playerStack[player];
				playerStack[player]=max;
				playerStack[player]+=raise;
				stack += raise;
			}
			else if (tokens[1].equals("calls")) {
				int max = 0;
				for(int i=0;i<3;i++){
					if(max<playerStack[i])max = playerStack[i];
				}
				statistic.call(name, common, time[player], seat[player], stack, max-playerStack[player], alive);
				if(!name.startsWith("OWWW"))
					statistic.call("all0000", common, time[player], seat[player], stack, max-playerStack[player], alive);
				
				time[player]++;
				stack+=max-playerStack[player];
				playerStack[player]=max;
			}
			else if (tokens[1].equals("checks")) {
				statistic.check(name, common, seat[player], alive);
				if(!name.startsWith("OWWW"))
					statistic.check("all0000", common, seat[player], alive);
				
				time[player]++;
			}
			else if (tokens[1].equals("FLOP")){
				common = 3;
				tablecards[0] = cardToInt(tokens[3].substring(1));
				tablecards[1] = cardToInt(tokens[4]);
				tablecards[2] = cardToInt(tokens[5]);
				for(int i=0;i<3;i++)time[i]=0;
			}
			else if (tokens[1].equals("TURN")){
				common = 4;
				tablecards[3] = cardToInt(tokens[6].substring(1));
				for(int i=0;i<3;i++)time[i]=0;
			}
			else if (tokens[1].equals("RIVER")){
				common = 5;
				tablecards[4] = cardToInt(tokens[7].substring(1));
				for(int i=0;i<3;i++)time[i]=0;
				
			}
		}
	}
	public int nameToInt(String name) {
		if (name.equals(names[0]))
			return 0;
		else if (name.equals(names[1]))
			return 1;
		else
			return 2;
	}
	
	public void parseFile(String file) {
		names = null;
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith("Hand #")) {
					assert strLine.endsWith(Integer.toString(player1.size() + 1));
					parseHand(br);
				}
				else {
					continue;
				}
			}
			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	public static void main(String[] args) {
		HandHistory all = new HandHistory(), all2 = new HandHistory();
		BufferedReader br = null; 
		int count = 0;
		String file = "C:\\Documents and Settings\\Administrator.888TIGER-E94494\\орн▒\\pokerbot\\dir.txt";
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strLine = null;
		String prefix = "C:\\Documents and Settings\\Administrator.888TIGER-E94494\\орн▒\\pokerbot\\HandHistory1\\";
		
		try {
			while ((strLine = br.readLine()) != null) {
				//if(count==0)all.parseFile(prefix + strLine);
				//if(count>600)all2.parseFile(prefix + strLine);
				all.parseFile(prefix + strLine);
				count++;
				System.out.println(strLine);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		all.statistic.reduce();
		System.out.println("Folding Prob");
		for(int i=0;i<all.statistic.players;i++){
			System.out.println(all.statistic.namelist.get(i));
			for(int j=0;j<4;j++){
				System.out.println(j==0?"PREFLOP":j==1?"FLOP":j==2?"TURN":"RIVER");
				for(int k=0;k<3;k++){
					System.out.println(k==0?"DEALER":k==1?"SB":"BB");
					for(int l=0;l<3;l++){
						for(int m=0;m<3;m++){
							for(int n=0;n<2;n++){
							/*if(all2.statistic.chanceFold[i][j][k][l][m]==0){
								System.out.print("N/A ");
								continue;
							}*/
							//System.out.print((double)all2.statistic.fold[i][j][k][l][m]/
							//		all2.statistic.chanceFold[i][j][k][l][m]+"-");
								/*System.out.print((double)all.statistic.fold[i][j][k][l][m][n]/
										all.statistic.chanceFold[i][j][k][l][m][n]+"~");*/
								System.out.print(all.statistic.chanceFold[i][j][k][l][m][n]+"~");
							}
							System.out.print(" ");
						}
						System.out.println("");
					}
				}
			}
		}
		System.out.println(all.statistic.toInitString());
		
		/*System.out.println("Raising Prob");
		for(int i=0;i<all.statistic.players;i++){
			System.out.println(all.statistic.namelist.get(i));
			for(int j=0;j<4;j++){
				System.out.println(j==0?"PREFLOP":j==1?"FLOP":j==2?"TURN":"RIVER");
				for(int k=0;k<3;k++){
					System.out.println(k==0?"DEALER":k==1?"SB":"BB");
					for(int l=0;l<3;l++){
						for(int m=0;m<3;m++){
							for(int n=0;n<2;n++){
								if(all.statistic.chanceRaise[i][j][k][l][n]==0){
									System.out.print("N/A ");
									continue;
								}
								System.out.print((double)all.statistic.raise[i][j][k][l][m][n]/
										all.statistic.chanceRaise[i][j][k][l][n]+" ");
							}
						}
						System.out.println("");
					}
				}
			}
		}*/
	}
}
