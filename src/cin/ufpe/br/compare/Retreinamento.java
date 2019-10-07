package cin.ufpe.br.compare;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cin.ufpe.br.statistic.StatisticTests;
import cin.ufpe.br.weka.ClassifierWrapper;
import cin.ufpe.br.weka.WekaUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Retreinamento {


    public enum ClassifierTypes {
        IBK, J48, JRIP, ZERO_R,
        NAIVE_BAYES, SMO_poly, SMO_rbf,
        MLP_1hidden, MLP_3hidden, RANDOMFOREST;
    }

    private static final int SEEDS = 30;

    private static ClassifierTypes[] classifierType = {
            ClassifierTypes.IBK,
            ClassifierTypes.RANDOMFOREST,
            ClassifierTypes.MLP_1hidden,
            ClassifierTypes.MLP_3hidden,
//            ClassifierTypes.NAIVE_BAYES,
//            ClassifierTypes.J48,
//            ClassifierTypes.JRIP,
            ClassifierTypes.SMO_poly,
            ClassifierTypes.SMO_rbf
    };

    private static Resultado media(MatrizConfusao[] resultados){
		double sensitivity = 0,specificity = 0,precision = 0,FPR = 0,FNR = 0,F1 = 0,accuracy=0;
        for (MatrizConfusao matrizConfusao : resultados) {
            if (matrizConfusao != null) {
                sensitivity += matrizConfusao.getSensitivity();
                specificity += matrizConfusao.getSpecificity();
                precision += matrizConfusao.getPrecision();
                FPR += matrizConfusao.getFPR();
                FNR += matrizConfusao.getFNR();
                F1 += matrizConfusao.getF1();
                accuracy += matrizConfusao.getAccuracy();
            }
        }
		Resultado resultado = new Resultado();
		resultado.sensitivity = (sensitivity/resultados.length);
		resultado.specificity = (specificity/resultados.length);
		resultado.precision = (precision/resultados.length);
		resultado.FPR = (FPR/resultados.length);
		resultado.FNR = (FNR/resultados.length);
		resultado.F1 = (F1/resultados.length);
		resultado.accuracy = (accuracy/resultados.length);
		return resultado;
	}

	private static ArrayList<File> findModels(){
        ArrayList<File> files = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir"));
        File[] fList = folder.listFiles();
        if (fList != null) {
            for (File file : fList) {
                String pes = file.getName();
                if (pes.endsWith(".model")) {
                    files.add(file.getAbsoluteFile());
                }
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
            StatisticTests statisticTests = new StatisticTests();
            statisticTests.algorithmsRank();
            DataSource source = new DataSource("creditcard.csv");
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes()-1);
            Resultado.clearCSVS();
            System.out.println("Start training");
            ArrayList<ClassifierWrapper> classifiers = WekaUtil.buildClassifiers(classifierType, data);
            System.out.println("End training");
            String lastClassifierType = "";
            ArrayList<ClassifierWrapper> sameTypeClassifiers = new ArrayList<>();
            clearModels();
            for (ClassifierWrapper wrapper : classifiers) {
                Class<? extends Classifier> classifierClass = wrapper.classifier.getClass();
                if (!lastClassifierType.equals(classifierClass.getName())) {
                    saveBestClassifier(sameTypeClassifiers);
                    sameTypeClassifiers.clear();
                    lastClassifierType = classifierClass.getName();
                    System.out.println("start testing "+lastClassifierType);
                }
                MatrizConfusao[] confusao = new MatrizConfusao[SEEDS];

                long testTime = 0;
                //This for is used to change the seed used on crossValidation
                for (int j = 1; j <= SEEDS; j++) {
                    long testTimeStart = System.currentTimeMillis();
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
                    Resultado resultado = confusao[j - 1].getResult();
                    resultado.alg = wrapper.name;
                    resultado.tuningTrainingTime = wrapper.tuningAndTrainingTime;
                    testTime += System.currentTimeMillis() - testTimeStart;
                    statisticTests.add(resultado);
                    resultado.toCSV(false);
                }
                //Calculate average
                Resultado resultado = media(confusao);
                wrapper.accuracy = resultado.accuracy;
                resultado.alg = wrapper.name;
                resultado.testTime = testTime/30;
                statisticTests.addTestTime(resultado);
                resultado.toCSV(true);
                sameTypeClassifiers.add(wrapper);
            }
            saveBestClassifier(sameTypeClassifiers);
            statisticTests.algorithmsRank();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

	public static void main(String[] args) {
        retreinamento();
	}
}
