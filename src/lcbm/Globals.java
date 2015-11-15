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

public class Globals {
    public String TRAIN_FILE;
    public String TEST_FILE;
    public int k = 2;
    public int THREADS = Runtime.getRuntime().availableProcessors();
    
    public Globals(String[] args){
        parse_arguments(args);
    }
    
    private void parse_arguments(String[] args){
        try{
            TRAIN_FILE = args[0];
            TEST_FILE = args[1];   
            for(int i=2; i < args.length; i+=2){
                if(args[i].equalsIgnoreCase("-k")){
                    k = Integer.parseInt(args[i+1]);
                }
                else if(args[i].equalsIgnoreCase("-threads")){
                    THREADS = Integer.parseInt(args[i+1]);
                }
                else throw new IllegalArgumentException();
            }
        } catch (Exception e){
            System.out.println("\nInvalid arguments ["+args.length+"]. Aborting.\n");
            System.out.println("Usage:\n LCBM train test [options]\n");
            System.out.println("Parameters:");
            System.out.println(" train: the name of the file with the train data.");
            System.out.println(" test: the name of the file with the test data.");
            System.out.println("\nOptions:");
            System.out.println(" -k int");
            System.out.println("\t specifies the multiplicative factor for the SE.");
            System.out.println(" -threads integer");
            System.out.println("\t specifies the number of threads used by the application. Default all available processors.");
            System.exit(-1);
        }
    }
    
    public void print(){
        System.out.println("\tTrain: "+TRAIN_FILE);
        System.out.println("\tTest: "+TEST_FILE);
        System.out.println("\tk: "+k);
        System.out.println("\tthreads: "+THREADS);
    }
}
