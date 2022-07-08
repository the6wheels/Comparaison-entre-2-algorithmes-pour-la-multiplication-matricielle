package algoTP1;

//TP3 Algorithmique et Complexité 2021-2022
//LIRE ET BIEN COMPRENDRE LE CODE AVANT DE LE COMPLETER
//Respecter la démarche à suivre donnée en commentaires 

//Nom:HADJAZI
//Prénom: Mohammed Hisham
//Spécialité:   RSSI      Groupe: 01

//Nom:Ameur
//Prénom: Wassim Malik
//Spécialité:   RSSI      Groupe: 01

import java.util.*;

public class StrassenMult {

	static long cpt1 = 0;
	static long cpt2 = 0;

	public static int[][] multiplication(int[][] A, int[][] B) {
		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns != bRows) {
			throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		}

		int[][] C = new int[aRows][bColumns];
		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				C[i][j] = 0;
			}
		}

		for (int i = 0; i < aRows; i++) {
			for (int j = 0; j < bColumns; j++) {
				for (int k = 0; k < aColumns; k++) {
					C[i][j] += A[i][k] * B[k][j];
					cpt1++;
				}
			}
		}

		return C;

	}

	public static int[][] strassen(int[][] A, int[][] B) {
		
		int n = A.length;
		int[][] resultat = new int[n][n];

		if ((n % 2 != 0) && (n != 1)) {
			/*
			 * Ajouter une ligne de 0 et une colonne de 0 pour A et B : définir 3 nouvelles
			 * matrices temporaires A1,B1,C1 de taille n+1, ensuite A1=A et B1=B
			 */
			int[][] A1, B1, C1;
			int n1 = n + 1;
			A1 = new int[n1][n1];
			B1 = new int[n1][n1];

			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					A1[i][j] = A[i][j];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					B1[i][j] = B[i][j];

			C1 = strassen(A1, B1);
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					resultat[i][j] = C1[i][j];
			return resultat;
		}

		if (n == 1) {
			resultat[0][0] = A[0][0] * B[0][0];
			cpt2++;
		} else {

//Creation de 4 sous matrices A11,A12,A21,A22 (n/2 x n/2) 
			int[][] A11 = new int[n / 2][n / 2];
			int[][] A12 = new int[n / 2][n / 2];
			int[][] A21 = new int[n / 2][n / 2];
			int[][] A22 = new int[n / 2][n / 2];

//Creation de 4 sous matrices B11,B12,B21,B22 (n/2 x n/2)
			int[][] B11 = new int[n / 2][n / 2];
			int[][] B12 = new int[n / 2][n / 2];
			int[][] B21 = new int[n / 2][n / 2];
			int[][] B22 = new int[n / 2][n / 2];

//Décomposition de A en 4 sous matrices A11,A12,A21,A22

			decomposer(A, A11, 0, 0);
			decomposer(A, A12, 0, n / 2);
			decomposer(A, A21, n / 2, 0);
			decomposer(A, A22, n / 2, n / 2);

//Décomposition de B en 4 sous matrices B11,B12,B21,B22

			decomposer(B, B11, 0, 0);
			decomposer(B, B12, 0, n / 2);
			decomposer(B, B21, n / 2, 0);
			decomposer(B, B22, n / 2, n / 2);

//les 7 appels recursifs M1,...M7 : 
			int[][] M1 = strassen(add(A11, A22), add(B11, B22));
			int[][] M3 = strassen(A11, sub(B12, B22));
			int[][] M2 = strassen(add(A21, A22), B11);
			int[][] M4 = strassen(A22, sub(B21, B11));
			int[][] M5 = strassen(add(A11, A12), B22);
			int[][] M6 = strassen(sub(A21, A11), add(B11, B12));
			int[][] M7 = strassen(sub(A12, A22), add(B21, B22));

// calcul de C11,C12,C21,C22 : 			
			int[][] C11 = add(sub(add(M1, M4), M5), M7);
			int[][] C12 = add(M3, M5);
			int[][] C21 = add(M2, M4);
			int[][] C22 = add(sub(add(M1, M3), M2), M6);

			/* Composition de la matrice C a partir de C11,C12,C21,C22 */
			composer(C11, resultat, 0, 0);
			composer(C12, resultat, 0, n / 2);
			composer(C21, resultat, n / 2, 0);
			composer(C22, resultat, n / 2, n / 2);

		}

		return resultat;
	}

	public static int[][] add(int[][] A, int[][] B) {
		int n = A.length;
		int[][] C = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				C[i][j] = A[i][j] + B[i][j];
		return C;

	}

	public static int[][] sub(int[][] A, int[][] B) {
		int n = A.length;
		int[][] C = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				C[i][j] = A[i][j] - B[i][j];
		return C;

	}

	public static void decomposer(int[][] p1, int[][] c1, int iB, int jB) {
		/*
		 * décomposition de p1: résultat dans c1 c1(n/2x n/2) doit contenir la partie de
		 * p1(nxn) a partir de la ligne iB et de la colonne jB de p1
		 */

		for (int i1 = 0, i2 = iB; i1 < c1.length; i1++, i2++)
			for (int j1 = 0, j2 = jB; j1 < c1.length; j1++, j2++)

				c1[i1][j1] = p1[i2][j2];

	}

	public static void composer(int[][] c1, int[][] p1, int iB, int jB) {
		/*
		 * Composition de p1( nxn) a partir de c1(n/2 x n/2) : affectation de c1 a la
		 * partie de p1 commençant a la ligne iB et de la colonne jB de p1
		 */

		for (int i1 = 0, i2 = iB; i1 < c1.length; i1++, i2++)
			for (int j1 = 0, j2 = jB; j1 < c1.length; j1++, j2++)
				p1[i2][j2] = c1[i1][j1];

	}

	public static void affiche(int[][] tab) {
		int n = tab.length;

		System.out.println();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(tab[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void lire(int[][] A, int[][] B) {
		Random r = new Random();

		int i, j;
		int N = A.length;

		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				A[i][j] = r.nextInt(10);
				B[i][j] = r.nextInt(10);

			}
		}
	}

	public static void main(String[] args) {
		long startTime, endTime;
		float res1, res2;
		Scanner scan = new Scanner(System.in);
		System.out.print("Donner la taille des matrices n :");
		int N = scan.nextInt();
		int[][] A = new int[N][N];
		int[][] B = new int[N][N];
		int[][] C = new int[N][N];
		int[][] D = new int[N][N];

		lire(A, B);

		System.out.println("Matrix A : ");

		affiche(A);
		System.out.println("Matrix B : ");

		affiche(B);

		startTime = System.nanoTime();
		C = multiplication(A, B);
		endTime = System.nanoTime();
		res1 = (float) (endTime - startTime) / 1000000;

		System.out.println("Resultat de la multiplication par la méthode classique : ");

		affiche(C);

		startTime = System.nanoTime();
		D = strassen(A, B);
		endTime = System.nanoTime();
		res2 = (float) (endTime - startTime) / 1000000;

		System.out.println("Résultat de la multiplication par la méthode de Strassen : ");

		affiche(D);

		System.out.println("Nombre de multiplications avec la solution classique   :  " + cpt1);
		System.out.println("Nombre de multiplications avec la solution de Strassen :  " + cpt2);
		System.out.println("Temps de multiplication classique :" + res1 + " ms");
		System.out.println("Temps de multiplication par Strassen :" + res2 + " ms");

	}

}