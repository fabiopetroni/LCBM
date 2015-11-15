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

package job;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import application.Globals;
import structures.ItemProfile;
import structures.PointWritable;
import user_profile_job.*;

/**
 * @author Fabio Petroni (petroni@dis.uniroma1.it)
 */

public class UserProfileJob extends Configured implements Tool{
	
	 private static Globals GLOBALS;
	 private Configuration conf;
		
	 public UserProfileJob(Globals g, Configuration c){
		 GLOBALS = g;
		 this.conf = c;
	 }
	
	/**
	 * 	il metodo run prende in input gli argomenti passati da riga di comando e setta i parametri d'esecuzione del job mapreduce
	 * 	fornisce in output 1 in caso di successo del processo, 0 in caso di fallimento
	 * 	@param args [] la prima cella di memoria dell'array contiene l'input path, 
	 *  la seconda l'output path per l'esecuzione ItemPDFJob la terza l'output path per l'esecuzione di UserProfileJob, 
	 *  la quarta e' opzionale e contiene il valore K con cui eseguire il processo
	 *  @return lo stato di completamento del job, 0 se fallisce l'esecuzione 1 se l'esecuzione termina con successo
	 *  @throws Exception
	 */
	@Override
	public int run(String[] args) throws Exception {
		String input = GLOBALS.getTRAIN_FILE_NAME();
		String output = GLOBALS.getOUTPUT2();		
		conf.set("k", GLOBALS.getK()+"");
		conf.set("output1", GLOBALS.getOUTPUT1());		
		Job pr = new Job(conf);
		pr.setJarByClass(UserProfileJob.class);
		pr.setJobName("UserProfile Job");
		FileInputFormat.setInputPaths(pr, new Path(input));
		FileOutputFormat.setOutputPath(pr, new Path(output));
		pr.setMapperClass(UserProfileMapper.class);
		pr.setReducerClass(UserProfileReducer.class);
		pr.setMapOutputKeyClass(IntWritable.class);
		pr.setMapOutputValueClass(PointWritable.class); 
		boolean success = pr.waitForCompletion(true);
		return success ? 0 : 1;
	}
}
