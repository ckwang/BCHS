package util;


public class testEval {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// HandEval eval = new HandEval();
		// int[] holeCards = new int[] {0, 1, 50, 51};
		// int[] boardCards = new int[] {3, 4, 5};
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
		for(int i=0;i<1000;i++){
			 equity = e.calc(new String[] { "AcTh", "KdTc", "2sTs" }, "9c8h7s6s");
		}

		long end = System.nanoTime();
		for (double z : equity) {
			System.out.println(z);
		}
		System.out.println((end-start)/1000000.0/1000);
	}
}