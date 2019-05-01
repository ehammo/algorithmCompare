package cin.ufpe.br.compare;

public class MatrizConfusao {

	private double TP;
	private double TN;
	private double FP;
	private double FN;
	private double F1;
	private double precision;


	public MatrizConfusao(double TN,double  FN,double  FP,double  TP, double precision, double F1){
		this.FN=FN;
		this.TP=TP;
		this.TN=TN;
		this.FP=FP;
		this.F1 = F1;
		this.precision = precision;
	}

	public double getSpecificity(){
		if(TN+FP>0)	{
			return TN/(TN+FP);
		}else{
			return 0;
		}
	}

	public double getSensitivity(){
		if((TP+FN)>0){
			return TP/(TP+FN);
		}else{
			return 0;
		}
	}

	public double getPrecision(){
		return precision;
	}

	public double getF1(){
		return F1;
	}

	public double getFPR(){
		return 1-getSpecificity();
	}

	public double getFNR(){
		return 1-getSensitivity();
	}

	public double getAccuracy(){
		return (TP+TN)/(TP+TN+FP+FN);
	}

	public Resultado getResult(){
		Resultado result = new Resultado();
		result.accuracy = getAccuracy();
		result.F1 = getF1();
		result.FNR = getFNR();
		result.FPR = getFPR();
		result.precision = getPrecision();
		result.sensitivity = getSensitivity();
		result.specificity = getSpecificity();


		return result;
	}

}
