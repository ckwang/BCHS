package alwayscall;

import util.*;

public class AlwaysCallBot extends GenericBot {
	
	public String preflop_computation() {
		return "CHECK";
	}

	public String flop_computation() {
		return "CHECK";
	}

	public String turn_computation() {
		return "CHECK";
	}

	public String river_computation() {
		return "CHECK";
	}
}
