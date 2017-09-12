package search_generics;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class QueueingHandler {
	private Queue<TreeNode> queue; // This could be a priority queue or a deque.
	// 0 => LIFO, 1 => FIFO, 2 => prioritize 
	private int strategy;
	public int maxDepth; // -1 denotes infinite depth. max depth supported = 0 -> (2^31 - 1)
	
	public QueueingHandler(int strategy, int maxDepth){
		
		this.strategy = strategy;
		if(strategy == 2)
			queue = new PriorityQueue<TreeNode>();
		else
			queue = new LinkedList<TreeNode>();
		
		this.maxDepth = maxDepth;
		
	}
	
	public void enqueue(TreeNode in){
		if(maxDepth == -1 || in.depth <= maxDepth){ // Only enqueue nodes within cutoff distance.
			if(strategy == 0 || strategy == 1){
			 LinkedList<TreeNode> collection = (LinkedList<TreeNode>) this.queue;
			 
			 if(strategy == 0)
				 collection.addFirst(in);
			 else
				 collection.addLast(in);
			}else
				queue.add(in); // overrided PriorityQueue method.
		}
	}
	
	public TreeNode dequeue(){
		return queue.poll();
	}
	
	public boolean isEmpty(){
		return queue.isEmpty();
	}
}
