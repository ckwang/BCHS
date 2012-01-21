package util;

import java.util.ArrayList;
import java.util.List;

public class GenericBot {
	// game information
	public int numHands;
	public int stackSize;
	public int bb;
	public int sb;
	public String leftName;
	public String rightName;

	// hand information
	public int handId;
	public int position;
	public Card holeCard1;
	public Card holeCard2;
	public int myBank;
	public int leftBank;
	public int rightBank;

	// current information
	public int potSize;
	public int myStack;
	public int leftStack;
	public int rightStack;
	public double timeBank;
	public List<Card> communityCards;
	public List<Action> lastActions;
	public List<Action> legalActions;

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
			handId = Integer.parseInt(tokens[1]);
			position = Integer.parseInt(tokens[2]);
			holeCard1 = parseCard(tokens[3]);
			holeCard2 = parseCard(tokens[4]);
			myBank = Integer.parseInt(tokens[5]);
			leftBank = Integer.parseInt(tokens[6]);
			rightBank = Integer.parseInt(tokens[7]);
			timeBank = Double.parseDouble(tokens[8]);
		} else if (tokens[0].compareToIgnoreCase("GETACTION") == 0) {
			potSize = Integer.parseInt(tokens[1]);
			communityCards = new ArrayList<Card>();
			lastActions = new ArrayList<Action>();
			legalActions = new ArrayList<Action>();

			int numBoardCards = Integer.parseInt(tokens[2]);
			if (numBoardCards > 0) {
				String[] boardCardsTokens = tokens[3].split(",");
				for (int i = 0; i < numBoardCards; i++) {
					communityCards.add(parseCard(boardCardsTokens[i]));
				}
			}

			int numLastActions = Integer.parseInt(tokens[3 + (numBoardCards > 0 ? 1 : 0)]);
			if (numLastActions > 0) {
				String[] lastActionsTokens = tokens[4 + (numBoardCards > 0 ? 1 : 0)].split(",");
				for (int i = 0; i < numLastActions; i++) {
					lastActions.add(parsePerformedAction(lastActionsTokens[i]));
				}
			}

			int numLegalActions = Integer.parseInt(tokens[4 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)]);
			if (numLastActions > 0) {
				String[] legalActionsTokens = tokens[5 + (numBoardCards > 0 ? 1 : 0) + (numLastActions > 0 ? 1 : 0)].split(",");
				for (int i = 0; i < numLegalActions; i++) {
					legalActions.add(parsePossibleAction(legalActionsTokens[i]));
				}
			}
			timeBank = Double.parseDouble(tokens[5 + (numBoardCards > 0 ? 1 : 0) +
					(numLastActions > 0 ? 1 : 0) +
					(numLegalActions > 0 ? 1 : 0)]);

			System.out.println("*" + lastActions.toString());
			System.out.println("*" + legalActions.toString());
			System.out.println("*" + holeCard1 + ", " + holeCard2);
			response = decide();
		} else if (tokens[0].compareToIgnoreCase("HANDOVER") == 0) {

		} else {
			System.out.println("Packet type parse error.");
			return null;
		}

		return response;
	}

	private Card parseCard(String input) {
		int rank, suit;
		switch (input.charAt(0)) {
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				rank = input.charAt(0) - '0';
				break;
			case 'T':
				rank = 10;
				break;
			case 'J':
				rank = 11;
				break;
			case 'Q':
				rank = 12;
				break;
			case 'K':
				rank = 13;
				break;
			case 'A':
				rank = 14;
				break;
			default:
				System.out.println("Hole card rank parse error.");
				return null;
		}
		switch (input.charAt(1)) {
			case 's':
				suit = 0;
				break;
			case 'h':
				suit = 1;
				break;
			case 'c':
				suit = 2;
				break;
			case 'd':
				suit = 3;
				break;
			default:
				System.out.println("Hole card suit parse error.");
				return null;
		}
		return new Card(suit, rank);
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
		return "CHECK";
	}
}
