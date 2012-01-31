package util;


public class PreflopTable {
	static int length = PreflopTableData1.prob.length;
	static double getProb(int c1,int c2,int c3,int c4){
		int v1,v2,v3,v4,h1,h2,tmp,index;
		if(c1>c2){
			tmp=c1;
			c1=c2;
			c2=tmp;
		}
		if(c3>c4){
			tmp=c3;
			c3=c4;
			c4=tmp;
		}
		if(c1%4!=c2%4){			
			v1=c1/4;
			v2=c2/4;	
		}else{
			v1=c2/4;
			v2=c1/4;
		}
		if(c3%4!=c4%4){			
			v3=c3/4;
			v4=c4/4;	
		}else{
			v3=c4/4;
			v4=c3/4;
		}
		h1=v1*13+v2;
		h2=v3*13+v4;
		boolean flip;
		if(h1<h2){
			index=h1*(169*2-h1+1)/2+h2-h1;
			flip = false;
		}else{
			index=h2*(169*2-h2+1)/2+h1-h2;
			flip = true;
		}
		double ans;
//		System.out.println(h1+" "+h2);
		if(index<length)ans = PreflopTableData1.prob[index]/1000.0;
		else ans = PreflopTableData2.prob[index-length]/1000.0;
		return flip?1-ans:ans;
	}
	public static void main(String[] args){
		System.out.println(getProb(42,38,35,13));
	}
}
