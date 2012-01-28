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
	
	private double EVForRaise(double winningPr, double raiseRate) {
		double raiseEV;
		ExpectedHand myEHFuture = myEH.clone();
		if (hasLeftFold) {
			double rightCallPr = updateEH2(myEHFuture, rightEH, (int) (potSize * (1 + raiseRate)),
					new Action(Action.Type.CALL, (int) (potSize * raiseRate)));
			raiseEV = rightCallPr * (winningPr * (potSize + (potSize + toCall) * raiseRate) -
					(1 - winningPr) * ((potSize + toCall) * raiseRate + toCall)) + (1 - rightCallPr) * potSize;
		} else if (hasRightFold) {
			double leftCallPr = updateEH2(myEHFuture, leftEH, (int) (potSize * (1 + raiseRate)),
					new Action(Action.Type.CALL, (int) (potSize * raiseRate)));
			raiseEV = leftCallPr * (winningPr * (potSize + (potSize + toCall) * raiseRate) -
					(1 - winningPr) * ((potSize + toCall) * raiseRate + toCall)) + (1 - leftCallPr) * potSize;
		} else {
			int myPot = stackSize - myStack;
			int leftPot = stackSize - leftStack;
			int rightPot = stackSize - rightStack;
			
			int r = (int) ((2*rightPot + leftPot) * (1 + raiseRate) - myPot);
			
			double leftCallPr = updateEH3(myEHFuture, leftEH, rightEH, potSize + r,
					new Action(Action.Type.CALL, (int) (potSize * raiseRate)));
			double rightCallPr = updateEH3(myEHFuture, rightEH, leftEH, potSize + r,
					new Action(Action.Type.CALL, (int) (potSize * raiseRate)));

			raiseEV = leftCallPr * rightCallPr * (winningPr * (3 * (myPot + r) - r - leftPot - rightPot) - (1 - winningPr) * r) +
			((1 - leftCallPr) * rightCallPr + leftCallPr * (1 - rightCallPr)) * (winningPr * (2 * (myPot + r) - r - leftPot - rightPot) - (1 - winningPr) * r) +
			(1 - leftCallPr) * (1 - rightCallPr) * (myPot + leftPot + rightPot);
		}
		
		return raiseEV;
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
		int c1 = myHand.hole[0].toLibValue();
		int c2 = myHand.hole[1].toLibValue();
		double winningPr = ExpectedHand.computeSixCardOdds3(c1, c2, leftEH, rightEH, 100);
		
		
		final double SMALL_RAISE = 0.5;
		final double MEDIUM_RAISE = 1;
		final double BIG_RAISE = 3;
		
		
		double[] decisionEV = new double[5];
		
		// call
		decisionEV[0] = canCall ? (winningPr * (potSize + toCall) - toCall) : -stackSize;
		
		// check
		decisionEV[1] = !canCall ? (winningPr * potSize) : -stackSize;
		
		// raise small
		decisionEV[2] = EVForRaise(winningPr, SMALL_RAISE);

		// raise medium
		decisionEV[3] = EVForRaise(winningPr, MEDIUM_RAISE);
		
		// raise big
		decisionEV[4] = EVForRaise(winningPr, BIG_RAISE);

		int largestIndex = 0;
		double largestEV = decisionEV[0];
		for (int i = 0; i < 5; i++) {
			double ev = decisionEV[i];
			if (ev > largestEV) {
				largestIndex = i;
				largestEV = ev;
			}
		}
		
		int myPot = stackSize - myStack;
		int leftPot = stackSize - leftStack;
		int rightPot = stackSize - rightStack;
		
		switch (largestIndex) {
		case 0:
			return "CALL";
		case 1:
			return "CHECK";
		case 2:
			int r = (int) ((2*rightPot + leftPot) * (1 + SMALL_RAISE) - myPot);
			return (canCall ? "RAISE " : "BET ") + r;
		case 3:
			r = (int) ((2*rightPot + leftPot) * (1 + MEDIUM_RAISE) - myPot);
			return (canCall ? "RAISE " : "BET ") + r;
		case 4:
			r = (int) ((2*rightPot + leftPot) * (1 + BIG_RAISE) - myPot);
			return (canCall ? "RAISE " : "BET ") + r;
		default:
			return "CHECK";
		}
	}

	@Override
	public String turn_computation() {
		return flop_computation();
	}

	@Override
	public String river_computation() {
		return flop_computation();
	}
}
