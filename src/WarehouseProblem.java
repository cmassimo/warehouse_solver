public class WarehouseProblem {
	// numero di merci
	int goods_count; //5
	// numero di location disponibili
	int locations_count; //12
	// capacita` delle location
//	int[] slots_volumes;
//	int[] slots_loads;
//	// unita` di prodotto disponibili
//	int[] boxes_volumes;
//	int[] boxes_weights;
//
//	int[] boxes_per_goods;

	public WarehouseProblem() {}
	
	public WarehouseProblem(int[] problem_data) {
		goods_count = problem_data[0];
		locations_count = problem_data[1];
//		slots_volumes = new int[locations_count];
//		slots_loads = new int[locations_count];
//		boxes_volumes = new int[goods_count];
//		boxes_weights = new int[goods_count];
//		boxes_per_goods = new int[goods_count];
		
		System.out.println("[WarehouseProblem] Instantiating problem with " + 
				problem_data[0] + " goods and " + problem_data[1] + " locations." );
	}
}
