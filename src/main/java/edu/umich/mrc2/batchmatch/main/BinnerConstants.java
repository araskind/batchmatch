package edu.umich.mrc2.batchmatch.main;

//import edu.umich.wld.BinnerConstants;

import java.awt.Color;

public class BinnerConstants {

	public static final String BINNER_VERSION = "0.9.14";
	public static final String POSTPROCESS_VERSION = "0.0.1";

	public static double DEFAULT_RT_GAP_FOR_BIN_SEPARATION = 0.03;
	public static double DEFAULT_ALLOWABLE_MISSINGNESS_PERCENT = 30.0;
	public static double DEFAULT_ALLOWABLE_OUTLIER_SD = 4.0;

	public static double DEFAULT_FORCED_SUBCLUSTER_THRESHOLD = 50.0;
	public static double OVERALL_MIN_RT_CLUSTER_GAP = 0.001;
	public static double DEFAULT_MIN_RT_CLUSTER_GAP = 0.025;
	public static double DEFAULT_ALWAYS_RT_CLUSTER_GAP = 0.075;
	public static double OVERALL_MAX_RT_CLUSTER_GAP = 300.0;

	public static double DEFAULT_ALLOWABLE_MEDIAN_INTENSITY = 5000.0;
	public static double DEFAULT_ADDUCT_NL_MASS_DIFFERENCE_TOL = 0.002;
	public static double DEFAULT_RT_ANNOTATION_TOL = 0.1;
	public static double DEFAULT_DEISOTOPING_MASS_DIFFERENCE_TOL = 0.002;
	public static double DEFAULT_DEISOTOPING_CORR_CUTOFF = 0.6;
	public static double DEFAULT_DEISOTOPING_RT_DIFF = 0.1;
	public static int DEFAULT_MIN_SIZE_TO_CLUSTER = 6;

	// Post-Processing Tab
	public static int DEFAULT_N_SEARCH_RESULTS = 500;
	public static double DEFAULT_RT_SEARCH_TOL = 0.1;
	public static double DEFAULT_MASS_SEARCH_TOL = 0.1;

	public static int MAX_RECOMMENDED_BINSIZE = 4000;
	public static int MAX_BINSIZE_FOR_BIN_OUTPUT = 3000;

	public static double DEFAULT_MAX_MASS_DIFF = 300.0;
	public static double DENSE_MASS_DIFF_THRESHOLD = 300.0;
	public static double DEFAULT_MIN_MASS_DIFF = 0.0;
	public static double LARGEST_MASS_DIFF = 950.0;

	public static double EPSILON = 1e-6;
	public static double BIG_NEGATIVE = -1e6;

	public static final String[] COMPOUND_CHOICES = { "compound", "compoundname", "compound name", "feature", "metabolite" };
	public static final String[] MASS_CHOICES = { "mass", "mz", "m/z", "m\\z", "masses" };
	public static final String[] RETENTION_TIME_CHOICES = { "retention time", "rt", "retentiontime", "retention index", "ri",
			"retentionindex" };
	public static final String[] ANNOTATION_CHOICES = { "annotation", "annotations" };
	public static final String[] MODE_CHOICES = { "mode", "modes" };
	public static final String[] CHARGE_CHOICES = { "charge", "charges", "z" };

	public static final String[] MISSINGNESS_LIST = { "0", "Inf", "na", ".", "NA", "N/A", "n/a", "NaN", "null", "Null",
			"NULL", null };

	public static final String[] TIER_CHOICES = { "tier", "tiers" };
	public static final String[] TIERS_ALLOWED = { "0", "1", "2" };

	public static boolean TIER1_DEFAULT_ALLOW_AS_BASE = true;
	public static boolean TIER1_DEFAULT_ALLOW_MULTIMER_BASE = false;
	public static boolean TIER1_REQUIRE_ALONE = false;

	public static boolean TIER2_DEFAULT_ALLOW_AS_BASE = false;
	public static boolean TIER2_DEFAULT_ALLOW_MULTIMER_BASE = false;
	public static boolean TIER2_REQUIRE_ALONE = true;

	public static int N_ANNOTATION_FILE_COLS = 5;
	public static final String SINGLETON_ANNOTATION = "[M]";
	
	public static String[] OUTPUT_FIXED_COLUMN_LABELS = { "Feature", "Mass", "RT", "Median Intensity", "KMD",
			"Isotopes", "Annotations", "Further Annotation", "Derivations", "Putative Molecular Mass", "Mass Error",
			"Molecular Ion Number", "Charge Carrier", "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster",
			"RT Subcluster" }; // ', "RT Diff"};

	public static int N_OUTPUT_FIXED_COLUMN_LABELS = OUTPUT_FIXED_COLUMN_LABELS.length;
	public static final int OUTPUT_FIRST_ADDED_COLUMN_INDEX = N_OUTPUT_FIXED_COLUMN_LABELS + 1;

	public static final String OUTPUT_CORRELATION_LABEL = "Correlations";
	public static final String OUTPUT_MASS_DIFFS_LABEL = "Mass Diffs";

	public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String HOME_DIRECTORY = System.getProperty("user.home", "~" + FILE_SEPARATOR);
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String REPORT_FILE_EXTENSION = "_Report.xlsx";

	public static final String CONFIGURATION_DIRECTORY = "PostProcessonfiguration";
	public static final String INPUT_PROPS_FILE = "input.props" + "." + POSTPROCESS_VERSION;
	public static final String DATA_CLEANING_PROPS_FILE = "data.cleaning.props" + "." + POSTPROCESS_VERSION;
	public static final String ANNOTATION_PROPS_FILE = "annotation.props" + "." + POSTPROCESS_VERSION;
	public static final String OUTPUT_PROPS_FILE = "output.props" + "." + POSTPROCESS_VERSION;
	public static final String DATA_ANALYSIS_PROPS_FILE = "data.analysis.props" + "." + POSTPROCESS_VERSION;
	public static final String INTERNAL_USE_ONLY_PROPS_FILE = "internal.use.only.props" + "." + POSTPROCESS_VERSION;
	public static final String POSTPROCESS_PROPS_FILE = "postprocess.props" + "." + POSTPROCESS_VERSION;

	public static final String ANNOTATION_FILE_KEY = "annotation.file";
	public static final String MASS_DIST_UPPER_LIMIT_KEY = "mass.diff.upper";

	public static final int OUTPUT_CORR_BY_CLUST_LOC = 0;
	public static final int OUTPUT_CORR_BY_CLUST_ABS = 1;
	public static final int OUTPUT_CORR_BY_BIN_RT_SORT_LOC = 2;
	public static final int OUTPUT_CORR_BY_BIN_RT_SORT_ABS = 3;
	public static final int OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC = 4;
	public static final int OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS = 5;
	public static final int OUTPUT_MASS_DIFF_BY_CLUST = 6;
	public static final int OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT = 7;
	public static final int OUTPUT_MASS_DIFF_BY_BIN_RT_SORT = 8;
	public static final int OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT = 9;
	public static final int OUTPUT_UNADJ_INTENSITIES = 10;
	public static final int OUTPUT_ADJ_INTENSITIES = 11;
	public static final int OUTPUT_REF_MASS_PUTATIVE = 12;
	public static final int OUTPUT_REF_MASS_POSSIBLE = 13;
	public static final int OUTPUT_NONANNOTATED = 14;

	public static int[] ALL_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT, OUTPUT_UNADJ_INTENSITIES,
			OUTPUT_ADJ_INTENSITIES, OUTPUT_REF_MASS_PUTATIVE, OUTPUT_REF_MASS_POSSIBLE, OUTPUT_NONANNOTATED };

	public static int[] CORR_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static int[] HIGHLIGHTED_REBIN_OUTPUTS = { OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT };

	public static int[] MASS_DIFF_OUTPUTS = { OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static int[] INTENSITY_OUTPUTS = { OUTPUT_UNADJ_INTENSITIES, OUTPUT_ADJ_INTENSITIES };
	public static int[] REF_MASS_OUTPUTS = { OUTPUT_REF_MASS_PUTATIVE, OUTPUT_REF_MASS_POSSIBLE,
			OUTPUT_NONANNOTATED };

	public static int[] BY_CLUST_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_UNADJ_INTENSITIES, OUTPUT_ADJ_INTENSITIES, OUTPUT_REF_MASS_PUTATIVE,
			OUTPUT_REF_MASS_POSSIBLE, OUTPUT_NONANNOTATED };

	public static int[] BY_BIN_OUTPUTS = { OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC, OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static int[] RT_SORT_OUTPUTS = { OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT };

	public static int[] MASS_SORT_OUTPUTS = { OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static int[] ADJ_OUTPUTS = { OUTPUT_ADJ_INTENSITIES };

	public static int[] UNADJ_OUTPUTS = { OUTPUT_UNADJ_INTENSITIES };

	public static int[] LOC_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC };

	public static int[] ABS_OUTPUTS = { OUTPUT_CORR_BY_CLUST_ABS, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static int[] COLOR_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static String[] OUTPUT_TAB_NAMES = { "Corrs by clust (loc)", "Corrs by clust (abs)",
			"Corrs by bin (RT sort, loc)", "Corrs by bin (RT sort, abs)", "Corrs by bin (clust sort, loc)",
			"Corrs by bin (clust sort, abs)", "Mass diffs by clust", "Mass diffs by bin (clust sort)",
			"Mass diffs by bin (RT sort)", "Mass diffs by bin (mass sort)", "Unadj intensities", "Adj intensities",
			"Molecular ions (putative)", "Molecular ions (possible)", "Unannotated features" };

	public static final Color TITLE_COLOR = new Color(0, 0, 205);
	public static final double PROGRESS_BAR_WIDTH = 500.0;
	public static final double DEISOTOPE_PROGRESS_WEIGHT = 15.0;
	public static final double CLUSTERING_PROGRESS_WEIGHT = 45.0;
	public static final double BINWISE_OUTPUT_WEIGHT = 20.0;
	public static final double CLUSTERWISE_OUTPUT_WEIGHT = 2.0;

	public static final double HYDROGEN_MASS = 1.00782;
	public static final double CARBON_MASS = 12.0;
	public static final double HC_BASE = 2 * HYDROGEN_MASS;
	public static final double HC_STEP = 2 * HYDROGEN_MASS + CARBON_MASS;
	public static final double KENDRICK_FACTOR = 14.0 / 14.01565;
	public static final int[] ISOTOPE_CHARGES = { 2, 3, 1 };
	public static final int[] ANNOTATION_CHARGES = { -3, -2, -1, 0, 1, 2, 3 };
	public static final double[] ISOTOPE_MASS_DIFFS = { 0.5017, 0.3345, 1.0034 };
	public static final int MAX_ISOTOPE_CHARGE = ISOTOPE_CHARGES.length;
	public static final int DEFAULT_BATCH_LEVEL_REPORTED = 14;

	public static final int FIRST = 0;
	public static final int LAST = 1;

	public static final int EXPERIMENT = 0;
	public static final int LOOKUP = 1;
	public static final int ANNOTATION = 2;

	public static final int SORT_TYPE_RT = 0;
	public static final int SORT_TYPE_MASS = 1;
	public static final int SORT_TYPE_INTENSITY = 2;

	public static final int STYLE_BORING = 0;
	public static final int STYLE_INTEGER = 1;
	public static final int STYLE_NUMERIC = 2;
	public static final int STYLE_YELLOW = 3;
	public static final int STYLE_LIGHT_BLUE = 4;
	public static final int STYLE_LIGHT_GREEN = 5;
	public static final int STYLE_LAVENDER = 6;
	public static final int STYLE_INTEGER_GREY = 7;
	public static final int STYLE_NUMERIC_GREY = 8;
	public static final int STYLE_INTEGER_GREY_CENTERED = 9;
	public static final int STYLE_NUMERIC_GREY_CENTERED = 10;
	public static final int STYLE_BORING_LEFT = 11;
	public static final int STYLE_HEADER_WRAPPED = 12;
	public static final int STYLE_NUMERIC_SHORTER = 13;
	public static final int STYLE_BORING_RIGHT = 14;
	public static final int STYLE_NUMERIC_LAVENDER = 15;
	public static final int STYLE_BORING_GREY = 16;
	public static final int STYLE_YELLOW_LEFT = 17;
	public static final int STYLE_GREY_LEFT = 18;
	public static final int STYLE_NUMERIC_SHORTER_GREY = 19;
	public static final int STYLE_NUMERIC_SHORTEST_GREY = 20;
	public static final int STYLE_NUMERIC_SHORTEST = 21;
	public static final int STYLE_ORANGE = 22;
}
