package edu.upenn.mkse212.pennbook.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class InitMapper extends Mapper<LongWritable, Text, Text,Text>
{
	//takes entity,entity lines (user,user or user,interest, interest,user etc.
	//outputs them split into key/value
	public void map(LongWritable key, Text value,Context context)  throws IOException,InterruptedException
	{
		String line = value.toString();
		
		String[] parts = line.split(",");
	
		String entity1 = parts[0];
		String entity2 = parts[1];
				
		context.write(new Text(entity1), new Text(entity2));	
	}
}
