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
import java.util.Iterator;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import structures.ItemProfile;

public class ItemProfileReducer extends Reducer<IntWritable, BooleanWritable, IntWritable, ItemProfile>{
	/**
	 *	il metodo reduce prende in input l'output del metodo map per ogni item (key) analizza il valore di voti positivi e negativi,
	 * 	calcola la media e standard error per tale item e salva in una struttura i risultati ottenuti
	 *	@param key l'item che si sta analizzando
	 *	@param value e' un iteratore di valori associati alla key passata
	 *	@param output -
	 *	@throws IOException
	 *	@throws InterruptedException
	 */
	public void reduce(IntWritable key, Iterable <BooleanWritable> value, Context output) throws IOException, InterruptedException{
		int OK=0;
		int KO=0;
		Iterator <BooleanWritable> it = value.iterator(); 
		while(it.hasNext()){
			Boolean vote= it.next().get();
			if(vote){OK++;}
			else{ KO++;	}
		}			
		ItemProfile profile= new ItemProfile();
		profile.setMEAN(OK,KO);
		profile.setSE(OK,KO);			
		output.write(key, profile);
	}			
}