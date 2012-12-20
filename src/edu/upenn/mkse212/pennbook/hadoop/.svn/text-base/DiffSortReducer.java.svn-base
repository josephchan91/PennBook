package edu.upenn.mkse212.pennbook.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Sort the differences found, writing only the maximum difference.
 * @author lixiao1
 *
 */
public class DiffSortReducer extends Reducer<IntWritable,DoubleWritable,Text,DoubleWritable>
{
	public static final double D = 0.15;
	public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException 
	{
		ArrayList<Double> diffs = new ArrayList<Double>();//list of differences computed
		for (DoubleWritable diffValue : values)
		{
			diffs.add(diffValue.get());
		}
		
		Collections.sort(diffs);
		double maxDiff = diffs.get(diffs.size()-1);//get last value (largest diff)
		
		context.write(null, new DoubleWritable(maxDiff));
	}
}
