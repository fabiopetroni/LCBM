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
import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import structures.ItemProfile;
import structures.UserProfile;

public class RecommenderEvaluator {

	/*  CM = Confusion Matrix
	 * 	CM[0][0] TP (True Positive)
	 * 	CM[0][1] FN (False Negative)
	 * 	CM[1][0] FP (False Positive)
	 * 	CM[1][1] TN (True Negative)*/
	private int [][] CM;
	private int skipped_evaluation;
	private Globals GLOBALS;
	private HashMap<Integer,ItemProfile> ITEM_STATE;	
	private HashMap<Integer,UserProfile> USER_STATE;
	private Configuration conf;
	
	RecommenderEvaluator(HashMap<Integer,ItemProfile> ITEM_STATE , HashMap<Integer,UserProfile> USER_STATE, Globals GLOBALS, Configuration c){
		this.CM = new int[2][2];
		this.skipped_evaluation = 0;
		this.GLOBALS = GLOBALS;
		this.ITEM_STATE = ITEM_STATE;
		this.USER_STATE = USER_STATE;
		this.conf = c;
	}
	
	/**
	 * 	il metodo evaluate prende in ingresso una collezione di item e media e standard error ad esso associato, 
	 * 	una collezione di user che ha come valore una collezione delle medie degli item per i quali ha espresso un voto,
	 * 	una stringa che contiene il path ad un file di testo (#user,#item,#vote) per effettuare i test. Il metodo fornisce in output la matrice di confusione cosi' formata 
	 * 	nella cella [0][0] ci sara' il numero di volte in cui il voto e' stato positivo e la previsione e' stata positiva (True Positive)
	 * 	nella cella [0][1] ci sara' il numero di volte in cui il voto e' stato positivo e la previsione e' stata negativa (False Negative)
	 * 	nella cella [1][0] ci sara' il numero di volte in cui il voto e' stato negativo e la previsione e' stata positiva (False Positive)
	 * 	nella cella [1][1] ci sara' il numero di volte in cui il voto e' stato negativo e la previsione e' stata negativa (True Negative)
	 * 	@param Stato_Item e' una collezione di item con media e standard error a ciascuno di esso associato
	 * 	@param Stato_User e' una collezione di user con valore una collezione delle medie degli item per i quali l'user ha espresso un voto
	 * 	@param s e' una stringa che contiene il path ad un file di testo per effettuare i test
	 * 	@return una matrice di confusione con il numero di TruePositive, FalsePositive, FalseNegative, TrueNegative ottenuti.
	 * 	@throws IOException
	 */
	public void evaluate(){
		String test_file = GLOBALS.getTEST_FILE_NAME();
		String split = GLOBALS.getSPLIT_TOKEN();
		try{
			FileSystem fs = FileSystem.get(conf);
			BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(new Path(test_file))));
			String line;
			while((line=br.readLine())!=null){
				StringTokenizer st = new StringTokenizer(line,split);
				int user=Integer.parseInt(st.nextToken());
				int item=Integer.parseInt(st.nextToken());
				int vote=Integer.parseInt(st.nextToken());
				UserProfile UP = USER_STATE.get(user);
				ItemProfile IP = ITEM_STATE.get(item);
				if (UP==null || IP==null){
					skipped_evaluation++;
				}
				else{
					
					Boolean prediction = estimatePreference(UP,IP);
					if (prediction==null){skipped_evaluation++; continue;}
					
//					System.out.println("prediction:"+prediction+", vote:"+vote);
					if(prediction){
						if(vote == 1){ CM[0][0] += 1; } //TP
						else if(vote == -1){ CM[1][0] += 1; }//FP
					}
					else{
						if(vote == -1){ CM[1][1] += 1; }//TN
						else if(vote == 1){ CM[0][1] += 1; }//FN
					}
				}
			}
			br.close();
			
			//STAT
//			System.out.println("TP: "+CM[0][0]);
//			System.out.println("FN: "+CM[0][1]);
//			System.out.println("FP: "+CM[1][0]);
//			System.out.println("TN: "+CM[1][1]);
//			System.out.println("SKYPPED: "+skipped_evaluation);
		}catch(Exception e){
			System.out.println("Exception "+e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 *  il metodo estimatePreference prende in ingresso l'utente di cui vuole stimare se piacera' un item ed un oggetto da stimare,
	 *  fornisce in output la differenza tra la media della distribuzione beta associata all'oggetto e la soglia di qualita' dell'utente
	 * @param user l'utente da analizzare
	 * @param item l'oggetto sta stimare
	 * @return la predizione per del voto che l'utente esprimera' per l'oggetto. Un valore positivo se l'oggetto piace, altrimenti un valore negativo
	 */
	private Boolean estimatePreference(UserProfile user,ItemProfile item){
		return user.getPrediction(item);
	}
	
	public int getSkipped_evaluation() {
		return skipped_evaluation;
	}

	/**
	 * 	il metodoMCC prende in ingresso la matrice di confusione, fornisce in output il Coefficiente di Correlazione di Matthews (MCC) misura la qualita' di classificazione binaria
	 * 	@return un valore compreso tra -1 e 1 in cui -1 indica un totale disaccordo tra predizione e osservazione dell'evento ed 1 indica un totale accordo tra predizione e osservazione dell'evento
	 */
	public double getMCC(){
		double TP = CM[0][0];
		double TN = CM[1][1];
		double FN = CM[0][1];
		double FP = CM[1][0];
		double NUM = (TP*TN)-(FP*FN);
		double DEN =Math.sqrt((TP+FP)*(TP+FN)*(TN+FP)*(TN+FN));
		double MCC = NUM;
		MCC /= DEN;
		return MCC;
	}
}
