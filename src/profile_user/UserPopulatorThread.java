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

package profile_user;

import input.Transaction;
import java.util.HashMap;
import java.util.List;
import profile_item.Item;

public class UserPopulatorThread implements Runnable{
    HashMap<Integer,User> users;
    HashMap<Integer,Item> items;
    List<Transaction> list;
    int k;

    public UserPopulatorThread(HashMap<Integer, User> users, HashMap<Integer, Item> items, List<Transaction> list, int k) {
        this.users = users;
        this.items = items;
        this.list = list;
        this.k = k;
    }

    @Override
    public void run() {
        User user = null;
        for (Transaction t: list){
            if (user==null || user.getId()!=t.user){user = users.get(t.user);}
            Item item = items.get(t.item);
            double key;
            if(t.rating){ key = item.getMEAN() + k * item.getSE();}
            else{ key = item.getMEAN() - k * item.getSE();}
            user.addRating(key,t.rating);
        }
    }
    
}
