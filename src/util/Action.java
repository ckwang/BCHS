package util;


public class Action {
	public enum Type {BET, CALL, CHECK, FOLD, RAISE,
		DEAL, POST, REFUND, SHOW, TIE, WIN};
	
	public final Type type;
	public final String actor;
	public final int amount;
	
	public Action(Type type, String actor, int amount) {
		this.type = type;
		this.actor = actor;
		this.amount = amount;
	}
	
	public Action(Type type, int amount) {
		this.type = type;
		this.actor = null;
		this.amount = amount;
	}
	
	public Action(Type type, String actor) {
		this.type = type;
		this.actor = actor;
		this.amount = -1;
	}
	
	public Action(Type type) {
		this.type = type;
		this.actor = null;
		this.amount = -1;
	}
	
	@Override
	public String toString() {
//		return type + (actor != null ? (":" + actor) : "") + (amount != -1 ? (":" + amount) : "");
		return type + " " + (amount != -1 ? amount : "");
	}
}
