package edu.upenn.mkse212.pennbook.hadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.upenn.mkse212.pennbook.server.RecommendationServiceImpl;

public class RecommendationLoader {
	static final String PATH = "recs";
	
	public static void main(String[] args) throws IOException
	{
		String path = PATH;
			File folder = new File(path);
			File[] files = folder.listFiles();
		
			for (int i=0; i<files.length; i++)
			{
				BufferedReader d = new BufferedReader(new FileReader(files[i]));
				  String content = d.readLine();
				  while (content!=null) {
					  String[] parts = content.split("\t");
					  RecommendationServiceImpl recServ = new RecommendationServiceImpl();
					  //System.out.println(parts[0]+" "+ parts[1]);
					  content = d.readLine();
					  recServ.setRecommendations(parts[0], parts[1]);
					  }
				  d.close();
			}
		  
		  
	}
}
