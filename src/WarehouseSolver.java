import java.util.Arrays;

import choco.cp.model.CPModel;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.Choco;
import choco.Options;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solution;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valselector.MaxVal;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.MinDomain;

public class WarehouseSolver {

	WarehouseProblem problem;

	public WarehouseSolver(int[] is) {
		problem = new WarehouseProblem(is);
	}

	public SimulationResult solve() {
		//constants of the problem:

		int m = problem.goods_count;
		int k = problem.locations_count;

		// capacita` delle location
		int[] slots_volumes = new int[k];
		int[] slots_loads = new int[k];
		// unita` di prodotto disponibili
		int[] boxes_volumes = new int[m];
		int[] boxes_weights = new int[m];

		int[] boxes_per_goods = new int[m];

		// init volumi slot
		String vols = new String();
		String loads = new String();
		String indexes = new String("\t\t");
		for(int j = 0; j < k; j++) {
			indexes += Integer.toString(j) + "\t";
			//			slots_volumes[j] = 1 + (int)(Math.random() * 100);
			slots_volumes[j] = 100 + j;
			vols += Integer.toString(slots_volumes[j]) + "\t";
			//			slots_loads[j] = 1 + (int)(Math.random() * 70);
			slots_loads[j] = 70 + j;
			loads += Integer.toString(slots_loads[j]) + "\t";
		}
		System.out.println(indexes);
		System.out.println();
		System.out.println("Volumi slot:\t" + vols);
		System.out.println("Pesi slot:\t" + loads);

		// init volumi slot
		String vols1 = new String();
		String loads1 = new String();
		String qt = new String();
		for(int i = 0; i < m; i++) {
			//			boxes_volumes[i] = 1 + (int)(Math.random() * 20);
			boxes_volumes[i] = 20+i;
			vols1 += Integer.toString(boxes_volumes[i]) + "\t";
			//			boxes_weights[i] = 1 + (int)(Math.random() * 10);
			boxes_weights[i] = 10+i;
			loads1 += Integer.toString(boxes_weights[i]) + "\t";
			//			boxes_per_goods[i] = 1 + (int)(Math.random() * 5);
			boxes_per_goods[i] = 5+i;
			qt += Integer.toString(boxes_per_goods[i]) + "\t";
		}
		System.out.println("Volumi box:\t" + vols1);
		System.out.println("Pesi box:\t" + loads1);
		System.out.println("# box/merce:\t" + qt);
		System.out.println();

		// Our model
		Model model = new CPModel();

		// Variables

		// y_i,j: unita` di prodotto i nella location j
		IntegerVariable[][] ys = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				ys[i][j] = Choco.makeIntVar("y_"+i+"_"+j, 0, (boxes_per_goods[i]));
				model.addVariables(ys[i][j]);
			}
		}

		// x_i,j: il prodotto i e` presente nella location j? binaria
		IntegerVariable[][] xs = new IntegerVariable[m][k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				xs[i][j] = Choco.makeIntVar("x_"+i+"_"+j, 0, 1, Options.V_NO_DECISION);
				model.addVariables(xs[i][j]);
			}
		}

		// v_somma_i accumula la somma degli indici delle location utilizzate per la merce i.
		IntegerVariable[] somme = new IntegerVariable[m];
		for (int i = 0; i < m; i++) {
			// TODO verificare upper bound su libro di analisi
			somme[i] = Choco.makeIntVar("v_somma_" + i, 0, k*k/2, Options.V_NO_DECISION);
		}
		model.addVariables(somme);

		IntegerVariable[] cards = new IntegerVariable[m];
		for (int i = 0; i < m; i++) {
			cards[i] = Choco.makeIntVar("v_card_" + i, 1, k, Options.V_NO_DECISION);
		}
		model.addVariables(cards);


		//Constraints

		// vincolo su y_i_j: La singola merce deve essere posizionata tutta
		Constraint[] rows = new Constraint[m];
		for(int i = 0; i < m; i++){
			rows[i] = Choco.eq(Choco.sum(ys[i]), boxes_per_goods[i]);
		}

		model.addConstraints(rows);

		// vincolo su x_i_j: se y_i_j = 0 => x_i_j = 0 | se y_i_j > 0 => x_i_j = 1
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				model.addConstraints( Choco.ifOnlyIf(Choco.gt(ys[i][j], Choco.ZERO), Choco.eq(xs[i][j], Choco.ONE)) );
			}
		}

		// vincolo che accumula la somma degli indici delle location per la merce i
		int[] coefficients = new int[k];
		for(int j = 0; j < k; j++){
			coefficients[j]=j;
		}
		
		for(int i = 0; i < m; i++){
			model.addConstraint(Choco.eq(somme[i], Choco.scalar(xs[i], coefficients)));
			model.addConstraint(Choco.eq(cards[i], Choco.sum(xs[i])));
		}

		// vincolo sul volume degli slot SUM(i, y_i_j*box_vol) <= slot_vol foreach j in k
		Constraint[] slot_vols = new Constraint[k];
		for(int j = 0; j < k; j++){
			IntegerExpressionVariable[] tmp = new IntegerExpressionVariable[m];

			for(int i = 0; i < m; i++){
				tmp[i] = Choco.mult(ys[i][j], boxes_volumes[i]);
			}
			slot_vols[j] = Choco.leq(Choco.sum(tmp), slots_volumes[j]);
		}

		model.addConstraints(slot_vols);

		// vincolo sul carico degli slot
		Constraint[] slot_lds = new Constraint[k];
		for(int j = 0; j < k; j++){
			IntegerExpressionVariable[] tmp = new IntegerExpressionVariable[m];

			for(int i = 0; i < m; i++){
				tmp[i] = Choco.mult(ys[i][j], boxes_weights[i]);
			}
			slot_lds[j] = Choco.leq(Choco.sum(tmp), slots_loads[j]);
		}

		model.addConstraints(slot_lds);		


		// Funzione obiettivo, dichiaro una var intera con limiti assurdi e la marco come OBJ
		IntegerVariable z = Choco.makeIntVar("z", 0, 10000, Options.V_OBJECTIVE);

		// L'espressione che modella la funzione obiettivo ovvero SUM(j, j*SUM(i, x_i_j)) da minimizzare.
		// In italiano significa che penalizziamo le location man mano che crescono di indice, ovvero
		// minimizziamo il numero di location usate e le manteniamo il piu` raggruppate possibile (non bastasse il gruppo di vincoli precedente). 

		IntegerVariable P1 = Choco.constant(1);
		IntegerVariable P2 = Choco.constant(5);
		IntegerExpressionVariable[] tmp = new IntegerExpressionVariable[m];
		for (int i = 0; i < m; i++) {
			tmp[i] = Choco.plus(Choco.mult(Choco.div(somme[i], cards[i]), P1), Choco.mult(cards[i], P2));
		}
		//			inner_sums[j] = Choco.mult(j, Choco.sum(tmp));
		//		}

		model.addConstraints(Choco.eq(z, Choco.sum(tmp)));


		//		SUM ( P1*(v_somma - v_min*v_card) + P2*(v_card) )
		// SUM(P1*(v_somma/v_card) + P2*v_card)

		//Our solver
		Solver solver = new CPSolver();

		//read the model
		solver.read(model);

		// getting the vars from the solver
		IntDomainVar[] solver_ys = new IntDomainVar[m*k];
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				solver_ys[i*k+j] = solver.getVar(ys[i][j]);
			}
		}

		//		solver.addGoal(new AssignVar(new MinDomain(solver, solver_ys), new MaxVal()));
		//		solver.addGoal(new AssignVar(new MinDomain(solver, solver_ys), new RandomIntValSelector()));

		ChocoLogging.setVerbosity(Verbosity.SOLUTION);

		//		solver.monitorFailLimit(true);
		//		solver.setFailLimit(120000);

		long tps = System.currentTimeMillis();

		//solve the problem
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		solver.setTimeLimit(120000);
		solver.minimize(false);
		//		solver.solve();

		//		String solution = solver.solutionToString();
		//		
		//		System.out.println(solution);
		//		System.out.println();

		System.out.println("Y vars:\n");
		//Print the values
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				int val = solver.getVar(ys[i][j]).getVal();
				String name = solver.getVar(ys[i][j]).getName();
				if (val > 0) {
					System.out.print(name + " qt di merce " + i + " in posizione " + j + ": " + val + " box\n");
				}

			}
		}
		System.out.println("------\n");

		int val1 = solver.getVar(z).getVal();
		String name1 = solver.getVar(z).getName();
		System.out.print(name1 + " (f.o.): " + val1 + "\n");

		System.out.println("X vars:\n");
		//Print the values
		for(int i = 0; i < m; i++){
			for(int j = 0; j < k; j++){
				int val = solver.getVar(xs[i][j]).getVal();
				String name = solver.getVar(xs[i][j]).getName();
				//				if (val > 0) {
				System.out.print(name + ": merce" + i + " in pos" + j + "? " + val + "\n");
				//				}

			}
		}
		System.out.println("------\n");

		System.out.println("somme vars:\n");
		//Print the values
		for(int i = 0; i < m; i++){
			int val = solver.getVar(somme[i]).getVal();
			String name = solver.getVar(somme[i]).getName();
			System.out.print(name + ": " + val + "\n");
		}
		System.out.println("------\n");

		System.out.println("cards vars:\n");
		//Print the values
		for(int i = 0; i < m; i++){
			int val = solver.getVar(cards[i]).getVal();
			String name = solver.getVar(cards[i]).getName();
			System.out.print(name + ": " + val + "\n");
		}
		System.out.println("------\n");

		System.out.println("tempo impiegato (ms): " + (System.currentTimeMillis() - tps));
		System.out.println("numero nodi visitati " + solver.getNodeCount());

		return new SimulationResult(solver.getNodeCount(), ((System.currentTimeMillis() - tps) / 1000.0	));

	}


}
