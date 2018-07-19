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
import fault.model.FaultZone_Point_Square;
import fault.model.FaultZone_Strip;
import fscs.art.FSCS_ART;
import kdfc.art.KDFC_ART;

public class TestEffectiveness {

	public static void main(String[] args) throws IOException {

		ArrayList<Float> area = new ArrayList<>();

		area.add(0.01f);
		area.add(0.005f);
		area.add(0.002f);
		area.add(0.001f);
		area.add(0.0005f);
		area.add(0.0002f);
		area.add(0.0001f);

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

		for (float a : area) {
			System.out.println(a);
			String s1 = "2d-Block-" + a + ".txt";
			String s2 = "2d-Strip-" + a + ".txt";
			String s3 = "2d-Point-" + a + ".txt";
			fixRateTest("E:/temp/" + s1, a, bd2, 1);
			fixRateTest("E:/temp/" + s2, a, bd2, 2);
			fixRateTest("E:/temp/" + s3, a, bd2, 3);

		}

	}

	public static void fixRateTest(String file, float area, int[][] bd, int t) throws IOException { 
		// 记录每条数据
		KDFC_ART kdfc;
		FSCS_ART fscs;
		FaultZone fzb;
		File f1 = new File(file);
		f1.createNewFile();

		int[] backNum = new int[100 * (int) (1 / area)];
		backNum[0] = 1;
		backNum[1] = 1;
		double d = bd.length;
		for (int i = 2; i < backNum.length; i++) {
			backNum[i] = (int) Math.ceil(1 / 2.0 * Math.pow((d + 1 / d), 2) * (Math.log(i) / Math.log(2)));

		}

		PrintWriter out = new PrintWriter(new FileWriter(f1));

		double num1 = 0, num2 = 0, num3 = 0, num4 = 0;
		for (int i = 0; i < 1000; i++) {
			if (t == 1) {
				fzb = new FaultZone_Block(bd, area);
			} else if (t == 2) {
				fzb = new FaultZone_Strip(bd, area, 0.9);
			} else {
				fzb = new FaultZone_Point_Square(bd, area);
			}
			for (int j = 0; j < 10; j++) {
				fscs = new FSCS_ART(10);
				int num = fscs.testFscsArt_Effectiveness(bd, fzb);
				num1 = num1 + num;
				out.print(num + "\t");

				kdfc = new KDFC_ART(bd);
				kdfc.testNaiveKDFC_Effectiveness(fzb);
				num2 = num2 + kdfc.size;
				out.print(kdfc.size + "\t");

				kdfc = new KDFC_ART(bd);
				kdfc.testSemiBalKDFC_Effectiveness(fzb);
				num3 = num3 + kdfc.size;
				out.print(kdfc.size + "\t");

				kdfc = new KDFC_ART(bd);
				kdfc.testLimBalKDFC_Effectiveness(fzb, backNum);
				num4 = num4 + kdfc.size;
				out.print(kdfc.size + "\t");
				out.println();

				out.flush();
			}
		}
 
		double n = 10000.0;
		double s = 1 / area / 100;
		out.println(new DecimalFormat("0.0000").format(num1 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num2 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num3 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num4 / n / s));
		System.out.println(new DecimalFormat("0.0000").format(num1 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num2 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num3 / n / s) + "\t"
				+ new DecimalFormat("0.0000").format(num4 / n / s));
		out.close(); 

	}

}
