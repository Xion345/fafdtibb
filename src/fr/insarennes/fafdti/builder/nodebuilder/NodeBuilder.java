package fr.insarennes.fafdti.builder.nodebuilder;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import fr.insarennes.fafdti.builder.Criterion;
import fr.insarennes.fafdti.builder.StatBuilder;
import fr.insarennes.fafdti.builder.namesinfo.DotNamesInfo;
import fr.insarennes.fafdti.builder.stopcriterion.ParentInfos;
import fr.insarennes.fafdti.builder.stopcriterion.StopCriterionUtils;
import fr.insarennes.fafdti.builder.stopcriterion.StoppingCriterion;
import fr.insarennes.fafdti.hadoop.QuestionScoreLeftDistribution;
import fr.insarennes.fafdti.tree.DecisionNodeSetter;
/**
 * Classe qui construit un noeud (ou une feuille) et fait l'appel récursif.
 * On note qu'une fois la construciton finie, dans le cas où c'est un noeud, le thread
 * lance 2 autres threads puis fini ; ses fils seront assigné grâce au mécanisme
 * de DecisionNodeSetter.
 */
public abstract class NodeBuilder {
	
	protected static Logger log = Logger.getLogger(NodeBuilder.class);

	protected Criterion criterion;
	protected DotNamesInfo namesInfo;
	protected String id;

	//First node constructor
	public NodeBuilder(DotNamesInfo namesInfo, Criterion criterion, String id){
		this.namesInfo = namesInfo;
		this.criterion = criterion;
		this.id = id;
	}
}
