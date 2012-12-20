package edu.upenn.mkse212.pennbook.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * emit nodeNumber	 node,adsorp. val.,node, adsop. val. for each node in either of two intermediate folder
 * @author lixiao1
 *
 */
public class DiffMapper extends Mapper<LongWritable, Text, Text,Text>
{
	public void map(LongWritable key, Text value,Context context)  throws IOException,InterruptedException
	{
		String line = value.toString();
		String[] parts = line.split("\t");
		String[] dataParts = parts[1].split(";");
		String ranks = "";
		if (dataParts.length>2)
			ranks = dataParts[2];
		//emit node, ranks
		context.write( new Text(parts[0]),new Text(ranks));	
	}
}
