package fr.insarennes.fafdti.builder;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import fr.insarennes.fafdti.visitors.QuestionExample;

public class LabeledExample implements Writable {
	QuestionExample example;
	Text label;

	public LabeledExample() {
		this.label = new Text();
		this.example = new QuestionExample(new ArrayList<String>());
	}

	public LabeledExample(String strRepr) {
		this.label = new Text();
		fromString(strRepr);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.example.readFields(in);
		this.label.readFields(in);
		//System.out.println("Got example " + this.example);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		this.example.write(out);
		this.label.write(out);
		//System.out.println("Got example " + );
	}

	public String getLabel() {
		return this.label.toString();
	}
	
	public QuestionExample getExample() {
		return this.example;
	}

	public void fromString(String strRepr) {
		strRepr = strRepr.substring(0, strRepr.length() - 1);
		String[] lineTokens = strRepr.split(",");
		String labelStr = lineTokens[lineTokens.length-1];
		label.set(labelStr.trim());
		List<String> values = new ArrayList<String>();
		for(int i=0; i<lineTokens.length -1; i++) {
			values.add(lineTokens[i].trim());
		}
		this.example = new QuestionExample(values);
	}
	
	public String toString() {
		StringBuilder strRepr = new StringBuilder();
		for(String value: example) {
			strRepr.append(value+",");
		}
		strRepr.append(label.toString());
		strRepr.append(".");
		return strRepr.toString();
	}
}
