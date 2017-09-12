package lights_out;

import search_generics.State;

public class BoardState extends State{
	public int board; // Will use 25 bits of the integer to represent the board state, from left to right.
	public int flippedBulbs; // keeps track of the already flipped bulbs.
	
	public BoardState(int board, int operator, int fb){
		super(operator);
		this.board = board;
		flippedBulbs = fb;
	}
	
	@Override
	public int hashCode(){
		return board;
	}
	
	@Override
	public String toString(){
		return super.toString() + " " +  board;
	}
	
	public String visualize(int boardSize){	// . => closed bulb, * => open bulb
		StringBuilder sb = new StringBuilder();
		
		for(int i = (boardSize * boardSize) - 1; i>-1; --i){
			if((i+1) % (boardSize) == 0 && (i+1) != (boardSize * boardSize))
				sb.append('\n');
			if((board & (1 << i)) > 0)
				sb.append('*');
			else
				sb.append('.');
		}
		
		return sb.toString();
	}
}
