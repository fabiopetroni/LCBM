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

public class Rating implements Comparable{
    private final double key;
    private final boolean vote; //True OK, False KO
    
    private int leftOK;
    private int rightKO; 

    public Rating(double key, boolean vote) {
        this.key = key;
        this.vote = vote;
    }

    public double getKey() {
        return key;
    }

    public boolean getVote() {
        return vote;
    }

    public int getLeftOK() {
        return leftOK;
    }

    public void setLeftOK(int leftOK) {
        this.leftOK = leftOK;
    }

    public int getRightKO() {
        return rightKO;
    }

    public void setRightKO(int rightKO) {
        this.rightKO = rightKO;
    }    

    @Override
    public int compareTo(Object o) {
        if (o==null || o.getClass()!=this.getClass()){
            System.out.println("Rating.compareTo ERROR");
            System.exit(-1);
        }
        Rating r = (Rating) o;
        if (key > r.key){ return 1;}
        else if (key < r.key){ return -1;}
        else return 0;
    }
}
