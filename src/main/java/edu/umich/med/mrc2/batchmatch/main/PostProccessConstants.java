package edu.umich.med.mrc2.batchmatch.main;

import java.util.Arrays;
import java.util.List;

public class PostProccessConstants {

	public static boolean BATCHMATCH_FORWARD_BATCHING = false;
	public static int BATCHMATCH_MAX_BATCH = 15;
	public static int BATCHMATCH_MIN_BATCH = 0;

	public static final String POSTPROCESS_VERSION = "0.0.7";
	public static final String POSTPROCESS_TAG = "Batch";

	public static final int DEFAULT_N_SEARCH_RESULTS = 100;
	public static final double DEFAULT_RT_SEARCH_TOL = 0.1;
	public static final double DEFAULT_ANNEALING_STRETCH_FACTOR = 1.7;
	public static final double DEFAULT_MASS_SEARCH_TOL = 0.1;
	public static final double DEFAULT_LOGISTIC_MID = 0.008;
	public static final double DEFAULT_LOGISTIC_MAX = 1.2;
	public static final double DEFAULT_LOGISTIC_CURVATURE = 1000.0;
	public static final double DEFAULT_MAX_MASS_DIFF = 10.0;
	public static final double DENSE_MASS_DIFF_THRESHOLD = 300.0;
	public static final double DEFAULT_MIN_MASS_DIFF = 0.0;
	public static final double LARGEST_MASS_DIFF = 950.0;

	public static final String SAMPLE_ID_FORMAT = "(S)\\d{8}";
	public static final String MASTER_POOL_ID_FORMAT = "CS00000MP";
	
	public static final String[] TIER_CHOICES = { "tier", "tiers" };
	public static final String[] TIERS_ALLOWED = { "0", "1", "2" };

	public static final int N_ANNOTATION_FILE_COLS = 5;
	public static final String SINGLETON_ANNOTATION = "[M]";

	public static final String[] OUTPUT_FIXED_COLUMN_LABELS = { "Index", "Feature", "Mass", "RT", "Median Intensity", "KMD",
			"Isotopes", "Other Isotopes In Group", "Annotations", "Other Annotations in Group", "Further Annotation",
			"Derivations", "Derived Molecular Mass", "Mass Error", "Feature Group Number", "Charge Carrier",
			"Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster", "RT Subcluster" };

	public static final List<String> SKIP_FOR_BATCH_COLUMN_LABELS = Arrays.asList("Index", "Median Intensity",
			"KMD", "Isotopes", "Other Isotopes In Group", "Annotations", "Other Annotations in Group",
			"Further Annotation", "Derivations", "Derived Molecular Mass", "Mass Error", "Feature Group Number",
			"Charge Carrier", "Adduct/NL", "Bin", "Corr Cluster", "Rebin Subcluster", "RT Subcluster");

	public static final List<String> SKIP_FOR_SIMPLE_COLUMN_LABELS = 
			Arrays.asList("Index", "Median Intensity", "KMD", "Isotopes", "Other Isotopes In Group",
					"Annotations", "Other Annotations in Group", "Further Annotation", "Mass Error",
					"Feature Group Number", "Bin", "Corr Cluster", "Rebin Subcluster", "RT Subcluster");

	public static final String[] POSTPROCESS_SEARCH_OUTPUT_COLUMN_LABELS = 
		{ "Match Replicate", "Compound Name", "Formula",
			"Compound Mass", "Compound RT", "Delta RT", "Delta Mass" };

	public static final List<String> POSTPROCESS_SEARCH_OUTPUT_COLUMN_TAGS = 
			Arrays.asList("matchreplicate", "compoundname", "formula", 
					"compoundmass", "compoundrt", "deltart", "deltamass");

	public static final int[] OUTPUT_FIXED_COLUMN_WIDTHS = { 
			5 * 256, 30 * 256, 18 * 256, // Mass
			15 * 256, // RT
			15 * 256, // MIntense
			15 * 256, // KMD
			40 * 256, // Iso
			50 * 256, // Other Isotopes
			40 * 256, // Annotations
			50 * 256, // Other Annotations
			40 * 256, // Further Annotaions
			40 * 256, // Derivation
			18 * 256, // Derived Mal Mass
			15 * 256, // Mass Error
			8 * 256, // Feature Group No
			8 * 256, // Charge Carrier
			8 * 256, // Adduct
			8 * 256, // Bin
			8 * 256, // Cluster
			8 * 256, // Rebin Cluster
			8 * 256  // RT Cluster
		};

	public static final int[] POSTPROCESS_SEARCH_OUTPUT_COLUMN_WIDTHS = { 
			8 * 256,  // Match Rep
			40 * 256, // COMPOUND
			25 * 256, // FORMULA
			15 * 256, // MASS
			15 * 256, // RT
			15 * 256, // MASS DIFF
			15 * 256  // RT DIFF
		}; 

	public static final List<String> BATCHMATCH_POSSIBLE_FILE_RTTYPE_TAGS = 
			Arrays.asList("obs", "exp", "par", "unk");

	public static final List<String> BINNER_INPUT_RECOGNIZED_COL_TAGS = 
			Arrays.asList("featurename", "neutralmass", "binnerm/z", 
					"rtexpected", "rtobserved", "monoisotopicm/z", "charge");

	public static final List<String> METABOLOMICS_RECOGNIZED_COL_TAGS = 
			Arrays.asList("matchgroupuniquect",
			"matchgroupct", "batch", "batchidx", "batchno", "batchindex", "rsd", "%missing", "pctmissing",
			"redundancygrp", "redundancygroup", "rgroup", "matchgroup", "matchgrp", "match", "index", "indices", "idx",
			"feature", "metabolite", "featurename", "metabolitename", "metabolites", "features", "compounds",
			"compound", "name", "mass", "mz", "m/z", "m\\z", "masses", "rt", "retentiontime", "ri", "retentionindex",
			"rts", "ris", "oldrt", "old_rt", "oldretentiontime", "old_retention_time", "intensity", "intensities",
			"medintensity", "medianintensity", "medintensities", "medianintensities", "kmd", "rmd", "isotopes",
			"isotope", "otherisotopesingroup", "otherisotopesforgroup", "otherisotope", "otherisotopes",
			"othergroupisotopes", "othergrpisotopes", "annotation", "annotations", "furtherannotation",
			"furtherannotations", "otherannotation", "otherannotationsingroup", "groupannotations", "groupannotation",
			"grpannotation", "grpannotations", "derivation", "derivations", "putativemolecularmass", "derivedmass",
			"neutralmass", "derivedmolecularmass", "derivedmolecularmasses", "putativemass", "masserror", "error",
			"molecularion", "molecularionnumber", "featuregroupnumber", "featuregroup", "featuregroupno",
			"chargecarrier", "carrier", "cc", "chargecarriers", "adducts/nls", "adduct/nl", "adduct/nls", "adduct",
			"adductnl", "adducts", "adductornl", "bin", "binindex", "binidx", "corrcluster", "oldcluster", "cluster",
			"correlationcluster", "newcluster", "rebincluster", "rebinsubcluster", "rtcluster", "rtsubcluster",
			"retentiontimecluster", "newnewcluster");

	// riginal RT
	public static final List<String> BATCH_MATCHGRP_CT_CHOICES_ARRAY = 
			Arrays.asList("matchgroupct");
	//BatchMatchConstants
	public static final List<String> BATCH_MATCHGRP_UNIQUECT_CHOICES_ARRAY = 
			Arrays.asList("matchgroupuniquect");

	public static final List<String> BATCH_IDX_CHOICES_ARRAY = 
			Arrays.asList("batch", "batchidx", "batchno", "batchindex");

	public static final List<String> RSD_CHOICES_ARRAY = Arrays.asList("rsd");

	public static final List<String> PCT_MISSING_CHOICES_ARRAY = 
			Arrays.asList("%missing", "pctmissing");

	public static final List<String> REDUNDANCY_GRP_CHOICES_ARRAY = 
			Arrays.asList("redundancygrp", "redundancygroup", 
					"rgroup", "matchgroup", "matchgrp", "match");

	public static final List<String> INDEX_CHOICES_ARRAY = 
			Arrays.asList("index", "idx", "indices");

	public static final String[] COMPOUND_CHOICES = 
		{ "compound", "feature", "featurename", "metabolite", 
				"metabolites", "metabolitename", "features", "compounds", "name" };
	public static final List<String> COMPOUND_CHOICES_ARRAY = Arrays.asList(COMPOUND_CHOICES);

	public static final String[] COMPOUND_CHOICES2 = { "binnername" };

	public static final List<String> COMPOUND_CHOICES_ARRAY2 = Arrays.asList(COMPOUND_CHOICES2);

	public static final String[] LIMITED_COMPOUND_CHOICES = { "featurename" };
	public static final List<String> LIMITED_COMPOUND_CHOICES_ARRAY =
			Arrays.asList(LIMITED_COMPOUND_CHOICES);

	public static final String[] LIMITED_MASS_MEASUREMENT_CHOICES = 
		{ "monoisotopicm/z", "monoisotopicm\\z", "monoisotopicmass", "monoisotopicmz" };
	public static final List<String> LIMITED_MASS_MEASUREMENT_CHOICES_ARRAY = 
			Arrays.asList(LIMITED_MASS_MEASUREMENT_CHOICES);

	public static final String[] MASS_CHOICES = 
		{ "mass", "mz", "m/z", "m\\z", "masses", "monoisotopicm/z" };
	public static final List<String> MASS_CHOICES_ARRAY = Arrays.asList(MASS_CHOICES);

	public static final String[] RETENTION_TIME_CHOICES =
		{ "rt", "retentiontime", "retention index", "ri", "retentionindex", "ris", "rts" };
	public static final List<String> RT_CHOICES_ARRAY = Arrays.asList(RETENTION_TIME_CHOICES);

	public static final String[] SPECIAL_EXPECTED_RETENTION_TIME_CHOICES = { "rtexpected" };
	public static final List<String> SPECIAL_EXPECTED_RETENTION_TIME_CHOICES_ARRAY = Arrays
			.asList(SPECIAL_EXPECTED_RETENTION_TIME_CHOICES);

	public static final String[] OLD_RETENTION_TIME_CHOICES = 
		{ "oldrt", "old_rt", "oldretentiontime", "old_retention_time" };
	public static final List<String> OLD_RT_CHOICES_ARRAY = Arrays.asList(OLD_RETENTION_TIME_CHOICES);

	public static final String[] FORMULA_NAME_CHOICES = { "formula", "Formula", "FORMULA" };
	public static final List<String> FORMULA_NAME_CHOICES_ARRAY = Arrays.asList(FORMULA_NAME_CHOICES);

	public static final List<String> INTENSITY_CHOICES_ARRAY = 
			Arrays.asList("intensity", "intensities", "medintensity", 
					"medianintensity", "medintensities", "medianintensities");

	public static final String[] ANNOTATION_CHOICES = { "annotation", "annotations" };
	public static final List<String> ANNOTATION_CHOICES_ARRAY = Arrays.asList(ANNOTATION_CHOICES);

	public static final List<String> GROUP_ANNOTATION_CHOICES_ARRAY = 
			Arrays.asList("otherannotationsingroup", "groupannotations", 
					"groupannotation", "grpannotation", "grpannotations");

	public static final String[] MODE_CHOICES = { "mode", "modes" };
	public static final List<String> MODE_CHOICES_ARRAY = Arrays.asList(MODE_CHOICES);

	public static final String[] CHARGE_CHOICES = { "charge", "charges", "z" };
	public static final List<String> CHARGE_CHOICES_ARRAY = Arrays.asList(CHARGE_CHOICES);

	public static final List<String> DERIVATION_CHOICES_ARRAY = 
			Arrays.asList("derivation", "derivations");
	public static final List<String> BIN_CHOICES_ARRAY = 
			Arrays.asList("bin", "binindex", "binidx");

	public static final List<String> RTCLUSTER_CHOICES_ARRAY = 
			Arrays.asList("newnewcluster", "rtcluster", "rtsubcluster", "retentiontimecluster");
	public static final List<String> REBINCLUSTER_CHOICES_ARRAY = 
			Arrays.asList("newcluster", "rebincluster", "rebinsubcluster");
	public static final List<String> CLUSTER_CHOICES_ARRAY = 
			Arrays.asList("corrcluster", "oldcluster", "cluster", "correlationcluster");
	public static final List<String> PUTATIVEMASS_CHOICES_ARRAY = 
			Arrays.asList("derivedmolecularmass", "derivedmolecularmasses", "putativemolecularmass", 
					"derivedmass", "neutralmass", "putativemass");
	public static final List<String> ISOTOPE_CHOICES_ARRAY = Arrays.asList("isotope", "isotopes");
	public static final List<String> GROUP_ISOTOPE_CHOICES_ARRAY = 
			Arrays.asList("otherisotopesingroup", "otherisotopesforgroup", 
					"otherisotope", "otherisotopes", "othergroupisotopes", "othergrpisotopes");

	public static final List<String> KMD_CHOICES_ARRAY = Arrays.asList("kmd", "rmd");
	public static final List<String> CHARGE_CARRIER_CHOICES_ARRAY = 
			Arrays.asList("chargecarrier", "carrier", "cc", "chargecarriers");
	public static final List<String> ADDUCT_CHOICES_ARRAY = 
			Arrays.asList("adducts/nls", "adduct/nl", "adduct/nls", 
			"adduct", "adducts", "adductnl", "adductornl");

	public static final List<String> MASS_ERROR_CHOICES_ARRAY = Arrays.asList("masserror", "error");
	public static final List<String> MOLECULAR_ION_CHOICES_ARRAY = 
			Arrays.asList("molecularion", "molecularionnumber", 
					"featuregroupnumber", "featuregroup", "featuregroupno");
	public static final List<String> FURTHER_ANNOTATION_CHOICES_ARRAY = 
			Arrays.asList("furtherannotation", "otherannotation", "furtherannotations");

	public static final List<String> POSTPROCESS_UNANNOTATED_TAB_TAGS = Arrays
			.asList("non-annotated", "unannotated", "nonannotated", "noannotation",
					"unannotatedfeatures", "non-annotatedfeatures", "nonannotatedfeatures");
	public static final List<String> POSTPROCESS_REFMASS_TAB_TAGS = 
			Arrays.asList("molecularions(putative)", "molecularions(possible)", "referencemasses",
					"putativemolecularions", "possiblemolecularions", "principalions", "principalion");

	public static final int N_OUTPUT_FIXED_COLUMN_LABELS = OUTPUT_FIXED_COLUMN_LABELS.length;
	public static final int OUTPUT_FIRST_ADDED_COLUMN_INDEX = N_OUTPUT_FIXED_COLUMN_LABELS + 1;

	public static final String OUTPUT_CORRELATION_LABEL = "Correlations";
	public static final String OUTPUT_MASS_DIFFS_LABEL = "Mass Diffs";

	public static final String POSTPROCESS_FILE_EXTENSION = "PostProcess";
	public static final String BATCHPROCESS_FILE_EXTENSION = "BatchProcess";
	public static final String POSTPROCESS_MERGE_FILE_EXTENSION = "PostProcessMerge";
	public static final String CLIENT_RESULTS_DIRECTORY = BinnerConstants.HOME_DIRECTORY;
	public static final String CLIENT_RESULTS_DIRECTORY_KEY = "results.directory";

	public static final String INTERNAL_USE_ONLY_PROPS_FILE = "internal.use.only.props" + "." + POSTPROCESS_VERSION;
}


