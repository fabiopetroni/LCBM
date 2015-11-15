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

package lcbm;

import input.Transaction;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import profile_item.Item;
import profile_item.ItemPopulatorThread;
import profile_user.User;
import profile_user.UserPopulatorThread;

public class PopulatorMethods {
    
    public static void populateUserProfiles(HashMap<Integer, User> users, HashMap<Integer, Item> items, LinkedList<Transaction> list, Globals GLOBALS){
        int processors = GLOBALS.THREADS;
        ExecutorService executor=Executors.newFixedThreadPool(processors);
        int n = list.size();
        int subSize = n / processors + 1;
        for (int t = 0; t < processors; t++) {
            final int iStart = t * subSize;
            final int iEnd = Math.min((t + 1) * subSize, n);
            if (iEnd>=iStart){
                List<Transaction> sublist= list.subList(iStart, iEnd);
                UserPopulatorThread thread = new UserPopulatorThread(users,items,sublist,GLOBALS.k);
                executor.execute(thread);
            }
        }
        try { 
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.DAYS);
        } catch (InterruptedException ex) {System.out.println("InterruptedException "+ex);ex.printStackTrace();System.exit(-1);}
    }
    
    public static void populateItemProfiles(HashMap<Integer, Item> items, LinkedList<Transaction> list, Globals GLOBALS){
        int processors = GLOBALS.THREADS;
        ExecutorService executor=Executors.newFixedThreadPool(processors);
        int n = list.size();
        int subSize = n / processors + 1;
        for (int t = 0; t < processors; t++) {
            final int iStart = t * subSize;
            final int iEnd = Math.min((t + 1) * subSize, n);
            if (iEnd>=iStart){
                List<Transaction> sublist= list.subList(iStart, iEnd);
                ItemPopulatorThread thread = new ItemPopulatorThread(items,sublist);
                executor.execute(thread);
            }
        }
        try { 
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.DAYS);
        } catch (InterruptedException ex) {System.out.println("InterruptedException "+ex);ex.printStackTrace();System.exit(-1);}
    }
}
