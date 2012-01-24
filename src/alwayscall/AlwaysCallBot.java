package alwayscall;

import util.*;

class AlwaysCallBot extends GenericBot {
	
	@Override
	public String preflop_computation() {
		double prob;
		if (leftName == null || rightName == null) {
			prob = PokerTable.preflopWinningProb2(myHand.hole[0], myHand.hole[1]);
		}
		else {
			prob = PokerTable.preflopWinningProb3(myHand.hole[0], myHand.hole[1]);
		}
		return "CHECK";
	}

	@Override
	public String flop_computation() {
		return "CHECK";
	}

	@Override
	public String turn_computation() {
		return "CHECK";
	}

	@Override
	public String river_computation() {
		return "CHECK";
	}
}
