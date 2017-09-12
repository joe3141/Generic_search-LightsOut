# Generic_search-LightsOut
Implementation of the generic search algorithm and utilizing it for solving the lights out game.

The project consists of two main parts, a generic search algorithm package that is capable of executing multiple search strategies, and an application of this package to solve the lightsout game.

Search strategies supported are:
* BFS
* DFS
* Iterative deepening
* Greedy search
* A*

Moreover, the generic search algorithm package can be configured to memoize the states by using a hashset if the user wishes to, however it runs the risk of eating out the available memory if this hashset gets too large. In the implementation of the solver, this internal memoization was not used and the states were memoized externally from the "LightsOut" class in a way that avoids saving state. 

The game is implemented in a text based version and its state is encapsulated into a 32-bit integer, making it at most support a 5x5 boaard for this current implementation.
