package simplebot;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.lang.Math;
import util.*;
import lib.FiveEval;

class SimpleBot extends GenericBot {
	
	public Map<Integer, List<Action>> lhistory = new HashMap<Integer, List<Action>>();
	public Map<Integer, List<Action>> rhistory = new HashMap<Integer, List<Action>>();
	public List<Action> leftAction;
	public List<Action> rightAction;
	public int lpreflopfold = 0;
	public int rpreflopfold = 0;
	public double maxpotential = 0;
	
	@Override
	public String preflop_computation() {
		Card h0 = myHand.hole[0];
		Card h1 = myHand.hole[1];
		leftAction = new ArrayList<Action>();
		rightAction = new ArrayList<Action>();
		double winProb;
		if (hasLeftFold || hasRightFold) {
			winProb = PokerTable.preflopWinningProb2(h0, h1);
			if (winProb > 0.7) {
				return justContinue();
			}
			else if (winProb > 0.65) {
				if (toRaise != -1) {
					myStack -= toRaise;
					return "RAISE:" + toRaise;
				}
				return justContinue();
			}
			else if (winProb > 0.5) {
				if (canCheck)
					return "CHECK";
				else if (canCall) {
					double odds = winProb*potSize - (1-winProb)*toCall;
					if (odds >= 0) {
						myStack -= toCall;
						return "CALL";
					}
					else {
						int rand = (int)System.nanoTime()%2;
						if (rand == 1) {
							myStack -= toCall;
							return "CALL";
						}
						else {
							assert rand == 0;
							return "FOLD";
						}
					}
				}
				else if (toBet != -1) {
					myStack -= toBet;
					return "BET:" + toBet;
				}
			}
		} else {
			winProb = PokerTable.preflopWinningProb3(h0, h1);
			if (winProb > 0.7) justContinue();
			else if (winProb > 0.5) {
				if (canCall) {
					double odds = winProb*potSize - (1-winProb)*toCall;
					if (odds > -5 && toCall < myStack * 0.6) {
						myStack -= toCall;
						return "CALL";
					}
					else
						return "FOLD";
				}
				else if (canCheck)
					return "CHECK";
				else if (toBet != -1) {
					myStack -= toBet;
					return "BET:" + toBet;
				}
			}
		}
		if (canCheck)
			return "CHECK";
		else if (canCall && toCall < 2*bb) {
			myStack -= toCall;
			return "CALL";
		}
		return "FOLD";
	}

	@Override
	public String flop_computation() {
		if (hasLeftFold) lpreflopfold++;
		if (hasRightFold) rpreflopfold++;
		double potential = projectOne();
		if (potential > maxpotential)
			maxpotential = potential;
		double loseprob = loseProb();
		if (loseprob < 0.15)
			return justContinue();
		else if (loseprob < 0.3) {
			int tobet = (int)Math.round(potSize * (1-loseprob));
			if (tobet > myStack) tobet = myStack;
			myStack -= tobet;
			if (toBet != -1)
				return "BET:" + tobet;
			else if (toRaise != -1)
				return "RAISE:" + tobet;
		}
		else if (loseprob < 0.4) {
			if (toBet != -1) {
				myStack -= toBet;
				return "BET:" + toBet;
			}
			if (canCall && ifCall(loseprob) > -10) {
				myStack -= toCall;
				return "CALL";
			}
			else if (canCheck)
				return "CHECK";
		}
		return "CHECK";
	}

	@Override
	public String turn_computation() {
		double potential = projectOne();
		if (potential > maxpotential)
			maxpotential = potential;
		double loseprob = loseProb();
		if (loseprob < 0.2) {
			int tobet = (int) Math.round(potSize * (1-loseprob));
			if (tobet > myStack) tobet = myStack;
			myStack -= tobet;
			if (toBet != -1)
				return "BET:" + tobet;
			else if (toRaise != -1)
				return "RAISE:" + tobet;
		}
		else if (loseprob < 0.45) {
			return justContinue();
		}
		else if (loseprob < 0.4) {
			if (toBet != -1) {
				myStack -= toBet;
				return "BET:" + toBet;
			}
			if (canCall && ifCall(loseprob) > -10) {
				myStack -= toCall;
				return "CALL";
			}
			else if (canCheck)
				return "CHECK";
		}
		return "CHECK";
	}

	@Override
	public String river_computation() {
		double potential = projectOne();
		if (potential > maxpotential)
			maxpotential = potential;
		double loseprob = loseProb();
		if (loseprob < 0.2) {
			int tobet = (int) Math.round(potSize * (1-loseprob));
			if (tobet > myStack) tobet = myStack;
			myStack -= tobet;
			if (toBet != -1)
				return "BET:" + tobet;
			else if (toRaise != -1)
				return "RAISE:" + tobet;
		}
		else if (loseprob < 0.45) {
			return justContinue();
		}
		else if (loseprob < 0.4) {
			if (toBet != -1) {
				myStack -= toBet;
				return "BET:" + toBet;
			}
			if (canCall && ifCall(loseprob) > -10) {
				myStack -= toCall;
				return "CALL";
			}
			else if (canCheck)
				return "CHECK";
		}
		return "CHECK";
	}

	public void handleShow(Action a) {
		if (a.actor.equals(leftName))
			lhistory.put(a.amount, leftAction);
		else
			rhistory.put(a.amount, rightAction);
	}
	
	public String justContinue() {
		if (canCall) return "CALL";
		if (canCheck) return "CHECK";
		if (toBet != -1) {
			int tobet = toBet;
			if (tobet > myStack) tobet = myStack;
			return "BET:" + tobet;
		}
		return "RAISE:" + toRaise;
	}

	public double ifCall(double loseprob) {
		double p = loseprob;
		if (!hasLeftFold && !hasRightFold) {
			loseprob *= 2;
		}
		return (1-loseprob)*potSize - loseprob*toCall;
	}

	public void processStatistics() {
		System.out.println("maxpotential: " + maxpotential);
	}

	public double projectOne() {
		int comsize = myHand.community.size();

		int h0 = myHand.hole[0].toLibValue();
		int h1 = myHand.hole[1].toLibValue();
		int[] c = new int[comsize];
		for (int i = 0; i < comsize; ++i)
			c[i] = myHand.community.get(i).toLibValue();

		boolean[] used = new boolean[52];
		used[h0] = true;
		used[h1] = true;
		for (int com: c) used[com] = true;

		int current, potential;
		double total = 50 - comsize;
		double expectation = 0;
		if (comsize == 3) {
			current = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2]);
			for (int i = 0; i < 52; ++i) {
				if (used[i]) continue;
				potential = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2], i);
				expectation += potential - current;
			}
		} else {
			current = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2], c[3]);
			for (int i = 0; i < 52; ++i) {
				if (used[i]) continue;
				potential = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2], c[3], i);
				expectation += potential - current;
			}
		}
		System.out.println("current: " + current);
		System.out.println("potential: " + (expectation/total));
		return current + expectation/total;
	}
			
	public double loseProb() {
		int comsize = myHand.community.size();

		int h0 = myHand.hole[0].toLibValue();
		int h1 = myHand.hole[1].toLibValue();
		int[] c = new int[comsize];
		for (int i = 0; i < comsize; ++i)
			c[i] = myHand.community.get(i).toLibValue();

		boolean[] used = new boolean[52];
		used[h0] = true;
		used[h1] = true;
		for (int com: c) used[com] = true;

		Set<Integer> better = new HashSet<Integer>();
		int myrank;
		if (comsize == 3) {
			myrank = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2]);
			for (int i = 1; i < 52; ++i) {
				if (used[i]) continue;
				for (int j = 0; j < i; ++j) {
					if (used[j]) continue;
					if (FiveEval.getBestRankOf(i, j, c[0], c[1], c[2]) > myrank)
						better.add(j*52 + i);
				}
			}
		}
		else if (comsize == 4) {
			myrank = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2], c[3]);
			for (int i = 1; i < 52; ++i) {
				if (used[i]) continue;
				for (int j = 0; j < i; ++j) {
					if (used[j]) continue;
					if (FiveEval.getBestRankOf(i, j, c[0], c[1], c[2], c[3]) > myrank)
						better.add(j*52 + i);
				}
			}
		}
		else {
			myrank = FiveEval.getBestRankOf(h0, h1, c[0], c[1], c[2], c[3], c[4]);
			for (int i = 1; i < 52; ++i) {
				if (used[i]) continue;
				for (int j = 0; j < i; ++j) {
					if (used[j]) continue;
					if (FiveEval.getBestRankOf(i, j, c[0], c[1], c[2], c[3], c[4]) > myrank)
						better.add(j*52 + i);
				}
			}
		}

		double totalpairs = (50 - comsize) * (49 - comsize);
		return (double)better.size()/totalpairs;
	}

	public void reactToAction(Action a) {}
	public void handInitialize() {}

}
