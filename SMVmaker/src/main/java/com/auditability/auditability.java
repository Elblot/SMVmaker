package com.auditability;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/*
 * checks the level of auditability of the log
 * uses the external tool ent 
 */
public class auditability {

	public static double getAuditability(File log) {
		if (!auditabilityLv1(log)) {
			System.out.println("the log is totally encrytped, it is not auditable");
			return 0;
		}
		else {
			return auditabilityLv2(log);
		}
	}

	private static  boolean auditabilityLv1(File log) {
		String command = "ent " + log; 

		// Running the above command 
		Runtime run  = Runtime.getRuntime(); 
		try {
			Process proc = run.exec(command);
			BufferedReader reader =	new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ( (line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			//System.out.println(result);
			double entropy = getEntropy(result);
			int compression = getCompression(result);
			double chi = getChiSquare(result);
			double mean = getMean(result);
			double carlo = getMonteCarlo(result);
			double serial = getSerialCorrelation(result);
			/*System.out.println("Entropy : " + entropy);
			System.out.println("Compression : " + compression);
			System.out.println("ChiSquare : " + chi);
			System.out.println("Mean : " + mean);
			System.out.println("Monte Carlo : " + carlo);
			System.out.println("Serial Correlation : " + serial);*/
			if (entropy > 7.9 | compression < 5 | (chi < 90 & chi > 10) | 
					(mean < 130 & mean > 125) | (carlo < 3.18 & carlo > 3.10) |
					(serial < 0.05 & serial > -0.05)) {
				return false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private static double auditabilityLv2(File log) {

		/*
		 * 	tshark pour recup la partie data seulement de chaque packet
		 *  lire le fichier generer par tshark 
		 *  pour chaque ligne lancer ent dessus
		 */

		/* run tshark */ 
		String command = "tshark -r " + log.getName() + " -T fields -e data -w tmpfile"; 
		Runtime run  = Runtime.getRuntime(); 
		try {
			Process proc = run.exec(command);
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader br =	new BufferedReader(new FileReader(new File("tmpfile")));
			String line = br.readLine();
			int c = 0;
			int tot = 0;
			while (line != null) {
				//System.out.println(line);
				tot++;
				/* lance ent */
				command = "ent"; 
				run  = Runtime.getRuntime(); 

				proc = run.exec(command);
				OutputStream stdin = proc.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

				writer.write("xoxoxoxoxoxoxxoxoxoxoxoxo");
				writer.flush();
				writer.close();

				BufferedReader reader =	new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String l = null;
				while ( (l = reader.readLine()) != null) {
					builder.append(l);
					builder.append(System.getProperty("line.separator"));
				}
				String result = builder.toString();
				//System.out.println(result);
				double entropy = getEntropy(result);
				int compression = getCompression(result);
				double chi = getChiSquare(result);
				double mean = getMean(result);
				double carlo = getMonteCarlo(result);
				double serial = getSerialCorrelation(result);
				/*System.out.println("Entropy : " + entropy);
				System.out.println("Compression : " + compression);
				System.out.println("ChiSquare : " + chi);
				System.out.println("Mean : " + mean);
				System.out.println("Monte Carlo : " + carlo);
				System.out.println("Serial Correlation : " + serial);*/
				if (entropy > 7.9 | compression < 5 | (chi < 90 & chi > 10) | 
						(mean < 130 & mean > 125) | (carlo < 3.18 & carlo > 3.10) |
						(serial < 0.05 & serial > -0.05)) {
					c++;
				}
				line = br.readLine();
			}
			br.close();
			System.out.println(c + " encrypted message on " + tot + " at total");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("no idea");
			e.printStackTrace();
		}
		return 1;
	}

	private static double getEntropy(String res) {
		//System.out.println(res);
		return Double.valueOf(res.substring(10, res.indexOf(" bits")));
	}

	private static int getCompression(String res) {
		return Integer.valueOf(res.substring(res.indexOf("by ") + 3, res.indexOf(" percent.")));
	}

	private static double getChiSquare(String res) {
		if (res.contains("than ")) {
			return Double.valueOf(res.substring(res.indexOf("than ") + 5, res.indexOf(" percent of the times")));
		}
		else {
			return Double.valueOf(res.substring(res.indexOf("value ") + 6, res.indexOf(" percent of the times")));
		}
	}

	private static double getMean(String res) {
		return Double.valueOf(res.substring(res.indexOf("bytes is ") + 9, res.indexOf(" (127.5")));
	}

	private static double getMonteCarlo(String res) {
		return Double.valueOf(res.substring(res.indexOf("Pi is ") + 6, res.indexOf(" (error")));
	}

	private static double getSerialCorrelation(String res) {
		return Double.valueOf(res.substring(res.indexOf("coefficient is ") + 15, res.indexOf(" (totally")));
	}

	public static void main (String[] args) {
		File log = new File(args[0]);
		getAuditability(log);
	}
}
