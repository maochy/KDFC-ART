package kdfc.art;

import java.awt.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.omg.Messaging.SyncScopeHelper;

import auxiliary.model.Node;
import auxiliary.model.Point;
import fault.model.FaultZone;
import fscs.art.FSCS_ART;

public class KDFC_ART {

	public Node root; // 根节点
	public int size = 0; // 树中节点个数
	public int candidateNum = 10; // 候选用例个数
	public int[][] inputDomain; // 输入域范围

	public KDFC_ART() {

	}

	public KDFC_ART(int[][] bound) {
		super();
		root = new Node();
		inputDomain = bound;
	}

	public Stack<Node> getTreePath(Point p) { // 获得p点在该树中的搜索路径

		Stack<Node> path = new Stack<>();
		Node pathNode = this.root;

		while (true) {
			path.push(pathNode);
			if (pathNode.p.coordPoint[pathNode.spilt] > p.coordPoint[pathNode.spilt]) { // 点p在pathNode左边
				if (pathNode.left == null) {
					break;
				}
				pathNode = pathNode.left;
			} else { // 点p在pathNode右边
				if (pathNode.right == null) {
					break;
				}
				pathNode = pathNode.right;
			}
		}
		return path;
	}

	public int judgeDirection(Point p, Node node) { // 判断点p在node节点的左边还是右边 0为左边 1为右边
		if (p.coordPoint[node.spilt] < node.p.coordPoint[node.spilt]) {
			return 0;
		} else
			return 1;
	}

	public double getMinDisByALL(Point p) { // 求出点p距离树中节点的最短距离-全回溯

		Stack<Node> path = this.getTreePath(p);
		Node pathNode = null;
		double distance = Double.MAX_VALUE;

		while ((!path.isEmpty())) {
			pathNode = path.pop(); // 从主路径中pop出pathNode节点
			if (this.isCrossSpiltLine(p, distance, pathNode)) { // 当前distance和PathNode所在的边界线相交时，需进入另一边查询
				double d = Point.getDistance(p, pathNode.p);
				if (distance > d) {
					distance = d;
				}

				int direction = this.judgeDirection(p, pathNode);// 判断点p在pathNode的左边还是右边
				Node tempNode = null;

				if (direction == 0) {
					if (pathNode.right != null) {
						tempNode = pathNode.right;
					}
				} else {
					if (pathNode.left != null) {
						tempNode = pathNode.left;
					}
				}

				if (tempNode != null) {
					Queue<Node> queue = new LinkedList<>();
					queue.offer(tempNode);
					while (!queue.isEmpty()) { // 对子树进行广度优先遍历
						tempNode = queue.poll();
						direction = this.judgeDirection(p, tempNode);
						if (this.isCrossSpiltLine(p, distance, tempNode)) { // 如果p节点和子树节点的边界线相交，则将子树节点的另一边push进队列
							d = Point.getDistance(p, tempNode.p);
							if (distance > d) {
								distance = d;
							}

							if (direction == 1) {
								if (tempNode.left != null) {
									queue.offer(tempNode.left);
								}
							} else {
								if (tempNode.right != null) {
									queue.offer(tempNode.right);
								}
							}
						}

						if (direction == 0) { // 在左边，首先将左边节点push进队列
							if (tempNode.left != null) {
								queue.offer(tempNode.left);
							}
						} else { // 在右边，首先将右边节点push进队列
							if (tempNode.right != null) {
								queue.offer(tempNode.right);
							}
						}

					}
				}
			}

		}
		return distance;
	}

	public double getMinDisByBacktracking(Point p, int back) { // 求出点p距离树中节点的最短距离-限制回溯

		int num = 0; // 记录当前回溯节点的个数
		Stack<Node> path = this.getTreePath(p);
		Node pathNode = null;
		double distance = Double.MAX_VALUE;

		while ((!path.isEmpty())) {
			pathNode = path.pop(); // 从主路径中pop出pathNode节点
			if (this.isCrossSpiltLine(p, distance, pathNode)) { // 当前distance和PathNode所在的边界线相交时，需进入另一边查询
				double d = Point.getDistance(p, pathNode.p);
				if (distance > d) {
					distance = d;
				}
				num++;
				if (num == back) {
					return distance;
				}

				int direction = this.judgeDirection(p, pathNode);// 判断点p在pathNode的左边还是右边
				Node tempNode = null;

				if (direction == 0) {
					if (pathNode.right != null) {
						tempNode = pathNode.right;
					}
				} else {
					if (pathNode.left != null) {
						tempNode = pathNode.left;
					}
				}

				if (tempNode != null) {
					Queue<Node> queue = new LinkedList<>();
					queue.offer(tempNode);
					while (!queue.isEmpty()) { // 对子树进行广度优先遍历
						tempNode = queue.poll();

						direction = this.judgeDirection(p, tempNode);

						if (this.isCrossSpiltLine(p, distance, tempNode)) { // 如果p节点和子树节点的边界线相交，则将子树节点的另一边push进队列
							d = Point.getDistance(p, tempNode.p);
							if (distance > d) {
								distance = d;
							}
							num++;
							if (num == back) {
								return distance;
							}
							if (direction == 1) {
								if (tempNode.left != null) {
									queue.offer(tempNode.left);
								}
							} else {
								if (tempNode.right != null) {
									queue.offer(tempNode.right);
								}
							}
						} else {
							num++;
							if (num == back) {
								return distance;
							}
						}

						if (direction == 0) { // 在左边，首先将左边节点push进队列
							if (tempNode.left != null) {
								queue.offer(tempNode.left);
							}

						} else { // 在右边，首先将右边节点push进队列
							if (tempNode.right != null) {
								queue.offer(tempNode.right);
							}
						}
					}
				}
			} else {
				num++;
				if (num == back) {
					return distance;
				}
			}

		}
		return distance;
	}

	public Boolean isCrossSpiltLine(Point p, double distance, Node node) { // 判断以节点p为中心，distance为半径的圆是否与node所在的边界线相交

		if (Math.abs(node.p.coordPoint[node.spilt] - p.coordPoint[node.spilt]) >= distance) { // 分界线
			return false;
		}
		return true;

	}

	public void insertPointByStrategy(Point p) { // 采用平衡策略向tree中插入新的节点p
		if (root.p == null) { // 如果root节点中的p点为空
			root.deep = 1;
			root.p = p;
			root.boundary = new float[p.n][2];
			for (int i = 0; i < p.n; i++) {
				root.boundary[i][0] = this.inputDomain[i][0];
				root.boundary[i][1] = this.inputDomain[i][1];
			}
			root.spilt = this.spiltSelect(root.boundary, p);
		} else {
			Node ntemp = root;
			Node n = new Node();
			while (true) {
				if (ntemp.p.coordPoint[ntemp.spilt] > p.coordPoint[ntemp.spilt]) {
					if (ntemp.left == null) { // ntemp的左边为空，则将n设置为左边子树,并退出循环
						ntemp.left = n;
						break;
					}
					ntemp = ntemp.left;
				} else {
					if (ntemp.right == null) { // ntemp的右边为空，则将n设置为右边子树，并退出循环
						ntemp.right = n;
						break;
					}
					ntemp = ntemp.right;
				}

			}
			n.p = p;
			n.boundary = new float[p.n][2];
			n.deep = ntemp.deep + 1;
			for (int i = 0; i < p.n; i++) {
				n.boundary[i][0] = ntemp.boundary[i][0];
				n.boundary[i][1] = ntemp.boundary[i][1];
			}
			if (n.p.coordPoint[ntemp.spilt] < ntemp.p.coordPoint[ntemp.spilt]) { // 在父亲节点的左边
				n.boundary[ntemp.spilt][1] = ntemp.p.coordPoint[ntemp.spilt];
			} else {
				n.boundary[ntemp.spilt][0] = ntemp.p.coordPoint[ntemp.spilt];
			}
			n.spilt = this.spiltSelect(n.boundary, p);
		}
		size++;
	}

	public void insertPointByTurn(Point p) { // 轮流各个维度向tree中插入新的节点p
		if (root.p == null) { // 如果root节点中的p点为空
			root.p = p;
			root.spilt = 0; // 第一层设置分裂为x
			root.deep = 1;
		} else {
			Node ntemp = root;
			Node n = new Node();
			while (true) {
				if (ntemp.p.coordPoint[ntemp.spilt] > p.coordPoint[ntemp.spilt]) {
					if (ntemp.left == null) { // ntemp的左边为空，则将n设置为左边子树,并退出循环
						ntemp.left = n;
						break;
					}
					ntemp = ntemp.left;
				} else {
					if (ntemp.right == null) { // ntemp的右边为空，则将n设置为右边子树，并退出循环
						ntemp.right = n;
						break;
					}
					ntemp = ntemp.right;
				}

			}
			n.p = p;
			n.deep = ntemp.deep + 1;
			if (ntemp.spilt == (p.coordPoint.length - 1)) {
				n.spilt = 0;
			} else
				n.spilt = ntemp.spilt + 1;
		}
		size++;
	}

	public void testNaiveKDFC_Effectiveness(FaultZone fzb) { // NaiveKDFC检测效果测试
		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByTurn(p);
		if (fzb.findTarget(p)) {
			return;
		}
		Point finalCase;
		ArrayList<Point> canD;
		while (true) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int i = 0; i < candidateNum; i++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			double distance = this.getMinDisByALL(finalCase);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByALL(canD.get(c)); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByTurn(finalCase);
			if (fzb.findTarget(finalCase)) {
				break;
			}

		}
	}

	public void testSemiBalKDFC_Effectiveness(FaultZone fzb) { // SemiBalKDFC检测效果测试
		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByStrategy(p);
		if (fzb.findTarget(p)) {
			return;
		}
		Point finalCase;
		ArrayList<Point> canD;
		while (true) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int i = 0; i < candidateNum; i++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			double distance = this.getMinDisByALL(finalCase);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByALL(canD.get(c)); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByStrategy(finalCase);
			if (fzb.findTarget(finalCase)) {
				break;
			}

		}
	}

	public void testLimBalKDFC_Effectiveness(FaultZone fzb, int[] backNum) {// LimBalKDFC检测效果测试

		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByStrategy(p);
		if (fzb.findTarget(p)) {
			return;
		}
		Point finalCase;
		ArrayList<Point> canD;
		while (true) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int i = 0; i < candidateNum; i++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			int back = backNum[this.size];
			double distance = this.getMinDisByBacktracking(finalCase, back);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByBacktracking(canD.get(c), back); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByStrategy(finalCase);
			if (fzb.findTarget(finalCase)) {
				break;
			}

		}
	}

	public void testNaiveKDFC_Efficiency(int pointNum) throws IOException { // NaiveKDFC计算效率测试

		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByTurn(p);
		Point finalCase;
		ArrayList<Point> canD;
		for (int i = 1; i < pointNum; i++) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int j = 0; j < candidateNum; j++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			double distance = this.getMinDisByALL(finalCase);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByALL(canD.get(c)); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByTurn(finalCase);
		}
	}

	public void testSemiBalKDFC_Efficiency(int pointNum) throws IOException { // SemiBalKDFC计算效率测试

		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByStrategy(p);
		Point finalCase;
		ArrayList<Point> canD;
		for (int i = 1; i < pointNum; i++) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int j = 0; j < candidateNum; j++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			double distance = this.getMinDisByALL(finalCase);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByALL(canD.get(c)); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByStrategy(finalCase);
		}
	}

	public void testLimBalKDFC_Efficiency(int pointNum, int[] backNum) throws IOException { // LimBalKDFC计算效率测试

		Point p = Point.generateRandP(inputDomain); // 随机产生一个用例
		this.insertPointByStrategy(p);
		Point finalCase;
		ArrayList<Point> canD;
		for (int i = 1; i < pointNum; i++) {
			canD = new ArrayList<>(); // 测试用例候选集
			for (int j = 0; j < candidateNum; j++) {
				canD.add(Point.generateRandP(inputDomain));
			}
			finalCase = canD.get(0);
			int back = backNum[this.size];
			double distance = this.getMinDisByBacktracking(finalCase, back);
			for (int c = 1; c < candidateNum; c++) {
				double d = this.getMinDisByBacktracking(canD.get(c), back); // 获得最小距离
				if (distance < d) { // 获得最小距离最大的那个候选点
					distance = d;
					finalCase = canD.get(c);
				}
			}
			this.insertPointByStrategy(finalCase);

		}
	}

	public int spiltSelect(float[][] boundary, Point p) { // 采用平衡策略 选择最佳分割维度
		double rate = 0;
		int spilt = 0;
		for (int i = 0; i < p.n; i++) {
			double length = boundary[i][1] - boundary[i][0]; // 边长
			double lx1 = boundary[i][1] - p.coordPoint[i];
			double lx2 = p.coordPoint[i] - boundary[i][0];
			double spread = length * (1 - (lx1 / length) * (lx1 / length) - (lx2 / length) * (lx2 / length));
			if (rate < spread) {
				rate = spread;
				spilt = i;
			}
		}
		return spilt;
	}

}
