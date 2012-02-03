package alwayscall;

import util.*;

class AlwaysCallBot extends GenericBot {
	
	@Override
	public String preflop_computation() {
		return "CALL";
	}

	@Override
	public String flop_computation() {
		return "CALL";
	}

	@Override
	public String turn_computation() {
		return "CALL";
	}

	@Override
	public String river_computation() {
		return "CALL";
	}

	@Override
	public void handInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleShow(Action a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reactToAction(Action a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processStatistics() {
		// TODO Auto-generated method stub
		
	}
}
