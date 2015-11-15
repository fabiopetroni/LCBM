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

package input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import profile_item.Item;
import profile_user.User;

public class Reader {    
    public static LinkedList<Transaction> readTrainData(String TRAIN_FILE, HashMap<Integer,User> users, HashMap<Integer,Item> items){
        LinkedList<Transaction> result = new LinkedList<Transaction>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(TRAIN_FILE));
            String line;
            while((line = br.readLine())!=null){
                String values[] = line.split(",");
                int user = Integer.parseInt(values[0]);
                if (!users.containsKey(user)){ users.put(user, new User(user));}
                int item = Integer.parseInt(values[1]);
                if (!items.containsKey(item)){ items.put(item, new Item(item));}
                int like_int = Integer.parseInt(values[2]);
                boolean like;
                if (like_int>0) {like = true;}
                else{like = false;}
                Transaction t = new Transaction(user,item,like);
                result.add(t);
            }
        } catch (Exception e) {
            System.out.println("Exception Reader.readTrainData()"+e);
            e.printStackTrace();
            System.exit(-1);
        }     
        return result;
    }
    
    public static LinkedList<Transaction> readTESTData(String TEST_FILE){
        LinkedList<Transaction> result = new LinkedList<Transaction>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(TEST_FILE));
            String line;
            while((line = br.readLine())!=null){
                String values[] = line.split(",");
                int user = Integer.parseInt(values[0]);
                int item = Integer.parseInt(values[1]);
                int like_int = Integer.parseInt(values[2]);
                boolean like;
                if (like_int>0) {like = true;}
                else{like = false;}
                Transaction t = new Transaction(user,item,like);
                result.add(t);
            }
        } catch (Exception e) {
            System.out.println("Exception Reader.readTrainData()"+e);
            e.printStackTrace();
            System.exit(-1);
        }     
        return result;
    }
}