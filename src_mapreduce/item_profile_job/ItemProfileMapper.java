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

package item_profile_job;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class ItemProfileMapper extends Mapper <LongWritable, Text, IntWritable, BooleanWritable>{
	
	private static String split;
	
	@Override
	public void setup(Context context) {
		split = context.getConfiguration().get("split");
	}
/**
*  il metodo map prende come parametro di input un file di testo (#user,#item,#vote) per ogni riga del file prende l'item e il vote, se il voto e' positivo scrive nell'HDFS (item true) altrimenti scrive (item false)
*	@param key indica i byte di offset della riga correntemente letta dal file
*	@param value riga letta
*	@param output -
*	@throws IOException
*	@throws InterruptedException
*/	
	public void map(LongWritable key, Text value, Context output) throws IOException, InterruptedException{
		
		String line=value.toString();
		StringTokenizer st = new StringTokenizer(line,split);
		if(st.hasMoreTokens()){	
			@SuppressWarnings("unused")
			int user=Integer.parseInt(st.nextToken());
			int item=Integer.parseInt(st.nextToken());
			int voto=Integer.parseInt(st.nextToken());
			Boolean bool_vote = null;
			
			if(voto == 1){ bool_vote = true; }
			else if(voto == -1){ bool_vote = false; }
			
			output.write(new IntWritable(item),new BooleanWritable(bool_vote));
		}
	}
}