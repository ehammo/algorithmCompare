import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

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
//		System.out.println("sensitivity: "+sensitivity);
//		System.out.println("specificity: "+specificity);
//		System.out.println("precision: "+precision);
//		System.out.println("FNR: "+FNR);
//		System.out.println("FPR: "+FPR);
//		System.out.println("F1: "+F1);
//		System.out.println("Accuracy: "+accuracy);
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
        out.print(","+specificity);
        out.print(","+sensitivity);
        out.print(","+precision);
        out.print(","+FPR);
        out.print(","+FNR);
        out.print(","+F1);
        out.print(","+accuracy+",");

//        System.out.println("specificityIC: "+getIntervaloConfianca(specificitySD));
//        System.out.println("sensitivityIC: "+getIntervaloConfianca(sensitivitySD));
//        System.out.println("precisionIC: "+getIntervaloConfianca(precisionSD));
//        System.out.println("FPRIC: "+getIntervaloConfianca(FPRSD));
//        System.out.println("FNRIC: "+getIntervaloConfianca(FNRSD));
//        System.out.println("F1IC: "+getIntervaloConfianca(F1SD));
//        System.out.println("accuracyIC: "+getIntervaloConfianca(accuracySD));
//        
//        out.print(""+getIntervaloConfianca(specificitySD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(sensitivitySD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(precisionSD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(FPRSD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(FNRSD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(F1SD));
//        out.print(",");
//        out.print(""+getIntervaloConfianca(accuracySD));
//        out.print(",");
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

	

}
