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
import item_profile_job.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import application.Globals;

public class ItemPDFJob extends Configured implements Tool{
	
	private Globals GLOBALS;
	private Configuration conf;
	
	public ItemPDFJob(Globals G, Configuration c){
		this.GLOBALS = G;
		this.conf = c;
	}
	 
/**
 *  il metodo run prende in input i parametri passati da riga di comando e setta i parametri d'esecuzione mapreduce
 *  fornisce in output 1 in caso di successo, 0 in caso di fallimento
 *  @param args [] la prima cella di memoria dell'array contiene l'input path, 
 *  la seconda l'output path per l'esecuzione ItemPDFJob la terza l'output path per l'esecuzione di UserProfileJob, 
 *  la quarta e' opzionale e contiene il valore K con cui eseguire il processo
 *  @return lo stato di completamento del job, 0 se fallisce l'esecuzione 1 se l'esecuzione termina con successo
 *  @throws Exception
 */
	@Override
	public int run(String[] args) throws Exception {
		String input = GLOBALS.getTRAIN_FILE_NAME();
		String output = GLOBALS.getOUTPUT1();		
		conf.set("split", GLOBALS.getSPLIT_TOKEN());		
		Job pr = new Job (conf);
		pr.setJarByClass(ItemPDFJob.class);
		pr.setJobName("ItemPDF Job");
		FileInputFormat.setInputPaths(pr, new Path(input));
		FileOutputFormat.setOutputPath(pr, new Path(output));
		pr.setMapperClass(ItemProfileMapper.class);
		pr.setReducerClass(ItemProfileReducer.class);
		pr.setMapOutputKeyClass(IntWritable.class);
		pr.setMapOutputValueClass(BooleanWritable.class);
		boolean success = pr.waitForCompletion(true);
		return success ? 0 : 1;
	}		
}
