package search_generics;

public class TreeNode implements Comparable<TreeNode>{
	public State state;
	public TreeNode parent;
	public final int operator; // A zero-valued operator denotes no operation, needed for root.
	public int depth; // Root has depth 0, i.e zero based counter.
	public int pathCost;
	public int heuristic;
	
	public TreeNode(State state, TreeNode parent, int operator, int depth, int pathCost, int heuristic){
		this.state = state;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		this.pathCost = pathCost;
		this.heuristic = heuristic;
	}

	@Override
	public int compareTo(TreeNode arg0) {
		return Integer.compare(this.pathCost + this.heuristic, arg0.pathCost + arg0.heuristic);
	}	
	
	@Override
	public String toString(){
		return state.toString();
	}
}
