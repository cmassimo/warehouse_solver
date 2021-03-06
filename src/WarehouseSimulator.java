public class WarehouseSimulator {
	
	// args[0] data file name (csv con 2 colonne intere)
	// esempio di invocazione: java Warehousesimulator data.csv
	public static void main(String[] args) {
		Simulation simulation = new Simulation();
		
		System.out.println("[WarehouseSimulator] Loading data file...");
		simulation.load_from_file(args[0]);
		
		System.out.println("[WarehouseSimulator] Simulating...");
		simulation.run();

		System.out.println("[WarehouseSimulator] Simulation results saved to results.csv.");
	}

}
