package cin.ufpe.br.statistic;

import javanpst.data.structures.dataTable.DataTable;
import javanpst.distributions.common.continuous.ChiSquareDistribution;
import javanpst.distributions.common.continuous.NormalDistribution;
import javanpst.tests.StatisticalTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExportedFriedmanTest extends StatisticalTest {

    private DataTable data;
    private double[][] samples;
    private double[][] ranks;
    private double[] sumRanks;
    private double[] avgRanks;
    private double S;
    private double Q;
    private double tiesWeight;
    private double pValue;
    private double criticalZ90;
    private double criticalZ95;

    public ExportedFriedmanTest() {
        this.setReportFormat();
        this.clearData();
    }

    public double[] getAvgRank(){
        return avgRanks;
    }

    public double[] getSumRank(){
        return sumRanks;
    }

    public double[][] getRanks(){
        return ranks;
    }

    public boolean isNullRejected(double alfa){
        if (performed) {
           return pValue < alfa;
        }
        return false;
    }

    public void clearData() {
        this.data = new DataTable();
        this.performed = false;
        this.dataReady = false;
        this.S = 0.0D;
        this.Q = 0.0D;
        this.samples = (double[][])null;
        this.ranks = (double[][])null;
        this.sumRanks = null;
        this.avgRanks = null;
        this.pValue = -1.0D;
    }

    public ExportedFriedmanTest(DataTable newData) {
        this.setReportFormat();
        this.data = DataTable.newInstance(newData);
        if (this.data.getColumns() < 3) {
            System.out.println("Extended median test only can be employed with more than two samples");
            this.clearData();
        } else {
            int i;
            for(i = 0; i < this.data.getColumns(); ++i) {
                if (this.data.getColumnNulls(i) > 0) {
                    System.out.println("No null values allowed in this test.");
                    this.clearData();
                    return;
                }
            }

            this.samples = new double[this.data.getRows()][this.data.getColumns()];
            this.ranks = new double[this.data.getRows()][this.data.getColumns()];
            this.sumRanks = new double[this.data.getColumns()];
            this.avgRanks = new double[this.sumRanks.length];

            for(i = 0; i < this.data.getRows(); ++i) {
                Arrays.fill(this.ranks[i], -1.0D);

                for(int j = 0; j < this.data.getColumns(); ++j) {
                    this.samples[i][j] = this.data.get(i, j);
                }
            }

            Arrays.fill(this.sumRanks, 0.0D);
            this.dataReady = true;
            this.performed = false;
        }
    }

    public void setData(DataTable newData) {
        this.data = DataTable.newInstance(newData);
        if (this.data.getColumns() < 3) {
            System.out.println("Friedman test only can be employed with more than two samples");
            this.clearData();
        } else {
            int i;
            for(i = 0; i < this.data.getColumns(); ++i) {
                if (this.data.getColumnNulls(i) > 0) {
                    System.out.println("No null values allowed in this test.");
                    this.clearData();
                    return;
                }
            }

            this.samples = new double[this.data.getRows()][this.data.getColumns()];
            this.ranks = new double[this.data.getRows()][this.data.getColumns()];
            this.sumRanks = new double[this.data.getColumns()];
            this.avgRanks = new double[this.sumRanks.length];

            for(i = 0; i < this.data.getRows(); ++i) {
                Arrays.fill(this.ranks[i], -1.0D);

                for(int j = 0; j < this.data.getColumns(); ++j) {
                    this.samples[i][j] = this.data.get(i, j);
                }
            }

            Arrays.fill(this.sumRanks, 0.0D);
            this.dataReady = true;
            this.performed = false;
        }
    }

    public void doTest() {
        if (!this.dataReady) {
            System.out.println("Data is not ready");
        } else {
            this.computeRanks();

            for(int i = 0; i < this.data.getRows(); i++) {
                for(int j = 0; j < this.data.getColumns(); j++) {
                    this.sumRanks[j] += this.ranks[i][j];
                }
            }

            for(int j = 0; j < this.sumRanks.length; ++j) {
                this.avgRanks[j] = this.sumRanks[j] / (double)this.data.getRows();
            }

            for(int j = 0; j < this.data.getColumns(); ++j) {
                this.S += this.sumRanks[j] * this.sumRanks[j];
            }

            this.S -= (double)(this.data.getRows() * this.data.getRows() * this.data.getColumns()) * ((double)this.data.getColumns() + 1.0D) * ((double)this.data.getColumns() + 1.0D) / 4.0D;
            this.Q = 12.0D * ((double)this.data.getColumns() - 1.0D) * this.S;
            this.Q /= (double)(this.data.getRows() * this.data.getColumns()) * ((double)(this.data.getColumns() * this.data.getColumns()) - 1.0D) - this.tiesWeight;
            this.computePValue(this.sumRanks.length - 1);
            this.multipleComparisonsProcedure();
            this.performed = true;
        }
    }

    private void multipleComparisonsProcedure() {
        double critical90 = 1.0D - 0.1D / (double)(this.sumRanks.length * (this.sumRanks.length - 1));
        double critical95 = 1.0D - 0.05D / (double)(this.sumRanks.length * (this.sumRanks.length - 1));
        NormalDistribution normal = new NormalDistribution();
        critical90 = normal.inverseNormalDistribution(critical90);
        critical95 = normal.inverseNormalDistribution(critical95);
        double N = (double)this.data.getColumns();
        double denominator = Math.sqrt(N * (N + 1.0D) / 12.0D * (1.0D / (double)this.data.getRows() + 1.0D / (double)this.data.getRows()));
        this.criticalZ90 = critical90 * denominator;
        this.criticalZ95 = critical95 * denominator;
    }

    private void computePValue(int dF) {
        ChiSquareDistribution chi = new ChiSquareDistribution();
        chi.setDegree(dF);
        this.pValue = chi.computeCumulativeProbability(this.Q);
    }

    private void computeRanks() {
        this.tiesWeight = 0.0D;

        for(int i = 0; i < this.data.getRows(); ++i) {
            double rank = 1.0D;

            do {
                double min = 1.7976931348623157E308D;
                int count = 0;

                int j;
                for(j = 0; j < this.data.getColumns(); ++j) {
                    if (this.ranks[i][j] == -1.0D && this.samples[i][j] == min) {
                        ++count;
                    }

                    if (this.ranks[i][j] == -1.0D && this.samples[i][j] < min) {
                        min = this.samples[i][j];
                        count = 1;
                    }
                }

                double newRank;
                if (count == 1) {
                    newRank = rank;
                } else {
                    this.tiesWeight += (double)count * ((double)(count * count) - 1.0D);
                    newRank = 0.0D;

                    for(j = 0; j < count; ++j) {
                        newRank += rank + (double)j;
                    }

                    newRank /= (double)count;
                }

                for(j = 0; j < this.data.getColumns(); ++j) {
                    if (this.samples[i][j] == min) {
                        this.ranks[i][j] = newRank;
                    }
                }

                rank += (double)count;
            } while(rank <= (double)this.data.getColumns());
        }

    }

    public double getS() {
        return this.S;
    }

    public double getQ() {
        return this.Q;
    }

    public double getPValue() {
        return this.pValue;
    }

    public double getAvgRanks(int pop) {
        return pop - 1 > -1 && pop - 1 < this.avgRanks.length ? this.avgRanks[pop - 1] : -1.0D;
    }

    public String printData() {
        String text = "";
        text = text + "\n" + this.data;
        return text;
    }

    public String printReport() {
        String report = "";
        if (!this.performed) {
            report = report + "The test has not been performed.\n";
            return report;
        } else {
            report = report + "\n******************\n";
            report = report + "Friedman test\n";
            report = report + "******************\n\n";
            report = report + "Sum of ranks:\n";

            int j;
            for(j = 0; j < this.sumRanks.length; ++j) {
                report = report + "S" + (j + 1) + "\t";
            }

            report = report + "\n";

            for(j = 0; j < this.sumRanks.length; ++j) {
                report = report + this.nf6.format(this.sumRanks[j]) + "\t";
            }

            report = report + "\n";
            report = report + "\n";
            report = report + "Average ranks:\n";

            for(j = 0; j < this.avgRanks.length; ++j) {
                report = report + "S" + (j + 1) + "\t";
            }

            report = report + "\n";

            for(j = 0; j < this.avgRanks.length; ++j) {
                report = report + this.nf6.format(this.avgRanks[j]) + "\t";
            }

            report = report + "\n";
            report = report + "\n";
            report = report + "S statistic: " + this.nf6.format(this.S) + "\n";
            report = report + "Q statistic: " + this.nf6.format(this.Q) + "\n\n";
            report = report + "P-Value computed :" + this.nf6.format(this.pValue) + "\n\n";
            return report;
        }
    }

    public boolean[] getMCPAvg(int length) {
        boolean[] report = new boolean[length];
        int i=0;
        System.out.println(String.valueOf(this.criticalZ95));
        for(int first = 0; first < this.data.getColumns() - 1; ++first) {
            for(int second = first + 1; second < this.data.getColumns(); ++second) {
                if(i>=length) break;
                double value1 = this.avgRanks[first]/this.data.getColumns();
                double value2 = this.avgRanks[second]/this.data.getColumns();
                double Z = Math.abs(value1 - value2);
                report[i] = Z >= this.criticalZ95;
                i++;
            }
        }
        return report;
    }

    HashMap<String, Boolean> getMCP(int length, ArrayList<String> algnames) {
        HashMap<String, Boolean> report = new HashMap<>();
        int i=0;
        System.out.println(String.valueOf(this.criticalZ95));
        for(int first = 0; first < this.data.getColumns() - 1; ++first) {
            for(int second = first + 1; second < this.data.getColumns(); ++second) {
                if(i>=length) break;
                double Z = Math.abs(this.avgRanks[first] - this.avgRanks[second]);
                String key = algnames.get(first)+"-"+algnames.get(second);
                String reverse_key = algnames.get(second)+"-"+algnames.get(first);
                report.put(key, (Z >= this.criticalZ95));
                report.put(reverse_key, (Z >= this.criticalZ95));
                i++;
            }
        }
        return report;
    }

    public String printMultipleComparisonsProcedureReport() {
        String report = "";
        if (!this.performed) {
            report = report + "The test has not been performed.\n";
            return report;
        } else {
            report = report + "\n***************************************\n";
            report = report + "Multiple comparisons procedure (Friedman test)\n";
            report = report + "***************************************\n\n";
            report = report + "Critical values: Alpha=0.90: " + this.criticalZ90 + " Alpha=0.95: " + this.criticalZ95 + "\n\n";
            report = report + "Individual comparisons:\n\n";

            for(int first = 0; first < this.data.getColumns() - 1; ++first) {
                for(int second = first + 1; second < this.data.getColumns(); ++second) {
                    double Z = Math.abs(this.avgRanks[first] - this.avgRanks[second]);
                    report = report + (first + 1) + " vs " + (second + 1) + ": Z= " + Z + "\n\n";
                }
            }

            return report;
        }
    }
}
