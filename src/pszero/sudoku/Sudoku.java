package pszero.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sac.graph.AStar;
import sac.graph.BestFirstSearch;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

//userguid pdf czytaæ
public class Sudoku extends GraphStateImpl {

	public static final int n = 3;
	public static final int n2 = n * n;

	private byte[][] board = null;

	public int zeros = n2 * n2;

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub		
		//return toString().hashCode();
		
		byte[] linearsudoku = new byte[n2*n2];
		int k=0;
		for(int i=0;i<n2;i++)
			for(int j=0;j<n2;j++)
				linearsudoku[k++]= board[i][j];
return Arrays.hashCode(linearsudoku);
	}

	public Sudoku() {
		board = new byte[n2][n2];
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				board[i][j] = 0;
	}

	public Sudoku(Sudoku parent) {
		board = new byte[n2][n2];
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				board[i][j] = parent.board[i][j];
		zeros = parent.zeros;
	}

	private void refreshZeros() {//Zlicz zera
		zeros = 0;
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++)
				if (board[i][j] == 0)
					zeros++;
	}

	public boolean islegal() {
		byte[] group = new byte[n2];

		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++)
				group[j] = board[i][j];
			if (!isgrouplegal(group))
				return false;
		}

		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++)
				group[j] = board[j][i];
			if (!isgrouplegal(group))
				return false;
		}

		for (int i = 0; i < n; i++) { // 4
			for (int j = 0; j < n; j++) {
				int k = 0;

				for (int l = 0; l < n; l++) {
					for (int m = 0; m < n; m++) {
						group[k++] = board[i * n + l][j * n + m];
					}
				}
				if (!isgrouplegal(group))
					return false;

			}
		}

		return true;
	}

	private boolean isgrouplegal(byte[] array) {
		boolean[] visited = new boolean[n2];
		for (int i = 0; i < n2; i++) {
			visited[i] = false;
		}
		for (int i = 0; i < n2; i++) {
			if (array[i] == 0)
				continue;
			if (visited[array[i] - 1])
				return false;
			visited[array[i] - 1] = true;
		}
		return true;
	}

	public static void main(String[] args) {
		Sudoku s = new Sudoku();

		//String sutxt = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";
		//String sutxt = "003020000000305001001806400000102900700000000006708200002600500800203009005000300";
		//String sutxt = "000000000000300001001800400008102900700000000000708200002609500800203000005010300";
		// String sutxt =
		// "003020600900305001031806400008102900700000008006708200002609500800203009005010300";
        String sutxt = "003020600900305001001806400008102900700000008006708200002609500800203009005010300";

		s.fromStringN3(sutxt);
		System.out.println(s);
		System.out.println(s.islegal());

		AStar algoritm = new AStar(); /// **
		// Stan rozwija sie na 2 stany graph issolution generatechildren
		//
		s.refreshZeros();
		System.out.print(s.zeros);// 0 bledow i 0 zer to git

		Sudoku.setHFunction(new HEmptyPlaces());
		
		GraphSearchConfigurator conf = new GraphSearchConfigurator();
		conf.setWantedNumberOfSolutions(Integer.MAX_VALUE);
		
		GraphSearchAlgorithm a = new BestFirstSearch(s,conf);
		a.execute();
		List<GraphState> solution = a.getSolutions();
		for (GraphState sol : solution) {
			System.out.println("--------");
			System.out.println(sol);

		}
		
		System.out.println("Time: "+a.getDurationTime());
		System.out.println("Close: "+a.getClosedStatesCount());
		System.out.println("Open: "+a.getOpenSet().size());
		System.out.println("Solution: "+solution.size());
		
		
	}

	public void fromStringN3(String txt) {
		int k = 0;
		for (int i = 0; i < n2; i++)
			for (int j = 0; j < n2; j++) {
				board[i][j] = Byte.valueOf(txt.substring(k, k + 1));//zmienia text w liczby i wpisuje je pokoleji do tablicy
				k++;
			}

	}

	@Override
	/*public String toString() {
		// TODO Auto-generated method stub

		String txt = "";

		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++)
				txt += board[i][j] + ",";
			txt += "\n";
		}

		return txt;

	}
*/
	public String toString() {
		// TODO Auto-generated method stub

		
		StringBuilder txt=new StringBuilder();

		for (int i = 0; i < n2; i++) {
			for (int j = 0; j < n2; j++)
				txt.append(board[i][j]+","); 
			txt.append("\n");
		}

		return txt.toString();

	}
	
	
	
	@Override
	public List<GraphState> generateChildren() {
		// TODO Auto-generated method stub

		List<GraphState> children = new ArrayList<GraphState>();
		int i = 0, j = 0;
		zerofound: for (i = 0; i < n2; i++)
			for (j = 0; j < n2; j++)
				if (board[i][j] == 0)
					break zerofound;

		if (i == n2) {
			return children;
		}

		for (int k = 0; k < n2; k++) {
			Sudoku child = new Sudoku(this);
			child.board[i][j] = (byte) (k + 1);
			if (child.islegal()) {
				children.add(child);
				child.zeros--;
			}
		}
		return children;

	}

	@Override
	public boolean isSolution() {
		// TODO Auto-generated method stub
		return ((zeros == 0) && (islegal())); // zad dom pdf

	}

	public int getZeros() {
		return zeros;
	}

	public void setZeros(int zeros) {
		this.zeros = zeros;
	}

}
//ZADANIE DOMOWE 
/*
 * Puzzle przesuwne zrobiæ
 * Pusty klocek jedzie po uk³adance
 * *algorytm new a star
 * Najkrótsza œcie¿ka
 * 
 * http://www.wikizmsi.zut.edu.pl/wiki/WsdSI/LS/z1
 * ma byc DWUWY<IAROWA PLANSZA
 * ¿eby nie kopiowaæ gotowca ale mo¿na sie na nim wzorowaæ 
 * 
 * !!Wyœwietliæ œcie¿kê ruchów (gora gura lewo itp)
 * 
 * A*
 * is solution >generate>star *
 * Heurestyka(klasy Misplaced, Manhatan) 2 heurystyki które mo¿na na zamiane komentowaæ ¿eby zobaczyæ jak dzia³aj¹(nie trzeba 3)
 * W pêtli 100 uk³adanek puszczamy 2 heurystyki i spisujemy wyniki srednie 
 * 
 * 
 * tostring hashcode generate children
 * 
 * java.until.random
 Random r=new Random()  //zalezy od stanu zegara
 Generator pseudolosowy (te same losujace)
 * r.newxt[int]()	//Zamapowaæ liczby 1,2,3,4 na ruchy(mozna) 
 * r.next[Double]()
 * 
 * 
 * Podczas tworzenia dzieci nadawaæ im nazwy zwiazen z ruchami(Generate children)
 * child.setMoveName("R");
 * 
 *  obiekt soulution
 *  sulution getMovesA(nawrot photo) Path();  robienie œcie¿ki
 *  
 *  
 *  Przyda sie kopiuj¹cy konstruktor
 *  
 *  Wejœciówka na czym polega Manhatan itp.(Teoria)
 *  Czwartek konsultacje od 7:30 do 8 107
 */ 
  
