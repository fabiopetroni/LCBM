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

public class UserProfile implements Writable{
    
	private DoubleWritable QUALITY_THRESHOLD;
	
    private class Node {
        double key;
        boolean vote; //True OK, False KO
        Node left;
        Node right;
        
        @SuppressWarnings("unused")
		Node parent;
        
        int number_of_left_OK;
        int number_of_right_KO; 

        public Node(double key, boolean vote) {
            this.key = key;
            this.vote = vote;
            left = null;
            right = null;
        }
    }
    
    private Node root;
    private int optimal_root_key_sum;
    private int optimal_root_key_difference;
    private Node optimal_root_value;
    
    private boolean modified;
    private boolean at_least_one_KO;
    
    public UserProfile(){
        root=null;
        optimal_root_key_sum = -1;
        optimal_root_key_difference = -1;
        optimal_root_value = null;
        modified = true;
        at_least_one_KO = false;
    }
    
    public UserProfile(double THRESHOLD){
    	QUALITY_THRESHOLD = new DoubleWritable(THRESHOLD);
    	modified = false;
    	root=null;
        optimal_root_key_sum = -1;
        optimal_root_key_difference = -1;
        optimal_root_value = null;
        at_least_one_KO = false;
    }
    
    protected boolean isEmpty(){
       return (root==null);
    }

    public void insert(double key, boolean vote){
        
        if (!vote){
            at_least_one_KO=true;
        }

        modified = true;
        if(root!=null){
            insert(key, vote, root);
        }
        else{
            root=new Node(key,vote);
            if (vote){
                root.number_of_left_OK = 1;
            }
            else{
                root.number_of_left_OK = 0;
            }
            root.number_of_right_KO = 0;
            root.parent=null;
        }
    }

    private void insert(double key, boolean vote, Node leaf){
        
        if( key < leaf.key){
            
            if (vote){
                leaf.number_of_left_OK++;
                add_left_OK(leaf.right);
            }

            if(leaf.left!=null){
                insert(key, vote, leaf.left);
            }
            else{
                leaf.left = new Node(key,vote);
                leaf.left.number_of_left_OK = leaf.number_of_left_OK;
                leaf.left.number_of_right_KO = leaf.number_of_right_KO;
                if(!leaf.vote) {
                    leaf.left.number_of_right_KO++;
                }
                if(leaf.vote) {
                    leaf.left.number_of_left_OK--;
                }
                leaf.left.parent=leaf;
            }
        }
        else if(key>leaf.key){
            if (!vote){
                leaf.number_of_right_KO++;
                add_rigth_KO(leaf.left);
            }

            if(leaf.right!=null){
                insert(key, vote, leaf.right);
            }
            else{
                leaf.right= new Node(key,vote);
                leaf.right.number_of_left_OK = leaf.number_of_left_OK;
                leaf.right.number_of_right_KO = leaf.number_of_right_KO;
                if(vote) {
                    leaf.right.number_of_left_OK++;
                }
                if(!vote) {
                    leaf.right.number_of_right_KO--;
                }
                leaf.right.parent= leaf;
            }
        }

        else if(key==leaf.key){
            //discard sample
        }
    }

    private void add_left_OK(Node leaf){
        if (leaf!=null){
            leaf.number_of_left_OK++;
            add_left_OK(leaf.left);
            add_left_OK(leaf.right);
        }
    }

    private void add_rigth_KO(Node leaf){
        if (leaf!=null){
            leaf.number_of_right_KO++;
            add_rigth_KO(leaf.left);
            add_rigth_KO(leaf.right);
        }
    }

    protected Node search(double key){
        return search(key, root);
    }

    private Node search(double key, Node leaf){
        if(leaf!=null){
            if(key==leaf.key) {
                return leaf;
            }
            if(key<leaf.key) {
                return search(key, leaf.left);
            }
            else {
                return search(key, leaf.right);
            }
        }
        else {
            return null;
        }
    }
    
    protected void in_order_print(){
        in_order_print(root);
    }

    private void in_order_print(Node leaf){
        if ( leaf != null ) {
            in_order_print( leaf.left );
            if (root==leaf){
                System.out.println("[" + leaf.key + "](" + leaf.vote + ") lOK:"+leaf.number_of_left_OK+" rKO"+leaf.number_of_right_KO);
            }
            else {
                System.out.println(leaf.key + "(" + leaf.vote + ") lOK:"+leaf.number_of_left_OK+" rKO"+leaf.number_of_right_KO);
            }
            in_order_print( leaf.right );
        }
    }

    private void padding ( char ch, int n ){
        int i;
        for ( i = 0; i < n; i++ ) {
            System.out.print( ch );
        }
    }

    private void structure( Node leaf, int level ){
        if ( leaf == null ) {
            padding( '\t', level );
            System.out.print( "~" );
        }
        else {
            structure ( leaf.right, level + 1 );
            padding ( '\t', level );
           System.out.println(leaf.key + "(" + leaf.vote + ") [" + leaf.number_of_left_OK +"," + leaf.number_of_right_KO + "] \n");
            structure ( leaf.left, level + 1 );
        }
    }

    protected void print_structure(){
        structure ( root, 0 );
    }

    protected double search_optimal_threshold(){
        if (!at_least_one_KO) {
            return 0;
        }
        if (modified){
            if (root!=null){
                
                /*
                * FUNZIONE OBIETTIVO = left_OK^2 + rigth_KO^2
                * i valori sono elevati al quadrato per dare pi valore a risultati bilanciati rispetto a quelli sbilanciati
                * ex. se non ci fosse il quadrato left_OK = 3 e rigth_KO = 1 varrebbe come left_OK = 2 e rigth_KO = 2. In questo modo il primo scenario ottiene una valore della funzione obiettivo di 10 e il secondo di 8 ed  dunque preferibile.
                */
                optimal_root_key_sum = root.number_of_left_OK + root.number_of_right_KO;
                optimal_root_key_difference = Math.abs(root.number_of_right_KO-root.number_of_left_OK);
               
                optimal_root_value = root;
                
                search_optimal_threshold(root);
                modified=false;
                //RITORNA IL KO dunque la threshold va messa >
                return optimal_root_value.key;
                //RITORNA L'OK dunque la threshold va messa >=
//                Node predecessor = predecessor(optimal_root_value);
//                if (predecessor!=null){ return predecessor.key;}
//                else if(optimal_root_value.parent!=null){return optimal_root_value.parent.key;}
//                else{return optimal_root_value.key;}
            }
            return 0;
        }
        else{
            //RITORNA IL KO dunque la threshold va messa >
            return optimal_root_value.key;
            
            //RITORNA L'OK dunque la threshold va messa >=
//            Node predecessor = predecessor(optimal_root_value);
//            if (predecessor!=null){ return predecessor.key;}
//            else if(optimal_root_value.parent!=null){return optimal_root_value.parent.key;}
//            else{return optimal_root_value.key;}
        }
    }
    
    @SuppressWarnings("unused")
	private Node predecessor(Node leaf){      
        if (leaf.left==null){
            return null;
        }      
        Node aux = leaf.left;
        
        while (aux.right != null){
            aux = aux.right;
        }
        return aux;
    }

    private void search_optimal_threshold(Node leaf){
        if ( leaf != null ){
            
            /*
             * PRIBLEMA DI MINIMIZZATIONE
             * FUNZIONE OBIETTIVO PRIMARIA = left_OK + rigth_KO
             * FUNZIONE OBIETTIVO SECONDARIA = |left_OK - rigth_KO|
             * A parit del valore della FUNZIONE OBIETTIVO PRIMARIA i nodi con FUNZIONE OBIETTIVO SECONDARIA minima vengono scelti
             * ra questi viene preso quello con chiave minore
             */
            int value_sum = leaf.number_of_left_OK+leaf.number_of_right_KO;
            int value_difference = Math.abs(leaf.number_of_right_KO-leaf.number_of_left_OK);
            
            boolean condition1 = (value_sum < optimal_root_key_sum);
            boolean condition2 = ((value_sum==optimal_root_key_sum) && (value_difference<optimal_root_key_difference));
            boolean condition3 = ((value_sum==optimal_root_key_sum) && (value_difference==optimal_root_key_difference) && (leaf.key<optimal_root_value.key));
 
            if ( condition1 || condition2 || condition3){
                optimal_root_key_sum = value_sum;
                optimal_root_key_difference=value_difference;
                optimal_root_value = leaf;
            }

            //SE IL NUMERO DI KO CHE VEDO A DESTRA  ZERO, E' INUTILE MUOVERMI IN QUELLA DIREZIONE, DI SICURO NON MIGLIORO LA FUNZIONE OBIETTIVO
            if (leaf.number_of_right_KO!=0){
                search_optimal_threshold(leaf.right);
            }
            
            //SE IL NUMERO DI OK CHE VEDO A SINISTRA  ZERO, E' INUTILE MUOVERMI IN QUELLA DIREZIONE, DI SICURO NON MIGLIORO LA FUNZIONE OBIETTIVO
            if (leaf.number_of_left_OK!=0){
                search_optimal_threshold(leaf.left);
            }
        }
    }
    
	public Boolean getPrediction(ItemProfile item) {
		double THRESHOLD = QUALITY_THRESHOLD.get();
		double MEAN = item.getMEAN();
		if(MEAN>THRESHOLD)
			return true;
		else return false;
	}
	
	public void train() {
		double t = search_optimal_threshold();
		QUALITY_THRESHOLD = new DoubleWritable(t);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		QUALITY_THRESHOLD.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		QUALITY_THRESHOLD.write(out);
	}

	@Override
	public String toString() {
		return QUALITY_THRESHOLD+"";
	}
}
