package cloudflow.core.hadoop;

import genepi.hadoop.CacheStore;
import genepi.hadoop.HadoopJob;

import java.io.IOException;

import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;

import cloudflow.core.SerializableSteps;
import cloudflow.core.operations.MapStep;
import cloudflow.core.operations.ReduceStep;

public class GenericJob extends HadoopJob {

	private Class<InputFormat<?, ?>> inputFormat;

	private Class<?> driverClass;

	public void setInputFormat(Class<InputFormat<?, ?>> inputFormat) {
		this.inputFormat = inputFormat;
	}

	public void setDriverClass(Class<?> driverClass) {
		this.driverClass = driverClass;
	}

	@Override
	public void setupJob(Job job) {

		job.setJarByClass(driverClass);
		job.setInputFormatClass(inputFormat);
		job.setMapperClass(GenericMapper.class);
		job.setMapOutputKeyClass(HadoopRecordKey.class);
		job.setMapOutputValueClass(HadoopRecordValue.class);
		job.setReducerClass(GenericReducer.class);
		job.setSortComparatorClass(HadoopRecordKeyComparator.class);
	}

	@Override
	protected void setupDistributedCache(CacheStore cache) throws IOException {

		// cache.

	}

	public GenericJob(String name) throws IOException {
		super(name);
	}

	public void setMapSteps(SerializableSteps<MapStep<?, ?>> steps) {
		set("cloudflow.steps.map", steps.serialize());
	}

	public void setMap2Steps(SerializableSteps<MapStep<?, ?>> steps) {
		set("cloudflow.steps.map2", steps.serialize());
	}

	public void setReduceSteps(SerializableSteps<ReduceStep<?, ?>> steps) {
		set("cloudflow.steps.reduce", steps.serialize());
	}

	public void setMapperOutputRecords(Class<?> mapperOutputRecordClass) {
		set("cloudflow.steps.map.output", mapperOutputRecordClass.getName());
	}

	public void setMapperOutputRecordsValue(
			Class<?> mapperOutputRecordValueClass) {
		set("cloudflow.steps.map.output.value",
				mapperOutputRecordValueClass.getName());
	}

	public void setMapperOutputRecordsKey(Class<?> mapperOutputRecordKeyClass) {
		set("cloudflow.steps.map.output.key",
				mapperOutputRecordKeyClass.getName());
	}

	public void setMapperInputRecords(Class<?> mapperInputRecordClass) {
		set("cloudflow.steps.map.input", mapperInputRecordClass.getName());
	}

}