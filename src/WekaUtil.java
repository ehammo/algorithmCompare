import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Gio on 11/05/2017.
 */
public class WekaUtil {

    public static Classifier buildClassifier(String classifierType, Instances instances) throws Exception {
        long start = System.nanoTime();
        Classifier classifier = getBaseClassifier(classifierType);
        classifier.buildClassifier(instances);
        long end = System.nanoTime();

  //      System.out.printf("Took %f seconds to build %s\n", (end - start) / 1e9, classifierType.toString());
        return classifier;
    }

    public static Evaluation crossValidateModel(Classifier classifier, Instances instances, int folds, Random random) throws Exception {
        long start = System.nanoTime();
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier, instances, folds, random);
        long end = System.nanoTime();
    //    System.out.printf("Took %f seconds to cross-validate %s with %d folds\n", (end - start) / 1e9, classifier.getClass().getName(), folds);
   //     System.out.println("Number of attributes: " + instances.numAttributes());

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


    public static Classifier getBaseClassifier(String desiredModel) {
        Classifier classifierModel = null;
        if (desiredModel.equals("naive")) {
            classifierModel = new NaiveBayes();
        } else if (desiredModel.equals("J48")) {
            classifierModel = new J48();
        } else if (desiredModel.equals("IBK")) {
            classifierModel = new IBk();
        } else if (desiredModel.equals("JRIP")) {
            classifierModel = new JRip();
        } else if(desiredModel.equals("zeroR")){
        	classifierModel = new ZeroR();
        }else if(desiredModel.equals("OneR")){
        	classifierModel = new OneR();
        }else {
            System.err.println("Undefined model " + desiredModel);
        }
        return classifierModel;
    }
}
