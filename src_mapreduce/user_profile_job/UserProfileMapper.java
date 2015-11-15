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

package user_profile_job;

import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import job.OutputReader;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import structures.ItemProfile;
import structures.PointWritable;
import org.apache.hadoop.conf.Configuration;

public class UserProfileMapper extends Mapper <LongWritable, Text, IntWritable, PointWritable>{
	
	private static int k;
	private static String output1;
	private static HashMap<Integer,ItemProfile> ITEM_STATE;
	
	@Override
	public void setup(Context context) {
		k = Integer.parseInt(context.getConfiguration().get("k"));
		Configuration conf = context.getConfiguration();
		output1 = conf.get("output1");
		ITEM_STATE = OutputReader.readOutputItemPDFJob(output1,conf);
	}
	
	/**
	 * 	il metodo map prende come parametri di input un file di testo (#user,#item,#vote) ed una struttura dati 
	 * 	di tipo hashMap con chiave item e valore media e standard error con cui e' stato votato l'item. Il metodo
	 * 	in output scrive nell'HDFS user (la key) ed una tripla formata dal voto espresso dall'user la media e lo 
	 * 	standard error per l'item votato
	 * 	@param key indica i byte di offset della riga corrente letta del file
	 * 	@param value e' il contenuto del riga correntemente letta dal file
	 * 	@param output -
	 * 	@throws IOException
	 * 	@throws InterruptedException
	 */
	public void map(LongWritable key, Text value, Context output) throws IOException, InterruptedException{
		String line=value.toString();
		StringTokenizer st= new StringTokenizer(line,",");
		int user = Integer.parseInt(st.nextToken());
		int item = Integer.parseInt(st.nextToken());
		int vote = Integer.parseInt(st.nextToken());
		ItemProfile profile = ITEM_STATE.get(item);
		double xkey;
		boolean bin_vote;
		if(vote>0){ xkey = profile.getMEAN() + k * profile.getSE(); bin_vote=true;}
		else{ xkey = profile.getMEAN() - k * profile.getSE(); bin_vote=false;}
		output.write(new IntWritable(user), new PointWritable(bin_vote, xkey));
	}
}