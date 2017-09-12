package search_generics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;


public class SearchProblem {
	private final int[] operators; // Private in order to keep its elements unchanged.
	private final State initialState;
	private final Predicate<State> goalTest;
	private final BiFunction<State, Integer, Integer> pathCost;
	private final Function<State, Integer> evaluationFunction;
	private final Function<State, ArrayList<State>> stateEnumerator;
	public int expansionCounter = 0;
	
	public SearchProblem(int[] operators, State initialState, Predicate<State> goalTest,
			BiFunction<State, Integer, Integer> pathCost, Function<State, ArrayList<State>> se,
			Function<State, Integer> eval){
		this.operators = operators;
		this.initialState = initialState;
		this.goalTest = goalTest;
		this.pathCost = pathCost;
		this.stateEnumerator = se;
		this.evaluationFunction = eval;
	}
	




	public TreeNode search(QueueingHandler qh, boolean memoize){
		HashSet<State> vis = null;
		if(memoize){
			vis = new HashSet<>();
			vis.add(initialState);
		}
		qh.enqueue(new TreeNode(initialState, null, 0, 0, 0, evaluationFunction.apply(initialState)));
		expansionCounter = 0;
		while(!qh.isEmpty()){
			TreeNode curr = qh.dequeue();
			if(goalTest.test(curr.state)){
				return curr;
			}
			++expansionCounter;
			ArrayList<State> children = stateEnumerator.apply(curr.state);
			
			for(State s : children){ 
				if(memoize){
					if(!vis.contains(s)){
						TreeNode c = new TreeNode(s, curr, s.operator, curr.depth + 1, curr.pathCost + 
								pathCost.apply(curr.state, s.operator),
								evaluationFunction.apply(s));
						qh.enqueue(c);
						vis.add(s);
					}
				}else{ // TODO: Refactor this.
					TreeNode c = new TreeNode(s, curr, s.operator, curr.depth + 1, curr.pathCost + 
							pathCost.apply(curr.state, s.operator),
							evaluationFunction.apply(s));
					qh.enqueue(c);
				}
			}
		}
		return null;
	}
		
}
