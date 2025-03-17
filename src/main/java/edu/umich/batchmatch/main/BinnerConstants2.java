package edu.umich.batchmatch.main;

//import edu.umich.wld.BinnerConstants;

import java.awt.Color;

public class BinnerConstants2 {

	public static String BINNER_VERSION = "0.9.14";
	public static String POSTPROCESS_VERSION = "0.0.1";

	public static Double DEFAULT_RT_GAP_FOR_BIN_SEPARATION = 0.03;
	public static Double DEFAULT_ALLOWABLE_MISSINGNESS_PERCENT = 30.0;
	public static Double DEFAULT_ALLOWABLE_OUTLIER_SD = 4.0;

	public static Double DEFAULT_FORCED_SUBCLUSTER_THRESHOLD = 50.0;
	public static Double OVERALL_MIN_RT_CLUSTER_GAP = 0.001;
	public static Double DEFAULT_MIN_RT_CLUSTER_GAP = 0.025;
	public static Double DEFAULT_ALWAYS_RT_CLUSTER_GAP = 0.075;
	public static Double OVERALL_MAX_RT_CLUSTER_GAP = 300.0;

	public static Double DEFAULT_ALLOWABLE_MEDIAN_INTENSITY = 5000.0;
	public static Double DEFAULT_ADDUCT_NL_MASS_DIFFERENCE_TOL = 0.002;
	public static Double DEFAULT_RT_ANNOTATION_TOL = 0.1;
	public static Double DEFAULT_DEISOTOPING_MASS_DIFFERENCE_TOL = 0.002;
	public static Double DEFAULT_DEISOTOPING_CORR_CUTOFF = 0.6;
	public static Double DEFAULT_DEISOTOPING_RT_DIFF = 0.1;
	public static Integer DEFAULT_MIN_SIZE_TO_CLUSTER = 6;

	// Post-Processing Tab
	public static Integer DEFAULT_N_SEARCH_RESULTS = 500;
	public static Double DEFAULT_RT_SEARCH_TOL = 0.1;
	public static Double DEFAULT_MASS_SEARCH_TOL = 0.1;

	public static Integer MAX_RECOMMENDED_BINSIZE = 4000;
	public static Integer MAX_BINSIZE_FOR_BIN_OUTPUT = 3000;

	public static Double DEFAULT_MAX_MASS_DIFF = 300.0;
	public static Double DENSE_MASS_DIFF_THRESHOLD = 300.0;
	public static Double DEFAULT_MIN_MASS_DIFF = 0.0;
	public static Double LARGEST_MASS_DIFF = 950.0;

	public static Double EPSILON = 1e-6;
	public static double BIG_NEGATIVE = -1e6;

	public static String[] COMPOUND_CHOICES = { "compound", "compoundname", "compound name", "feature", "metabolite" };
	public static String[] MASS_CHOICES = { "mass", "mz", "m/z", "m\\z", "masses" };
	public static String[] RETENTION_TIME_CHOICES = { "retention time", "rt", "retentiontime", "retention index", "ri",
			"retentionindex" };
	public static String[] ANNOTATION_CHOICES = { "annotation", "annotations" };
	public static String[] MODE_CHOICES = { "mode", "modes" };
	public static String[] CHARGE_CHOICES = { "charge", "charges", "z" };

	public static String[] MISSINGNESS_LIST = { "0", "Inf", "na", ".", "NA", "N/A", "n/a", "NaN", "null", "Null",
			"NULL", null };

	public static String[] TIER_CHOICES = { "tier", "tiers" };
	public static String[] TIERS_ALLOWED = { "0", "1", "2" };

	public static Boolean TIER1_DEFAULT_ALLOW_AS_BASE = true;
	public static Boolean TIER1_DEFAULT_ALLOW_MULTIMER_BASE = false;
	public static Boolean TIER1_REQUIRE_ALONE = false;

	public static Boolean TIER2_DEFAULT_ALLOW_AS_BASE = false;
	public static Boolean TIER2_DEFAULT_ALLOW_MULTIMER_BASE = false;
	public static Boolean TIER2_REQUIRE_ALONE = true;

	public static Integer N_ANNOTATION_FILE_COLS = 5;
	public static String SINGLETON_ANNOTATION = "[M]";

	// public static String [] OUTPUT_FIXED_COLUMN_LABELS = {"Feature", "Mass",
	// "RT", "Median Intensity",
	// "KMD", "Isotopes", "Annotations", "Further Annotation", "Derivations", "Mass
	// Error", "Molecular Ion Number", "Charge Carrier",
	// "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster", "RT Subcluster"};
	// //', "RT Diff"};

	public static String[] OUTPUT_FIXED_COLUMN_LABELS = { "Feature", "Mass", "RT", "Median Intensity", "KMD",
			"Isotopes", "Annotations", "Further Annotation", "Derivations", "Putative Molecular Mass", "Mass Error",
			"Molecular Ion Number", "Charge Carrier", "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster",
			"RT Subcluster" }; // ', "RT Diff"};

	public static Integer N_OUTPUT_FIXED_COLUMN_LABELS = OUTPUT_FIXED_COLUMN_LABELS.length;
	public static final Integer OUTPUT_FIRST_ADDED_COLUMN_INDEX = N_OUTPUT_FIXED_COLUMN_LABELS + 1;

	public static String OUTPUT_CORRELATION_LABEL = "Correlations";
	public static String OUTPUT_MASS_DIFFS_LABEL = "Mass Diffs";

	public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String HOME_DIRECTORY = System.getProperty("user.home", "~" + FILE_SEPARATOR);
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static String REPORT_FILE_EXTENSION = "_Report.xlsx";

	public static String CONFIGURATION_DIRECTORY = "PostProcessonfiguration";
	public static String INPUT_PROPS_FILE = "input.props" + "." + POSTPROCESS_VERSION;
	public static String DATA_CLEANING_PROPS_FILE = "data.cleaning.props" + "." + POSTPROCESS_VERSION;
	public static String ANNOTATION_PROPS_FILE = "annotation.props" + "." + POSTPROCESS_VERSION;
	public static String OUTPUT_PROPS_FILE = "output.props" + "." + POSTPROCESS_VERSION;
	public static String DATA_ANALYSIS_PROPS_FILE = "data.analysis.props" + "." + POSTPROCESS_VERSION;
	public static String INTERNAL_USE_ONLY_PROPS_FILE = "internal.use.only.props" + "." + POSTPROCESS_VERSION;
	public static String POSTPROCESS_PROPS_FILE = "postprocess.props" + "." + POSTPROCESS_VERSION;

	public static String ANNOTATION_FILE_KEY = "annotation.file";
	public static String MASS_DIST_UPPER_LIMIT_KEY = "mass.diff.upper";

	public static Integer OUTPUT_CORR_BY_CLUST_LOC = 0;
	public static Integer OUTPUT_CORR_BY_CLUST_ABS = 1;
	public static Integer OUTPUT_CORR_BY_BIN_RT_SORT_LOC = 2;
	public static Integer OUTPUT_CORR_BY_BIN_RT_SORT_ABS = 3;
	public static Integer OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC = 4;
	public static Integer OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS = 5;
	public static Integer OUTPUT_MASS_DIFF_BY_CLUST = 6;
	public static Integer OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT = 7;
	public static Integer OUTPUT_MASS_DIFF_BY_BIN_RT_SORT = 8;
	public static Integer OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT = 9;
	public static Integer OUTPUT_UNADJ_INTENSITIES = 10;
	public static Integer OUTPUT_ADJ_INTENSITIES = 11;
	public static Integer OUTPUT_REF_MASS_PUTATIVE = 12;
	public static Integer OUTPUT_REF_MASS_POSSIBLE = 13;
	public static Integer OUTPUT_NONANNOTATED = 14;

	public static Integer[] ALL_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT, OUTPUT_UNADJ_INTENSITIES,
			OUTPUT_ADJ_INTENSITIES, OUTPUT_REF_MASS_PUTATIVE, OUTPUT_REF_MASS_POSSIBLE, OUTPUT_NONANNOTATED };

	public static Integer[] CORR_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static Integer[] HIGHLIGHTED_REBIN_OUTPUTS = { OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT };

	public static Integer[] MASS_DIFF_OUTPUTS = { OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static Integer[] INTENSITY_OUTPUTS = { OUTPUT_UNADJ_INTENSITIES, OUTPUT_ADJ_INTENSITIES };
	public static Integer[] REF_MASS_OUTPUTS = { OUTPUT_REF_MASS_PUTATIVE, OUTPUT_REF_MASS_POSSIBLE,
			OUTPUT_NONANNOTATED };

	public static Integer[] BY_CLUST_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_MASS_DIFF_BY_CLUST, OUTPUT_UNADJ_INTENSITIES, OUTPUT_ADJ_INTENSITIES, OUTPUT_REF_MASS_PUTATIVE,
			OUTPUT_REF_MASS_POSSIBLE, OUTPUT_NONANNOTATED };

	public static Integer[] BY_BIN_OUTPUTS = { OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC, OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS, OUTPUT_MASS_DIFF_BY_BIN_CLUST_SORT,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT, OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static Integer[] RT_SORT_OUTPUTS = { OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_MASS_DIFF_BY_BIN_RT_SORT };

	public static Integer[] MASS_SORT_OUTPUTS = { OUTPUT_MASS_DIFF_BY_BIN_MASS_SORT };

	public static Integer[] ADJ_OUTPUTS = { OUTPUT_ADJ_INTENSITIES };

	public static Integer[] UNADJ_OUTPUTS = { OUTPUT_UNADJ_INTENSITIES };

	public static Integer[] LOC_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC };

	public static Integer[] ABS_OUTPUTS = { OUTPUT_CORR_BY_CLUST_ABS, OUTPUT_CORR_BY_BIN_RT_SORT_ABS,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static Integer[] COLOR_OUTPUTS = { OUTPUT_CORR_BY_CLUST_LOC, OUTPUT_CORR_BY_CLUST_ABS,
			OUTPUT_CORR_BY_BIN_RT_SORT_LOC, OUTPUT_CORR_BY_BIN_RT_SORT_ABS, OUTPUT_CORR_BY_BIN_CLUST_SORT_LOC,
			OUTPUT_CORR_BY_BIN_CLUST_SORT_ABS };

	public static String[] OUTPUT_TAB_NAMES = { "Corrs by clust (loc)", "Corrs by clust (abs)",
			"Corrs by bin (RT sort, loc)", "Corrs by bin (RT sort, abs)", "Corrs by bin (clust sort, loc)",
			"Corrs by bin (clust sort, abs)", "Mass diffs by clust", "Mass diffs by bin (clust sort)",
			"Mass diffs by bin (RT sort)", "Mass diffs by bin (mass sort)", "Unadj intensities", "Adj intensities",
			"Molecular ions (putative)", "Molecular ions (possible)", "Unannotated features" };

	public static final Color TITLE_COLOR = new Color(0, 0, 205);
	public static final Double PROGRESS_BAR_WIDTH = 500.0;
	public static final Double DEISOTOPE_PROGRESS_WEIGHT = 15.0;
	public static final Double CLUSTERING_PROGRESS_WEIGHT = 45.0;
	public static final Double BINWISE_OUTPUT_WEIGHT = 20.0;
	public static final Double CLUSTERWISE_OUTPUT_WEIGHT = 2.0;

	public static final Double HYDROGEN_MASS = 1.00782;
	public static final Double CARBON_MASS = 12.0;
	public static final Double HC_BASE = 2 * HYDROGEN_MASS;
	public static final Double HC_STEP = 2 * HYDROGEN_MASS + CARBON_MASS;
	public static final Double KENDRICK_FACTOR = 14.0 / 14.01565;
	public static final Integer[] ISOTOPE_CHARGES = { 2, 3, 1 };
	public static final Integer[] ANNOTATION_CHARGES = { -3, -2, -1, 0, 1, 2, 3 };
	public static final Double[] ISOTOPE_MASS_DIFFS = { 0.5017, 0.3345, 1.0034 };
	public static final Integer MAX_ISOTOPE_CHARGE = ISOTOPE_CHARGES.length;

	public static final Integer FIRST = 0;
	public static final Integer LAST = 1;

	public static final Integer EXPERIMENT = 0;
	public static final Integer LOOKUP = 1;
	public static final Integer ANNOTATION = 2;

	public static final Integer SORT_TYPE_RT = 0;
	public static final Integer SORT_TYPE_MASS = 1;
	public static final Integer SORT_TYPE_INTENSITY = 2;

	public static final Integer STYLE_BORING = 0;
	public static final Integer STYLE_INTEGER = 1;
	public static final Integer STYLE_NUMERIC = 2;
	public static final Integer STYLE_YELLOW = 3;
	public static final Integer STYLE_LIGHT_BLUE = 4;
	public static final Integer STYLE_LIGHT_GREEN = 5;
	public static final Integer STYLE_LAVENDER = 6;
	public static final Integer STYLE_INTEGER_GREY = 7;
	public static final Integer STYLE_NUMERIC_GREY = 8;
	public static final Integer STYLE_INTEGER_GREY_CENTERED = 9;
	public static final Integer STYLE_NUMERIC_GREY_CENTERED = 10;
	public static final Integer STYLE_BORING_LEFT = 11;
	public static final Integer STYLE_HEADER_WRAPPED = 12;
	public static final Integer STYLE_NUMERIC_SHORTER = 13;
	public static final Integer STYLE_BORING_RIGHT = 14;
	public static final Integer STYLE_NUMERIC_LAVENDER = 15;
	public static final Integer STYLE_BORING_GREY = 16;
	public static final Integer STYLE_YELLOW_LEFT = 17;
	public static final Integer STYLE_GREY_LEFT = 18;
	public static final Integer STYLE_NUMERIC_SHORTER_GREY = 19;

}
