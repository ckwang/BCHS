package probbot;

import bot1.Statistics;
import bot1.StatisticsInit;
import util.*;
import util.Action.Type;

class ProbBot extends GenericBot {
	
	boolean hasFlop = false;
	boolean hasTurn = false;
	boolean hasRiver = false;
	ExpectedHand myEH;
	ExpectedHand leftEH;
	ExpectedHand rightEH;
	Statistics statistics = new Statistics(StatisticsInit.init);
	int leftActiveCount = 0;
	int rightActiveCount = 0;
	
	public ProbBot() {
		myHand = new Hand(new Card(25), new Card(32));
		myHand.community.add(new Card(3));
		myHand.community.add(new Card(4));
		myHand.community.add(new Card(5));
		
		leftName = "testLeft";
		rightName = "testRight";
		
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
	}

	public void processStatistics() {}
	
	private double updateEH(final ExpectedHand eh, final Action action,
			final String name, final int position, final int common, final int actionCount, final int pot, final int toCall, final int numPlayers) {
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = eh.getWinningProbility(c1, c2);
				double threshold = statistics.getFoldProb(name, common, actionCount, position, pot, toCall, numPlayers);	
				double playingProb = 1.0/(1+Math.exp(5*(threshold - winningProb)));
				return playingProb;
			}
		}
		
		return eh.multiply(new HP());
	}
	
	private double updateEHRaise(final ExpectedHand eh, final Action action,
			final String name, final int position, final int common, final int actionCount, final int numPlayers) {
		if(action.type == Type.CHECK) return 1.0;
		final double prob = statistics.getRaiseProb(name, common, actionCount, position, numPlayers);
		class HP implements HandsProbability {
			@Override
			public double getProb(int c1, int c2) {
				double winningProb = eh.getWinningProbility(c1, c2);
				double threshold = prob;	
				double playingProb = 1.0/(1+Math.exp(2*(threshold - winningProb)));
				return playingProb;
			}
		}
		eh.multiply(new HP());
		return prob;
	}
	
	private double[] EVForRaise(double raiseRate) {
		double raiseEV;
		int raiseValue;

		int myPot = stackSize - myStack;
		int leftPot = stackSize - leftStack;
		int rightPot = stackSize - rightStack;
		
		toCall = (toCall == -1) ? 0 : toCall;
		int calledPot = potSize + toCall;
		raiseValue = (int) (calledPot * raiseRate + toCall);
		raiseValue = (raiseValue > myStack) ? myStack: raiseValue;
		raiseValue = (raiseValue < toRaise) ? toRaise : raiseValue;
		raiseValue = (raiseValue < toBet) ? toBet : raiseValue;
		
		if (hasLeftFold) {
			ExpectedHand rightFuture = rightEH.clone();
			
			double rightCallPr = updateEH(rightFuture, new Action(Action.Type.CALL),
					rightName, (position + 2) % 3, rightEH.common, rightActiveCount, potSize + raiseValue, rightStack - (myStack - raiseValue), 2);
			
			double winningPr = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), rightFuture);
			System.out.println("RightCallPr: " + rightCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);
			
			raiseEV = rightCallPr * (winningPr * (potSize + raiseValue - toCall) - (1 - winningPr) * raiseValue) + (1 - rightCallPr) * potSize;
		} else if (hasRightFold) {
			ExpectedHand leftFuture = leftEH.clone();
			
			double leftCallPr = updateEH(leftFuture, new Action(Action.Type.CALL),
					leftName, (position + 1) % 3, leftEH.common, leftActiveCount, potSize + raiseValue, leftStack - (myStack - raiseValue), 2);
			
			double winningPr = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture);
			System.out.println("LeftCallPr: " + leftCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);
			
			raiseEV = leftCallPr * (winningPr * (potSize + raiseValue - toCall) - (1 - winningPr) * raiseValue) + (1 - leftCallPr) * potSize;
		} else {
			ExpectedHand leftFuture = leftEH.clone();
			ExpectedHand rightOnlyFuture = rightEH.clone();
			ExpectedHand rightFuture = rightEH.clone();
						
			double leftCallPr = updateEH(leftFuture, new Action(Action.Type.CALL),
					leftName, (position + 1) % 3, leftEH.common, leftActiveCount, potSize + raiseValue, leftStack - (myStack - raiseValue), 3);
			double rightOnlyCallPr = updateEH(rightOnlyFuture, new Action(Action.Type.CALL),
					rightName, (position + 2) % 3, rightEH.common, rightActiveCount, potSize + raiseValue, rightStack - (myStack - raiseValue), 2);
			double rightCallPr = updateEH(rightFuture, new Action(Action.Type.CALL),
					rightName, (position + 2) % 3, rightEH.common, rightActiveCount, 2*(myPot + raiseValue) + rightPot, rightStack - (myStack - raiseValue), 3);
			
			double winningPrLeftOnly = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture);
			double winningPrRightOnly = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), rightOnlyFuture);
			double winningPrBoth = ExpectedHand.computeSixCardOdds3(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture, rightFuture, 100);
			System.out.println("LeftCallPr: " + leftCallPr + ", rightOnlyCallPr: " + rightOnlyCallPr + ", rightCallPr: " + rightCallPr +
					",\n\t winningPrLeftOnly: " + winningPrLeftOnly + ", winningPrRightOnly: " + winningPrRightOnly + ", winningPrBoth: " + winningPrBoth + ", raiseValue" + raiseValue);

			raiseEV = leftCallPr * rightCallPr * (winningPrBoth * (3 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrBoth) * raiseValue) +
			(1 - leftCallPr) * rightOnlyCallPr * (winningPrRightOnly * (2 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrRightOnly) * raiseValue) +
			leftCallPr * (1 - rightCallPr) * (winningPrLeftOnly * (2 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrLeftOnly) * raiseValue) +
			(1 - leftCallPr) * (1 - rightOnlyCallPr) * (myPot + leftPot + rightPot);
		}
		
		double[] result = {raiseEV, (double) raiseValue};
		
		return result;
	}
	
	private double[] EVForRaiseWithReRaise(double raiseRate) {
		double raiseEV;
		int raiseValue;

		int myPot = stackSize - myStack;
		int leftPot = stackSize - leftStack;
		int rightPot = stackSize - rightStack;
		
		toCall = (toCall == -1) ? 0 : toCall;
		int calledPot = potSize + toCall;
		raiseValue = (int) (calledPot * raiseRate + toCall);
		raiseValue = (raiseValue > myStack) ? myStack: raiseValue;
		raiseValue = (raiseValue < toRaise) ? toRaise : raiseValue;
		raiseValue = (raiseValue < toBet) ? toBet : raiseValue;
		
		if (hasLeftFold || hasRightFold) {
			ExpectedHand oppEH = hasLeftFold ? rightEH : leftEH;
			int seat =  hasLeftFold ? (position + 2)%3 : (position + 1) %3;
			int oppActiveCount = hasLeftFold ? rightActiveCount : leftActiveCount;
			int oppStack = hasLeftFold ? rightStack : leftStack;
			String oppName = hasLeftFold ? rightName : leftName;
			
			ExpectedHand oppFuture = oppEH.clone();
			
			double oppCallPr = updateEH(oppFuture, new Action(Action.Type.CALL),
					oppName, seat, oppEH.common, oppActiveCount, potSize + raiseValue, oppStack - (myStack - raiseValue), 2);
			
			double winningPr = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), oppFuture);
			System.out.println("oppCallPr: " + oppCallPr + ", winningPr: " + winningPr + ", raiseValue" + raiseValue);
			
			double oppRaisingPr = updateEHRaise(oppFuture, new Action(Action.Type.RAISE),
					oppName, seat, oppEH.common, oppActiveCount, 2);
			
			double winningPrRaised = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), oppFuture);
					
			double newpot = potSize + 2*raiseValue - toCall;
			
			double reRaise = 0;
			if(winningPrRaised<0.4){
				reRaise = newpot * winningPrRaised / (1-2*winningPrRaised) * 0.8;
				if(reRaise > newpot) reRaise = newpot;
			}else{
				reRaise = newpot;
			}
			
			double myFoldingPr = statistics.getRaiseAboveProb(oppName, (position + 2) % 3, oppEH.common, oppActiveCount, 2, winningPrRaised);
			
			double EVcall = (winningPr * (newpot - raiseValue) - (1 - winningPr) * raiseValue);
			double EVreraisecall = ( winningPrRaised * (newpot + reRaise - raiseValue) - (1 - winningPrRaised) * (raiseValue + reRaise));
			double EVreraise = ( myFoldingPr * ( -raiseValue ) + ( 1 - myFoldingPr ) * EVreraisecall );
			
			raiseEV = oppCallPr * ( oppRaisingPr * EVreraise +
					(1 - oppRaisingPr) * EVcall )
					+ (1 - oppCallPr) * potSize;
			
		} else {
			ExpectedHand leftFuture = leftEH.clone();
			ExpectedHand rightOnlyFuture = rightEH.clone();
			ExpectedHand rightFuture = rightEH.clone();
						
			double leftCallPr = updateEH(leftFuture, new Action(Action.Type.CALL),
					leftName, (position + 1) % 3, leftEH.common, leftActiveCount, potSize + raiseValue, leftStack - (myStack - raiseValue), 3);
			double rightOnlyCallPr = updateEH(rightOnlyFuture, new Action(Action.Type.CALL),
					rightName, (position + 2) % 3, rightEH.common, rightActiveCount, potSize + raiseValue, rightStack - (myStack - raiseValue), 2);
			double rightCallPr = updateEH(rightFuture, new Action(Action.Type.CALL),
					rightName, (position + 2) % 3, rightEH.common, rightActiveCount, 2*(myPot + raiseValue) + rightPot, rightStack - (myStack - raiseValue), 3);
			
			double winningPrLeftOnly = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture);
			double winningPrRightOnly = ExpectedHand.computeSixCardOdds(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), rightOnlyFuture);
			double winningPrBoth = ExpectedHand.computeSixCardOdds3(myHand.hole[0].toLibValue(), myHand.hole[1].toLibValue(), leftFuture, rightFuture, 100);
			System.out.println("LeftCallPr: " + leftCallPr + ", rightOnlyCallPr: " + rightOnlyCallPr + ", rightCallPr: " + rightCallPr +
					",\n\t winningPrLeftOnly: " + winningPrLeftOnly + ", winningPrRightOnly: " + winningPrRightOnly + ", winningPrBoth: " + winningPrBoth + ", raiseValue" + raiseValue);

			raiseEV = leftCallPr * rightCallPr * (winningPrBoth * (3 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrBoth) * raiseValue) +
			(1 - leftCallPr) * rightOnlyCallPr * (winningPrRightOnly * (2 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrRightOnly) * raiseValue) +
			leftCallPr * (1 - rightCallPr) * (winningPrLeftOnly * (2 * (myPot + raiseValue) - raiseValue - leftPot - rightPot) - (1 - winningPrLeftOnly) * raiseValue) +
			(1 - leftCallPr) * (1 - rightOnlyCallPr) * (myPot + leftPot + rightPot);
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
			
			while (myHand.community.size() - myEH.common > 0) {
				int card = myHand.community.get(myEH.common).toLibValue();
				myEH.addCard(card);
				leftEH.addCard(card);
				rightEH.addCard(card);
			}
			
			if (myHand.community.size() == 3) {
				hasFlop = true;
			} else {
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
			int potSize = 3 * stackSize - (leftStack + rightStack + myStack);
			int numPlayers = (hasLeftFold || hasRightFold) ? 2 : 3;
			
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
			
			System.out.println("toCall: " + toCall + ", toBet: " + toBet);
			
			if ((action.actor.compareToIgnoreCase(leftName) == 0) || (action.actor.compareToIgnoreCase(rightName) == 0)) {
				switch (action.type) {
				case BET:
					statistics.bet(action.actor, common, activeCount, p, potSize, action.amount, numPlayers);
					break;
				case CALL:
					statistics.call(action.actor, common, activeCount, p, potSize, toCall, numPlayers);
					break;
				case CHECK:
					statistics.check(action.actor, common, p, numPlayers);
					break;
				case FOLD:
					statistics.fold(action.actor, common, activeCount, p, potSize, toCall, numPlayers);
					break;
				case RAISE:
					statistics.raise(action.actor, common, activeCount, p, potSize, toCall, action.amount, numPlayers);
				}
			}
			
			if (hasFlop) {
				if (action.actor.compareToIgnoreCase(leftName) == 0) {
					if (hasRightFold) {
						updateEH(leftEH, action, leftName, p, common, activeCount, potSize, toCall, 2);
					} else {
						updateEH(leftEH, action, leftName, p, common, activeCount, potSize, toCall, 3);
					}
					if(action.type == Type.RAISE){
						if (hasRightFold) {
							updateEHRaise(leftEH, action, leftName, p, common, activeCount, 2);
						} else {
							updateEHRaise(leftEH, action, leftName, p, common, activeCount, 3);
						}
					}
				} else if (action.actor.compareToIgnoreCase(rightName) == 0) {
					if (hasLeftFold) {
						updateEH(rightEH, action, rightName, p, common, activeCount, potSize, toCall, 2);
					} else {
						updateEH(rightEH, action, rightName, p, common, activeCount, potSize, toCall, 3);
					}
					if(action.type == Type.RAISE){
						if (hasLeftFold) {
							updateEHRaise(rightEH, action, rightName, p, common, activeCount, 2);
						} else {
							updateEHRaise(rightEH, action, rightName, p, common, activeCount, 3);
						}
					}
				} else {
//					if (hasLeftFold) {
//						updateEH2(myEH, rightEH, action);
//					} else if (hasRightFold) {
//						updateEH2(myEH, leftEH, action);
//					} else {
//						updateEH3(myEH, leftEH, rightEH, action);
//					}
				}
			}
			
		}
	}
	
	@Override
	public String preflop_computation() {
		return flop_computation();
	}

	@Override
	public String flop_computation() {		
		System.out.println("***");
		System.out.println("toCall: " + toCall + ", toBet: " + toBet + ", toRaise: " + toRaise);
//		System.out.println("Folding Prob");
//		for(int i=1;i<2;i++){
//			System.out.println(statistics.namelist.get(i));
//			for(int j=0;j<4;j++){
//				System.out.println(j==0?"PREFLOP":j==1?"FLOP":j==2?"TURN":"RIVER");
//				for(int k=0;k<3;k++){
//					System.out.println(k==0?"DEALER":k==1?"SB":"BB");
//					for(int l=0;l<3;l++){
//						for(int m=0;m<3;m++){
//							System.out.print((double)statistics.fold[i][j][k][l][m]/
//									statistics.chanceFold[i][j][k][l][m]+"-");
//						}
//						System.out.println("");
//					}
//				}
//			}
//		}
		
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
		
		final double SMALL_RAISE = 0.2;
		final double MEDIUM_RAISE = 0.5;
		final double BIG_RAISE = 1;
		
		
		double[] decisionEV = new double[5];
		double[] raiseValue = new double[5];
		
		// call
		decisionEV[0] = canCall ? (winningPr * (potSize + toCall) - toCall) : -stackSize;
		
		// check
		decisionEV[1] = !canCall ? (winningPr * potSize) : -stackSize;
		
		// raise small
		double[] temp = EVForRaiseWithReRaise(SMALL_RAISE);
		decisionEV[2] = temp[0];
		raiseValue[2] = temp[1];

		// raise medium
		temp = EVForRaiseWithReRaise(MEDIUM_RAISE);
		decisionEV[3] = temp[0];
		raiseValue[3] = temp[1];
		
		// raise big
		temp = EVForRaiseWithReRaise(BIG_RAISE);
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
			return (toRaise != -1 ? "RAISE:" : "BET:") + (int) raiseValue[2];
		case 3:
			return (toRaise != -1 ? "RAISE:" : "BET:") + (int) raiseValue[3];
		case 4:
			return (toRaise != -1 ? "RAISE:" : "BET:") + (int) raiseValue[4];
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
