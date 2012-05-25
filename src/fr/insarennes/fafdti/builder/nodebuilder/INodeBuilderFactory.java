package fr.insarennes.fafdti.builder.nodebuilder;

import org.apache.hadoop.fs.Path;

import fr.insarennes.fafdti.FAFException;
import fr.insarennes.fafdti.builder.ScoredDistributionVector;

public interface INodeBuilderFactory {
	public INodeBuilder makeNodeBuilder(String[][] database, ScoredDistributionVector parentDistribution,
			String id) throws FAFException;
	public INodeBuilder makeNodeBuilder(Path dataPath,
			ScoredDistributionVector parentDistribution, String id, Path workdir);
}
