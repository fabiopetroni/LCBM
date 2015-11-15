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

import evaluator.Evaluator;
import input.Reader;
import input.Transaction;
import java.util.HashMap;
import java.util.LinkedList;
import profile_item.Item;
import profile_user.User;

public class LCBM {
        
    public static void main(String[] args) {
        
        long global_START = System.currentTimeMillis();
        
        System.out.println("\n--------------------------------------------------");
        System.out.println(" LCBM: a fast and lightweight collaborative filtering algorithm for binary ratings.");
        System.out.println(" author: Fabio Petroni (http://www.fabiopetroni.com)");
        System.out.println("--------------------------------------------------\n");
        Globals GLOBALS = new Globals(args);
        System.out.println(" Parameters:");
        GLOBALS.print();
        
        //begin READING input data
        System.out.println("\n Loading dataset into main memory... ");  
        long local_START = System.currentTimeMillis();
        HashMap<Integer,User> users = new HashMap<Integer,User>();
        HashMap<Integer,Item> items = new HashMap<Integer,Item>();
        LinkedList<Transaction> train = Reader.readTrainData(GLOBALS.TRAIN_FILE,users,items);
        long local_MIDDLE = System.currentTimeMillis();
        LinkedList<Transaction> test = Reader.readTESTData(GLOBALS.TEST_FILE);
        long local_END = System.currentTimeMillis();
        System.out.println("\ttime to load train: "+(local_MIDDLE-local_START));
        System.out.println("\ttime to load test: "+(local_END-local_MIDDLE));
        System.out.println("\ttotal loading time: "+(local_END-local_START));
        System.out.println();
        //end READING input data
        
        //begin ITEM PROFILING PROCEDURE
        System.out.println(" Computing item profiles... ");
        local_START = System.currentTimeMillis();
        PopulatorMethods.populateItemProfiles(items, train,GLOBALS);        
        local_MIDDLE = System.currentTimeMillis();
        ProfileMethods.computeItemProfiles(items,GLOBALS);
        local_END = System.currentTimeMillis();
        System.out.println("\ttime to populate item profiles: "+(local_MIDDLE-local_START));
        System.out.println("\ttime to compute item profiles: "+(local_END-local_MIDDLE));
        System.out.println("\ttime item profiling procedure: "+(local_END-local_START));
        System.out.println();
        //end ITEM PROFILING PROCEDURE
        
        //begin USER PROFILING PROCEDURE
        System.out.println(" Computing user profiles... ");
        local_START = System.currentTimeMillis();
        PopulatorMethods.populateUserProfiles(users, items, train, GLOBALS);
        local_MIDDLE = System.currentTimeMillis();
        ProfileMethods.computeUserProfiles(users,GLOBALS);
        local_END = System.currentTimeMillis();
        System.out.println("\ttime to populate user profiles: "+(local_MIDDLE-local_START));
        System.out.println("\ttime to compute user profiles: "+(local_END-local_MIDDLE));
        System.out.println("\ttime user profiling procedure: "+(local_END-local_START));
        System.out.println();
        //end USER PROFILING PROCEDURE

        //begin EVALUATION
        System.out.println("\n Evaluating algorithm performace... ");
        local_START = System.currentTimeMillis();
        Evaluator e = new Evaluator(test, users, items,GLOBALS);
        e.run();
        local_END = System.currentTimeMillis();
        double MCC = e.getMCC();
        System.out.println("\ttime evaluator: "+(local_END-local_START));
        System.out.println("\tMCC: "+MCC);
        System.out.println();
        //end EVALUATION        
        
        long global_END = System.currentTimeMillis();
        long TIME = global_END-global_START;        
        System.out.println("  Total Time: "+TIME+"\n");
        
    }
}
