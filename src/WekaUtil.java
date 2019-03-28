import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gio on 11/05/2017.
 */
public class WekaUtil {

    public static Classifier buildClassifier(Principal.ClassifierTypes classifierType, Instances instances) throws Exception {
        Classifier classifier = getBaseClassifier(classifierType);
        classifier.buildClassifier(instances);
        return classifier;
    }

    public static Evaluation crossValidateModel(Classifier classifier, Instances instances, int folds, Random random) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier, instances, folds, random);
        return evaluation;
    }

    public static ArrayList<Attribute> buildWekaAttributes(List<String> features) throws IOException {
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


    public static Classifier getBaseClassifier(Principal.ClassifierTypes desiredModel) {
        Classifier classifierModel = null;
        if (desiredModel.equals(Principal.ClassifierTypes.NAIVE_BAYES)) {
            classifierModel = new NaiveBayes();
        } else if (desiredModel.equals(Principal.ClassifierTypes.J48)) {
            classifierModel = new J48();
        } else if (desiredModel.equals(Principal.ClassifierTypes.IBK)) {
            classifierModel = new IBk();
        } else if (desiredModel.equals(Principal.ClassifierTypes.JRIP)) {
            classifierModel = new JRip();
        } else if(desiredModel.equals(Principal.ClassifierTypes.ZERO_R)){
        	classifierModel = new ZeroR();
        } else if(desiredModel.equals(Principal.ClassifierTypes.SMO)){
            classifierModel = new SMO();
        } else {
            System.err.println("Undefined model " + desiredModel);
        }
        return classifierModel;
    }
}
