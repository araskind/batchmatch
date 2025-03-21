////////////////////////////////////////////////////
//BatchNamedDataIntegrator.java
//Written by Jan Wigginton February 2023
////////////////////////////////////////////////////

package edu.umich.mrc2.batchmatch.process.orig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import edu.umich.mrc2.batchmatch.data.orig.FeatureFromFile;
import edu.umich.mrc2.batchmatch.data.orig.FeatureMatch;

public class BatchNamedDataIntegrator {

	public BatchNamedDataIntegrator() {
	}

	public PostProcessDataSet updateInfo(PostProcessDataSet data, List<FeatureMatch> namedUnamedList) {

		PostProcessDataSet shiftedData = data; // data.makeDeepCopy();

		System.out.println("Checking for named matches...");

		Map<String, String> namedToUnnamedMap = new HashMap<String, String>();
		Map<String, String> namedToAnnotationMap = new HashMap<String, String>();

		for (int i = 0; i < namedUnamedList.size(); i++) {
			String unnamed = StringUtils.strip(namedUnamedList.get(i).getUnnamedFeature());
			String named = StringUtils.strip(namedUnamedList.get(i).getNamedFeature());
			namedToUnnamedMap.put(unnamed, named);
			namedToAnnotationMap.put(unnamed, named);
			// System.out.println("Adding <" + unnamed + "> = <" + named + ">");
		}

		int ct = 0;
		for (FeatureFromFile feature : shiftedData.getFeatures()) {

			String oldName = StringUtils.strip(feature.getName());
			String oldIsotope = feature.getIsotope();
			String batch = "-" + feature.getBatchIdx().toString();
			String fullKey = oldName + batch;
			if (namedToUnnamedMap.containsKey(fullKey)) {
				// System.out.println("Found name " + oldName);
				ct++;
				feature.setName(namedToUnnamedMap.get(oldName));
				feature.setIsotope(oldIsotope + ", " + namedToAnnotationMap.get(oldName));
				feature.setFurtherAnnotation(fullKey);
			}
		}

		System.out.println("Substituted " + ct + " names.");
		return shiftedData;
	}

	public PostProcessDataSet updateInfoTheOtherWay(PostProcessDataSet data, List<FeatureMatch> namedUnamedList) {

		PostProcessDataSet shiftedData = data; // data.makeDeepCopy();
		shiftedData.updateNamesForBatch();
		System.out.println("Checking for named matches... other way");

		Map<String, List<String>> namedForUnnamedByUnnamed = new HashMap<String, List<String>>();
		for (int i = 0; i < namedUnamedList.size(); i++) {
			String unnamed = StringUtils.strip(namedUnamedList.get(i).getUnnamedFeature());
			String named = StringUtils.strip(namedUnamedList.get(i).getNamedFeature());
			if (!namedForUnnamedByUnnamed.containsKey(unnamed))
				namedForUnnamedByUnnamed.put(unnamed, new ArrayList<String>());
			namedForUnnamedByUnnamed.get(unnamed).add(named);
		}

		int ct = 0;
		for (FeatureFromFile f : shiftedData.getFeatures()) {
			String originalName = f.getName();
			String batchSuffix = "-" + f.getBatchIdx().toString();
			String fullKey = originalName;
			if (!originalName.endsWith(batchSuffix)) {
				fullKey = originalName + batchSuffix;
			}

			if (fullKey.startsWith("UNK_595") && fullKey.endsWith("-5"))
				System.out.println("Checking for :" + fullKey);

			/// UNK_400.3436_10.238_400.34360-5
			if (namedForUnnamedByUnnamed.containsKey(fullKey)) {
				List<String> matchingNamed = namedForUnnamedByUnnamed.get(fullKey);
				ct++;
				for (int i = 0; i < matchingNamed.size(); i++) {
					if (i == 0)
						f.setName(matchingNamed.get(i));
					else
						f.setIsotope(f.getIsotope() + " " + matchingNamed.get(i));

					f.setFurtherAnnotation(originalName);
				}
			}
		}
		System.out.println("Substituted " + ct + " names.");
		return shiftedData;
	}
}
