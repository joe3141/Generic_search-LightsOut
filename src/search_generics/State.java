package search_generics;

public abstract class State {
	public int operator;
	
	public State(int operator){
		this.operator = operator;
	}
	
	@Override
	public String toString(){
		return "" + operator;
	}
}
