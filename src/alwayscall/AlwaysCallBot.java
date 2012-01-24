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
		double odds;
		if (toCheck) {
			odds = expected_value(call_value(), prob);
			return "CHECK";
		}
		else if (toCall) {
			return "CALL";
		}
		else if (toBet > 0) {
			return "BET " + toBet;
		}
		else if (toRaise > 0) {
			return "RAISE " + toRaise;
		}
		return "FOLD";
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
