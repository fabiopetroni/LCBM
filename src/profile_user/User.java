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

import java.util.Iterator;
import java.util.TreeSet;

public class User {
    
    private final int id;
    private TreeSet<Rating> ratings;
    private double THRESHOLD;
    
    public User(int i){
        this.id = i;
        ratings = new TreeSet<Rating>();
    }
            
    public synchronized void addRating(double key, boolean vote){
        Rating r = new Rating(key,vote);
        ratings.add(r);
    }

    public double getTHRESHOLD() {
        return THRESHOLD;
    }
    
    public void computeProfile(){
        
        //compute left ok
        int left_ok = 0;
        for (Rating x : ratings){
            if (x.getVote()){left_ok++;}
            x.setLeftOK(left_ok);
        }
        
        //compute right ko
        int right_ko = 0;
        Iterator<Rating> it = ratings.descendingIterator();
        while (it.hasNext()){
            Rating x = it.next();
            x.setRightKO(right_ko);
            if (!x.getVote()){right_ko++;}
        }
        
        //compute threshold
        int f = Integer.MAX_VALUE;
        int abs_diff = Integer.MAX_VALUE;
        it = ratings.descendingIterator();
        while (it.hasNext()){
            Rating x = it.next();
            int f_x = x.getLeftOK() + x.getRightKO();
            if (f_x < f){ 
                THRESHOLD = x.getKey(); 
                f = f_x;
            }
            else if (f_x == f){
                int abs_diff_x = Math.abs(x.getLeftOK() - x.getRightKO());
                if (abs_diff_x <= abs_diff){ THRESHOLD = x.getKey(); abs_diff = abs_diff_x;}
            }
        }
        
//        if (id == 1){
//            for (Rating x : ratings){
//                System.out.println("> "+x.getVote()+" "+x.getKey()+" ["+x.getLeftOK()+"] ["+x.getRightKO()+"]");
//            }
//            System.out.println("THRESHOLD: "+THRESHOLD);
//        }
    }
    

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
}
