package cin.ufpe.br.compare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cin.ufpe.br.weka.ClassifierWrapper;
import cin.ufpe.br.weka.WekaUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Retreinamento {


    public enum ClassifierTypes {
        IBK,J48, JRIP, ZERO_R, NAIVE_BAYES, SMO, DEEP;
    }

    private static final int SEEDS = 30;

//    private static ClassifierTypes[] classifierType = {
//	        ClassifierTypes.IBK,
//            ClassifierTypes.J48,
//            ClassifierTypes.JRIP,
//            ClassifierTypes.ZERO_R,
//            ClassifierTypes.NAIVE_BAYES,
//            ClassifierTypes.SMO };

    private static ClassifierTypes[] classifierType = {
            ClassifierTypes.IBK,
            ClassifierTypes.J48,
            ClassifierTypes.JRIP,
            ClassifierTypes.NAIVE_BAYES,
            ClassifierTypes.SMO };

    private static Resultado media(MatrizConfusao[] resultados){
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

	public static ArrayList<File> findModels(){
        ArrayList<File> files = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir"));
        File fList[] = folder.listFiles();
        for (int i = 0; i < fList.length; i++) {
            String pes = fList[i].getName();
            if (pes.endsWith(".model")) {
                files.add(fList[i].getAbsoluteFile());
            }
        }
        return files;
    }

	private static void clearModels(){
        ArrayList<File> files = findModels();
        for(File file : files) {
            file.delete();
        }
    }

    public static HashMap<String, byte[]> getModels() throws IOException {
        ArrayList<File> files = findModels();
        HashMap<String, byte[]> models = new HashMap<>();
        for(File file: files){
            byte[] fileContent = Files.readAllBytes(file.toPath());
            models.put(file.getName(), fileContent);
        }
        return models;
    }

	private static void saveBestClassifier(ArrayList<ClassifierWrapper> classifierWrappers) {
        ClassifierWrapper bestWrapper = new ClassifierWrapper();
        for (ClassifierWrapper wrapper : classifierWrappers) {
            if(bestWrapper.accuracy < wrapper.accuracy) {
                bestWrapper = wrapper;
            }
        }
        bestWrapper.saveToFile();
    }

    public static boolean retreinamento() {
        try {
            DataSource source = new DataSource("database_ic.csv");
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes()-1);
            Resultado.clearCSVS();
            ArrayList<ClassifierWrapper> classifiers = WekaUtil.buildClassifiers(classifierType, data);
            String lastClassifierType = "";
            ArrayList<ClassifierWrapper> sameTypeClassifiers = new ArrayList<>();
            clearModels();
            for (ClassifierWrapper wrapper : classifiers) {
                Class<? extends Classifier> classifierClass = wrapper.classifier.getClass();
                if (!lastClassifierType.equals(classifierClass.getName())) {
                    saveBestClassifier(sameTypeClassifiers);
                    sameTypeClassifiers.clear();
                    lastClassifierType = classifierClass.getName();
                    System.out.println("start: "+lastClassifierType);
                }
                MatrizConfusao[] confusao = new MatrizConfusao[SEEDS];
                //This for is used to change the seed used on crossValidation
                for (int j = 1; j <= SEEDS; j++) {
                    // Evaluate classifier using 10-fold cross-validation
                    Evaluation evaluation = WekaUtil.crossValidateModel(wrapper.classifier, data, 10, new Random(j));
                    double[][] matrix = evaluation.confusionMatrix();
                    double precision, F1, TN, FN, FP, TP = 0;
                    //check the if the matrix is Transposed or not
                    if (data.classAttribute().value(0).equals("Nao")) {
                        TN = matrix[0][0];
                        FN = matrix[1][0];
                        FP = matrix[0][1];
                        TP = matrix[1][1];
                        precision = evaluation.precision(1);
                        F1 = evaluation.fMeasure(1);
                    } else {
                        TP = matrix[0][0];
                        FP = matrix[1][0];
                        FN = matrix[0][1];
                        TN = matrix[1][1];
                        precision = evaluation.precision(0);
                        F1 = evaluation.fMeasure(0);
                    }

                    confusao[j - 1] = new MatrizConfusao(TN, FN, FP, TP, precision, F1);
                    Resultado result = confusao[j - 1].getResult();
                    result.alg = wrapper.name;
                    result.toCSV(false);
                }
                //Calculate average
                Resultado resultado = media(confusao);
                wrapper.accuracy = resultado.accuracy;
    //			resultado.sd(confusao);
                resultado.alg = wrapper.name;
                resultado.toCSV(true);
                sameTypeClassifiers.add(wrapper);
            }
            saveBestClassifier(sameTypeClassifiers);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

	public static void main(String[] args) {
        retreinamento();
	}
}
