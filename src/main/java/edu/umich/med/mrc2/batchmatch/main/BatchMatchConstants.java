package edu.umich.med.mrc2.batchmatch.main;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class BatchMatchConstants {

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String HOME_DIRECTORY = "."; //System.getProperty("user.home", "~") + FILE_SEPARATOR;
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static String CONFIGURATION_DIRECTORY = "config";
	public static String TMP_DIRECTORY = "tmp";

	public static String VERSION = "0.0.5";
	public static String PROPS_FILE = "batchmatch.props" + "." + VERSION;

	public static final Integer N_MAX_INTENSITIES = 100;
	public static final Integer N_MASTER_POOLS = 14;
	public static final Integer N_BATCHES = 2;
	public static final Integer LAST_BATCH_IDX = 7;
	public static final Integer OUTLIER_FILTERING_WINDOW = 3;

	public static final Double MASS_TOL = 0.003;
	public static final Double RT_TOL = 1.5;
	public static final Double EPSILON = 5e-4;

	public static final Double NAMED_ALL_MISSING = Double.NaN;
	public static final Double UNNAMED_ALL_MISSING = Double.NaN;
	public static final Double SIGNIFICANT_MAP_CORRELATION = 0.9;
	public static final Double MASS_RANGE_BREAK_GAP = .05;
	public static final String ALTERNATE_ISOTOPE_SEP = "|";

	public static final String NAMED_ALL_MISSING_MSG = "Named_All_Missing";
	public static final String UNNAMED_ALL_MISSING_MSG = "Unnamed_All_Missing";

	public static final String NOT_MAPPED_MSG = "Not_Mapped";

	public static final Integer RT_FROM_BINNER_NAME = 0;
	public static final Integer RT_FROM_BATCH_EXPECTED = 1;
	public static final Integer RT_FROM_BATCH_OBSERVED = 2;
	public static final Integer RT_FROM_CONVERSION = 3;
	public static final Integer N_RTS = 4;

	public static final String MERGE_TYPE_CENTER_OUT = "1";
	public static final String MERGE_TYPE_BOTTOM_UP = "2";
	public static final String MERGE_TYPE_TOP_DOWN = "3";

	public static final Color TITLE_COLOR = new Color(0, 0, 205);

	public static String[] COLLAPSE_OUTPUT_FIXED_COLUMN_LABELS = { "Batch", "Match Group", "# Batches Covered",
			"# Features", "RSD", "%Missing", "Feature", "Average Monoisotopic M/Z", "Average RT", "Average Old RT",
			"Median Median Intensity" };

	public static String[] REGULAR_OUTPUT_FIXED_COLUMN_LABELS = { "Feature", "Monoisotopic M/Z", "RT", "Old RT",
			"Median Intensity", "KMD", "Isotopes", "Other Isotopes In Group", "Annotations",
			"Other Annotations in Group", "Further Annotation", "Derivations", "Derived Molecular Mass", "Mass Error",
			"Feature Group Number", "Charge Carrier", "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster",
			"RT Subcluster" };

	public static List<String> SKIP_FOR_BATCH_COLUMN_LABELS = Arrays.asList(new String[] { "Index", "Median Intensity",
			"KMD", "Isotopes", "Other Isotopes In Group", "Annotations", "Other Annotations in Group",
			"Further Annotation", "Derivations", "Derived Molecular Mass", "Mass Error", "Feature Group Number",
			"Charge Carrier", "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster", "RT Subcluster" });

	public static String[] OLDRETENTION_TIME_CHOICES = { "oldrt", "oldretentiontime", "oldri", "oldretentionindex",
			"oldrts", "oldris", "rtobserved" };

	public static List<String> OLDRT_CHOICES_ARRAY = Arrays.asList(OLDRETENTION_TIME_CHOICES);

	public static List<String> BATCHMATCH_POSSIBLE_FIXED_COL_TAGS = Arrays.asList(new String[] { "matchgroupuniquect",
			"matchgroupct", "batch", "batchidx", "batchno", "batchindex", "rsd", "%missing", "pctmissing",
			"redundancygrp", "redundancygroup", "rgroup", "matchgroup", "matchgrp", "match", "index", "indices", "idx",
			"feature", "metabolite", "featurename", "metabolitename", "metabolites", "features", "compounds",
			"compound", "mass", "mz", "m/z", "m\\z", "masses", "monoisotopicm/z", "oldrt", "oldretentiontime", "oldri",
			"oldretentionindex", "oldrts", "oldris", "rt", "retentiontime", "ri", "retentionindex", "rts", "ris",
			"intensity", "intensities", "medintensity", "medianintensity", "medintensities", "medianintensities" });
}
