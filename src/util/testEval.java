package util;


public class testEval {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		 HandEval eval = new HandEval();
		 int[] holeCards = {0, 6};
//		 int[] holeCards = {0, 6, 50, 8, 51, 8};
		 int[] boardCards = {27, 31, 33};
		// System.out.println("" +
		// eval.computePreFlopEquityForSpecificHoleCards(holeCards, 2));
		// long a = System.currentTimeMillis();
		// System.out.println("" +
		// eval.computeFlopEquityForSpecificCards(holeCards, boardCards, 2));
		// long b = System.currentTimeMillis();
		// System.out.println(b-a);
		// eval.timeRankMethod();
		Equity e = new Equity();
		long start = System.nanoTime();
		double[] equity = null;
//		for(int i=0;i<1000;i++){
			equity = e.monteCarlo(new String[] { "AcTh" }, "9c8h7s");
//			equity = eval.computeFlopEquityForSpecificCards(holeCards, boardCards, 3);
//			equity = e.calc(new String[] { "AcTh", "KdTc", "2sTs" }, "9c8h7s");
//		}

		long end = System.nanoTime();
		for (double z : equity) {
			System.out.println(z);
		}
		System.out.println((end-start)/1000000.0);
	}
}