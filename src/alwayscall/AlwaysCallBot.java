package alwayscall;

import util.*;

public class AlwaysCallBot extends GenericBot {

	@Override
	public String decide() {
		for (Action a: legalActions) {
			switch (a.type) {
				case CHECK:
					return "CHECK";
				case CALL:
					return "CALL";
				default:
			}
		}
		return "FOLD";
	}

}
