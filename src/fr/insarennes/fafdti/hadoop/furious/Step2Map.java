package fr.insarennes.fafdti.hadoop.furious;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

import fr.insarennes.fafdti.FAFException;
import fr.insarennes.fafdti.builder.LabeledExample;
import fr.insarennes.fafdti.builder.Question;
import fr.insarennes.fafdti.builder.ScoreLeftDistribution;
import fr.insarennes.fafdti.builder.ScoredDistributionVector;
import fr.insarennes.fafdti.builder.namesinfo.DotNamesInfo;
import fr.insarennes.fafdti.hadoop.MapperBase;
import fr.insarennes.fafdti.hadoop.QuestionScoreLeftDistribution;

public class Step2Map extends
		MapperBase<Object, Text, Text, QuestionScoreLeftDistribution> {

	protected void map(Object key, Text dataLine, Context context)
			throws IOException, InterruptedException {
		String[] tokens = dataLine.toString().split("\t", 2);
		Question question = new Question(tokens[0]);
		ScoreLeftDistribution scoreLeftDist = new ScoreLeftDistribution(tokens[1]);
		QuestionScoreLeftDistribution qSLDist = 
				new QuestionScoreLeftDistribution(question, scoreLeftDist);
		context.write(new Text("best"), qSLDist);
	}
}
