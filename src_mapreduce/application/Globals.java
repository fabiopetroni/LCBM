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

package application;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class Globals implements Writable {
    
	private Text OUTPUT1 = new Text("output1");
	private Text OUTPUT2 = new Text("output2");
	private Text TRAIN_FILE_NAME;
	private Text TEST_FILE_NAME;
	private IntWritable K = new IntWritable(2);
	private Text SPLIT_TOKEN = new Text(",");
	
	
    public Globals(String[] args){
        parse_arguments(args);
        
      //remove output directories, if executed locally (not on hdfs)
  		File directory = new File(getOUTPUT1());
      	if(directory.exists()){
             try{
                 delete(directory);
             }catch(IOException e){
                 e.printStackTrace();
                 System.exit(-1);
             }
          }
      	directory = new File(getOUTPUT2());
      	if(directory.exists()){
             try{
                 delete(directory);
             }catch(IOException e){
                 e.printStackTrace();
                 System.exit(-1);
             }
          }
    }
    
    public String getOUTPUT1() {
		return OUTPUT1.toString();
	}
	public void setOUTPUT1(String oUTPUT1) {
		OUTPUT1 = new Text(oUTPUT1);
	}
	public String getOUTPUT2() {
		return OUTPUT2.toString();
	}
	public void setOUTPUT2(String oUTPUT2) {
		OUTPUT2 = new Text(oUTPUT2);
	}
	public String getTRAIN_FILE_NAME() {
		return TRAIN_FILE_NAME.toString();
	}
	public void setTRAIN_FILE_NAME(String tRAIN_FILE_NAME) {
		TRAIN_FILE_NAME = new Text(tRAIN_FILE_NAME);
	}
	public String getTEST_FILE_NAME() {
		return TEST_FILE_NAME.toString();
	}
	public void setTEST_FILE_NAME(String tEST_FILE_NAME) {
		TEST_FILE_NAME = new Text(tEST_FILE_NAME);
	}
	public int getK() {
		return K.get();
	}
	public void setK(int k) {
		K = new IntWritable(k);
	}
	public String getSPLIT_TOKEN() {
		return SPLIT_TOKEN.toString();
	}
	public void setSPLIT_TOKEN(String sPLIT_TOKEN) {
		SPLIT_TOKEN = new Text(sPLIT_TOKEN);
	}
	
	private void parse_arguments(String[] args){
        try{
        	setTRAIN_FILE_NAME(args[0]);
        	setTEST_FILE_NAME(args[1]);   
            for(int i=2; i < args.length; i+=2){
                if(args[i].equalsIgnoreCase("-k")){
                	setK(Integer.parseInt(args[i+1]));
                }
                else if(args[i].equalsIgnoreCase("-split_token")){
                	setSPLIT_TOKEN(args[i+1]);
                }
                else if(args[i].equalsIgnoreCase("-output1")){
                	setOUTPUT1(args[i+1]);
                }
                else if(args[i].equalsIgnoreCase("-output2")){
                	setOUTPUT2(args[i+1]);
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
            System.out.println(" -split_token char");
            System.out.println("\t specifies the character that splits the dataset.");
            System.out.println(" -output1 string");
            System.out.println("\t specifies the name of the first output directory in the hdfs.");
            System.out.println(" -output1 sting");
            System.out.println("\t specifies the name of the second output directory in the hdf.");
            System.exit(-1);
        }
    }
    
    public static void delete(File file) throws IOException{ 
    	if(file.isDirectory()){
    		//directory is empty, then delete it
    		if(file.list().length==0){
    		   file.delete();
    		   //System.out.println("Directory is deleted : "+ file.getAbsolutePath());
    		}else{
    		   //list all the directory contents
        	   String files[] = file.list();
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
        	      //recursive delete
        	     delete(fileDelete);
        	   }
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
           	     file.delete();
        	     //System.out.println("Directory is deleted : "+ file.getAbsolutePath());
        	   }
    		}
    	}else{
    		//if file, then delete it
    		file.delete();
    		//System.out.println("File is deleted : " + file.getAbsolutePath());
    	}
	}
    
    public void print(){
        System.out.println("\tTrain: "+TRAIN_FILE_NAME);
        System.out.println("\tTest: "+TEST_FILE_NAME);
        System.out.println("\tk: "+K);
        System.out.println("\tsplit_token: "+SPLIT_TOKEN);
        System.out.println("\toutput1: "+OUTPUT1);
        System.out.println("\toutput2: "+OUTPUT2);
    }

	@Override
	public void readFields(DataInput in) throws IOException {
		OUTPUT1.readFields(in);
		OUTPUT2.readFields(in);
		TRAIN_FILE_NAME.readFields(in); 
		TEST_FILE_NAME.readFields(in);
		K.readFields(in);
		SPLIT_TOKEN.readFields(in); 
	}

	@Override
	public void write(DataOutput out) throws IOException {
		OUTPUT1.write(out);
		OUTPUT2.write(out);
		TRAIN_FILE_NAME.write(out); 
		TEST_FILE_NAME.write(out);
		K.write(out);
		SPLIT_TOKEN.write(out); 
	}
}
