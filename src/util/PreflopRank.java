package util;

public class PreflopRank {
	public static int[] sort = new int[]{
		0,
		14,
		28,
		42,
		56,
		70,
		13,
		84,
		26,
		39,
		1,
		98,
		52,
		2,
		3,
		27,
		65,
		4,
		112,
		40,
		78,
		53,
		15,
		91,
		5,
		16,
		126,
		41,
		104,
		117,
		66,
		6,
		17,
		130,
		54,
		7,
		143,
		79,
		156,
		8,
		9,
		29,
		92,
		67,
		18,
		140,
		55,
		10,
		30,
		105,
		11,
		80,
		118,
		19,
		68,
		12,
		131,
		20,
		31,
		154,
		43,
		144,
		93,
		69,
		21,
		81,
		106,
		157,
		22,
		32,
		119,
		44,
		82,
		94,
		23,
		132,
		168,
		24,
		33,
		145,
		83,
		57,
		45,
		95,
		107,
		34,
		25,
		158,
		120,
		35,
		96,
		58,
		133,
		46,
		108,
		36,
		97,
		146,
		37,
		109,
		71,
		159,
		59,
		121,
		47,
		38,
		110,
		134,
		48,
		111,
		122,
		72,
		147,
		49,
		60,
		123,
		160,
		85,
		50,
		124,
		125,
		135,
		73,
		51,
		61,
		148,
		136,
		86,
		139,
		62,
		161,
		137,
		138,
		99,
		74,
		63,
		149,
		87,
		64,
		152,
		150,
		162,
		151,
		100,
		113,
		153,
		75,
		76,
		165,
		88,
		163,
		127,
		164,
		77,
		101,
		114,
		166,
		167,
		89,
		128,
		102,
		90,
		115,
		141,
		129,
		103,
		116,
		142,
		155
	};
	public static int[] rank = new int[169];
	static{
		for(int i=0;i<169;i++)rank[sort[i]]=168-i;
	}
	public static int getRank(int c1,int c2){
		int v1,v2,tmp;
		if(c1>c2){
			tmp=c1;
			c1=c2;
			c2=tmp;
		}
		if(c1%4!=c2%4){			
			v1=c1/4;
			v2=c2/4;	
		}else{
			v1=c2/4;
			v2=c1/4;
		}
		return rank[v1*13+v2];
	}
}
