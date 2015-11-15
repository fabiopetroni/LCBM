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
import java.util.List;
import profile_item.Item;
import profile_user.User;

public class TestThread implements Runnable{
    HashMap<Integer,User> users;
    HashMap<Integer,Item> items;
    List<Transaction> list;
    /*  CM = Confusion Matrix
    * 	CM[0][0] TP (True Positive)
    * 	CM[0][1] FN (False Negative)
    * 	CM[1][0] FP (False Positive)
    * 	CM[1][1] TN (True Negative)*/
    private int [][] CM;
    private boolean completed;

    public TestThread(HashMap<Integer, User> users, HashMap<Integer, Item> items, List<Transaction> list) {
        this.users = users;
        this.items = items;
        this.list = list;
        this.CM = new int[2][2];
        completed = false;
    }

    @Override
    public void run() {
        for (Transaction t: list){
            User user = users.get(t.user);
            Item item = items.get(t.item);
            if (user!=null && item!=null){
                boolean prediction = estimatePreference(user,item);
                if(prediction){
                    if(t.rating){ CM[0][0] += 1; } //TP
                    else{ CM[1][0] += 1; }//FP
                }
                else{
                    if(!t.rating){ CM[1][1] += 1; }//TN
                    else { CM[0][1] += 1; }//FN
                }
            }
        }
        completed = true;
    }
    
    private Boolean estimatePreference(User user,Item item){
        if(item.getMEAN()>user.getTHRESHOLD()){ return true;}
        else return false;
    }

    public int[][] getCM() {
        if (!completed){ 
            System.out.println("ERRORE: TestThread NOT COMPLETED but result was asked!!");
            System.exit(-1);
        }
        return CM;
    }   
}