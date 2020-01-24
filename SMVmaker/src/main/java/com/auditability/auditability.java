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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

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

	private static boolean auditabilityLv1(File log) {
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
			System.out.println(result);
			double entropy = getEntropy(result);
			//int compression = getCompression(result);
			double chi = getChiSquare(result);
			double mean = getMean(result);
			//double carlo = getMonteCarlo(result);
			double serial = getSerialCorrelation(result);
			/*System.out.println("Entropy : " + entropy);
			System.out.println("Compression : " + compression);
			System.out.println("ChiSquare : " + chi);
			System.out.println("Mean : " + mean);
			System.out.println("Monte Carlo : " + carlo);
			System.out.println("Serial Correlation : " + serial);*/
			if (entropy > 7.9 | /*compression < 5 |*/ (chi < 90 & chi > 10) | 
					(mean < 130 & mean > 125) | /*(carlo < 3.18 & carlo > 3.10) |*/
					(serial < 0.05 & serial > -0.05)) {
				return false;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}


	/*
	 * 	tshark pour recup la partie data seulement de chaque packet
	 *  lire le fichier generer par tshark 
	 *  pour chaque ligne lancer ent dessus
	 */
	private static double auditabilityLv2(File log) {
		/* run tshark */ 
		String command = "tshark -r " + log.getName() + " -E separator=\":\" -E aggregator=\":\" -T fields -e tcp.payload -e data.data"; 
		Runtime run  = Runtime.getRuntime(); 
		String line= "";
		try {
			Process proc = run.exec(command);
			//proc.waitFor();
			BufferedReader br =	new BufferedReader(new InputStreamReader(proc.getInputStream()));
			//String 
			line = br.readLine();
			int c = 0;
			int tot = 0;
			while (line != null) {
				line = line.replaceAll(":", "");
				line = line.replaceAll("\"", "");
				byte[] bytes;
				bytes = Hex.decodeHex(line.toCharArray());
				line = new String(bytes);
				//System.out.println("line = " + line);
				if (line.isEmpty()) {
					line = br.readLine();
					continue;
				}
				tot++;
				/* run ent */
				command = "ent"; 
				run  = Runtime.getRuntime(); 

				proc = run.exec(command);
				OutputStream stdin = proc.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

				writer.write(line);
				writer.flush();
				writer.close();

				BufferedReader reader =	new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String l = null;
				while ( (l = reader.readLine()) != null) {
					builder.append(l);
					builder.append(System.getProperty("line.separator"));
				}
				reader.close();
				String result = builder.toString();
				//System.out.println(result);
				double entropy = getEntropy(result);
				//int compression = getCompression(result);
				double chi = getChiSquare(result);
				double mean = getMean(result);
				//double carlo = getMonteCarlo(result);
				double serial = getSerialCorrelation(result);
				/*System.out.println("Entropy : " + entropy);
				System.out.println("Compression : " + compression);
				System.out.println("ChiSquare : " + chi);
				System.out.println("Mean : " + mean);
				System.out.println("Monte Carlo : " + carlo);
				System.out.println("Serial Correlation : " + serial);*/
				if (entropy > 7.9 | /*compression < 5 |*/ (chi < 90 & chi > 10) | 
						(mean < 129 & mean > 126) | /*(carlo < 3.18 & carlo > 3.10) |*/
						(serial < 0.05 & serial > -0.05)) {
					//System.out.println(line);
					//System.out.println(tot);
					//System.out.println(result);
					c++;
				}
				line = br.readLine();
			}
			br.close();
			System.out.println(c + " encrypted message on " + tot + " at total");
			return 1 - (double) c/tot;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("no idea");
			e.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			//System.out.println("line = " + line);
			e.printStackTrace();
		} 
		return 2;
	}

	public static boolean runEnt(String value) {
		try {
			/* run ent */
			/*value = "23815578651917874f3a7e80df88cfccb7568a756abf203eeff9e8bcffaf331c";
				try {
					byte[] bytes;
					bytes = Hex.decodeHex(value.toCharArray());
					value = new String(bytes);
				} catch (DecoderException e) {
					e.printStackTrace();
				}
				
				//////////////////////*/
			String command = "ent"; 
			Runtime run  = Runtime.getRuntime(); 

			Process proc = run.exec(command);
			OutputStream stdin = proc.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

			writer.write(value);
			writer.flush();
			writer.close();

			BufferedReader reader =	new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String l = null;
			while ( (l = reader.readLine()) != null) {
				builder.append(l);
				builder.append(System.getProperty("line.separator"));
			}
			reader.close();
			String result = builder.toString();
			//System.out.println(result);
			double entropy = getEntropy(result);
			//int compression = getCompression(result);
			double chi = getChiSquare(result);
			double mean = getMean(result);
			//double carlo = getMonteCarlo(result);
			double serial = getSerialCorrelation(result);
			/*System.out.println("Entropy : " + entropy);
				System.out.println("Compression : " + compression);
				System.out.println("ChiSquare : " + chi);
				System.out.println("Mean : " + mean);
				System.out.println("Monte Carlo : " + carlo);
				System.out.println("Serial Correlation : " + serial);*/
			if (entropy > 7.8 | /*compression < 5 |*/ (chi < 90 & chi > 10) | 
					(mean < 129 & mean > 126) | /*(carlo < 3.18 & carlo > 3.10) |*/
					(serial < 0.08 & serial > -0.08)) {
				//System.out.println(value);
				//System.out.println(result);
				return true;
			}
			//System.out.println(value);
			//System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("no idea");
			e.printStackTrace();
		} 
		return false;
	}

	private static double getEntropy(String res) {
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
		if (res.substring(res.indexOf("Pi is ") + 6, res.indexOf(" (error")).equals("-nan")) {
			return 0;
		}
		return Double.valueOf(res.substring(res.indexOf("Pi is ") + 6, res.indexOf(" (error")));
	}

	private static double getSerialCorrelation(String res) {
		return Double.valueOf(res.substring(res.indexOf("coefficient is ") + 15, res.indexOf(" (totally")));
	}

	public static void main (String[] args) {
		File log = new File(args[0]);
		double lvl = getAuditability(log);
		System.out.println("The lvl of auditability = " + lvl);
	}
}
