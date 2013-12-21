public class SimulationResult {
	
	int node_count;
	double elapsed_seconds;
	
	public SimulationResult() {}
	
	public SimulationResult(int count, double secs) {
		node_count = count;
		elapsed_seconds = secs;
	}

}
