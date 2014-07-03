package org.myorg;

import java.io.IOException;
import java.util.Iterator;
import java.util.Calendar; 

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;



import util.hashing.Geohash;

public class LatLong {
	
        
 public static class Map extends Mapper<Object, Text, Text, Text> {
    //private final static IntWritable one = new IntWritable(1);
    
	public static boolean checkNotNULL(String s)
	{
		return (s.compareTo("")!=0 && s.compareTo("\\N")!=0);
	}
    int A;
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] longLatArray = line.split("\t");
        
        if (checkNotNULL(longLatArray[A]) && checkNotNULL(longLatArray[A]) 
        		&& checkNotNULL(longLatArray[A]) && checkNotNULL(longLatArray[A]) 
        		&& checkNotNULL(longLatArray[A]))
        		{
			        double longi = Double.parseDouble(longLatArray[A]);
			        double lat = Double.parseDouble(longLatArray[A]);
			        int S = Integer.parseInt(longLatArray[A]);
			        String l = longLatArray[A];
			        int blah =  Integer.parseInt(longLatArray[A]);
			        Calendar rN = Calendar.getInstance();
			        String rN2yString =  Integer.toString(rN.get(Calendar.YEAR)-2)+Integer.toString(rN.get(Calendar.MONTH)+1) 
			        		+ Integer.toString(rN.get(Calendar.DAY_OF_MONTH));
			               
			        if((S == A || S == A) && blah == 1 && l.compareTo(rN2yString)>0)
			        {
			        	Geohash inst = Geohash.getInstance();
			            //encode is the library's encoding function
			            String hash = inst.encode(lat,longi);
			            //Using the first 5 characters just for testing purposes
			            //Need to find the right one later
			            int accuracy = 4;
			            //hash of the thing is shortened to whatever I figure out
			            //to be the right size of each tile
			            Text origHash = new Text(hash);
			            if(hash.length()>=accuracy)
			            {
			            	Text shortenedHash = new Text(hash.substring(0,accuracy));
			            	context.write(shortenedHash, origHash);
			            }
			            else context.write(origHash,origHash);
			        }
        		}
			        
         
    }
 } 
        
 public static class Reduce extends Reducer<Text, Text, Text, Text> {
	 
	 private IntWritable totalTileElementCount = new IntWritable();
	 private Text latlongimag = new Text();
	 private Text dataSeparator = new Text();
	 
	 @Override
	 public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	  int elementCount = 0;
	  boolean first = true;
	  Iterator<Text> it = values.iterator();
	  String lat = new String();
	  String longi = new String();
	  Geohash inst = Geohash.getInstance();
	  
	  while (it.hasNext()) {
	   elementCount = elementCount+1;
	   if(first)
	   {
		   double[] doubleArray = (inst.decode(it.next().toString()));
		   lat = Double.toString(doubleArray[0]);
		   longi = Double.toString(doubleArray[1]);
		   first = false;
			  
	   }
	   else
	   {
		   @SuppressWarnings("unused")
		   String blah = it.next().toString();	   
	   }
  	   
	  }
	  totalTileElementCount.set(elementCount);
	  //Geohash inst = Geohash.getInstance();
	  
	  String mag = totalTileElementCount.toString();
	  	  
	  latlongimag.set(lat+","+ longi +","+mag+",");
	  dataSeparator.set("");
	  context.write(latlongimag, dataSeparator );
	 }
 }
         
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = new Job(conf, "latlong");
    job.setJarByClass(LatLong.class);
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    job.waitForCompletion(true);
 }
        
}