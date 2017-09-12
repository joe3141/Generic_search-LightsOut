package lights_out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.sound.midi.Sequence;

import search_generics.QueueingHandler;
import search_generics.SearchProblem;
import search_generics.State;
import search_generics.Strategy;
import search_generics.TreeNode;

public class Game {
	
	static int boardDim = 5, boardSize = boardDim * boardDim;
	
	// Index = 0 means right most bit in board.
	private static int toggleBulb(int board, int index){
		int res = board;
		res ^= (index + 1) > -1 && (index + 1) < boardSize ? (1 << (index + 1)) : 0;
		res ^= (index - 1) > -1 && (index - 1) < boardSize ? (1 << (index - 1)) : 0;
		res ^= (index + boardDim) > -1 && (index + boardDim) < boardSize ? (1 << (index + boardDim)) : 0;
		res ^= (index - boardDim) > -1 && (index - boardDim) < boardSize ? (1 << (index - boardDim)) : 0;
		res ^= 1 << index;
		return res;
	}
	
	private static int countSetBits(int board){
		board = board - ((board >>> 1) & 0x55555555);
		board = (board & 0x33333333) + ((board >>> 2) & 0x33333333);
		board = (board + (board >>> 4)) & 0x0f0f0f0f; 
		board = board + (board >>> 8); 
		board = board + (board >>> 16); 
		return board & 0x3f;
	}
	
	private static SearchProblem createSearchProblem(Strategy strat){
		int[] actions = new int[boardSize];
		for(int i = 1; i <boardSize + 1; ++i) // We have boardSize actions, toggle each bulb on the board, 0 is reserved.
			actions[i-1] = i;
		
		State initialState = new BoardState(GenBoard(), 0, 0);
		return new SearchProblem(actions, initialState, new Predicate<State>(){
			
			/*
			 * Goal Test Function
			 */
			@Override
			public boolean test(State s){
				BoardState bs = (BoardState) s;
				return bs.board  == 0;
			}
		}
		, new BiFunction<State, Integer, Integer>(){
			
			/*
			 * Path Cost Function
			 */
			@Override
			public Integer apply(State arg0, Integer arg1) { // There is no pathCost.
				return 1;
			}
			
		}, new Function<State, ArrayList<State>>(){
			
			/*
			 * State Enumerator 
			 */
			@Override
			public ArrayList<State> apply(State arg0) {
				BoardState bs = (BoardState) arg0;
				ArrayList<State> res = new ArrayList<State>();
				for(int i = 0; i < boardSize; ++i){
					if((bs.flippedBulbs & (1 << i)) == 0) // If we didn't toggle this bulb before.
						res.add(new BoardState(toggleBulb(bs.board, i), i+1, bs.flippedBulbs |= 1 << i));
				}
				return res;
			}
			
		}, new Function<State, Integer>() {
			
			/*
			 * Heuristic function
			 */
			
			@Override
			public Integer apply(State t) {
				BoardState bs = (BoardState) t;
				int openbulbs = countSetBits(bs.board);
				switch(strat){
				case AS1:
				case GR1:
					return openbulbs/boardDim;
				case AS2:
				case GR2:
					// Only works when boardDim > 2, otherwise will work as the first one.
					// Rewards for the number of closed bulbs that are not on the edges.
					if(boardDim > 2)
						return (openbulbs/boardDim) - ((openbulbs - openEdges(bs.board))/boardDim);	
					return openbulbs/boardDim;
				case AS3:
				case GR3:
					//2 plus rewards for patterns that are optimal for winning the game.
					if(boardDim > 2)
						return (openbulbs/boardDim) - ((openbulbs - openEdges(bs.board))/boardDim)
								- calcPatterns(bs.board);
					return (openbulbs/boardDim) - calcPatterns(bs.board);
				default:
					return 0;
				}
			}
		});
		

	}
	
	
	/*
	 *  Board operator numbering
	 * 	
	 *  8	7	6		
	 *  5	4	3
	 *  2	1	0		
	 *  
	 *  +1 if reading from the state class.
	 */
	
	
	/*	15	14	13	12
	 *	11	10	9	8 	
	 *	7	6	5	4 
	 * 	3	2	1	0
	 */
	
	private static int openEdges(int board){
		int res = 0;
		
		// Lower row
		for(int i = 0; i < boardDim; ++i)
			if((board & (1 << i)) > 0)
				++res;
		
		// side row
		for(int i = boardDim + 1, j = boardDim + boardDim -1; i < boardSize; i+=boardDim, j+=boardDim){
			if((board & (1 << i)) > 0)
				++res;
			if((board & (1 << j)) > 0)
				++res;
		}
		
		// Upper row
		for(int i = boardDim + boardDim + 1; i < boardSize - 1; ++i)
			if((board & (1 << i)) > 0)
				++res;
		
		return res;
	}
	
	private static int calcPatterns(int board){
		int res = 0;
		// Bottom right corner
		if(((board & 1) > 0) && ((board & (1 << boardDim)) > 0) && 
				(board & 2) > 0)
			res += 3;
		
		//Bottom left corner
		if(((board & 1<<(boardDim-1)) > 0) && ((board & (1 << (boardDim+boardDim-1))) > 0) && 
				(board & 2) > 0)
			res += 3;
		
		//Top right corner 
		if(((board & (1 << (2*boardDim))) > 0) && ((board & (1 << boardDim)) > 0) && 
				(board & (1 << (1 + boardDim + boardDim))) > 0)
			res += 3;
		
		if(((board & (1 << ((2*boardDim) + boardDim - 1))) > 0) && ((board & (1 << (boardDim) + boardDim 
				- 1)) > 0) && (board & (1 << (1 + boardDim + boardDim))) > 0)
			res += 3;
		
		return res;
	}
	
	public static int GenBoard(){
		Random rand = new Random();
		int r;
		int board = 0;
		
		for(int i = 0; i < boardSize; ++i){
			r = rand.nextInt(2); // Returns either 0 or 1
			if(r == 1)
				board |= 1 << i;
		}
		
		return board;
	}
	
	public static Object[] Search(SearchProblem board, Strategy strat, boolean visualize){
		QueueingHandler qh;
		TreeNode goal;
		
		switch(strat){
		case BF:
			qh = new QueueingHandler(1, -1);
			goal = board.search(qh, false);
			break;
		case DF:
			qh = new QueueingHandler(0, -1);
			goal = board.search(qh, false);
			break;
		case ID:
			qh = new QueueingHandler(0, 0);
			while(true){
				TreeNode temp = board.search(qh, false);
				if(temp != null){
					goal = temp;
					break;
				}
				++qh.maxDepth;
			}
			break;
		default:
			qh = new QueueingHandler(2, -1);
			goal = board.search(qh, false);
			break;
			
		}
//		System.out.println("Here!!");
		Object[] res = new Object[3];
		if(goal != null){
			int solutionCost = goal.pathCost; Stack<TreeNode> sequence = new Stack<TreeNode>();
			TreeNode curr = goal;
			while(curr!=null){
				sequence.push(curr);
				curr = curr.parent;
			}
			res[0] = sequence;
			res[1] = solutionCost;
			
			if(visualize){
				@SuppressWarnings("unchecked")
				Stack<TreeNode> temp = (Stack<TreeNode>) sequence.clone();
				while(!(temp.isEmpty())){
					BoardState s = (BoardState) temp.pop().state;
					System.out.println(s.visualize(boardDim) + "\n\n");
					
				}
			}
			
		}
		
		res[2] = board.expansionCounter;
		
		
		return res;
	}
	

	public static void main(String[] args) {
		SearchProblem board = createSearchProblem(Strategy.AS2);
		System.out.println(Arrays.toString(Search(board, Strategy.AS2, true)));
	}

}
