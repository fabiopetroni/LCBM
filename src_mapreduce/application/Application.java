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

import java.util.HashMap;

import job.ItemPDFJob;
import job.OutputReader;
import job.UserProfileJob;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

import structures.ItemProfile;
import structures.UserProfile;

// Classe Main
public class Application {
	
	/**
	 * 	il metodo main prende in ingresso gli argomenti passati da riga di comando, inizialmente svolge la fase di training
	 * 	esegue il job mapreduce per gli item, una volta ottenuto l'output del job esegue il secondo job mapreduce per gli user,
	 * 	infine svolge fase di working in cui stima la valutazione dell'utente e confronta con l'effettivo voto espresso dall'utente
	 * @param args [] la prima cella di memoria dell'array contiene l'input path, 
	 *  la seconda l'output path per l'esecuzione ItemPDFJob la terza l'output path per l'esecuzione di UserProfileJob, 
	 *  la quarta e' opzionale e contiene il valore K con cui eseguire il processo
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		long START_TIME = System.currentTimeMillis();
		
		System.out.println("\n--------------------------------------------------");
	    System.out.println(" LCBM mapreduce: a fast and lightweight collaborative filtering algorithm for binary ratings.");
	    System.out.println(" author: Fabio Petroni (http://www.fabiopetroni.com)");
	    System.out.println("--------------------------------------------------\n");
	    Globals GLOBALS = new Globals(args);
	    System.out.println(" Parameters:");
	    GLOBALS.print();
	    System.out.println("\n");
	    
	    HashMap<Integer,ItemProfile> ITEM_STATE;	
		HashMap<Integer,UserProfile> USER_STATE;	
		Configuration conf = new Configuration();
			
		//begin ITEM PROFILING PROCEDURE
        System.out.println(" JOB1 - Computing item profiles... ");
        long local_START = System.currentTimeMillis();
        ItemPDFJob ItemPDF_DRIVER = new ItemPDFJob(GLOBALS,conf);
		ToolRunner.run(conf,ItemPDF_DRIVER,args);
		long local_END = System.currentTimeMillis();
		System.out.println("\ttime item profiling procedure: "+(local_END-local_START));
        System.out.println("\n");        
        //end ITEM PROFILING PROCEDURE
        
        System.out.println(" JOB2 - Computing user profiles... ");
        local_START = System.currentTimeMillis();
		UserProfileJob UserProfiler_DRIVER = new UserProfileJob(GLOBALS,conf);
		ToolRunner.run(conf,UserProfiler_DRIVER,args);
		local_END = System.currentTimeMillis();
		System.out.println("\ttime user profiling procedure: "+(local_END-local_START));
        System.out.println("\n");
        //end USER PROFILING PROCEDURE
        
		ITEM_STATE = OutputReader.readOutputItemPDFJob(GLOBALS.getOUTPUT1(),conf);
		USER_STATE = OutputReader.readOutputUserProfileJob(GLOBALS.getOUTPUT2(),conf);
		
		//begin EVALUATION
        System.out.println("\n Evaluating algorithm performace... ");
        local_START = System.currentTimeMillis();
		RecommenderEvaluator RE = new RecommenderEvaluator(ITEM_STATE, USER_STATE, GLOBALS, conf);
		RE.evaluate();
		local_END = System.currentTimeMillis();
        double MCC = RE.getMCC();
        System.out.println("\ttime evaluator: "+(local_END-local_START));
        System.out.println("\tMCC: "+MCC);
        System.out.println("\n");
        //end EVALUATION   
				
		long END_TIME = System.currentTimeMillis();
		long TOTAL_TIME = END_TIME-START_TIME;
		System.out.println("  Total Time: "+TOTAL_TIME+"\n");
	}
}