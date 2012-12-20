package edu.upenn.mkse212.pennbook.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * mapper for finish job--emits (-1*rank, node number) from intermediate format lines
 * @author lixiao1
 *
 */
public class FinishMapper extends Mapper<LongWritable, Text,Text,Text>
{
	public void map(LongWritable key, Text value,Context context)  throws IOException,InterruptedException
	{
		String line = value.toString();
		String[] parts = line.split("\t");
		//System.out.println("finish"+line);
		//take original input lines to collect existing friends
		if (parts.length<=1) {
			parts = line.split(",");
			System.out.println(parts[0]+"\tEXISTING;"+parts[1]);
			context.write(new Text(parts[0]), new Text("EXISTING;"+parts[1]));
		} else
			context.write( new Text(parts[0]),new Text(parts[1]));	
		}
}
