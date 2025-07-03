/*******************************************************************************
 *
 * (C) Copyright 2018-2020 MRC2 (http://mrc2.umich.edu).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Alexander Raskind (araskind@med.umich.edu)
 *
 ******************************************************************************/

package edu.umich.med.mrc2.batchmatch.process;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import edu.umich.med.mrc2.batchmatch.data.BatchMatchFeatureInfo;
import edu.umich.med.mrc2.batchmatch.data.BatchMatchInputObject;
import edu.umich.med.mrc2.batchmatch.data.LatticeObject;
import edu.umich.med.mrc2.batchmatch.data.RtPair;
import edu.umich.med.mrc2.batchmatch.data.comparators.BatchMatchFeatureInfoComparator;
import edu.umich.med.mrc2.batchmatch.data.comparators.ObjectCompatrator;
import edu.umich.med.mrc2.batchmatch.data.comparators.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.data.comparators.SortDirection;
import edu.umich.med.mrc2.batchmatch.data.comparators.SortProperty;
import edu.umich.med.mrc2.batchmatch.data.enums.BinnerExportFields;
import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.data.enums.RTValueSource;
import edu.umich.med.mrc2.batchmatch.utils.BinnerIOUtils;
import edu.umich.med.mrc2.batchmatch.utils.DelimitedTextParser;
import edu.umich.med.mrc2.batchmatch.utils.MSUtils;
import edu.umich.med.mrc2.batchmatch.utils.Range;

public class BatchMatchLatticeBuilder {
	
	private static final String pooledSampleIdentifier = "CS00000MP";
	
	private BatchMatchInputObject batchOneData;
	private BatchMatchInputObject referenceBatchData;
	private int latticeSize;
	private double massError;
	private MassErrorType massErrorType;
	private double rtError;
	private LatticeObject latticeObject;
	
	public BatchMatchLatticeBuilder(
			BatchMatchInputObject batchOneData, 
			BatchMatchInputObject referenceBatchData,
			int latticeSize, 
			double massError, 
			MassErrorType massErrorType,
			double rtError) {
		super();
		this.batchOneData = batchOneData;
		this.referenceBatchData = referenceBatchData;
		this.latticeSize = latticeSize;
		this.massError = massError;
		this.massErrorType = massErrorType;
		this.rtError = rtError;
	}

	public void buildLattice() {
		
		BatchMatchFeatureInfoComparator massComparator = 
				new BatchMatchFeatureInfoComparator(SortProperty.MZ);
		
		List<BatchMatchFeatureInfo>batchOneFeatures = 
				extractFeaturesFromDataFile(batchOneData.getPeakAreasFile());
		batchOneFeatures.stream().forEach(f -> f.setBatch(batchOneData.getBatchNumber()));
		batchOneFeatures = batchOneFeatures.stream().
				sorted(massComparator).collect(Collectors.toList());
		
		List<BatchMatchFeatureInfo>batchTwoFeatures = 
				extractFeaturesFromDataFile(referenceBatchData.getPeakAreasFile());
		batchTwoFeatures.stream().forEach(f -> f.setBatch(referenceBatchData.getBatchNumber()));
		batchTwoFeatures = batchTwoFeatures.stream().
				sorted(massComparator).collect(Collectors.toList());
		
		//	int maxFeatureNumber = Math.min(batchOneFeatures.size(), batchTwoFeatures.size());		
		//	latticeSize = Math.min(latticeSize, maxFeatureNumber);
		
		BatchMatchFeatureInfoComparator reverseIntensityComparator = 
				new BatchMatchFeatureInfoComparator(SortProperty.Intensity, SortDirection.DESC);
		
		List<BatchMatchFeatureInfo>batchOneTopFeatures = 
				batchOneFeatures.stream().sorted(reverseIntensityComparator).
					limit(latticeSize).collect(Collectors.toList());
		List<BatchMatchFeatureInfo>batchTwoTopFeatures = 
				batchTwoFeatures.stream().sorted(reverseIntensityComparator).
					limit(latticeSize).collect(Collectors.toList());
		
		List<RtPair>rtPairsForLattice = new ArrayList<RtPair>();
		rtPairsForLattice.add(new RtPair(0.0d, 0.0d));
		
		rtPairsForLattice.addAll(createRTPairs(batchOneTopFeatures,batchTwoFeatures,false));
		rtPairsForLattice.addAll(createRTPairs(batchTwoTopFeatures,batchOneFeatures,true));	
		
		double batchOneMaxRt = batchOneFeatures.stream().
				mapToDouble(f -> f.getObservedRt()).max().getAsDouble();
		double batchTwoMaxRt = batchTwoFeatures.stream().
				mapToDouble(f -> f.getObservedRt()).max().getAsDouble();
		rtPairsForLattice.add(new RtPair(batchOneMaxRt, batchTwoMaxRt));
		
		rtPairsForLattice = sortAndRemoveDuplicatePairs(rtPairsForLattice);
		
		latticeObject = new LatticeObject(
				batchOneData, referenceBatchData, rtPairsForLattice);
	}
	
	private List<RtPair> sortAndRemoveDuplicatePairs(List<RtPair> rtPairsForLattice) {

		List<RtPair>rtPairsForLatticeClean = new ArrayList<RtPair>(rtPairsForLattice);
		Set<RtPair>toRemove = new HashSet<RtPair>();
		
		for(int i=0; i<rtPairsForLattice.size(); i++) {
			
			RtPair ref = rtPairsForLattice.get(i);
			for(int j=i+1; j<rtPairsForLattice.size(); j++) {
				
				RtPair toCompare = rtPairsForLattice.get(j);
				if(!toRemove.contains(toCompare) 
						&& ref.isEquivalentWithTolerance(toCompare, ObjectCompatrator.EPSILON))
						toRemove.add(toCompare);				
			}
		}
		if(!toRemove.isEmpty())
			rtPairsForLatticeClean.removeAll(toRemove);
		
		List<RtPair>rtPairsForLatticeSorted = rtPairsForLatticeClean.stream().
				sorted(new RtPairComparator(SortProperty.RT)).
				collect(Collectors.toList());
		
//		List<String>sorted = rtPairsForLatticeSorted.stream().
//				map(p -> p.toString()).collect(Collectors.toList());
//		
//		String outDirPath = "E:\\Development\\BatchMatch\\BatchMatch_Process_Demo_02_11_25\\_OUT";
//		try {
//			Files.write(Paths.get(outDirPath, "newSorterCleanedResult.txt"), 
//				sorted,
//				StandardCharsets.UTF_8,
//				StandardOpenOption.CREATE, 
//				StandardOpenOption.TRUNCATE_EXISTING);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
		return rtPairsForLatticeSorted;
	}

	private List<RtPair> createRTPairs(
			List<BatchMatchFeatureInfo> batchOneTopIntensityFeatures,
			List<BatchMatchFeatureInfo> batchTwoMassSortedFeatures,
			boolean flipOrder){
		
		List<RtPair> rtPairs = new ArrayList<RtPair>();
		for(BatchMatchFeatureInfo topFeature : batchOneTopIntensityFeatures) {
			
			double tfMass = topFeature.getMass();
			Range mzRange = MSUtils.createMassRange(tfMass, massError, massErrorType);
			Range rtRange = new Range(					
					topFeature.getObservedRt() - rtError, topFeature.getObservedRt() + rtError);
			
			List<BatchMatchFeatureInfo>filtered = batchTwoMassSortedFeatures.stream().
				filter(f -> mzRange.contains(f.getMass())).
				filter(f -> rtRange.contains(f.getObservedRt())).
				collect(Collectors.toList());
			if(filtered.isEmpty())
				continue;
			
			if(filtered.size() == 1) {
				
				RtPair rtPair = new RtPair(
						topFeature.getObservedRt(),filtered.get(0).getObservedRt());
				if (flipOrder)
					rtPair.flipRT();

				rtPairs.add(rtPair);
				continue;
			}				
			BatchMatchFeatureInfo match = filtered.get(0);
			double maxError = massError * 2.0d;
			for(int i=1; i<filtered.size(); i++) {
				
				double deltaMass = Math.abs(tfMass - filtered.get(i).getMass());
				if(deltaMass < maxError) {
					match = filtered.get(i);
					maxError = deltaMass;
				}
			}
			RtPair rtPair = new RtPair(
					topFeature.getObservedRt(),match.getObservedRt());
			if (flipOrder)
				rtPair.flipRT();

			rtPairs.add(rtPair);
		}		
		return rtPairs;		
	}

	private List<BatchMatchFeatureInfo>extractFeaturesFromDataFile(File dataFile){
		
		List<BatchMatchFeatureInfo>featureList = new ArrayList<BatchMatchFeatureInfo>();
		String[][] fileData = DelimitedTextParser.parseTextFile(
				dataFile, DelimitedTextParser.TAB_DELIMITER);
		Map<BinnerExportFields,Integer>headerMap = 
				BinnerIOUtils.mapBinnerInputFileHeader(fileData[0]);
		
		if(headerMap.get(BinnerExportFields.MZ) == null 
				|| headerMap.get(BinnerExportFields.RT_OBSERVED) == null)		
			throw new IllegalArgumentException("No MZ or RT column found.");
		
		Set<Integer>pooledColumnIndices = 
				BinnerIOUtils.getPooledSampleIndices(fileData[0], pooledSampleIdentifier);
		DescriptiveStatistics ds = new DescriptiveStatistics();
		
		for(int i=1; i<fileData.length; i++) {
						
			ds.clear();
			String mzString = fileData[i][headerMap.get(BinnerExportFields.MZ)];
			String rtString = fileData[i][headerMap.get(BinnerExportFields.RT_OBSERVED)];
			
			if(mzString.isBlank() || rtString.isBlank())
				continue;
			
			BatchMatchFeatureInfo feature = new BatchMatchFeatureInfo();
			feature.setMass(Double.parseDouble(mzString));
			feature.setRTofType(Double.parseDouble(rtString), RTValueSource.FROM_BATCH_OBSERVED);
			
			//	Add pooled sample intensities
			for(int j : pooledColumnIndices) {
				
				String areaString = fileData[i][j];
				if(!areaString.isBlank()) {
					Double area = Double.parseDouble(areaString);
					if(area != null && !area.equals(Double.NaN))
						ds.addValue(area);	
				}
			}
			if(ds.getN() > 0) {
				// TODO This is based on the original algo - median better?
				feature.setIntensity(ds.getMax());
				featureList.add(feature);
			}
		}
		return featureList;
	}

	public LatticeObject getLatticeObject() {
		return latticeObject;
	}
}
