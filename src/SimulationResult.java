public class SimulationResult {
	
	int node_count;
	double elapsed_seconds;
	int objective_value;
	boolean solution_exists;
	
	public SimulationResult() {}
	
	public SimulationResult(int count, double secs, int objval, boolean sol_exists) {
		node_count = count;
		elapsed_seconds = secs;
		objective_value = objval;
		solution_exists = sol_exists;
	}

}
