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

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Writable;

public class ItemProfile implements Writable{

	private DoubleWritable MEAN;
	private DoubleWritable SE;
	
	
/**
*	il metodo costruttore ItemProfile prende in ingresso come parametro la media dell'oggetto e lo standard error	
*	crea un nuovo oggetto ItemProfile
* 	@param media la media dell'item
* 	@param standard_error dell'item
*/	
	public ItemProfile(Double media, Double standard_error){
		MEAN = new DoubleWritable(media);
		SE = new DoubleWritable(standard_error);
	}

/**	
*	il metodo costruttore ItemProfile crea un nuovo oggetto ItemProfile con media e standard error inizializzati null	
*	
*/	
	public ItemProfile(){
		MEAN=null;
		SE=null;
	}

/**
*	il metodo getMean restituisce la media dell'oggetto passato	
*	@return la media dell'item passato
*/	
	public double getMEAN(){
		return MEAN.get();
	}
	
/**	
*	il metodo getSe restituisce lo standard error dell'oggetto passato	
*	@return lo standard_error dell'item passato
*/	
	public double getSE(){
		return SE.get();
	}

/**	
*	il metodo setMEAN prende in ingresso il numero di voti positivi (OK) e il numero di voti negativi (KO) ricevuti dall'item
*  	calcola e setta variabile MEAN con la media tra il numero di voti positivi e voti negativi ricevuti dall'item 
*	@param OK il numero di voti positivi ricevuti dall'item
*	@param KO il numero di voti negativi ricevuti dall'item
*/	
	public void setMEAN(double OK, double KO){
		double x = (OK+1);
		x /= (OK+KO+2);
		MEAN = new DoubleWritable(x);
	}
	
/**	
*	il metodo setSE prende in ingresso il numero di voti positivi (OK) e il numero di voti negativi (KO) ricevuti dall'item
*  	calcola e setta variabile SE con lo standard error tra il numero di voti positivi e voti negativi ricevuti dall'item 
*	@param OK il numero di voti positivi ricevuti dall'item
*	@param KO il numero di voti negativi ricevuti dall'item
*/	
	public void setSE(double OK,double KO){
		double x  = (1/(OK+KO+2)) * Math.sqrt(((OK+1)*(KO+1))/((OK+KO)*(OK+KO+3)));
		SE = new DoubleWritable(x);
	}
	
/**
*	il metodo equals prende in ingresso un Object e fornisce in output il valore True se media e standard error sono uguali alla media e standard error dell' oggetto Object passato 	
*	@param o l'oggetto con cui si vuole confrontare
*/	
	public boolean equals(Object o){
		if(o!=null && getClass().equals(o.getClass())){
			ItemProfile t = (ItemProfile) o;
			return t.MEAN==this.MEAN && t.SE==this.SE;
		}
		return false;
	}
	
/**
*	il metodo toString stampa la media e l'errore standard dell' item
*	@return la stringa con la descrizione dell' item (Media e Standard error)
*/	
	public String toString(){
		return Double.toString(MEAN.get())+"\t"+Double.toString(SE.get());
	}

/**
*	il metodo readFields prende in ingresso un DataInput permette di leggere stringhe dall' HDFS e setta le variabili media e standard error con i valori acquisiti dal HDFS  
*	@param in provvede alla lettura di uno stream binario e genera un tipo primitivo Java
*	@throws IOException
*/	
	@Override
	public void readFields(DataInput in) throws IOException {
		MEAN.readFields(in);
		SE.readFields(in);
	}

/**
*	il metodo write prende in ingresso DataOutput e scrive nel HDFS la media e lo standard error
*	@param out provvede alla conversione di tipi primitivi Java e li converte in una serie di byte e li scrive in uno stream binario
*	@throws IOException
*/	
	@Override
	public void write(DataOutput out) throws IOException {
		MEAN.write(out);
		SE.write(out);
	}
	
}
