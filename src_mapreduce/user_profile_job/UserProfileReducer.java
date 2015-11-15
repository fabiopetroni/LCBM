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
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

import structures.PointWritable;
import structures.UserProfile;

public class UserProfileReducer extends Reducer <IntWritable, PointWritable, IntWritable, UserProfile>{

	/**
	 *	il metodo reduce prende in input l'output fornito dal metodo map, per ogni voto espresso dall'user (key) analizza:
	 *	se il voto e' positivo somma media e standard error dell'item votato, altrimenti se il voto e' negativo sottrae
	 *	dalla media lo standard error. Infine inserisce il risultato trovato in una struttura di tipo hashMap con l'user (key) 
	 *	e il valore trovato dall'operazione(value)
	 * 	@param key e' l'user in analisi
	 * 	@param value e' un iteratore di valori associati alla key passata
	 * 	@param output -
	 * 	@throws IOException
	 * 	@throws InterruptedException
	 */
	public void reduce(IntWritable key, Iterable <PointWritable> value, Context output) throws IOException, InterruptedException{
		Iterator <PointWritable> it = value.iterator(); 
		int user = key.get();
		UserProfile profile = new UserProfile();
		while(it.hasNext()){
			PointWritable tw = it.next();
			double x = tw.getKey();
			boolean vote = tw.getVote();
			profile.insert(x,vote);
		}
		profile.train();
		output.write(key, profile);	
	}
}