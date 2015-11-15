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

package structures;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

public class PointWritable implements Writable {
	
	private boolean vote;
	private double key;
	
	/**
	 *	il metodo TripleWritable e' il costruttore della classe prende in input vote, media e standard error, 
	 * 	crea un nuovo oggetto di tipo TripleWritable
	 * 	@param vote e' il voto espresso per l'item
	 * 	@param mean e' la media tra il numero di voti positivi e negativi ricevuti dall'item
	 * 	@param se e' lo standard error con cui prende la media dell'item
	 */
	public PointWritable(boolean vote, double key){
		this.vote=vote;
		this.key=key;
	}
	
	/**
	 * il metodo crea un nuovo oggetto di tipo TripleWritable con valori di default
	 */
	public PointWritable(){
		this(false,-1);
	}
	
	
	/**
	 * il metodo readFields prende in ingresso un DataInput permette di leggere stringhe dall' HDFS e setta le variabili vote, media e standard error con i valori acquisiti dal HDFS  
	 *	@param in provvede alla lettura di uno stream binario e genera un tipo primitivo Java
	 *	@throws IOException
	 */
	@Override
	public void readFields(DataInput in) throws IOException {
		vote=in.readBoolean();
		key=in.readDouble();
	}
/**
 * il metodo write prende in ingresso DataOutput e scrive nel HDFS ilvoto, la media e lo standard error
 *	@param out provvede alla conversione di tipi primitivi Java e li converte in una serie di byte e li scrive in uno stream binario
 *	@throws IOException
 */
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(vote);
		out.writeDouble(key);
	}
	
	/**
	 *	il metodo toString fornisce in output una descrizione dell'oggetto voto ricevuto, media e standard error dell'item 
	 */
	public String toString() {
	    return Boolean.toString(vote) + ", "
	            + Double.toString(key);
	}
	
	/**
	 * il metodo getVote restituisce in output il voto ricevuto dall'item
	 * @return il voto ricevuto dall'item. True se il voto e' positivo, false se il voto e' negativo.
	 */
	public boolean getVote(){
		return vote;
	}
	
	/**
	 * il metodo getMean restituisce in output la media dei voti positivi e i voti negativi ricevuti dall'item
	 * @return la media dei voti positivi e negativi ricevuti dall'item 
	 */
	public double getKey(){
		return key;
	}
	
}
