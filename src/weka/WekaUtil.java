package weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import weka.core.Attribute;
import weka.core.Instances;

import compare.Principal.ClassifierTypes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gio on 11/05/2017.
 */
public class WekaUtil {
    private static int BEST_K = 5;
    private static boolean pruned(int k) {
        return k == 1;
    }
    private static boolean knowsBestK() {
        return BEST_K != -1;
    }
    private static String getKernel(int k) {
        if (k == 1) {
            return "_rbf";
        } else if (k == 2){
            return  "_poly2";
        } else {
            return "_default";
        }
    }
    private static String getAlgName(ClassifierTypes classifier, int k) {
        if (classifier.equals(ClassifierTypes.J48) || classifier.equals(ClassifierTypes.JRIP)) {
            if(pruned(k)){
                return classifier + "_pruned";
            } else {
                return classifier + "_unpruned";
            }
        } else if (classifier.equals(ClassifierTypes.SMO)) {
            return classifier + getKernel(k);
        } else if(classifier.equals(ClassifierTypes.IBK)) {
            return !knowsBestK() ? classifier + "_k_=_"+k : classifier + "_k_=_"+BEST_K;
        }
        return classifier.toString();
    }

    public static ArrayList<ClassifierWrapper> buildClassifiers(ClassifierTypes[] classifierType, Instances instances) throws Exception {
        ArrayList<ClassifierWrapper> classifiers = new ArrayList<>();
        for (ClassifierTypes aClassifierType : classifierType) {
            // This is used for IBK model to find best k;
            // When IBK is deactivated we still use this to prune or not our tree base models
            int max_k = 1;
            if (ClassifierTypes.IBK.equals(aClassifierType)) {
                if (!knowsBestK()) max_k = 30;
            } else if (ClassifierTypes.J48.equals(aClassifierType) ||
                ClassifierTypes.JRIP.equals(aClassifierType)) {
                max_k = 2;
            } else if (ClassifierTypes.SMO.equals(aClassifierType)) {
                max_k = 3;
            }
            for (int k = 1; k <= max_k; k++) {
                Classifier classifier = WekaUtil.getBaseClassifier(aClassifierType);
                if (aClassifierType.equals(ClassifierTypes.IBK) && knowsBestK()) {
                    ((IBk) classifier).setKNN(BEST_K);
                } else if (aClassifierType.equals(ClassifierTypes.IBK) && !knowsBestK()) {
                    ((IBk) classifier).setKNN(k);
                } else if (aClassifierType.equals(ClassifierTypes.J48) && !pruned(k)) {
                    ((J48) classifier).setUnpruned(true);
                } else if (aClassifierType.equals(ClassifierTypes.J48) && pruned(k)) {
                    ((J48) classifier).setUnpruned(false);
                } else if (aClassifierType.equals(ClassifierTypes.JRIP) && !pruned(k)) {
                    ((JRip) classifier).setUsePruning(false);
                } else if (aClassifierType.equals(ClassifierTypes.JRIP) && pruned(k)) {
                    ((JRip) classifier).setUsePruning(true);
                } else if (aClassifierType.equals(ClassifierTypes.SMO) && k == 1) {
                    RBFKernel rbfKernel = new RBFKernel();
                    ((SMO) classifier).setKernel(rbfKernel);
                } else if (aClassifierType.equals(ClassifierTypes.SMO) && k == 2) {
                    PolyKernel polyKernel = new PolyKernel();
                    polyKernel.setExponent(2);
                    ((SMO) classifier).setKernel(polyKernel);
                }
                classifier.buildClassifier(instances);
                ClassifierWrapper classifierWrapper = new ClassifierWrapper(classifier);
                classifierWrapper.name = getAlgName(aClassifierType, k);
                classifiers.add(classifierWrapper);
            }
        }
        return classifiers;
    }

    public static Evaluation crossValidateModel(Classifier classifier, Instances instances, int folds, Random random) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier, instances, folds, random);
        return evaluation;
    }

    public static ArrayList<Attribute> buildWekaAttributes(List<String> features) {
        // Declare the class attribute along with its values
        ArrayList<String> classValues = new ArrayList<>(2);
        classValues.add("positive");
        classValues.add("negative");
        classValues.add("?");
        Attribute classAttribute = new Attribute("_class_", classValues);

        // Declare the feature vector
        ArrayList<Attribute> wekaAttributes = new ArrayList<>(features.size() + 1);
        for (String feature : features) {
            wekaAttributes.add(new Attribute(feature));
        }
        wekaAttributes.add(classAttribute);

        return wekaAttributes;
    }


    private static Classifier getBaseClassifier(ClassifierTypes desiredModel) {
        Classifier classifierModel = null;
        if (desiredModel.equals(ClassifierTypes.NAIVE_BAYES)) {
            classifierModel = new NaiveBayes();
        } else if (desiredModel.equals(ClassifierTypes.J48)) {
            classifierModel = new J48();
        } else if (desiredModel.equals(ClassifierTypes.IBK)) {
            classifierModel = new IBk();
        } else if (desiredModel.equals(ClassifierTypes.JRIP)) {
            classifierModel = new JRip();
        } else if(desiredModel.equals(ClassifierTypes.ZERO_R)){
        	classifierModel = new ZeroR();
        } else if(desiredModel.equals(ClassifierTypes.SMO)){
            classifierModel = new SMO();
        } else if (desiredModel.equals(ClassifierTypes.DEEP)){
            // classifierModel = new Dl4jMlpClassifier();
        } else {
            System.err.println("Undefined model " + desiredModel);
        }
        return classifierModel;
    }
}
