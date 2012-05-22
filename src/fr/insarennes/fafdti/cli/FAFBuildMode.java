/**
 * Point d'entrée du mode de construction de FAFDTIBB
 */

package fr.insarennes.fafdti.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.insarennes.fafdti.bagging.Launcher;
import fr.insarennes.fafdti.builder.*;
import fr.insarennes.fafdti.builder.stopcriterion.DepthMax;
import fr.insarennes.fafdti.builder.stopcriterion.ExampleMin;
import fr.insarennes.fafdti.builder.stopcriterion.GainMin;
import fr.insarennes.fafdti.builder.stopcriterion.StoppingCriterion;
import fr.insarennes.fafdti.visitors.XmlConst;

public class FAFBuildMode {

	static Logger log = Logger.getLogger(FAFBuildMode.class);

	public static final String APP_NAME = "fafbuild";
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 0;
	public static final String HEAD_USAGE = "java -jar " + APP_NAME
			+ MAJOR_VERSION + "." + MINOR_VERSION + ".jar";

	// options
	public static final String NAMES = "names";
	public static final String DATA = "data";
	public static final String OUT = "output";
	public static final String WORKINGDIR = "workdir";
	public static final String BAGGING = "bagging";
	public static final String CRITERION = "criterion";
	public static final String MAXDEPTH = "maxdepth";
	public static final String MINEXBYLEAF = "minex";
	public static final String GAINMIN = "gainmin";
	public static final String PERCENTBAGGING = "percent";
	public static final String THREADS = "threads";

	public static Options opts;

	// criterion names constants
	public static final String ENTROPY = "entropy";
	public static final String GINI = "gini";

	// default values constants
	public static final String DEFAULT_WORKING_DIR = "./working_dir";
	public static final String DEFAULT_CRITERION = ENTROPY;
	public static final String DEFAULT_BAGGING = "1";
	public static final String DEFAULT_MINEX = "1";
	public static final String DEFAULT_GAINMIN = "10e-3";
	public static final String DEFAULT_MAXDEPTH = Integer.MAX_VALUE + "";
	public static final String DEFAULT_PERCENTBAGGING = "0.6";

	public static void initOptions() {
		opts = new Options();
		Option o1 = new Option(NAMES.substring(0, 1), NAMES, true,
				"Set .names filename");
		Option o2 = new Option(DATA.substring(0, 1), DATA, true,
				"Set .data filename");
		Option o3 = new Option(OUT.substring(0, 1), OUT, true,
				"Set output filename (optional)");
		Option o4 = new Option(BAGGING.substring(0, 1), BAGGING, true,
				"Set number of trees built for bagging (optional)");
		Option o5 = new Option(CRITERION.substring(0, 1), CRITERION, true,
				"Set the criterion used to build the tree (optional)");
		Option o6 = new Option(MAXDEPTH.substring(0, 1).toUpperCase(),
				MAXDEPTH, true,
				"Set the maximum number of leaves for one tree (optional)");
		Option o7 = new Option(MINEXBYLEAF.substring(0, 1), MINEXBYLEAF, true,
				"Set the minimum number of examples by leaf (optional)");
		Option o8 = new Option(GAINMIN.substring(0, 1), GAINMIN, true,
				"Set the minimum gain to make a node (optional)");
		Option o9 = new Option(WORKINGDIR.substring(0, 1), WORKINGDIR, true,
				"Set the directory where hadoop will work (optional)");
		Option o10 = new Option(
				PERCENTBAGGING.substring(0, 1),
				PERCENTBAGGING,
				true,
				"Set the percentage of data file (between 0 and 1) used to build each trees (optional)");
		Option o11 = new Option(THREADS.substring(0, 1), THREADS, true,
				"Set pool size of scheduler (optional)");
		o1.setRequired(true);
		o2.setRequired(true);
		opts.addOption(o1);
		opts.addOption(o2);
		opts.addOption(o3);
		opts.addOption(o4);
		opts.addOption(o5);
		opts.addOption(o6);
		opts.addOption(o7);
		opts.addOption(o8);
		opts.addOption(o9);
		opts.addOption(o10);
		opts.addOption(o11);
	}

	public static void displayHelp() {
		HelpFormatter h = new HelpFormatter();
		Writer w = new StringWriter();
		PrintWriter pw = new PrintWriter(w, true);
		h.printHelp(pw, HelpFormatter.DEFAULT_WIDTH, HEAD_USAGE, "", opts,
				HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD,
				"");
		log.log(Level.INFO, w.toString());
		System.exit(FAFOuputCode.EXIT_ERROR);
	}

	public static void main(String[] args) {
		LoggerManager.setupLogger();

		initOptions();

		CommandLineParser parser = new GnuParser();
		CommandLine cmdline = null;
		try {
			cmdline = parser.parse(opts, args);
		} catch (ParseException e) {
			log.log(Level.INFO, e.getMessage());
			displayHelp();
		}

		String data = cmdline.getOptionValue(DATA);
		String names = cmdline.getOptionValue(NAMES);
		// si pas de sortie précisée, même nom que le .data par défaut
		String out = cmdline.getOptionValue(OUT, data);

		log.log(Level.INFO, "Parsing done");
		log.log(Level.INFO, "names = " + names);
		log.log(Level.INFO, "data = " + data);
		log.log(Level.INFO, "output = " + out);

		// Set pool size if needed
		if (cmdline.hasOption(THREADS)) {
			String s = cmdline.getOptionValue(THREADS);
			int sint = Integer.parseInt(s);
			if (sint < 1) {
				log.error("Parameter <" + THREADS
						+ "> must be an integer greater or equal than 1");
				System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
			} else {
				Scheduler.setPoolSize(sint);
			}
		}

		String workingdir = cmdline.getOptionValue(WORKINGDIR,
				DEFAULT_WORKING_DIR);
		String bagging = cmdline.getOptionValue(BAGGING, DEFAULT_BAGGING);
		String maxdepth = cmdline.getOptionValue(MAXDEPTH, DEFAULT_MAXDEPTH);
		String minex = cmdline.getOptionValue(MINEXBYLEAF, DEFAULT_MINEX);
		String gainmin = cmdline.getOptionValue(GAINMIN, DEFAULT_GAINMIN);
		String crit = cmdline.getOptionValue(CRITERION, DEFAULT_CRITERION);
		String percent = cmdline.getOptionValue(PERCENTBAGGING,
				DEFAULT_PERCENTBAGGING);
		int intbagging = Integer.parseInt(bagging);
		int intmaxdepth = Integer.parseInt(maxdepth);
		int intminex = Integer.parseInt(minex);
		double doublegainmin = Double.parseDouble(gainmin);
		double doublepercent = Double.parseDouble(percent);

		// verification des bornes des différents paramètres rentrés par
		// l'utilisateur
		if (intbagging < 1) {
			log.error("Parameter <" + BAGGING
					+ "> must be an integer greater or equal than 1");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}
		if (intmaxdepth < 1) {
			log.error("Parameter <" + MAXDEPTH
					+ "> must be an integer greater or equal than 1");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}
		if (intminex < 1) {
			log.error("Parameter <" + MINEXBYLEAF
					+ "> must be an integer greater or equal than 1");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}
		if (doublegainmin < 0.0 || doublegainmin > 1.0) {
			log.error("Paramater <" + GAINMIN
					+ "> must be a double between 0.0 and 1.0");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}
		if (doublepercent <= 0.0 || doublepercent > 1.0) {
			log.error("Paramater <" + PERCENTBAGGING
					+ "> must be a double between 0.0 (excluded) and 1.0");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}

		// construction des critères d'arrêt
		List<StoppingCriterion> stopping = new ArrayList<StoppingCriterion>();
		stopping.add(new DepthMax(intmaxdepth));
		stopping.add(new ExampleMin(intminex));
		stopping.add(new GainMin(doublegainmin));

		// construction du critère de construction
		Criterion criterion = null;
		if (crit.equals(ENTROPY))
			criterion = new EntropyCriterion();
		else {
			log.error("Criterion <" + crit + "> not recognized");
			System.exit(FAFOuputCode.EXIT_BAD_ARGUMENT);
		}

		// construction du commentaire à insérer dans le fichier de sortie
		Map<String, String> comment = new HashMap<String, String>();
		comment.put(XmlConst.NAMES, names);
		comment.put(XmlConst.DATA, data);
		comment.put(XmlConst.CRITERION, crit);
		comment.put(XmlConst.MAXDEPTH, maxdepth);
		comment.put(XmlConst.MINEX, minex);
		comment.put(XmlConst.GAINMIN, gainmin);
		comment.put(XmlConst.BAGGING, bagging);
		comment.put(XmlConst.THREADS, String.valueOf(Scheduler.getPoolSize()));
		// on lance le launcher
		try {
			new Launcher(names + ".names", data + ".data", workingdir, out,
					stopping, criterion, intbagging, doublepercent, comment);
		} catch (fr.insarennes.fafdti.builder.ParseException e) {
			log.error("File " + names + "malformed.");
			log.error(e.getMessage());
			System.exit(FAFOuputCode.EXIT_UNOCCURED_EXCEPTION);
		}
	}
}
