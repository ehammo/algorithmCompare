package cin.ufpe.br.statistic;

import cin.ufpe.br.compare.Resultado;
import javanpst.data.structures.dataTable.DataTable;
import javanpst.tests.multiple.friedmanTest.FriedmanTest;
import javanpst.tests.oneSample.wilcoxonTest.WilcoxonTest;
//import javanpst.tests.multiple.friedmanTest.FriedmanTest;
import java.io.IOException;
import java.util.*;

public class StatisticTests {

    LinkedHashMap<String,ArrayList<Double>> wilcoxonData = new LinkedHashMap<>();
    ArrayList<String> algnames = new ArrayList<>();
    DataTable data = new DataTable();
    int rows, cols= 0;


    public StatisticTests(){}

    public void add(Resultado resultado){
        ArrayList<Double> currentData = wilcoxonData.get(resultado.alg);
        if (currentData == null) {
            algnames.add(cols, resultado.alg);
            currentData = new ArrayList<>();
            cols++;
            rows = 0;
        }
        currentData.add(resultado.accuracy);
        wilcoxonData.put(resultado.alg,currentData);
        rows++;
    }

    public static double[] toPrimitive(ArrayList<Double> arrayList) {
        double[] target = new double[arrayList.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = arrayList.get(i);
        }
        return target;
    }

    private void friedmanToData() {
        Integer lastUsedCol = 0;
        data.setDimensions(rows,cols);
        for(ArrayList<Double> array : wilcoxonData.values()){
            data.setColumn(lastUsedCol,toPrimitive(array));
            lastUsedCol++;
        }
        System.out.println("Done StatisticTests");
    }

    public int combination() {
        return cols*(cols-1) / 2;
    }

    /*
    *
    * Rank this algs
    *
    * */
    public void algorithmsRank() throws IOException {
        friedmanToData();
        int length = combination();
        System.out.println("Lenght="+length);
        ExportedFriedmanTest test = new ExportedFriedmanTest(data);
        test.doTest();
        double[] rank = test.getAvgRank();
        boolean[] mcp = test.getMCP(length, algnames);
        for(int i = 0; i< cols; i++) {
            for(int j = i+1; j< cols; j++){
                if (rank[i] < rank[j]) {
                    double aux = rank[i];
                    rank[i] = rank[j];
                    rank[j] = aux;

                    String auxString = algnames.get(i);
                    algnames.set(i, algnames.get(j));
                    algnames.set(j, auxString);
                }
            }
        }
        for (int i = 0, pos = 1; i<cols; i++) {
            if (i > 0 && ((rank[i-1] == rank[i]) || mcp[get(i-1,i)])) {
               pos = i;
            }
            System.out.println(pos+":"+algnames.get(i));
            pos = pos+1;
        }
        test.getMCP(length, algnames);
//        int alg1 = 0 , alg2 = 0;
//      int[] rank = new int[cols];
//         for (int i = 0; i < length; i++) {
//            System.out.println(i+"/"+length);
//            alg2++;
//            if (alg2 == cols) {
//                alg1++;
//                alg2 = alg1 + 1;
//            }
//
//            DataTable dataTable = new DataTable();
//            dataTable.setDimensions(rows, 2);
//            dataTable.setColumn(0, data.getColumn(alg1));
//            dataTable.setColumn(1, data.getColumn(alg2));
//            String alg1Name = algnames.get(alg1);
//            String alg2Name = algnames.get(alg2);
//            System.out.println("\nComparing "+alg1Name+" with "+alg2Name);
//            int whoIsBetter = wilcoxon(dataTable, alg1Name, alg2Name);
//            if (whoIsBetter != 0) {
//               rank[alg1] += whoIsBetter;
//               rank[alg2] += whoIsBetter*-1;
//            }
//        }
//        System.out.println("pre-result");
//        for (int i = 0; i<cols; i++) {
//            System.out.println(algnames.get(i));
//            System.out.println(rank[i]);
//        }
//        System.out.println("Sorting...");
//        for(int i = 0; i< cols; i++){
//            for(int j = i+1; j< cols; j++){
//                if (rank[i] < rank[j]) {
//                    int aux = rank[i];
//                    rank[i] = rank[j];
//                    rank[j] = aux;
//
//                    String auxString = algnames.get(i);
//                    algnames.set(i, algnames.get(j));
//                    algnames.set(j, auxString);
//                }
//            }
//        }
//        System.out.println("Sorted");
//        for (int i = 0, pos = 1; i<cols; i++) {
//            if (i > 0 && rank[i-1] == rank[i]) {
//               pos = i;
//            }
//            System.out.println(pos+":"+algnames.get(i));
//            pos = pos+1;
//        }
    }

    private int wilcoxon(DataTable dataTable, String alg1, String alg2){
        WilcoxonTest test = new WilcoxonTest(dataTable);
        test.doTest();

        // para alfa = 5%
        System.out.println();
        if(test.getExactLeftPValue()<0.05){
            System.out.println(alg2+" é melhor que "+alg1);
            return -1;
        }else if(test.getExactRightPValue()<0.05){
            System.out.println(alg1+" é melhor que "+alg2);
            return 1;
        }
        if(test.getExactDoublePValue()>0.05){
            System.out.println("Eles sao iguais");
        }
        System.out.println();
        return 0;
    }

    public int get(int x, int y) {
        return get(x, y, x);
    }

    public int get(int x, int y, int times) {
        if(times == 0 || x == 0) return y-x-1;
        return cols-times + get(x, y, times-1);
    }
}
