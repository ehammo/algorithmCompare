package cin.ufpe.br.compare;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Resultado {
	public double sensitivity = 0,specificity = 0,precision = 0,FPR = 0,FNR = 0,F1 = 0,accuracy=0;
	public double sensitivitySD = 0,specificitySD = 0,precisionSD = 0,FPRSD = 0,FNRSD = 0,F1SD = 0,accuracySD=0;
	public double instances=0;

	public long tuningTrainingTime, testTime;

	public String alg;
	public Resultado(){};
	public Resultado(String[] results) {
		alg = results[0].trim();
		specificity = Double.parseDouble(results[1]);
		sensitivity = Double.parseDouble(results[2]);
		precision = Double.parseDouble(results[3]);
		FPR = Double.parseDouble(results[4]);
		FNR = Double.parseDouble(results[5]);
		F1 = Double.parseDouble(results[6]);
		accuracy = Double.parseDouble(results[7]);
	}

	public void sd(MatrizConfusao[] resultados){
		double variancia1=0.0;
		double variancia2=0.0;
		double variancia3=0.0;
		double variancia4=0.0;
		double variancia5=0.0;
		double variancia6=0.0;
		double variancia7=0.0;
		instances = resultados.length;
		for(int i=0;i<resultados.length;i++){
			variancia1+=Math.pow(sensitivity - resultados[i].getSensitivity(), 2);
			variancia2+=Math.pow(specificity - resultados[i].getSpecificity(), 2);
			variancia3+=Math.pow(precision - resultados[i].getPrecision(), 2);
			variancia4+=Math.pow(FPR - resultados[i].getFPR(), 2);
			variancia5+=Math.pow(FNR - resultados[i].getFNR(), 2);
			variancia6+=Math.pow(F1 - resultados[i].getF1(), 2);
			variancia7+=Math.pow(accuracy - resultados[i].getAccuracy(), 2);
		}
		sensitivitySD = Math.sqrt(variancia1/(resultados.length-1));
		specificitySD = Math.sqrt(variancia2/(resultados.length-1));
		precisionSD = Math.sqrt(variancia3/(resultados.length-1));
		FPRSD = Math.sqrt(variancia4/(resultados.length-1));
		FNRSD = Math.sqrt(variancia5/(resultados.length-1));
		F1SD = Math.sqrt(variancia6/(resultados.length-1));
		accuracySD = Math.sqrt(variancia7/(resultados.length-1));
	}

	public double getIntervaloConfianca(double sd){
		return 1.96*sd/Math.sqrt(instances);
	}

	public void toCSV(boolean avg) throws IOException{
		if (avg) {
			toCSV("result.csv");
		} else {
			toCSV("all.csv");
		}
	}

	private void toCSV(String file) throws IOException{
		FileWriter fw = new FileWriter(file, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out  = new PrintWriter(bw);

		out.print(alg + " , ");
		out.print(specificity + " , ");
		out.print(sensitivity + " , ");
		out.print(precision + " , ");
		out.print(FPR + " , ");
		out.print(FNR + " , ");
		out.print(F1 + " , ");
		out.print(accuracy);
		out.println();
		out.flush();
		out.close();
		if(!file.equals("all.csv")) System.out.println(String.format("Done %s", file));
	}

	public static void clearCSVS() throws IOException {
		FileWriter fw = new FileWriter("result.csv", false);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out  = new PrintWriter(bw);
		out.print("");
		out.flush();
		out.close();

		fw = new FileWriter("all.csv", false);
		bw = new BufferedWriter(fw);
		out  = new PrintWriter(bw);
		out.print("");
		out.flush();
		out.close();
	}
}
