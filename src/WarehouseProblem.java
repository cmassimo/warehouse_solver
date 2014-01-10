public class WarehouseProblem {
	// numero di merci
	int goods_count; //5
	// numero di location disponibili
	int locations_count; //12
	// capacita` delle location
	int[] slots_volumes;
	int[] slots_loads;
	// unita` di prodotto disponibili
	int[] boxes_volumes;
	int[] boxes_weights;
	int[] boxes_per_goods;

	public WarehouseProblem() {}
	
	public WarehouseProblem(int gc, int lc, int[] sv, int[] sl, int[] bv, int[] bw, int[] bpg) {
		goods_count = gc;
		locations_count = lc;
		slots_volumes = sv;
		slots_loads = sl;
		boxes_volumes = bv;
		boxes_weights = bw;
		boxes_per_goods = bpg;
		
		System.out.println("[WarehouseProblem] Instantiating problem with " + 
				goods_count + " goods and " + locations_count + " locations." );
	}
}
