package weka;

import weka.classifiers.Classifier;
import weka.core.Debug;

public class ClassifierWrapper {

    public Classifier classifier;
    public String name;
    public double accuracy = 0;

    public ClassifierWrapper(){}

    public ClassifierWrapper(Classifier classifier) {
        this.classifier = classifier;
        this.name = classifier.toString();
    }

    public void saveToFile() {
        if (this.classifier != null && this.name != null) Debug.saveToFile(this.name+".model", this.classifier);
    }

}
