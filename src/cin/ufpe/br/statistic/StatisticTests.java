package cin.ufpe.br.statistic;

import cin.ufpe.br.compare.Resultado;
import javanpst.data.structures.dataTable.DataTable;
import javanpst.tests.oneSample.wilcoxonTest.WilcoxonTest;
//import javanpst.tests.multiple.friedmanTest.FriedmanTest;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

public class StatisticTests {

    private LinkedHashMap<String,ArrayList<Double>> friedmanData = new LinkedHashMap<>();
    private HashMap<String, Long> trainingTimes = new HashMap<>();
    private HashMap<String, Long> testTimes = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> finalRank = new HashMap<>();

    private ArrayList<String> algnames = new ArrayList<>();
    private DataTable data = new DataTable();
    private int rows, cols= 0;


    public StatisticTests(){}

    public LinkedHashMap<String, ArrayList<Double>> getFriedmanData() {
        return friedmanData;
    }

    public void add(Resultado resultado){
        ArrayList<Double> currentData = friedmanData.get(resultado.alg);
        if (currentData == null) {
            trainingTimes.put(resultado.alg, resultado.tuningTrainingTime);
            algnames.add(cols, resultado.alg);
            currentData = new ArrayList<>();
            cols++;
            rows = 0;
        }
        currentData.add(resultado.accuracy);
        friedmanData.put(resultado.alg,currentData);
        rows++;
    }

    public void addTestTime(Resultado resultado) {
        testTimes.put(resultado.alg, resultado.testTime);
    }

    private static double[] toPrimitive(ArrayList<Double> arrayList) {
        double[] target = new double[arrayList.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = arrayList.get(i);
        }
        return target;
    }

    private void friedmanToData() {
        int lastUsedCol = 0;
        data.setDimensions(rows,cols);
        for(ArrayList<Double> array : friedmanData.values()){
            data.setColumn(lastUsedCol,toPrimitive(array));
            lastUsedCol++;
        }
        System.out.println("Done StatisticTests");
    }

    public void friedmanToDataFromCsv() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("all.csv")));
            Stream<String> lines = in.lines();
            lines.forEach(line -> {
                String[] columns = line.split(",");
                Resultado resultado = new Resultado(columns);
                add(resultado);
            });
        } catch (FileNotFoundException e) {}
    }

    private int combination() {
        return cols*(cols-1) / 2;
    }

    private void swap(double[] array, int i, int j) {
        double aux = array[i];
        array[i] = array[j];
        array[j] = aux;
    }

    /*
    * Rank this algs
    * */
    public void algorithmsRank() {
        if(friedmanData.size() == 0) friedmanToDataFromCsv();
        if(friedmanData.size() == 0) return;
        System.out.println(friedmanData.size());
        friedmanToData();
        System.out.println(data.getRows()+"x"+data.getColumns());
        ExportedFriedmanTest test = new ExportedFriedmanTest(data);
        int length = combination();
        test.doTest();
        if(test.isPerformed()){
            System.out.println(test.printReport());
            double[] rank = test.getAvgRank();
            double[] sd = test.getSdRanks();
            HashMap<String, Boolean> mcp = test.getMCP(length, algnames);
            for(int i = 0; i< cols; i++) {
                for(int j = i+1; j< cols; j++){
                    if (rank[i] < rank[j]) {
                        swap(rank, i, j);
                        swap(sd, i, j);

                        String auxString = algnames.get(i);
                        algnames.set(i, algnames.get(j));
                        algnames.set(j, auxString);
                    }
                }
            }

            for (int i = 0, pos = 1; i<cols; i++) {
                if(i > 0 && pos > 1) {
                    boolean notStatisticDiff = !isStatisticalDifferent(algnames, mcp, i, pos-1);
                    if ((rank[i-1] == rank[i]) || notStatisticDiff) {
                        pos = pos-1;
                    }
                }
                long trainingTime = trainingTimes.getOrDefault(algnames.get(i), 0L);
                long testTime = testTimes.getOrDefault(algnames.get(i), 0L);
                System.out.print(pos+":"+algnames.get(i)+" value: "+rank[i]+" sd: "+sd[i]);
                System.out.println(" train: "+trainingTime+" test: "+testTime);
                ArrayList<Integer> algList = finalRank.getOrDefault(pos, new ArrayList<>());
                algList.add(i);
                finalRank.put(pos, algList);
                pos = pos+1;
            }
        }
    }

    private boolean isStatisticalDifferent(ArrayList<String> algnames, HashMap<String, Boolean> mcp, int i, int pos) {
        boolean result = true;
        String mainAlg = algnames.get(i);
        ArrayList<Integer> arrayList = finalRank.getOrDefault(pos, new ArrayList<>());
        for (Integer j : arrayList) {
            String currentAlg = algnames.get(j);
            String key = mainAlg + "-" + currentAlg;
            String reverse_key = currentAlg + "-" + mainAlg;
            boolean isDiff = mcp.get(key) && mcp.get(reverse_key);
            if (isDiff) {
                result = true;
                break;
            } else {
                result = false;
            }
        }
        return result;
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
