package MTSP_ACO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class Mtsp_Aco {

    public static void main(String[] args) {
        //generator graph
        int numVertex = 5;
        int[][] adjacency = new int[numVertex][numVertex];
        for (int i = 0; i < numVertex; i++) {
            adjacency[i][i] = 0;
            for (int j = i + 1; j < numVertex; j++) {
                int value = ThreadLocalRandom.current().nextInt(1, 10);
                adjacency[j][i] = value;
                
                adjacency[i][j] = value;
            }
        }

        //cetak adjacency
        System.out.print(" \t");
        for (int i = 0; i < adjacency.length; i++) {
            System.out.print("V" + (i) + "\t");
        }
        System.out.println();

        for (int i = 0; i < adjacency.length; i++) {
            System.out.print("v" + (i) + ("\t"));
            for (int j = 0; j < adjacency[i].length; j++) {
                System.out.print(adjacency[i][j] + "\t");

            }
            System.out.println();
            //System.out.println(Arrays.toString(adjacency[i]));
        }
        int start = 2;//start dari v2
        int M = 2;

        //PARAMETER ALGORITMA ACO
        double[][] pheromone = new double[numVertex][numVertex];
        double[][] visibility = new double[numVertex][numVertex];//eta
        int S = 20;//banyaknya semut atau banyknya salesman pada MTSP
        int NcMax = 5;//menyatakan jumlah siklus maksimun
        double alpha = 0.6; //konstanta pengendali pheromone (a)
        double beta = 0.5; // konstanta pengendali intensitas visibilitas (b)
        double rho = 0.4;//konstanta penguapan pherompne
        double Q = 0.5;//konstanta siklus semut

        //PHEROMONE
        for (int i = 0; i < numVertex; i++) {
            for (int j = 0; j < numVertex; j++) {
                if (adjacency[i][j] > 0) {
                    pheromone[i][j] = 1;
                    visibility[i][j] = 1.0 / (double) adjacency[i][j];

                }

            }

        }
        System.out.println("Cetak visibility");
        for (int i = 0; i < numVertex; i++) {
            System.out.println(Arrays.toString(visibility[i]));
        }
        System.out.println("-----------------------------------");
        //CETAK VISIBILITY

        System.out.println("Cetak Pheromone");
        for (int i = 0; i < numVertex; i++) {
            System.out.println(Arrays.toString(pheromone[i]));

        }
        System.out.println("----------------------------------");
        
        // LOOPING SIKLUS SEMUT
         ArrayList<Integer> bestSolution = null;
        double bestDistance = Double.MAX_VALUE;
        //loop siklus semut
        for (int n = 0; n < NcMax; n++) {//siklus semut
            double [][] deltaTau = new double[numVertex][numVertex];
            for (int semut = 0; semut < S; semut++) {
                Stack<Integer> visited = new Stack<>();
                visited.push(start);

                while(true){
                    int origin = visited.peek();
                    if (visited.size() >= numVertex){
                        //pencarian berakhir karena jalur ditemukan

                        visited.add(start);
                        System.out.println("Solusi : "+visited);

                        double jarak = 0;
                        int ori = visited.get(0);
                        for (int i = 1; i < visited.size(); i++) {
                            int dest = visited.get(i);
                            jarak += adjacency[ori][dest];
                            ori = dest;
                        }
//                        jarak += adjacency[ori][depot];
                        System.out.println(" jarak : "+jarak);

                        if (jarak < bestDistance){
                            bestDistance = jarak;
                            bestSolution = new ArrayList<>();
                            for (int i = 0; i < visited.size(); i++) {
                                bestSolution.add(visited.get(i));

                            }
                        }
                        break;
                    }else{
                        int i = origin;
                        double[] pembilang = new double[numVertex];
                        double penyebut = 0;// ini adalah sigma
                        int[] candidate = new int[numVertex];
                        for (int j = 0; j < numVertex; j++) {
                            if (adjacency[i][j] > 0 && !visited.contains(j)){
                                candidate [j] = 1;
                                double tau = pheromone[i][j];
                                double eta = visibility[i][j];
                                double tauEta = Math.pow(tau, alpha) * Math.pow(eta, beta);
                                pembilang [j] = tauEta;
                                penyebut += tauEta;//increment penyebut = SIGMA tau x eta
                            }
                        }

                        //hitung probabilitas semut
                        double[] probabilitasKomulatif = new double[numVertex];
                        double totalProbabilitas = 0;
                        for (int j = 0; j < numVertex; j++) {
                            if (candidate [j] == 1){
                                double probabilitas = pembilang[j] / penyebut;// menghitung probabilitas semut
                                totalProbabilitas += probabilitas;
                                probabilitasKomulatif[j] = totalProbabilitas;
                            }
                        }
                        //tetakan destinasi
                        int destination = origin;
                        double randomProbabilitas = new Random().nextDouble()*totalProbabilitas;
                        for (int j = 0; j < numVertex; j++) {
                            if(candidate[j] == 1
                                    && probabilitasKomulatif[j]>0
                                    && randomProbabilitas < probabilitasKomulatif[j]){
                                destination = j;
                                break;

                            }

                        }
                        if (destination != origin) {//menemukan tujuan baru
                            visited.push(destination);
                        }else{
                            visited.pop();//ada tambahan operasi backtracking
                        }

                    }
                }

                //HITUNG JARAK YANG TELAH DILALUI OLEH SEMUT
                double Lk = 0;
                int origin = visited.get(0);
                for (int i = 1; i < visited.size(); i++) {
                    int destination = visited.get(i);
                    Lk += adjacency[origin][destination];
                    origin = destination;
                }
//                Lk += adjacency[origin][depot];

                //UPDATE DELTA TAU OLEH SEMUT KE-M
                origin = visited.get(0); //mula-mula tetapkan vertex awal sebagai origin
                for (int k = 1; k < visited.size(); k++) {
                    int destination = visited.get(k);
                    deltaTau[origin][destination] += (Q / Lk);// update SIGMA delta tau xy

                }
            }//end of pencarian oleh semut
            //UPDATE PHEROMONE SEMUT
            for (int i = 0; i < numVertex; i++) {
                for (int j = 0; j < numVertex; j++) {
                    pheromone[i][j] = (1.0 - rho) * pheromone[i][j] + deltaTau[i][j];
                    
                }
                
            }

        }//end of lop siklus semut
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println("SOLUSI ANT COLONY OPTIMIZATION ");
        System.out.println(bestSolution);
        System.out.println("Jarak : "+bestDistance);
        System.out.println("--------------------------------------------------------------------------------------------");

    }

}
