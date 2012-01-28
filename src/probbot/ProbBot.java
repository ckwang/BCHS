package probbot;

import util.*;

class ProbBot extends GenericBot {
	
	boolean hasInit = false;
	boolean hasFlop = false;
	boolean hasTurn = false;
	boolean hasRiver = false;
	ExpectedHand myEH;
	ExpectedHand leftEH;
	ExpectedHand rightEH;
	
	
	private void initialize() {
		int c1 = myHand.hole[0].toLibValue();
		int c2 = myHand.hole[1].toLibValue();
		
		myEH = new ExpectedHand(c1, c2);
		leftEH = new ExpectedHand();
		rightEH = new ExpectedHand();
		
		hasInit = true;
	}
	
	private double updateEH3(ExpectedHand eh1, final ExpectedHand eh2, final ExpectedHand eh3, int potSize, Action action) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = eh2.getWinningProbility(c1, c2) * eh3.getWinningProbility(c1, c2);
				double threshold = 0.33;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(20*(threshold - winningProb)));
				
				return playingProb;
			}
		}
		
		return eh1.multiply(new HP());
	}
	
	private double updateEH2(ExpectedHand eh1, final ExpectedHand eh2, int potSize, Action action) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = eh2.getWinningProbility(c1, c2);
				double threshold = 0.5;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(20*(threshold - winningProb)));
				
				return playingProb;
			}
		}
		
		return eh1.multiply(new HP());
	}
	
	public void reactToAction(Action action) {
		switch (action.type) {
		case DEAL:
			if (myHand.community.size() == 3) {
				int card1 = myHand.community.get(0).toLibValue();
				int card2 = myHand.community.get(1).toLibValue();
				int card3 = myHand.community.get(2).toLibValue();
				
				myEH.addCard(card1);
				myEH.addCard(card2);
				myEH.addCard(card3);
				leftEH.addCard(card1);
				leftEH.addCard(card2);
				leftEH.addCard(card3);
				rightEH.addCard(card1);
				rightEH.addCard(card2);
				rightEH.addCard(card3);
			} else {
				int card = myHand.community.get(myHand.community.size() - 1).toLibValue();
				myEH.addCard(card);
				leftEH.addCard(card);
				rightEH.addCard(card);
			}
			
			break;
		case BET: case CALL: case CHECK: case FOLD: case RAISE:
			if (action.actor.compareToIgnoreCase(leftName) == 0) {
				if (hasRightFold) {
					updateEH2(leftEH, myEH, potSize, action);
				} else {
					updateEH3(leftEH, myEH, rightEH, potSize, action);
				}
			} else {
				if (hasLeftFold) {
					updateEH2(rightEH, myEH, potSize, action);
				} else {
					updateEH3(rightEH, myEH, leftEH, potSize, action);
				}
			}
			
		}
	}
	
	@Override
	public String preflop_computation() {
		
		if (!hasInit)	initialize();
		
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
//		// update left eh
//		if (!hasLeftFold) {
//			if (!hasFlop) {
//				if (position == 0) {
//					leftEH.addCard(myHand.community.get(0).toLibValue());
//					leftEH.addCard(myHand.community.get(1).toLibValue());
//					leftEH.addCard(myHand.community.get(2).toLibValue());
//				}
//			}
//			
//			if (rightAction != null) {	// right hasn't fold at that point
//				updateEH3(leftEH, myEH, rightEH, leftAction);
//			} else {
//				updateEH2(leftEH, myEH, leftAction);
//			}
//		}
//		
//		// update right eh
//		if (!hasRightFold) {
//			if (!hasFlop) {
//				if (position == 0 || position == 2) {
//					rightEH.addCard(myHand.community.get(0).toLibValue());
//					rightEH.addCard(myHand.community.get(1).toLibValue());
//					rightEH.addCard(myHand.community.get(2).toLibValue());
//				}
//			}
//			
//			if (!hasLeftFold) {	// left hasn't fold at that point
//				updateEH3(rightEH, myEH, leftEH, rightAction);
//			} else {
//				updateEH2(rightEH, myEH, rightAction);
//			}
//		}
		
		Action nextAction = null;
		int c1 = myHand.hole[0].toLibValue();
		int c2 = myHand.hole[1].toLibValue();
		double winningPr = ExpectedHand.computeSixCardOdds3(c1, c2, leftEH, rightEH, 100);
		
		
		final int SMALL_RAISE = 5;
		final int MEDIUM_RAISE = 5;
		final int BIG_RAISE = 5;
		
		// call
		double callEV = canCall ? (winningPr * (potSize + toCall) - toCall) : -stackSize;
		
		// raise small
		double raiseSmallEV;
		ExpectedHand myEHFuture = myEH.clone();
		if (hasLeftFold) {
			double rightCallPr = updateEH2(myEHFuture, rightEH, potSize * (1 + SMALL_RAISE),
					new Action(Action.Type.CALL, potSize * SMALL_RAISE));
			raiseSmallEV = rightCallPr * (winningPr * (potSize + (potSize + toCall) * SMALL_RAISE) -
					(1 - winningPr) * ((potSize + toCall) * SMALL_RAISE + toCall)) + (1 - rightCallPr) * potSize;
		} else if (hasRightFold) {
			double leftCallPr = updateEH2(myEHFuture, leftEH, potSize * (1 + SMALL_RAISE),
					new Action(Action.Type.CALL, potSize * SMALL_RAISE));
			raiseSmallEV = leftCallPr * (winningPr * (potSize + (potSize + toCall) * SMALL_RAISE) -
					(1 - winningPr) * ((potSize + toCall) * SMALL_RAISE + toCall)) + (1 - leftCallPr) * potSize;
		} else {
			double leftCallPr = updateEH3(myEHFuture, leftEH, rightEH, potSize * (1 + SMALL_RAISE),
					new Action(Action.Type.CALL, potSize * SMALL_RAISE));
			double rightCallPr = updateEH3(myEHFuture, rightEH, leftEH, potSize * (1 + SMALL_RAISE),
					new Action(Action.Type.CALL, potSize * SMALL_RAISE));
			
			
		}
		
		// raise medium
		// raise big
		
//		// update my eh
//		if (!hasFlop) {
//			myEH.addCard(myHand.community.get(0).toLibValue());
//			myEH.addCard(myHand.community.get(1).toLibValue());
//			myEH.addCard(myHand.community.get(2).toLibValue());
//			
//			hasFlop = true;
//		}
//		
//		if (hasLeftFold) {
//			updateEH2(myEH, rightEH, nextAction);
//		} else if (hasRightFold) {
//			updateEH2(myEH, leftEH, nextAction);
//		} else {
//			updateEH3(myEH, leftEH, rightEH, nextAction);
//		}
		
		return nextAction.toString();
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
