package fr.insarennes.fafdti.builder;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.Path;

import fr.insarennes.fafdti.Pair;
import fr.insarennes.fafdti.builder.stopcriterion.ParentInfos;
import fr.insarennes.fafdti.builder.stopcriterion.StoppingCriterion;
import fr.insarennes.fafdti.hadoop.QuestionScoreLeftDistribution;
import fr.insarennes.fafdti.tree.DecisionNodeSetter;

public class NodeBuilderFast extends  NodeBuilder implements INodeBuilder{
	//root constructor
	public NodeBuilderFast(DotNamesInfo featureSpec, Criterion criterion, StatBuilder stats) {
		super(featureSpec, criterion, stats);
	}


	@Override
	public Pair<Path, Path> getSplitPath() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Pair<String[][], String[][]> getSplitData() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public QuestionScoreLeftDistribution buildNode(String[][] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuestionScoreLeftDistribution buildNode(Path dataPath,
			ScoredDistributionVector parentDistribution, Path workDir, String id)
			throws IOException, InterruptedException, ClassNotFoundException {
		throw new UnsupportedOperationException(this.getClass().getName()+" cannot build node with Path");
	}


	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}
}
