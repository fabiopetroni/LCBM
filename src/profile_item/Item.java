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

package profile_item;

public class Item {

    private final int id;
    private int OK;
    private int KO;
    private double MEAN;
    private double SE;

    public Item(int i) {
        this.id = i;
        this.OK = 0;
        this.KO = 0;
    }

    public int getId() {
        return id;
    }

    public double getMEAN() {
        return MEAN;
    }

    public double getSE() {
        return SE;
    }
    
    public synchronized void addRating(boolean vote){
        if (vote){ OK++;}
        else{ KO++; }
    }
    
    public void computeProfile(){
        computeMEAN();
        computeSE();
    }
    
    private void computeMEAN(){
        MEAN = (OK+1);
        MEAN /= (OK+KO+2);
    }
    
    private void computeSE(){
	SE  = (1/(OK+KO+2)) * Math.sqrt(((OK+1)*(KO+1))/((OK+KO)*(OK+KO+3)));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + this.id;
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
        final Item other = (Item) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }   
}
