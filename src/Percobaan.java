
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ardifirmansyah
 */
public class Percobaan {
    public static double dataTrain[][];
    public static double dataTest[][];
    
    public static int binerTrain[];
    public static int binerTest[];
    public static int hasilKNN[];
    
    public static int banyakSample;
    
    public static void AmbilDataTrain() throws IOException, BiffException {
            /* Mengambil data train pada file DataTrain.xls */
        Workbook w = Workbook.getWorkbook(new File("Data_Train.xls"));
        Sheet s = w.getSheet(0);
        dataTrain = new double[s.getRows()][s.getColumns() - 1];
        for (int baris = 0; baris < s.getRows(); baris++) {
            for (int kolom = 0; kolom < s.getColumns() - 1; kolom++) {
                Cell data = s.getCell(kolom,baris);
                dataTrain[baris][kolom] = Double.parseDouble(data.getContents());
            }
        }
    }
    
    public static void AmbilBinerTrain() throws IOException, BiffException {
            /* Mengambil data train pada file DataTrain.xls */
        Workbook w = Workbook.getWorkbook(new File("Data_Train.xls"));
        Sheet s = w.getSheet(0);
        binerTrain = new int[s.getRows()];
        for (int baris = 0; baris < s.getRows(); baris++) {
            Cell data = s.getCell(10,baris);
            binerTrain[baris] = Integer.parseInt(data.getContents());
        }
    }
    
    public static void AmbilDataTest() throws IOException, BiffException {
            /* Mengambil data train pada file DataTrain.xls */
        Workbook w = Workbook.getWorkbook(new File("Data_Test.xls"));
        Sheet s = w.getSheet(0);
        dataTest = new double[s.getRows()][s.getColumns() - 1];
        for (int baris = 0; baris < s.getRows(); baris++) {
            for (int kolom = 0; kolom < s.getColumns() - 1; kolom++) {
                Cell data = s.getCell(kolom,baris);
                dataTest[baris][kolom] = Double.parseDouble(data.getContents());
            }
        }
    }
    
    public static void AmbilBinerTest() throws IOException, BiffException {
            /* Mengambil data train pada file DataTrain.xls */
        Workbook w = Workbook.getWorkbook(new File("Data_Test.xls"));
        Sheet s = w.getSheet(0);
        binerTest = new int[s.getRows()];
        for (int baris = 0; baris < s.getRows(); baris++) {
            Cell data = s.getCell(10,baris);
            binerTest[baris] = Integer.parseInt(data.getContents());
        }
    }
    
    public static int findMajorityClass(int[] array) {
            /* Mencari class dominan dalam range tetangga terdekat */ 
        int jumlahSatu = 0;
        int jumlahNol = 0;
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) {
                jumlahSatu += 1;
            }
            else if (array[i] == 0) {
                jumlahNol +=1;
            }
        }
        if (jumlahSatu > jumlahNol) {
            return 1;
        }
        else {
            return 0;
        }
    }
    
    public static int proses(double[] query, List<Kasus> kasusList, int k) {
        List<Result> resultList = new ArrayList<Result>();
        
        for (Kasus kasus : kasusList) {
            double dist = 0.0;
            for (int j = 0; j < kasus.atributKasus.length; j++) {
                dist += Math.pow(kasus.atributKasus[j] - query[j], 2);
            }
            double distance = Math.sqrt(dist);
            resultList.add(new Result(kasus.atributKasus,distance,kasus.biner));
        }
        Collections.sort(resultList, new DistanceComparator());
        int[] ii = new int[k];
//        System.out.println(k + " tetangga terdekat adalah :");
        for (int x = 0; x < k; x++) {
//            for (int i = 0; i < resultList.get(x).atribut.length; i++) {
//                System.out.print(resultList.get(x).atribut[i] + " | ");
//            }
//            System.out.print("--> ");
//            System.out.println(resultList.get(x).biner + " dengan jarak : " + 
//                    resultList.get(x).distance);
            ii[x] = resultList.get(x).biner;
        }
        int majClass = findMajorityClass(ii);
        return majClass;
    }
    
    public static void main(String[] args) throws IOException, BiffException {
        int k = 15; // # of neighbours
        double[] query;
        int jumlahBenar = 0;
        int totalSample;
        
        List<Kasus> kasusList = new ArrayList<Kasus>();
        List<Kasus> testList = new ArrayList<Kasus>();
//        List<Kasus> akurasiList = new ArrayList<Kasus>();
        
        AmbilDataTrain();
        AmbilBinerTrain();
        AmbilDataTest();
        AmbilBinerTest();
        for (int baris = 0; baris < dataTrain.length; baris++) {
            kasusList.add(new Kasus(dataTrain[baris],binerTrain[baris]));
        }
        
        for (int baris = 0; baris < dataTest.length; baris++) {
            testList.add(new Kasus(dataTest[baris],binerTest[baris]));
        }
        
        for (Kasus kasus : testList) {
            query = kasus.atributKasus;
            int binerBaru = proses(query, kasusList, k);
            kasus.predicted = binerBaru;        // memasukkan hasil knn kedalam variable predicted
            for (int z = 0; z < kasus.atributKasus.length; z++) {
                System.out.print(kasus.atributKasus[z] + "\t|\t");
            }
            if (kasus.biner == kasus.predicted) {
                jumlahBenar += 1;
            }
            System.out.print(" --> "); System.out.print(kasus.predicted);
            System.out.print(" ... Actual ... "); System.out.print(kasus.biner);
            System.out.println();
        }
        totalSample = testList.size();
        System.out.print("Jumlah benar : "); System.out.println(jumlahBenar);
        System.out.print("Total sample : "); System.out.println(totalSample);
        System.out.println("Tingkat Akurasi = " + ((jumlahBenar * 100) / totalSample) + "%");
//        for (int baris = 0; baris < dataTrain.length; baris++) {
//            for (int kolom = 0; kolom < 10; kolom++) {
//                System.out.print(dataTrain[baris][kolom] + "\t|\t");
//            }
//            System.out.println();
//        }
//        
//        for (int baris = 0; baris < binerTrain.length; baris++) {
//            System.out.println(binerTrain[baris]);
//            System.out.println(baris+1);
//        }
        
//        for (int baris = 0; baris < dataTest.length; baris++) {
//            for (int kolom = 0; kolom < 10; kolom++) {
//                System.out.print(dataTest[baris][kolom] + "\t|\t");
//            }
//            System.out.println(baris+1);
//            System.out.println();
//        }

//        for (int baris = 0; baris < binerTest.length; baris++) {
//            System.out.println(binerTest[baris]);
//            System.out.println(baris+1);
//        }
    }
    
    static class Kasus {
        double[] atributKasus;
        int biner; // actual output
        int predicted; // result of knn algorithm
        
        public Kasus(double[] atributKasus, int biner) {
            this.atributKasus = atributKasus;
            this.biner = biner;
        }
    }
    
    static class Result {
        double[] atribut;
        double distance;
        int biner;
        
        public Result (double[] atribut, double distance, int biner) {
            this.atribut = atribut;
            this.distance = distance;
            this.biner = biner;
        }
    }
    
    static class DistanceComparator implements Comparator<Result> {
        @Override
        public int compare(Result a, Result b) {
            return a.distance < b.distance ? -1 : a.distance == b.distance ? 0 : 1;
        }
    }
}
