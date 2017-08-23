import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Principal {

//	static String[] classifierType = {"IBK","J48","zeroR","JRIP","naive"};
	static String[] classifierType = {"JRIP", "J48"};
//	static String[] classifierType = {"IBK"};

	public static Resultado media(MatrizConfusao[] resultados){
		double sensitivity = 0,specificity = 0,precision = 0,FPR = 0,FNR = 0,F1 = 0,accuracy=0;
		for(int i=0;i<resultados.length;i++){
			if(resultados[i]!=null){
				sensitivity+=resultados[i].getSensitivity();
				specificity+=resultados[i].getSpecificity();
				precision+=resultados[i].getPrecision();
				FPR+=resultados[i].getFPR();
				FNR+=resultados[i].getFNR();
				F1+=resultados[i].getF1();
				accuracy+=resultados[i].getAccuracy();
			}
		}
		Resultado resultado = new Resultado();
		resultado.sensitivity = (sensitivity/=resultados.length);
		resultado.specificity = (specificity/=resultados.length);
		resultado.precision = (precision/=resultados.length);
		resultado.FPR = (FPR/=resultados.length);
		resultado.FNR = (FNR/=resultados.length);
		resultado.F1 = (F1/=resultados.length);
		resultado.accuracy = (accuracy/=resultados.length);
		return resultado;
	}

	public static void main(String[] args) throws Exception {
		MatrizConfusao[] confusao = new MatrizConfusao[30];
		DataSource source = new DataSource("database_ic.csv");
		double TN,TP,FN,FP;
		Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);

		for(int i=0;i<classifierType.length;i++){
			for( int k=1;k<=2;k++){
				System.out.println(classifierType[i]);

				//This for is used to change the seed used on crossValidation
				for(int j=1;j<=30;j++){

					//	System.out.println(classifierType[i]+": "+j);
					long start, end;

					// Build classifier
					Classifier classifier = WekaUtil.buildClassifier(classifierType[i], data);
					if(classifierType[i].equals("IBK")) ((IBk) classifier).setKNN(4);	
					if(classifierType[i].equals("J48")&&k==2) ((J48) classifier).setUnpruned(true);
					if(classifierType[i].equals("J48")&&k==1) ((J48) classifier).setUnpruned(false);
					if(classifierType[i].equals("JRIP")&&k==2) ((JRip) classifier).setUsePruning(false);
					if(classifierType[i].equals("JRIP")&&k==1) ((JRip) classifier).setUsePruning(true);
					// Evaluate classifier using 10-fold cross-validation
					// Realize that 'j' is the seed used
					Evaluation evaluation = WekaUtil.crossValidateModel(classifier, data, 10, new Random(j));
					double[][] matrix = evaluation.confusionMatrix();
					double precision = 0;
					double F1 =0;
					//check the if the matrix is Transposed or not	
					if(data.classAttribute().value(0).equals("Nao")){
//						System.out.println("a=nao");
						TN = matrix[0][0];
						FN = matrix[1][0];
						FP = matrix[0][1];
						TP = matrix[1][1];
						precision = evaluation.precision(1);
						F1 = evaluation.fMeasure(1);
					}else{
//						System.out.println("a=sim");
						TP = matrix[0][0];
						FP = matrix[1][0];
						FN = matrix[0][1];
						TN = matrix[1][1];
						precision = evaluation.precision(0);
						F1 = evaluation.fMeasure(0);
					}

					confusao[j-1] = new MatrizConfusao(TN, FN, FP, TP, precision,F1);
					Resultado result = confusao[j-1].getResult();
					result.alg = classifierType[i];
					result.toCSV(false);
				}
				
				//Calculate average
				Resultado resultado = media(confusao);
				//				resultado.sd(confusao);
				resultado.alg = classifierType[i];
				resultado.toCSV(true);
				

			}

		}
	}
}
