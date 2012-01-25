package alwayscall;

import util.*;

class AlwaysCallBot extends GenericBot {
	
	@Override
	public String preflop_computation() {
		Card[] holeCards = myHand.hole;
		double winningProb;
		
		switch (position) {
		case 0:	// dealer
			winningProb = PokerTable.preflopWinningProb3(holeCards[0], holeCards[1]);
			if (winningProb >= 0.33) {
				return "CALL";
			} else {
				return "FOLD";
			}
		case 1:	// sb
			if (hasRightFold) {
				winningProb = PokerTable.preflopWinningProb2(holeCards[0], holeCards[1]);
				if (winningProb >= 0.5) {
					return "CALL"; 
				} else {
					return "FOLD";
				}
			} else {
				winningProb = PokerTable.preflopWinningProb3(holeCards[0], holeCards[1]);
				if (winningProb >= 0.33) {
					return "CALL";
				} else {
					return "FOLD";
				}
			}
		case 2:	// bb
			if (hasLeftFold || hasRightFold) {
				winningProb = PokerTable.preflopWinningProb2(holeCards[0], holeCards[1]);
				if (winningProb >= 0.5) {
					return "CALL"; 
				} else {
					return "FOLD";
				}
			} else {
				winningProb = PokerTable.preflopWinningProb3(holeCards[0], holeCards[1]);
				if (winningProb >= 0.33) {
					return "CALL";
				} else {
					return "FOLD";
				}
			}
		}
		
		return null;
	}

	@Override
	public String flop_computation() {
		switch (position) {
		case 1:	// sb
			if (hasLeftFold || hasRightFold) {
				
			} else {

			}
		case 2:	// bb
			if (hasLeftFold || hasRightFold) {

			} else {

			}
		case 0:	// dealer
			if (hasLeftFold || hasRightFold) {
				
			} else {

			}
		}
		
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
