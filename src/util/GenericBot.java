package util;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericBot {
	// game information
	public int numHands;
	public int stackSize;
	public int bb;
	public int sb;
	public String leftName = null;
	public String rightName = null;

	// hand information
	public int handId;
	public int position;	// 0 = dealer, 1 = sb, 2 = bb
	public int myBank = 0;
	public int leftBank;
	public int rightBank;
	public Hand myHand;

	// temp information
	public int potSize;
	public int myStack;
	public int leftStack;
	public int rightStack;
	public double timeBank;
	public Action leftAction;
	public Action rightAction;
	public boolean hasLeftFold = false;
	public boolean hasRightFold = false;
	public List<Action> legalActions = new ArrayList<Action>();
	public boolean canCheck = false;
	public boolean canCall = false;
	public int toCall = -1;	// TODO: not implemented yet
	public int toBet = -1;
	public int toRaise = -1;

	public String parse(String input) {
		String response = null;
		// Parsing the input files
		String[] tokens = input.split(" ");
		if (tokens[0].compareToIgnoreCase("NEWGAME") == 0) {
			leftName = tokens[2];
			rightName = tokens[3];
			numHands = Integer.parseInt(tokens[4]);
			stackSize = Integer.parseInt(tokens[5]);
			bb = Integer.parseInt(tokens[6]);
			sb = Integer.parseInt(tokens[7]);
			timeBank = Double.parseDouble(tokens[8]);
			myStack = leftStack = rightStack = stackSize;
		} else if (tokens[0].compareToIgnoreCase("NEWHAND") == 0) {
			System.out.println("\nHand " + tokens[1]);
			handId = Integer.parseInt(tokens[1]);
			position = Integer.parseInt(tokens[2]);
			myHand = new Hand(new Card(tokens[3]), new Card(tokens[4]));
			myBank = Integer.parseInt(tokens[5]);
			leftBank = Integer.parseInt(tokens[6]);
			rightBank = Integer.parseInt(tokens[7]);
			timeBank = Double.parseDouble(tokens[8]);
			
			myStack = stackSize;
			leftStack = stackSize;
			rightStack = stackSize;
			
			switch (position) {
			case 0:
				leftStack -= sb;
				rightStack -= bb;
				break;
			case 1:
				myStack -= sb;
				leftStack -= bb;
				break;
			case 2:
				myStack -= bb;
				rightStack -= sb;
				break;
			}
			
			System.out.println("Hole Cards: " + myHand.hole[0] + ", " + myHand.hole[1]);
		} else if (tokens[0].compareToIgnoreCase("GETACTION") == 0) {
			reset_variables();
			potSize = Integer.parseInt(tokens[1]);
			int numBoardCards = Integer.parseInt(tokens[2]);
			if (numBoardCards > 0) {
				String[] boardCardsTokens = tokens[3].split(",");
				for (int i = myHand.community.size(); i < numBoardCards; i++) {
					myHand.addCards(new Card(boardCardsTokens[i]));
				}
			}
			int numLastActions = Integer.parseInt(tokens[3 + (numBoardCards > 0 ? 1 : 0)]);
			if (numLastActions > 0) {
				String[] lastActionsTokens = tokens[4 + (numBoardCards > 0 ? 1 : 0)].split(",");
				for (int i = 0; i < numLastActions; i++) {
					parsePerformedAction(lastActionsTokens[i]);
				}
			}

			int numLegalActions = Integer.parseInt(tokens[4 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)]);
			if (numLastActions > 0) {
				String[] legalActionsTokens = tokens[5 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)].split(",");
				Action a;
				for (int i = 0; i < numLegalActions; i++) {
					a = parsePossibleAction(legalActionsTokens[i]);
					legalActions.add(a);
				}
			}
			timeBank = Double.parseDouble(tokens[5 + (numBoardCards > 0 ? 1 : 0) +
					(numLastActions > 0 ? 1 : 0) +
					(numLegalActions > 0 ? 1 : 0)]);

			if (leftAction != null)
				System.out.println("Left action: " + leftAction);
			if (rightAction != null)
				System.out.println("Right action: " + rightAction);
			System.out.println("Legal action:" + legalActions.toString());
			response = decide(); //compute next action
		} else if (tokens[0].compareToIgnoreCase("HANDOVER") == 0) {
			myBank -= (stackSize - myStack);
			// do nothing
			System.out.println("Invested " + (stackSize - myStack));
			System.out.println("Bank: " + myBank);
		} else {
			System.out.println("Packet type parse error.");
			return null;
		}
		if (response != null)
			System.out.println("My action: " + response);
		return response;
	}

	private Action parsePerformedAction(String input) {
		String[] tokens = input.split(":");
		Action result = null;
		if (tokens[0].compareToIgnoreCase("BET") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			potSize += amount;
			toCall = amount;
			result = new Action(Action.Type.BET, actor, amount);
			if (actor.compareToIgnoreCase(leftName) == 0)
				leftStack -= amount;
			else
				rightStack -= amount;
		} else if (tokens[0].compareToIgnoreCase("CALL") == 0) {
			String actor = tokens[1];
			result = new Action(Action.Type.CALL, actor);
			if (actor.compareToIgnoreCase(leftName) == 0) {
				potSize += leftStack - myStack;
				leftStack = myStack;
			}
			else {
				if (!hasLeftFold) {
					potSize += rightStack - leftStack;
					rightStack = leftStack;
				}
				else {
					potSize += rightStack - myStack;
					rightStack = myStack;
				}
			}
		} else if (tokens[0].compareToIgnoreCase("CHECK") == 0) {
			String actor = tokens[1];
			result = new Action(Action.Type.CHECK, actor);
		} else if (tokens[0].compareToIgnoreCase("FOLD") == 0) {
			String actor = tokens[1];
			if (actor.compareToIgnoreCase(leftName) == 0)
				hasLeftFold = true;
			else if (actor.compareToIgnoreCase(rightName) == 0)
				hasRightFold = true;
			result = new Action(Action.Type.FOLD, actor);
		} else if (tokens[0].compareToIgnoreCase("RAISE") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			potSize += amount;
			toCall = amount;
			result = new Action(Action.Type.RAISE, actor, amount);
			if (actor.compareToIgnoreCase(leftName) == 0)
				leftStack -= amount;
			else
				rightStack -= amount;
		} else if (tokens[0].compareToIgnoreCase("DEAL") == 0) {
			result = new Action(Action.Type.DEAL);
		} else if (tokens[0].compareToIgnoreCase("POST") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			potSize += amount;
			toCall = amount;
			result = new Action(Action.Type.POST, actor, amount);
			if (actor.compareToIgnoreCase(leftName) == 0)
				leftStack -= amount;
			else
				rightStack -= amount;
		} else if (tokens[0].compareToIgnoreCase("REFUND") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			result = new Action(Action.Type.REFUND, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("SHOW") == 0) {
			String actor = tokens[1];
			String[] hand = tokens[2].split(" ");
			Card c1 = new Card(hand[0]);
			Card c2 = new Card(hand[1]);
			int pairValue = Card.pairToValue(c1.toLibValue(), c2.toLibValue());
			result = new Action(Action.Type.SHOW, actor, pairValue);
			handleShow(result);
		} else if (tokens[0].compareToIgnoreCase("TIE") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			result = new Action(Action.Type.TIE, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("WIN") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			result = new Action(Action.Type.WIN, actor, amount);
			myBank += amount;
			System.out.println("Win " + amount);
		} else {
			System.out.println("Action parse error.");
		}
		if (result.actor != null) {
			if (result.actor.compareToIgnoreCase(leftName) == 0)
				leftAction = result;
			else if (result.actor.compareToIgnoreCase(rightName) == 0)
				rightAction = result;
		}
		return result;
	}

	private Action parsePossibleAction(String input) {
		String[] tokens = input.split(":");
		if (tokens[0].compareToIgnoreCase("BET") == 0) {
			int amount = Integer.parseInt(tokens[1]);
			toBet = amount;
			return new Action(Action.Type.BET, amount);
		} else if (tokens[0].compareToIgnoreCase("CALL") == 0) {
			canCall = true;
			return new Action(Action.Type.CALL);
		} else if (tokens[0].compareToIgnoreCase("CHECK") == 0) {
			canCheck = true;
			return new Action(Action.Type.CHECK);
		} else if (tokens[0].compareToIgnoreCase("FOLD") == 0) {
			return new Action(Action.Type.FOLD);
		} else if (tokens[0].compareToIgnoreCase("RAISE") == 0) {
			int amount = Integer.parseInt(tokens[1]);
			toRaise = amount;
			return new Action(Action.Type.RAISE, amount);
		} else {
			System.out.println("Action parse error.");
			return null;
		}
	}

	protected String decide() {
		String decision = "CHECK";
		switch (myHand.community.size()) {
			case 0:
				decision = preflop_computation();
				break;
			case 3:
				decision = flop_computation();
				break;
			case 4:
				decision = turn_computation();
				break;
			case 5:
				decision = river_computation();
				break;
			default:
				System.out.print("Decide: invalid number of community cards");
		}
		return decision;
	}

	protected void reset_variables() {
		canCheck = false;
		canCall = false;
		toCall = -1;
		toBet = -1;
		toRaise = -1;
		leftAction = null;
		rightAction = null;
		legalActions.clear();
	}

	protected int call_value() {
		assert canCall;
		int result = 0;
		if (leftAction != null && leftAction.amount > result)
			result = leftAction.amount;
		if (rightAction != null && rightAction.amount > result)
			result = rightAction.amount;
		return result;
	}

	protected double expected_value(int minbet, double winProb) {
		return winProb*stackSize - (1-winProb)*minbet;
	}

	public abstract String preflop_computation();
	public abstract String flop_computation();
	public abstract String turn_computation();
	public abstract String river_computation();

	public abstract void handleShow(Action a);
}
