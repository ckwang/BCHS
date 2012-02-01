package probbot;

import bot1.Statistics;
import util.*;

class ProbBot extends GenericBot {
	
	boolean hasFlop = false;
	boolean hasTurn = false;
	boolean hasRiver = false;
	ExpectedHand myEH;
	ExpectedHand leftEH;
	ExpectedHand rightEH;
	Statistics statistics = new Statistics();
	int leftActiveCount = 0;
	int rightActiveCount = 0;
	
	public ProbBot() {
		myHand = new Hand(new Card(25), new Card(32));
		myHand.community.add(new Card(3));
		myHand.community.add(new Card(4));
		myHand.community.add(new Card(5));
		
		handInitialize();
		potSize = 5;
		hasFlop = true;
		
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
	
		flop_computation();
		
		myHand.addCards(new Card(6));
		int card4 = myHand.community.get(3).toLibValue();
		myEH.addCard(card4);
		leftEH.addCard(card4);
		rightEH.addCard(card4);
		
		flop_computation();
		
		myHand.addCards(new Card(7));
		int card5 = myHand.community.get(4).toLibValue();
		myEH.addCard(card5);
		leftEH.addCard(card5);
		rightEH.addCard(card5);
		
		flop_computation();
	}
	
	@Override
	public void handInitialize() {
		int c1 = myHand.hole[0].toLibValue();
		int c2 = myHand.hole[1].toLibValue();
		
		hasFlop = false;
		hasTurn = false;
		hasRiver = false;
		leftActiveCount = 0;
		rightActiveCount = 0;
		
		myEH = new ExpectedHand(c1, c2);
		leftEH = new ExpectedHand();
		rightEH = new ExpectedHand();
		
		priorEH3(myEH);
		priorEH3(leftEH);
		priorEH3(rightEH);
	}

	public void processStatistics() {}
	
	private void priorEH3(ExpectedHand eh)	 {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = PokerTable.preflopWinningProb2(new Card(Card.libValueToValue(c1)),
						new Card(Card.libValueToValue(c2)));
				double threshold = 0.4;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(20*(threshold - winningProb)));				
				return playingProb;
			}
		}
		
		eh.multiply(new HP());
	}

	private double updateEH3Preflop(ExpectedHand eh1, final ExpectedHand eh2, final ExpectedHand eh3, int potSize, final Action action) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = PokerTable.preflopWinningProb2(new Card(Card.libValueToValue(c1)),
						new Card(Card.libValueToValue(c2)));
				winningProb *= winningProb;
				double threshold = 0.33;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(5*(threshold - winningProb)));
				return playingProb;
			}
		}
		
		return eh1.multiply(new HP());
	}
	
	private double updateEH2Preflop(ExpectedHand eh1, final ExpectedHand eh2, int potSize, Action action) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = PokerTable.preflopWinningProb2(new Card(Card.libValueToValue(c1)),
						new Card(Card.libValueToValue(c2)));
				double threshold = 0.5;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(5*(threshold - winningProb)));
				return playingProb;
			}
		}
		
		return eh1.multiply(new HP());
	}
	
	private double updateEH3(ExpectedHand eh1, final ExpectedHand eh2, final ExpectedHand eh3, int potSize, final Action action) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = eh2.getWinningProbility(c1, c2) * eh3.getWinningProbility(c1, c2);
				double threshold = 0.33;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(5*(threshold - winningProb)));
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
				double threshold = 0.3;	// TODO: define the relation between threshold and parameters
				double playingProb = 1.0/(1+Math.exp(5*(threshold - winningProb)));
				return playingProb;
			}
		}
		
		return eh1.multiply(new HP());
	}
	
	private double[] EVForRaise(double winningPr, double raiseRate) {
		double raiseEV;
		int raiseValue;

		int myPot = stackSize - myStack;
		int leftPot = stackSize - leftStack;
		int rightPot = stackSize - rightStack;
		
		toCall = toCall == -1 ? 0 : toCall;
		int calledPot = potSize + toCall;
		raiseValue = (int) (calledPot * raiseRate + toCall);
		raiseValue = (raiseValue > myStack) ? myStack: raiseValue;
		
		ExpectedHand myEHFuture = myEH.clone();
		if (hasLeftFold) {
			ExpectedHand rightFuture = rightEH.clone();
			updateEH2(myEHFuture, rightEH, calledPot, new Action(Action.Type.RAISE, raiseValue));
			
			double rightCallPr = updateEH2(rightFuture, myEHFuture, potSize + raiseValue, new Action(Action.Type.CALL));
			
			winningPr = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), rightFuture);
			System.out.println("RightCallPr: " + rightCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);
			
			raiseEV = rightCallPr * (winningPr * (potSize + raiseValue - toCall) -
					(1 - winningPr) * raiseValue) + (1 - rightCallPr) * potSize;
		} else if (hasRightFold) {
			ExpectedHand leftFuture = leftEH.clone();
			updateEH2(myEHFuture, leftEH, calledPot, new Action(Action.Type.RAISE, raiseValue));
			
			double leftCallPr = updateEH2(leftFuture, myEHFuture, potSize + raiseValue, new Action(Action.Type.CALL));
			
			winningPr = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture);
			System.out.println("LeftCallPr: " + leftCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);
			
			raiseEV = leftCallPr * (winningPr * (potSize + raiseValue - toCall) -
					(1 - winningPr) * raiseValue) + (1 - leftCallPr) * potSize;
		} else {
			ExpectedHand leftFuture = leftEH.clone();
			ExpectedHand rightFuture = rightEH.clone();
						
			updateEH3(myEHFuture, leftEH, rightEH, potSize + raiseValue,
					new Action(Action.Type.RAISE, raiseValue));
			
			double leftCallPr = updateEH3(leftFuture, myEHFuture, rightEH, potSize + raiseValue, 
					new Action(Action.Type.CALL));
			double rightCallPr = updateEH3(rightFuture, myEHFuture, leftEH, potSize + raiseValue,
					new Action(Action.Type.CALL));
			
			winningPr = ExpectedHand.computeSixCardOdds3(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture, rightFuture, 100);
			System.out.println("LeftCallPr: " + leftCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);

			raiseEV = leftCallPr * rightCallPr * (winningPr * (3 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPr) * raiseValue) +
			((1 - leftCallPr) * rightCallPr + leftCallPr * (1 - rightCallPr)) * (winningPr * (2 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPr) * raiseValue) +
			(1 - leftCallPr) * (1 - rightCallPr) * (myPot + leftPot + rightPot);
		}
		
		double[] result = {raiseEV, (double) raiseValue};
		
		return result;
	}
	
	@Override
	public void handleShow(Action a) {
	}
	
	@Override
	public void reactToAction(Action action) {
			
		System.out.println("---");
		System.out.println(action.type + ":" + action.actor + ":" + action.amount);
		System.out.println("myStack: " + myStack + ", leftStack: " + leftStack + ", rightStack: " + rightStack);
		System.out.println("toCall: " + toCall + ", toBet: " + toBet);
		System.out.println("potSize: " + potSize);
		
		switch (action.type) {
		case DEAL:
			leftActiveCount = 0;
			rightActiveCount = 0;
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
				
				hasFlop = true;
			} else {
				int card = myHand.community.get(myHand.community.size() - 1).toLibValue();
				myEH.addCard(card);
				leftEH.addCard(card);
				rightEH.addCard(card);
				
				if (!hasTurn) {
					hasTurn = true;
				} else {
					hasRiver = true;
				}
			}
			
			break;
		case BET: case CALL: case CHECK: case FOLD: case RAISE:
		
			int common = 0;
			int activeCount = 0;
			int p = 0;
			int toCall = 0;
			
			if (action.actor.compareToIgnoreCase(leftName) == 0) {
				activeCount = leftActiveCount;
				leftActiveCount++;
				common = leftEH.common;
				p = (position + 1) % 3;
				toCall = leftStack - myStack;

			} else if (action.actor.compareToIgnoreCase(rightName) == 0) {
				activeCount = rightActiveCount;
				rightActiveCount++;
				common = rightEH.common;
				p = (position + 2) % 3;
				toCall = hasLeftFold ? (rightStack - myStack) : (rightStack - leftStack);
			}
			
			if (action.actor.compareToIgnoreCase(leftName) == 0 || action.actor.compareToIgnoreCase(rightName) == 0) {
				switch (action.type) {
				case BET:
					statistics.bet(action.actor, common, activeCount, p, potSize, action.amount);
					break;
				case CALL:
					statistics.call(action.actor, common, activeCount, p, potSize, toCall);
					break;
				case CHECK:
					statistics.check(action.actor, common, p);
					break;
				case FOLD:
					statistics.fold(action.actor, common, activeCount, p, potSize, toCall);
					break;
				case RAISE:
					statistics.raise(action.actor, common, activeCount, p, potSize, toCall, action.amount);
				}
			}
			
			if (hasFlop) {
				if (action.actor.compareToIgnoreCase(leftName) == 0) {
					if (hasRightFold) {
						updateEH2(leftEH, myEH, potSize, action);
					} else {
						updateEH3(leftEH, myEH, rightEH, potSize, action);
					}
				} else if (action.actor.compareToIgnoreCase(rightName) == 0) {
					if (hasLeftFold) {
						updateEH2(rightEH, myEH, potSize, action);
					} else {
						updateEH3(rightEH, myEH, leftEH, potSize, action);
					}
				} else {
					if (hasLeftFold) {
						updateEH2(myEH, rightEH, potSize, action);
					} else if (hasRightFold) {
						updateEH2(myEH, leftEH, potSize, action);
					} else {
						updateEH3(myEH, leftEH, rightEH, potSize, action);
					}
				}
			}
			
		}
	}
	
	@Override
	public String preflop_computation() {
		System.out.println("***");
		System.out.println("toCall: " + toCall + ", toBet: " + toBet + ", toRaise: " + toRaise);

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
		System.out.println("***");
		System.out.println("toCall: " + toCall + ", toBet: " + toBet + ", toRaise: " + toRaise);
		System.out.println("Folding Prob");
		for(int i=1;i<2;i++){
			System.out.println(statistics.namelist.get(i));
			for(int j=0;j<4;j++){
				System.out.println(j==0?"PREFLOP":j==1?"FLOP":j==2?"TURN":"RIVER");
				for(int k=0;k<3;k++){
					System.out.println(k==0?"DEALER":k==1?"SB":"BB");
					for(int l=0;l<3;l++){
						for(int m=0;m<3;m++){
							System.out.print((double)statistics.fold[i][j][k][l][m]/
									statistics.chanceFold[i][j][k][l][m]+"-");
						}
						System.out.println("");
					}
				}
			}
		}
		
//		System.out.println("myStack: " + myStack + ", leftStack: " + leftStack + ", rightStack: " + rightStack);

		int c1 = myHand.hole[0].toLibValue();
		int c2 = myHand.hole[1].toLibValue();
		
		double winningPr;
		
		if (hasLeftFold) {
			winningPr = ExpectedHand.computeSixCardOdds(c1, c2, rightEH);
		} else if (hasRightFold) {
			winningPr = ExpectedHand.computeSixCardOdds(c1, c2, leftEH);
		} else {
			winningPr = ExpectedHand.computeSixCardOdds3(c1, c2, leftEH, rightEH, 100);
		}		
		
		final double SMALL_RAISE = 0.5;
		final double MEDIUM_RAISE = 1;
		final double BIG_RAISE = 3;
		
		
		double[] decisionEV = new double[5];
		double[] raiseValue = new double[5];
		
		// call
		decisionEV[0] = canCall ? (winningPr * (potSize + toCall) - toCall) : -stackSize;
		
		// check
		decisionEV[1] = !canCall ? (winningPr * potSize) : -stackSize;
		
		// raise small
		double[] temp = EVForRaise(winningPr, SMALL_RAISE);
		decisionEV[2] = temp[0];
		raiseValue[2] = temp[1];

		// raise medium
		temp = EVForRaise(winningPr, MEDIUM_RAISE);
		decisionEV[3] = temp[0];
		raiseValue[3] = temp[1];
		
		// raise big
		temp = EVForRaise(winningPr, BIG_RAISE);
		decisionEV[4] = temp[0];
		raiseValue[4] = temp[1];
		
		System.out.println("WinningPr: " + winningPr + ", Pot Size: " + potSize);
		System.out.println("EV: " + decisionEV[0] + ", " + decisionEV[1] + ", " + decisionEV[2] + ", " + decisionEV[3] + ", " + decisionEV[4]);

		int largestIndex = 0;
		double largestEV = decisionEV[0];
		for (int i = 0; i < 5; i++) {
			double ev = decisionEV[i];
			if (ev > largestEV) {
				largestIndex = i;
				largestEV = ev;
			}
		}
		
		if (largestEV < 0)	return "FOLD";
		
		switch (largestIndex) {
		case 0:
			return "CALL";
		case 1:
			return "CHECK";
		case 2:
			return (canCall ? "RAISE:" : "BET:") + (int) raiseValue[2];
		case 3:
			return (canCall ? "RAISE:" : "BET:") + (int) raiseValue[3];
		case 4:
			return (canCall ? "RAISE:" : "BET:") + (int) raiseValue[4];
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
