package fault.model;

import java.util.Arrays;

import auxiliary.model.Point;

public class FaultZone_Point_Square extends FaultZone {   //点状失效模式

	public int[][] inputDomain; // 输入域范围
	public int num = 25;   //小的失效区域个数
	public Point[] faultPoint; // 存储故障点的数组

	public double delta; // 失效区域的边长
 
	public FaultZone_Point_Square() {

	}
 
	public FaultZone_Point_Square(int[][] boundary, double area) {

		theta = area;
		inputDomain = boundary;
		double sum = 1.0;
		int n = boundary.length;
		for (int i = 0; i < n; i++) {
			sum = sum * (inputDomain[i][1] - inputDomain[i][0]); 															//
		}

		delta = Math.pow(sum * theta / num, 1.0 / n); 
		faultPoint = new Point[num];
		int temp = 0;

		while (temp < num) {
			Point faulttemp = new Point(n);
			do {
				for (int i = 0; i < n; i++) {
					faulttemp.coordPoint[i] = inputDomain[i][0]+ (float) ((inputDomain[i][1] - inputDomain[i][0] - delta) * Math.random());
				}
			} while (isOverlap(temp, faulttemp, delta));

			faultPoint[temp] = faulttemp;
			temp++;
		}
	}

	boolean isOverlap(int gNum, Point p, double delta) {   //判断是否与已产生子失效区域重叠
		if (gNum == 0)
			return false;
		else {
			for (int i = 0; i < gNum; i++) {
				boolean ftemp = true;
				for (int j = 0; j < p.n; j++) {
					if (!(Math.abs(p.coordPoint[j] - faultPoint[i].coordPoint[j]) < delta)) {
						ftemp = false;
						break;
					}
				}
				if (ftemp) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Boolean findTarget(Point p) {   // 判断是否发现失效
		// TODO Auto-generated method stub
		for (int i = 0; i < this.num; i++) {
			boolean ftemp = true;
			for (int j = 0; j < p.n; j++) {
				if (!(p.coordPoint[j] >= this.faultPoint[i].coordPoint[j]
						&& p.coordPoint[j] <= this.faultPoint[i].coordPoint[j] + this.delta)) {
					ftemp = false;
					break;
				}
			}
			if (ftemp) {
				return true;
			}
		}
		return false;
	}
}

