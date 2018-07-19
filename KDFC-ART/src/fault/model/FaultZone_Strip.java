
package fault.model;

import java.util.Random;

import auxiliary.model.Point;

/**
 * 
 * @author zxz 随意角度的Strip方法
 */
public class FaultZone_Strip extends FaultZone {   //条状失效模式

	public int[][] inputDomain;
	public long edge; // 每一维度的边长
	public double aboveLineDelta; // 上方线段的截距
	public double belowLineDelta; // 下方线段的截距
	public double ratio; // 两条线段的斜率

	public FaultZone_Strip() {

	}

	public FaultZone_Strip(int[][] boundary, float area, double rate) { // rate为限制边界的范围 
		inputDomain = boundary;
		theta = area; // 失效率
		edge = inputDomain[0][1] - inputDomain[0][0]; // 维度边长

		Random random = new Random();
		int lineLocation = random.nextInt(3); // 产生随机数，确定线段的生成位置

		double p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y; // p1、p2共同在x=-5000 p3、p4在y=5000 p1-p3线段均在p2-p4的上方

		if (lineLocation == 0) { // 在上方生成两点
			while (true) {
				p1x = -5000;
				p2x = -5000;
				p2y = -5000 + (10000 * rate * Math.random());
				p3y = 5000;
				p4x = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random());
				p4y = 5000;
				double bigTriangleArea = (5000 - p2y) * (p4x + 5000) / 2;
				ratio = (p4y - p2y) / (p4x - p2x);
				double temp = 2 * (bigTriangleArea - 10000 * 10000 * area) / ratio;
				p3x = Math.sqrt(temp) - 5000;
				p1y = 5000 - ratio * (p3x + 5000);
				if ((p3x >= (-5000 + (10000 * (1 - rate)))) && (p1y <= (-5000 + 10000 * rate))) { // p3x与p1y也在指定范围内
					break;
				}
			}
		} else if (lineLocation == 1) { // 在右边生成两点
			while (true) {
				p1x = -5000;
				p2x = -5000;
				p2y = -5000 + (10000 * Math.random()); // 生成p2点
				p3x = 5000;
				p4x = 5000;
				p4y = (-5000 + (10000 * Math.random()));
				p1y = p2y + 10000 * theta;
				p3y = p4y + 10000 * theta;
				ratio = (p4y - p2y) / (p4x - p2x);
				if (p1y <= 5000 && p3y <= 5000) { // p3x与p1y也在指定范围内
					break;
				}
			}
		} else { // 在下方生成两点
			while (true) {
				p1x = -5000;
				p1y = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random()); // 生成p2点
				p2x = -5000;
				p3x = (-5000 + (10000 * (1 - rate))) + (10000 * rate * Math.random());
				p3y = -5000;
				p4y = -5000;
				ratio = (p3y - p1y) / (p3x - p1x);
				double bigTriangleArea = (p1y + 5000) * (p3x + 5000) / 2;
				double temp = 2 * (10000 * 10000 * area - bigTriangleArea) / ratio;
				p4x = Math.sqrt(temp) - 5000;
				p2y = -ratio * (p4x + 5000) - 5000;
				if ((p4x >= (-5000 + (10000 * (1 - rate)))) && (p2y >= (-5000 + (10000 * (1 - rate))))) {
					break;
				}
			}
		}
		aboveLineDelta = p1y - ratio * p1x;
		belowLineDelta = p4y - ratio * p4x;
	}

	@Override
	public Boolean findTarget(Point p) {  // 判断是否发现失效
		// TODO Auto-generated method stub
		if (p.coordPoint[1] - ratio * p.coordPoint[0] >= belowLineDelta
				&& p.coordPoint[1] - ratio * p.coordPoint[0] <= aboveLineDelta) {
			return true;
		} else
			return false;

	}
}
