package fr.insarennes.fafdti.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.insarennes.fafdti.visitors.XmlConst;
/**
 * Classe servant à construire le rapport html d'une campagne de test
 * à partir des résultat du {@link QueryStater} et des informations de construction
 * de l'arbre
 */
public class HtmlStater {
	private static Logger log = Logger.getLogger(HtmlStater.class);
	Map<String, String> buildopts;
	QueryStater stater;
	String output;
	
	public HtmlStater(Map<String,String> buildopts, QueryStater stater){
		this.buildopts = buildopts;
		this.stater = stater;
	}
	
	public void make(String output){
		this.output = output;
		this.launch();
	}
	
	private void launch(){
		Writer writer = null;
		try {
			writer = new FileWriter(output);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return;
		}
		PrintWriter print = new PrintWriter(writer);
		String date = (new Date()).toString();
		print.write(
		"<html>" +
				"<title>Report " + output + " generated on " + date +
				"</title>" +
				"<H2 align=center>Report generated by FAFDTIBB on " + date +
				"<br>Database : " + buildopts.get(XmlConst.DATA) + "</H2><br>" +
				"<h3 align=center> with the following parameters :</h3><br>" +
				"<TABLE BORDER=\"1\">" +
				"<TR>" +
				"<TH> names </TH>" +
				"<TH> bagging </TH>" +
				"<TH> data rate </TH>" +
				"<TH> criterion </TH>" +
				"<TH> minimum gain</TH>" +
				"<TH> minimum examples by leaf </TH>" +
				"<TH> maximum depth</TH>" +
				"<TH> built in </TH>" +
				"<TH> pool size </TH>" +
				"<TH> data file size </TH>" +
				"</TR>" +
				"<TR>" +
				"<TD> " + buildopts.get(XmlConst.NAMES) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.BAGGING) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.DATARATE) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.CRITERION) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.GAINMIN) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.MINEX) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.MAXDEPTH) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.TIME) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.THREADS) + "</TD>" +
				"<TD> " + buildopts.get(XmlConst.FILESIZE) + "</TD>" +
				"</TR>" +
				"</TABLE>");
		
		Set<Entry<String, Integer>> searchByLabel = stater.getSearchByLabel().entrySet();
		Map<String, Integer> correctByLabel = stater.getCorrectByLabel();
		Map<String, Integer> errorByLabel = stater.getErrorByLabel();
		Map<String, Integer> foundByLabel = stater.getFoundByLabel();
		double totalPrecision, totalRecall, nbLabels;
		totalPrecision=totalRecall=nbLabels=0;
		final int NB = 3;
		print.write(
				"<h3 align=center> errors statistics :</h3><br>" +
				"<TABLE BORDER=\"1\">" +
				"<TR>" +
				"<TH> label </TH>" +
				"<TH> tested </TH>" +
				"<TH> classified </TH>" +
				"<TH> correct </TH>" +
				"<TH> error </TH>" +
				"<TH> precision </TH>" +
				"<TH> recall </TH>" +
				"<TH> error rate </TH>" +
				"</TR>");
		for(Entry<String, Integer> e : searchByLabel){
			String key = e.getKey();
			int found = foundByLabel.get(key);
			int error = errorByLabel.get(key);
			int correct = correctByLabel.get(key);
			int search = e.getValue();
			nbLabels++;
			double precision = (double)correct / (double)found;
			double recall = (double)correct / (double)search;
			totalPrecision+=precision;
			totalRecall+=recall;
			int tmperr=search-found;
			if(tmperr>0)
				error+=tmperr;
			print.write(
					"<TR>" +
					"<TD> " + key + "</TD>" +
					"<TD> " + String.valueOf(search) + "</TD>" +
					"<TD> " + String.valueOf(found) + "</TD>" +
					"<TD> " + String.valueOf(correct) + "</TD>" +
					"<TD> " + String.valueOf(error) + "</TD>" +
					"<TD> " + String.valueOf(precision*100.0).substring(0,NB)+"%" + "</TD>" +
					"<TD> " + String.valueOf(recall*100.0).substring(0,NB)+"%" + "</TD>" +
					"<TD> " + String.valueOf(100.0*((double)error / (double)search)).substring(0,NB)+"%" + "</TD>" +
					"</TR>");
		}
		int totalError = stater.getTotalError();
		int totalCorrect = stater.getTotalSuccess();
		int totalSearch, totalFound;
		totalSearch = totalFound = stater.getTotal();
		print.write(
				"<TR>" +
				"<TD> total </TD>" +
				"<TD>" + String.valueOf(totalSearch) + "</TD>" +
				"<TD>" + String.valueOf(totalFound) + "</TD>" +
				"<TD>" + String.valueOf(totalCorrect) + "</TD>" +
				"<TD>" + String.valueOf(totalError) + "</TD>" +
				"<TD>" + String.valueOf(100.0*(totalPrecision / nbLabels)).substring(0,NB)+"%" + "</TD>" +
				"<TD>" + String.valueOf(100.0*(totalRecall / nbLabels)).substring(0,NB)+"%" + "</TD>" +
				"<TD>" + String.valueOf(100.0*((double)totalError / (double)totalSearch)).substring(0,NB)+"%" + "</TD>");
		print.write("</TABLE>" +
					"</html>");
		print.flush();
		print.close();
	}
}
