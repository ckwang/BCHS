package bot1;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import util.HandEval;

public class TableGen {
	public static void main(String[] args){
		HandEval he = new HandEval();
		PrintWriter output =null;
		try {
			 output = new PrintWriter(new FileOutputStream("tmp.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=12;i<13;i++){
			for(int j=10;j<13;j++){System.out.println(i+" "+j);
				for(int k=0;k<13;k++){
					for(int l=0;l<13;l++){
						int used = 0;
						int[] send = new int[4];
						if(i<=j){
							send[0]=i*4+used;
							used++;
							send[1]=j*4+used;
							used++;
						}else{
							send[0]=i*4+used;
							send[1]=j*4+used;
							used++;
						}
						if(k<=l){
							send[2]=k*4+used;
							used++;
							send[3]=l*4+used;
							used++;
						}else{
							send[2]=k*4+used;
							send[3]=l*4+used;
							used++;
						}
						double equity = he.computePreFlopEquityForSpecificHoleCards(send, 2)[0];
						output.println(String.format("%.4f", equity)+",\t//"
								+handDisplay(i,j)+" v.s. "+handDisplay(k,l));
					}
				}
			}
		}
		output.flush();
		output.close();
	}
	static String toChar(int i){
		if(i==0)return "A";
		if(i==1)return "K";
		if(i==2)return "Q";
		if(i==3)return "J";
		if(i==4)return "T";
		return ((Character)((char)((int)'2'+12-i))).toString();
	}
	static String handDisplay(int i,int j){
		return i<=j?toChar(i)+toChar(j)+"o":toChar(j).toString()+toChar(i)+"s";
	}
	
}
