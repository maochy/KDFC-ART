package test.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import auxiliary.model.Point;
import fault.model.FaultZone;
import fault.model.FaultZone_Block;
import fault.model.FaultZone_Point_Square;
import fault.model.FaultZone_Strip;
import fscs.art.FSCS_ART;
import kdfc.art.KDFC_ART;

public class TestEfficiency {

	final static int n = 1003; // ÖØ¸´´ÎÊý

	public static void main(String[] args) throws IOException {

		ArrayList<Integer> num = new ArrayList<>();

		num.add(100);
		num.add(200);
		num.add(500);
		num.add(1000);
		num.add(2000);
		num.add(5000);
		num.add(10000);
		num.add(15000);
		num.add(20000);

		int[][] bd2 = { { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd3 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd4 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd5 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd6 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 },
				{ -5000, 5000 } };
		int[][] bd7 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 },
				{ -5000, 5000 }, { -5000, 5000 } };
		int[][] bd8 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 },
				{ -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd9 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 },
				{ -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };
		int[][] bd10 = { { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 },
				{ -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 }, { -5000, 5000 } };

		for (int n : num) {
			String s1 = "E:/temp/2d-FscsART-" + n + ".txt";
			String s2 = "E:/temp/2d-NaiveKDFC-" + n + ".txt";
			String s3 = "E:/temp/2d-SemiBalKDFC-" + n + ".txt";
			String s4 = "E:/temp/2d-LimBalKDFC-" + n + ".txt";

			testFscsART(s1, bd2, n);
			testNaiveKDFC(s2, bd2, n);
			testSemiBalKDFC(s3, bd2, n);
			testLimBalKDFC(s4, bd2, n);

		}

	}

	public static void testFscsART(String file, int[][] bd, int pointNum) throws IOException {
		File f1 = new File(file);
		f1.createNewFile();
		PrintWriter out = new PrintWriter(new FileWriter(f1));

		FSCS_ART fscs;
		double sum = 0;
		for (int i = 0; i < n; i++) {
			long n1 = System.nanoTime();
			fscs = new FSCS_ART();
			fscs.testFscsArt_Efficiency(pointNum, bd);
			long n2 = System.nanoTime();
			if (i != 0 && i != 1 && i != 2) {
				sum = sum + (n2 - n1);
				out.println(1.0 * (n2 - n1) / 1e6);
			}
		}

		double num = 1000.0;
		System.out.print(sum / num / 1e6 + "\t");
		out.print(sum / num / 1e6 + "\t");
		out.close();

	}

	public static void testNaiveKDFC(String file, int[][] bd, int pointNum) throws IOException {
		File f1 = new File(file);
		f1.createNewFile();

		PrintWriter out = new PrintWriter(new FileWriter(f1));
		KDFC_ART kdfc;

		double sum = 0;
		for (int i = 0; i < n; i++) {
			long n1 = System.nanoTime();
			kdfc = new KDFC_ART(bd);
			kdfc.testNaiveKDFC_Efficiency(pointNum);
			long n2 = System.nanoTime();
			if (i != 0 && i != 1 && i != 2) {
				sum = sum + (n2 - n1);
				out.println(1.0 * (n2 - n1) / 1e6);
			}

		}

		double num = 1000.0;
		System.out.print(sum / num / 1e6 + "\t");
		out.print(sum / num / 1e6 + "\t");
		out.close();

	}

	public static void testSemiBalKDFC(String file, int[][] bd, int pointNum) throws IOException {
		File f1 = new File(file);
		// if(!f1.exists()){
		f1.createNewFile();
		// }
		PrintWriter out = new PrintWriter(new FileWriter(f1));
		KDFC_ART kdfc;

		double sum = 0;
		for (int i = 0; i < n; i++) {
			long n1 = System.nanoTime();
			kdfc = new KDFC_ART(bd);
			kdfc.testSemiBalKDFC_Efficiency(pointNum);
			long n2 = System.nanoTime();
			if (i != 0 && i != 1 && i != 2) {
				sum = sum + (n2 - n1);
				out.println(1.0 * (n2 - n1) / 1e6);
			}
		}

		double num = 1000.0;
		System.out.print(sum / num / 1e6 + "\t");
		out.print(sum / num / 1e6 + "\t");
		out.close();

	}

	public static void testLimBalKDFC(String file, int[][] bd, int pointNum) throws IOException {
		File f1 = new File(file);
		// if(!f1.exists()){
		f1.createNewFile();
		// }
		PrintWriter out = new PrintWriter(new FileWriter(f1));
		KDFC_ART kdfc;

		double d = bd.length;
		int[] backNum = new int[pointNum];
		backNum[1] = 1;
		for (int i = 2; i < pointNum; i++) {
			backNum[i] = (int) Math.ceil(1 / 2.0 * Math.pow((d + 1 / d), 2) * (Math.log(i) / Math.log(2)));
		}

		double sum = 0;

		for (int i = 0; i < n; i++) {
			long n1 = System.nanoTime();
			kdfc = new KDFC_ART(bd);
			kdfc.testLimBalKDFC_Efficiency(pointNum, backNum);
			long n2 = System.nanoTime();
			if (i != 0 && i != 1 && i != 2) {
				sum = sum + n2 - n1;
				out.println(1.0 * (n2 - n1) / 1e6);
			}
		}

		double num = 1000.0;
		System.out.println(sum / num / 1e6 + "\t");
		out.print(sum / num / 1e6 + "\t");
		out.close();

	}

}
