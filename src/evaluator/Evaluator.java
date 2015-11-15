// Copyright (C) 2015 Fabio Petroni
// Contact: http://www.fabiopetroni.com
//
// This file is part of LCBM (a fast and lightweight collaborative filtering algorithm for binary ratings).
//
// LCBM is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LCBM is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LCBM.  If not, see <http://www.gnu.org/licenses/>.
//
// Based on the publication:
// - Fabio Petroni, Leonardo Querzoni, Roberto Beraldi, Mario Paolucci: 
//   "LCBM: Statistics-Based Parallel Collaborative Filtering".
//   BIS, 2014.

package evaluator;

import input.Transaction;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lcbm.Globals;
import profile_item.Item;
import profile_user.User;

public class Evaluator {
    
    private LinkedList<Transaction> list;
    private HashMap<Integer,User> users;
    private HashMap<Integer,Item> items;
    /*  CM = Confusion Matrix
    * 	CM[0][0] TP (True Positive)
    * 	CM[0][1] FN (False Negative)
    * 	CM[1][0] FP (False Positive)
    * 	CM[1][1] TN (True Negative)*/
    private int [][] CM;
    private Globals GLOBALS;
    
    public Evaluator(LinkedList<Transaction> list, HashMap<Integer, User> users, HashMap<Integer, Item> items, Globals G) {
        this.list = list;
        this.users = users;
        this.items = items;
        this.CM = new int[2][2];
        this.GLOBALS = G;
    }
    
    public void run(){        
        int processors = GLOBALS.THREADS;
        ExecutorService executor=Executors.newFixedThreadPool(processors);
        TestThread[] threads = new TestThread[processors];
        int n = list.size();
        int subSize = n / processors + 1;
        for (int t = 0; t < processors; t++) {
            final int iStart = t * subSize;
            final int iEnd = Math.min((t + 1) * subSize, n);
            if (iEnd>=iStart){
                List<Transaction> sublist= list.subList(iStart, iEnd);
                threads[t] = new TestThread(users,items,sublist);
                executor.execute(threads[t]);
            }
        }
        try { 
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.DAYS);
        } catch (InterruptedException ex) {System.out.println("InterruptedException "+ex);ex.printStackTrace();}
        //end: parallel polarization procedure
        
        /*begin: stat collection*/
        for (int t = 0; t < processors; t++) {
            int [][] tCM = threads[t].getCM();
//            System.out.println("t"+t);
//            System.out.println("TP: "+tCM[0][0]);
//            System.out.println("FN: "+CM[0][1]);
//            System.out.println("FP: "+tCM[1][0]);
//            System.out.println("TN: "+tCM[1][1]);
//            System.out.println("");
            CM[0][0] += tCM[0][0];
            CM[0][1] += tCM[0][1];
            CM[1][0] += tCM[1][0];
            CM[1][1] += tCM[1][1];
        }
    }
    
    public double getMCC(){
        double TP = CM[0][0];
        double TN = CM[1][1];
        double FN = CM[0][1];
        double FP = CM[1][0];
        double NUM = (TP*TN)-(FP*FN);
        double DEN =Math.sqrt((TP+FP)*(TP+FN)*(TN+FP)*(TN+FN));
        double MCC = NUM;
        MCC /= DEN;
        return MCC;
    }
}