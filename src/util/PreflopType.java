package util;

public class PreflopType {
	public static int getType(int c1,int c2){
		if(c1>c2){
			int tmp = c1;
			c1 = c2;
			c2 = tmp;
		}
		int r1 = c1/4,s1 = c1%4,r2 = c2/4,s2 = c2%4;
		if(r1 == r2){
			if(r1<4)return 0;
			if(r1<8)return 1;
			return 2;
		}
		int answer;
		if(r1 == 0){
			if(r2<4)answer = 3;
			else if(r2<8)answer = 4;
			else answer = 5; 
		}else if(r1 == 1){
			if(r2==2)answer = 3;
			else if(r2<6)answer = 6;
			else answer = 7;
		}else if(r1 ==2){
			if(r2 == 3)answer = 6;
			else if(r2<7)answer = 8;
			else answer = 9;
		}else if(r1==3){
			if(r2==4)answer = 8;
			else answer = 9;
		}else{
			if(r1==4&&r2==5)answer = 9;
			else if(r2-r1==1&&r1<11)answer = 10;
			else if(r1<6)answer = 11;
			else answer = 12;
		}
		if(s1==s2)answer += 10;
		return answer;
	}
}
