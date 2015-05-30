package shippingTraffic.util;

import java.util.Arrays;

/**
 * A Simple Matrix.<br/>
 * It is used for Distance Matrix and Safe Distance Matrix.
 * 
 * @author Chenfeng ZHU
 * 
 */
public class SimpleMatrix {

	private double[][] matrix;
	private int m;
	private int n;

	public SimpleMatrix() {
		this(0, 0);
	}

	public SimpleMatrix(int m, int n) {
		this.m = m;
		this.n = n;
		this.matrix = new double[m][n];
	}

	public SimpleMatrix(double[][] matrix) {
		this.matrix = matrix.clone();
		this.m = this.matrix.length;
		this.n = this.matrix[0].length;
	}

	public double[][] getMatrix() {
		return this.matrix;
	}

	/**
	 * Set the value of the element in (i,j).
	 * 
	 * @param i
	 *            Row
	 * @param j
	 *            Column
	 * @param value
	 *            the new value
	 */
	public void setMatrixElement(int i, int j, double value) {
		if (i > m || j > n) {
			try {
				throw new Exception("Error: out of bound.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			matrix[i][j] = value;
		}
	}

	public String toFormatString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				buf.append(matrix[i][j]).append("\t");
			}
			buf.append("\n");
		}

		return buf.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m;
		result = prime * result + Arrays.hashCode(matrix);
		result = prime * result + n;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMatrix other = (SimpleMatrix) obj;
		if (m != other.m)
			return false;
		if (!Arrays.equals(matrix, other.matrix))
			return false;
		if (n != other.n)
			return false;
		return true;
	}

	public int getM() {
		return this.m;
	}

	public int getN() {
		return this.n;
	}

}
