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
	public int position;
	public int myBank;
	public int leftBank;
	public int rightBank;
	public Hand myHand;

	// current information
	public int potSize;
	public int myStack;
	public int leftStack;
	public int rightStack;
	public double timeBank;
	public Action leftAction;
	public Action rightAction;
	public List<Action> legalActions = new ArrayList<Action>();
	public boolean toCheck;
	public int toBet;
	public int toCall;
	public int toCaise;

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
			System.out.println("Hole Cards: " + myHand.hole[0] + ", " + myHand.hole[1]);
		} else if (tokens[0].compareToIgnoreCase("GETACTION") == 0) {
			potSize = Integer.parseInt(tokens[1]);
			legalActions.clear();
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
				Action a;
				leftAction = null;
				rightAction = null;
				for (int i = 0; i < numLastActions; i++) {
					a = parsePerformedAction(lastActionsTokens[i]);
					assert a != null;
					if (a.actor == null)
						continue;
					if (leftName != null && a.actor.compareToIgnoreCase(leftName) == 0) {
						leftAction = a;
						if (a.type == Action.Type.FOLD)
							leftName = null;
					}
					else if (rightName != null && a.actor.compareToIgnoreCase(rightName) == 0) {
						rightAction = a;
						if (a.type == Action.Type.FOLD)
							rightName = null;
					}
				}
			}

			int numLegalActions = Integer.parseInt(tokens[4 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)]);
			if (numLastActions > 0) {
				String[] legalActionsTokens = tokens[5 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)].split(",");
				for (int i = 0; i < numLegalActions; i++) {
					legalActions.add(parsePossibleAction(legalActionsTokens[i]));
					parsePossibleAction(legalActionsTokens[i]);
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
			// do nothing
		} else {
			System.out.println("Packet type parse error.");
			return null;
		}
		return response;
	}

	private Action parsePerformedAction(String input) {
		String[] tokens = input.split(":");
		if (tokens[0].compareToIgnoreCase("BET") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.BET, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("CALL") == 0) {
			String actor = tokens[1];
			return new Action(Action.Type.CALL, actor);
		} else if (tokens[0].compareToIgnoreCase("CHECK") == 0) {
			String actor = tokens[1];
			return new Action(Action.Type.CHECK, actor);
		} else if (tokens[0].compareToIgnoreCase("FOLD") == 0) {
			String actor = tokens[1];
			return new Action(Action.Type.FOLD, actor);
		} else if (tokens[0].compareToIgnoreCase("RAISE") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.RAISE, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("DEAL") == 0) {
			return new Action(Action.Type.DEAL);
		} else if (tokens[0].compareToIgnoreCase("POST") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.POST, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("REFUND") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.REFUND, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("SHOW") == 0) {
			String actor = tokens[1];
			return new Action(Action.Type.SHOW, actor);
		} else if (tokens[0].compareToIgnoreCase("TIE") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.TIE, actor, amount);
		} else if (tokens[0].compareToIgnoreCase("WIN") == 0) {
			String actor = tokens[1];
			int amount = Integer.parseInt(tokens[2]);
			return new Action(Action.Type.WIN, actor, amount);
		} else {
			System.out.println("Action parse error.");
			return null;
		}
	}

	private Action parsePossibleAction(String input) {
		String[] tokens = input.split(":");
		if (tokens[0].compareToIgnoreCase("BET") == 0) {
			int amount = Integer.parseInt(tokens[1]);
			toBet = Integer.parseInt(tokens[1]);
			return new Action(Action.Type.BET, amount);
		} else if (tokens[0].compareToIgnoreCase("CALL") == 0) {
			return new Action(Action.Type.CALL);
		} else if (tokens[0].compareToIgnoreCase("CHECK") == 0) {
			return new Action(Action.Type.CHECK);
		} else if (tokens[0].compareToIgnoreCase("FOLD") == 0) {
			return new Action(Action.Type.FOLD);
		} else if (tokens[0].compareToIgnoreCase("RAISE") == 0) {
			int amount = Integer.parseInt(tokens[1]);
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

	protected double expected_value(double winProb) {
		Action call = null;
		for (Action a: legalActions) {
		}
		return winProb*stackSize;
	}

	public abstract String preflop_computation();
	public abstract String flop_computation();
	public abstract String turn_computation();
	public abstract String river_computation();
}
