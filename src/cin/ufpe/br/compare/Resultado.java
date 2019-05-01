package cin.ufpe.br.compare;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Resultado {
	public double sensitivity = 0,specificity = 0,precision = 0,FPR = 0,FNR = 0,F1 = 0,accuracy=0;
	public double sensitivitySD = 0,specificitySD = 0,precisionSD = 0,FPRSD = 0,FNRSD = 0,F1SD = 0,accuracySD=0;
	public double instances=0;

	public String alg;

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

	public void toALL_CSV() throws IOException{
		FileWriter fw = new FileWriter("all.csv", true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    PrintWriter out  = new PrintWriter(bw);
	    out.print(alg);
        out.print(","+specificity);
        out.print(","+sensitivity);
        out.print(","+precision);
        out.print(","+FPR);
        out.print(","+FNR);
        out.print(","+F1);
        out.print(","+accuracy);
        out.println();
        out.flush();
        out.close();
//        System.out.println("done all.csv!");
	}

	public void toAVG_CSV() throws IOException{
		FileWriter fw = new FileWriter("result.csv", true);
	    BufferedWriter bw = new BufferedWriter(fw);
	    PrintWriter out  = new PrintWriter(bw);
	    out.print(alg);
        out.print(" , "+specificity);
        out.print(" , "+sensitivity);
        out.print(" , "+precision);
        out.print(" , "+FPR);
        out.print(" , "+FNR);
        out.print(" , "+F1);
        out.print(" , "+accuracy+" , ");
        out.println();
        out.flush();
        out.close();
        System.out.println("done avg!");
	}

	public void toCSV(boolean avg) throws IOException{
		if(avg){
			toAVG_CSV();
		}else{
			toALL_CSV();
		}
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
