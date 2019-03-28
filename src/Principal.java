import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Principal {

    public enum ClassifierTypes {
        IBK,J48, JRIP, ZERO_R, NAIVE_BAYES, SMO;

    }


    static int BEST_K = 4;
    static final int SEEDS = 30;
	static ClassifierTypes[] classifierType = {
	        ClassifierTypes.IBK,
            ClassifierTypes.J48,
            ClassifierTypes.JRIP,
            ClassifierTypes.ZERO_R,
            ClassifierTypes.NAIVE_BAYES,
            ClassifierTypes.SMO };
//	static ClassifierTypes[] classifierType = {ClassifierTypes.SMO, ClassifierTypes.JRIP, ClassifierTypes.J48};
//	static ClassifierTypes[] classifierType = {ClassifierTypes.SMO};

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

	public static boolean pruned(int k) {
        return k == 1;
    }

    public static String getAlgName(ClassifierTypes classifier, int k) {
	    if (classifier.equals(ClassifierTypes.J48) || classifier.equals(ClassifierTypes.JRIP)) {
	        if(pruned(k)){
	            return classifier + " pruned";
            } else {
                return classifier + " unpruned";
            }
        } else if(classifier.equals(ClassifierTypes.IBK)) {
	        return classifier + " k = "+k;
        }
        return classifier.toString();
    }

    public static boolean knowsBestK() {
	    return BEST_K != -1;
    }

	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource("database_ic.csv");
        Instances data = source.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
        Resultado.clearCSVS();
		for(int i=0;i<classifierType.length;i++){
			// This is used for IBK model to find best k;
            // When IBK is deactivated we still use this to prune or not our tree base models
            int max_k = 1;
            if (classifierType[i].equals(ClassifierTypes.IBK)) {
                if (!knowsBestK()) max_k = 30;
            } else if (classifierType[i].equals(ClassifierTypes.J48) ||
                    classifierType[i].equals(ClassifierTypes.JRIP)) {
                max_k = 2;
            }
			for( int k = 1; k <= max_k; k++){
				System.out.println(classifierType[i]);
                MatrizConfusao[] confusao = new MatrizConfusao[SEEDS];
                double TN,TP,FN,FP;
                String algName = getAlgName(classifierType[i], k);
                //This for is used to change the seed used on crossValidation
				for (int j=1;j<=SEEDS;j++) {

					//	System.out.println(classifierType[i]+": "+j);
					long start, end;

					// Build classifier
					Classifier classifier = WekaUtil.getBaseClassifier(classifierType[i]);
                    if (classifierType[i].equals(ClassifierTypes.IBK) && knowsBestK()) {
                        ((IBk) classifier).setKNN(BEST_K);
                    } else if (classifierType[i].equals(ClassifierTypes.IBK) && !knowsBestK()) {
                        ((IBk) classifier).setKNN(k);
                    } else if (classifierType[i].equals(ClassifierTypes.J48) && !pruned(k)) {
                        ((J48) classifier).setUnpruned(true);
                    } else if  (classifierType[i].equals(ClassifierTypes.J48) && pruned(k)) {
                        ((J48) classifier).setUnpruned(false);
                    } else if (classifierType[i].equals(ClassifierTypes.JRIP) && !pruned(k)) {
                        ((JRip) classifier).setUsePruning(false);
                    } else if (classifierType[i].equals(ClassifierTypes.JRIP) && pruned(k)) {
                        ((JRip) classifier).setUsePruning(true);
                    }
                    classifier.buildClassifier(data);
					// Evaluate classifier using 10-fold cross-validation
					// Realize that 'j' is the seed used
					Evaluation evaluation = WekaUtil.crossValidateModel(classifier, data, 10, new Random(j));
					double[][] matrix = evaluation.confusionMatrix();
					double precision, F1 = 0;
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
					result.alg = algName;
					result.toCSV(false);
				}
				
				//Calculate average
				Resultado resultado = media(confusao);
				//				resultado.sd(confusao);
				resultado.alg = algName;
				resultado.toCSV(true);
			}

		}
	}
}
