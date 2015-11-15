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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import structures.ItemProfile;
import structures.UserProfile;

public class OutputReader {

	private final static String FIELD_SPLIT = "\t";
	private final static String INCIPIT = "part-";
	
	public static HashMap<Integer,ItemProfile> readOutputItemPDFJob(String output1, Configuration conf){
		HashMap<Integer,ItemProfile> result = new HashMap<Integer,ItemProfile>();
		try {
			FileSystem fs = FileSystem.get(conf);
		    FileStatus[] fss = fs.listStatus(new Path(output1));
		    for (FileStatus status : fss) {
		        Path path = status.getPath();		        
		        if (path.getName().startsWith(INCIPIT)){
		        	String sCurrentLine;
		        	BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
					while ((sCurrentLine = br.readLine()) != null) {
						String [] split = sCurrentLine.split(FIELD_SPLIT);
						int ID = Integer.parseInt(split[0]);
						double MEAN = Double.parseDouble(split[1]);
						double SE = Double.parseDouble(split[2]);
//						System.out.println("ID = "+ID);
//						System.out.println("MEAN = "+MEAN);
//						System.out.println("SE = "+SE);
						result.put(ID, new ItemProfile(MEAN,SE));
					}
					br.close();
		        }
		    }
		} catch (Exception e) {
			System.out.println("Exception "+e);
			e.printStackTrace();
			System.exit(-1);
		} 
	    return result;
	}
	
	public static HashMap<Integer,UserProfile> readOutputUserProfileJob(String output2, Configuration conf){
	HashMap<Integer,UserProfile> result = new HashMap<Integer,UserProfile>();
	try {
		FileSystem fs = FileSystem.get(conf);
	    FileStatus[] fss = fs.listStatus(new Path(output2));
	    for (FileStatus status : fss) {
	        Path path = status.getPath();		        
	        if (path.getName().startsWith(INCIPIT)){
	        	String sCurrentLine;
				BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(path)));
				while ((sCurrentLine = br.readLine()) != null) {
					String [] split = sCurrentLine.split(FIELD_SPLIT);
					int ID = Integer.parseInt(split[0]);
					double THRESHOLD = Double.parseDouble(split[1]);
					result.put(ID, new UserProfile(THRESHOLD));
				}
				br.close();
	        }
	    }
	} catch (Exception e) {
		System.out.println("Exception "+e);
		e.printStackTrace();
		System.exit(-1);
	} 
	return result;
}
}
