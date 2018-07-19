package fscs.art;

import fault.model.*;
import kdfc.art.KDFC_ART;

import java.io.IOException;
import java.util.ArrayList;

import auxiliary.model.Point;

public class FSCS_ART {
	public int candNum = 10; // 候选用例个数
	public int[][] inputDomain;

	public FSCS_ART() {
		candNum = 10;
	}

	public FSCS_ART(int n) {
		candNum = n;
	}

	public int findFarestCandidate(Point[] tcP, int size, Point[] candP) // 确定最远的候选用例
	{
		double[] dist = new double[candP.length];
		double tempDist, farestDist = 0;
		int farestIndex = 0;

		for (int i = 0; i < candP.length; i++) {
			dist[i] = Point.getDistance(candP[i], tcP[0]);

			for (int j = 1; j < size; j++) {
				tempDist = Point.getDistance(candP[i], tcP[j]);
				if (tempDist < dist[i])
					dist[i] = tempDist;
			}

			if (i == 0) {
				farestDist = dist[0];
				farestIndex = 0;
			} else {
				if (farestDist < dist[i]) {
					farestDist = dist[i];
					farestIndex = i;
				}

			}
		}
		return farestIndex;
	}

	public int testFscsArt_Effectiveness(int bound[][], FaultZone fzb) { // 失效检测效果测试

		int generatedNum = 0; // 已生成的测试用例数目
		int maxTry = (int) (30 / fzb.theta); // 如果超过maxTry次还未击中故障区则停止
		int selected;
		Point[] tcP = new Point[maxTry + 2]; // 已测试用例的集合
		Point[] candP = new Point[candNum]; // 候选用例集合

		tcP[0] = Point.generateRandP(bound);
		generatedNum++;

		do {
			for (int i = 0; i < candNum; i++) { // 随机生成n个候选的测试用例
				candP[i] = Point.generateRandP(bound);
			}
			selected = findFarestCandidate(tcP, generatedNum, candP);
			tcP[generatedNum] = candP[selected];
			generatedNum++;
			if (fzb.findTarget(tcP[generatedNum - 1])) {
				break;
			}
		} while (generatedNum < maxTry);
		return generatedNum;
	}

	public void testFscsArt_Efficiency(int num, int bound[][]) { // 计算效率测试

		int selected;
		Point[] tcP = new Point[num];
		Point[] candP = new Point[candNum];
		tcP[0] = Point.generateRandP(bound);
		for (int j = 1; j < num; j++) { // 随机生成n个候选的测试用例
			for (int i = 0; i < candNum; i++) {
				candP[i] = Point.generateRandP(bound);
			}
			selected = findFarestCandidate(tcP, j, candP);
			tcP[j] = candP[selected];
		}
	}

}
