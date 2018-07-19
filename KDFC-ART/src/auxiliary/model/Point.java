package auxiliary.model;

import java.util.ArrayList;

public class Point {
	public int n; // 维度
	public float[] coordPoint; //存储该点各个维度的坐标值

	public Point(int n) {
		this.n = n;
		this.coordPoint = new float[n];

	}
	
	public static double getDistance(Point p1, Point p2) {   //获得两个测试点之间的欧式距离
		double sum = 0;
		for (int i = 0; i <  p1.n; i++) {
			sum=sum+Math.pow(p1.coordPoint[i] - p2.coordPoint[i], 2);
		}
		return Math.sqrt(sum);
		
	}
 
	public static Point generateRandP(int[][] inputDomain) { // 从指定空间中随机产生一个测试用例
		int n = inputDomain.length;
		Point newPoint = new Point(n);
		for (int i = 0; i < n; i++) {
			newPoint.coordPoint[i] = (float) (inputDomain[i][0] + (inputDomain[i][1] - inputDomain[i][0]) * Math.random());
		}
		return newPoint;
	}

}
