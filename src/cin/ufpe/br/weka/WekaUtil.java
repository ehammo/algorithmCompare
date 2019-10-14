package cin.ufpe.br.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import cin.ufpe.br.compare.Retreinamento.ClassifierTypes;
import weka.core.SelectedTag;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class WekaUtil {

    private static Classifier doGridSearch(ClassifierTypes aClassifierType, Instances instances) throws Exception {
        GridSearch gs = new GridSearch();
        int requiredIndex = 6; // para acuracia
        SelectedTag st=new SelectedTag(requiredIndex , weka.classifiers.meta.GridSearch.TAGS_EVALUATION);
        gs.setEvaluation(st);
        gs.setLogFile(new File("GridSearchLog_"+aClassifierType.toString()+".txt"));
        if (aClassifierType.equals(ClassifierTypes.IBK)) {
            gs.setXProperty("KNN");
            gs.setXMin(0);
            gs.setXMax(300);
            gs.setYProperty("MeanSquared");
        } else if (aClassifierType.equals(ClassifierTypes.J48)) {
            gs.setXProperty("Unpruned");
            gs.setYProperty("ConfidenceFactor");
       } else if (aClassifierType.equals(ClassifierTypes.JRIP)) {
            gs.setXProperty("UsePruning");
            gs.setYProperty("Optimizations");
        } else if (aClassifierType.equals(ClassifierTypes.SMO_poly)) {
            gs.setXProperty("kernel.Exponent");
            gs.setYProperty("C");
        } else if (aClassifierType.equals(ClassifierTypes.SMO_rbf)) {
            gs.setXProperty("kernel.gamma");
            gs.setYProperty("C");
        } else if (aClassifierType.equals(ClassifierTypes.MLP_1hidden) ||
                aClassifierType.equals(ClassifierTypes.MLP_3hidden) ) {
            gs.setXProperty("LearningRate");
            gs.setYProperty("Momentum");
        } else if (aClassifierType.equals(ClassifierTypes.NAIVE_BAYES)) {
            gs.setXProperty("UseKernelEstimator");
            gs.setYProperty("UseSupervisedDiscretization");
        } else if (aClassifierType.equals(ClassifierTypes.RANDOMFOREST)) {
            gs.setXProperty("Seed");
            gs.setYProperty("NumIterations");// amount of trees
        }
        Classifier classifier = WekaUtil.getBaseClassifier(aClassifierType);
        gs.setClassifier(classifier);
        System.out.println("Building");
        gs.buildClassifier(instances);
        System.out.println("get best calassifier");
        return gs.getBestClassifier();
    }

    public static ArrayList<ClassifierWrapper> buildClassifiers(ClassifierTypes[] classifierType, Instances instances) throws Exception {
        ArrayList<ClassifierWrapper> classifiers = new ArrayList<>();
        for (ClassifierTypes aClassifierType : classifierType) {
            long tuningTrainingTimeStart = System.currentTimeMillis();
            Classifier classifier;
            boolean doGridSearch = false;
            if(doGridSearch){
                System.out.println("Start grid search for "+aClassifierType.toString());
                classifier = doGridSearch(aClassifierType,instances);
                System.out.println("grid search end");
            }else{
                classifier = WekaUtil.getBaseClassifier(aClassifierType);
//                System.out.println("Start training for "+aClassifierType.toString());
//                classifier.buildClassifier(instances);
                System.out.println("training end");
            }
            ClassifierWrapper classifierWrapper = new ClassifierWrapper(classifier);
            classifierWrapper.tuningAndTrainingTime = System.currentTimeMillis() - tuningTrainingTimeStart;
            classifierWrapper.simple_name = aClassifierType.name();
            classifierWrapper.name = aClassifierType.name();
            classifiers.add(classifierWrapper);

        }
        return classifiers;
    }

    public static Evaluation crossValidateModel(Classifier classifier, Instances instances, int folds, Random random) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier, instances, folds, random);
        return evaluation;
    }

    private static Classifier getBaseClassifier(ClassifierTypes desiredModel) {
        Classifier classifierModel = null;
        if (desiredModel.equals(ClassifierTypes.NAIVE_BAYES)) {
            NaiveBayes naiveBayes = new NaiveBayes();
            naiveBayes.setUseKernelEstimator(true);
            classifierModel = naiveBayes;
        } else if (desiredModel.equals(ClassifierTypes.J48)) {
            classifierModel = new J48();
        } else if (desiredModel.equals(ClassifierTypes.IBK)) {
            classifierModel = new IBk();
        } else if (desiredModel.equals(ClassifierTypes.JRIP)) {
            classifierModel = new JRip();
        } else if(desiredModel.equals(ClassifierTypes.ZERO_R)){
        	classifierModel = new ZeroR();
        } else if(desiredModel.equals(ClassifierTypes.SMO_rbf)){
            classifierModel = new SMO();
            RBFKernel rbfKernel = new RBFKernel();
            ((SMO)classifierModel).setKernel(rbfKernel);
        } else if(desiredModel.equals(ClassifierTypes.SMO_poly)){
            classifierModel = new SMO();
            PolyKernel polyKernel = new PolyKernel();
            ((SMO)classifierModel).setKernel(polyKernel);
        } else if (desiredModel.equals(ClassifierTypes.RANDOMFOREST)){
             classifierModel = new RandomForest();
        } else if (desiredModel.equals(ClassifierTypes.MLP_1hidden)){
            classifierModel = new MultilayerPerceptron();
        } else if (desiredModel.equals(ClassifierTypes.MLP_3hidden)){
            classifierModel = new MultilayerPerceptron();
            ((MultilayerPerceptron) classifierModel).setHiddenLayers("a, a, a");
        } else {
            System.err.println("Undefined model " + desiredModel);
        }
        return classifierModel;
    }
}
