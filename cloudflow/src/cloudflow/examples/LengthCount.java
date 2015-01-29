package cloudflow.examples;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;

import cloudflow.Pipeline;
import cloudflow.core.hadoop.RecordValues;
import cloudflow.core.io.TextLoader;
import cloudflow.core.operations.MapStep;
import cloudflow.core.operations.ReduceStep;
import cloudflow.core.records.Record;
import cloudflow.core.records.TextRecord;

public class LengthCount {

	static public class MyRecord extends Record<IntWritable, IntWritable>{
		
		public MyRecord() {
			setWritableKey(new IntWritable());
			setWritableValue(new IntWritable());
		}

		public int getValue() {
			return getWritableValue().get();
		}

		public void setValue(int value) {
			getWritableValue().set(value);
		}

		public int getKey() {
			return getWritableKey().get();
		}

		public void setKey(int key) {
			getWritableKey().set(key);
		}
		
	}
	
	static public class SplitByWordLength extends MapStep<TextRecord, MyRecord> {

		private MyRecord outRecord = new MyRecord();

		@Override
		public void process(TextRecord record) {

			String[] tiles = record.getValue().split(" ");
			for (String tile : tiles) {
				outRecord.setKey(tile.length());
				outRecord.setValue(1);
				createRecord(outRecord);
			}

		}

	}

	static public class CountWordLength extends
			ReduceStep<MyRecord, MyRecord> {

		private MyRecord outRecord = new MyRecord();

		@Override
		public void process(String key, RecordValues<MyRecord> values) {

			int sum = 0;
			while (values.hasNextRecord()) {
				int intValue = values.getRecord().getValue();
				sum += intValue;
			}
			outRecord.setKey(Integer.parseInt(key));
			outRecord.setValue(sum);
			createRecord(outRecord);
		}

	}

	public static void main(String[] args) throws IOException {

		String input = args[0];
		String output = args[1];

		Pipeline pipeline = new Pipeline("Wordcount-Length!", LengthCount.class);

		pipeline.load(input, new TextLoader());

		pipeline.perform(SplitByWordLength.class, MyRecord.class)
				.groupByKey()
				.perform(CountWordLength.class);

		pipeline.save(output);

		boolean result = pipeline.run();
		if (!result) {
			System.exit(1);
		}
	}
}
