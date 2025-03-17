package edu.umich.batchmatch.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.umich.batchmatch.data.BatchMatchFeatureInfo;
import edu.umich.batchmatch.data.IndexListItem;
import edu.umich.batchmatch.data.RtPair;
import edu.umich.batchmatch.data.TextFile;
import edu.umich.batchmatch.main.BatchMatchConstants;

public class BatchMatchLatticeBuilder {

	private Double[] maxParsedRtVal = new Double[BatchMatchConstants.N_BATCHES];
	private Double[] maxExpectedRtVal = new Double[BatchMatchConstants.N_BATCHES];
	private Double[] maxObservedRtVal = new Double[BatchMatchConstants.N_BATCHES];

	private String outputFileName;

	public BatchMatchLatticeBuilder() {
	}

	public Boolean buildLatticeFile(String file1, String file2, String outputDirectory, String batch1Label,
			String batch2Label, Integer poolSampleSize, Integer rtToUse) {
		List<String> fileNames = new ArrayList<String>();
		fileNames.add(file1);
		fileNames.add(file2);

		List<List<BatchMatchFeatureInfo>> featuresByBatch = new ArrayList<List<BatchMatchFeatureInfo>>();

		for (int batchIndex = 0; batchIndex < BatchMatchConstants.N_BATCHES; batchIndex++) {
			TextFile textFile = null;
			File file = new File(fileNames.get(batchIndex));
			try {
				textFile = new TextFile(file);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			// int binnerNameColIndex = -1;
			// for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
			// String colHeader = textFile.getString(0, colIndex);
			// if (colHeader.contains("Binner name")) {
			// binnerNameColIndex = colIndex;
			// break;
			// }
			// }

			int neutralMassColIndex = -1;
			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.contains("Monoisotopic M/Z")) {

					neutralMassColIndex = colIndex;
					break;
				}
			}

			int rtExpectedColIndex = -1, rtObservedColIndex = -1;
			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.equals("RT expected") || colHeader.equals("RT observed")) {
					rtExpectedColIndex = colIndex;
					break;
				}
			}

			for (int colIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.equals("RT observed")) {
					rtObservedColIndex = colIndex;
					break;
				}
			}

			ArrayList<Integer> mpColArray = new ArrayList<Integer>();

			for (int colIndex = 0, mpIndex = 0; colIndex <= textFile.getEndColIndex(0); colIndex++) {
				String colHeader = textFile.getString(0, colIndex);
				if (colHeader.contains("CS00000MP")) {
					mpColArray.add(mpIndex++);
				}
			}

			int mpColIndices[] = new int[mpColArray.size()];
			for (int i = 0; i < mpColArray.size(); i++)
				mpColIndices[i] = mpColArray.get(i);

			List<BatchMatchFeatureInfo> featuresForBatch = getFeaturesByIntensity(textFile, batchIndex, mpColIndices,
					-1, rtExpectedColIndex, rtObservedColIndex, neutralMassColIndex);
			featuresByBatch.add(featuresForBatch);
		}
		for (int batch1 = 0; batch1 < BatchMatchConstants.N_BATCHES - 1; batch1++) {
			for (int batch2 = batch1 + 1; batch2 < BatchMatchConstants.N_BATCHES; batch2++) {
				String fileName = writeLatticeFile(batch1, batch2, batch1Label, batch2Label, // batchTags.get(batch1),
																								// batchTags.get(batch2),
						featuresByBatch, outputDirectory, rtToUse, poolSampleSize);
				this.outputFileName = fileName;
			}
		}

		return true;
	}

	private List<BatchMatchFeatureInfo> getFeaturesByIntensity(TextFile textFile, int batch, int[] mpColIndices,
			int binnerNameColIndex, int rtExpectedColIndex, int rtObservedColIndex, int neutralMassColIdx) {
		List<IndexListItem<Double>> indexedDataByIntensity = new ArrayList<IndexListItem<Double>>();
		for (int iRow = 1; iRow <= textFile.getEndRowIndex(); iRow++) {
			Double maxIntensityForFeature = -1.0;
			for (int mpIndex = 0; mpIndex < mpColIndices.length; mpIndex++) {
				String str = textFile.getString(iRow, mpColIndices[mpIndex]);
				if (str == null || str.isEmpty()) {
					continue;
				}
				Double value = null;
				try {
					value = Double.parseDouble(str);
				} catch (NumberFormatException nfe) {
					continue;
				}
				if (value > maxIntensityForFeature) {
					maxIntensityForFeature = value;
				}
			}
			indexedDataByIntensity.add(new IndexListItem<Double>(maxIntensityForFeature, iRow));
		}

		Collections.sort(indexedDataByIntensity, new Comparator<IndexListItem<Double>>() {
			@Override
			public int compare(IndexListItem<Double> item1, IndexListItem<Double> item2) {
				return item2.getValue().compareTo(item1.getValue());
			}
		});

		List<BatchMatchFeatureInfo> featuresByIntensity = new ArrayList<BatchMatchFeatureInfo>();
		maxParsedRtVal[batch] = -1.0;
		maxExpectedRtVal[batch] = -1.0;
		maxObservedRtVal[batch] = -1.0;
		for (int i = 0; i < indexedDataByIntensity.size(); i++) {
			BatchMatchFeatureInfo featureInfo = new BatchMatchFeatureInfo();
			featureInfo.setBatch(batch + 1);
			int iRow = indexedDataByIntensity.get(i).getIndex();
			featureInfo.setIntensity(indexedDataByIntensity.get(i).getValue());

			String binnerName = binnerNameColIndex == -1 || (neutralMassColIdx != -1 && rtExpectedColIndex != -1)
					? (neutralMassColIdx != -1
							? (textFile.getString(iRow, neutralMassColIdx) + "@"
									+ textFile.getString(iRow, rtExpectedColIndex))
							: "")
					: textFile.getString(iRow, binnerNameColIndex);
			// if (binnerName.isEmpty()) {
			// System.out.println("Empty binner name value at row " + iRow + ", col " +
			// binnerNameColIndex);
			// continue;
			// }
			String tag = "";

			// Window Size
			try {
				int idx = binnerName.indexOf("@");
				Boolean useParsedVersionOnly = false;

				if (useParsedVersionOnly) {
					if (idx == -1)
						idx = binnerName.indexOf("_");

					tag = binnerName.substring(0, idx);
					featureInfo.setMass(Double.valueOf(tag));
					tag = binnerName.substring(idx + 1, binnerName.length());

					featureInfo.setRtFromBinnerName(Double.valueOf(tag));
					if (featureInfo.getRtFromBinnerName() > maxParsedRtVal[batch]) {
						maxParsedRtVal[batch] = featureInfo.getRtFromBinnerName();
					}
				}

				if (!useParsedVersionOnly) {

					tag = "";

					if (neutralMassColIdx != -1) {
						tag = textFile.getString(iRow, neutralMassColIdx);
						featureInfo.setMass(Double.valueOf(tag));
					}

					if (rtExpectedColIndex != -1)
						tag = textFile.getString(iRow, rtExpectedColIndex);
					if (tag.isEmpty()) {
						System.out.println("Empty RT expected value at row " + iRow + ", col " + rtExpectedColIndex);
						// featureInfo.setRtFromConversion(0.0); // placeholder
						continue;
					}
					featureInfo.setRtFromBatchExpected(Double.valueOf(tag));
					if (featureInfo.getRtFromBatchExpected() > maxExpectedRtVal[batch]) {
						maxExpectedRtVal[batch] = featureInfo.getRtFromBatchExpected();
					}

					tag = "";
					if (rtObservedColIndex != -1)
						tag = textFile.getString(iRow, rtObservedColIndex);
					if (tag.isEmpty()) {
						featureInfo.setRtFromConversion(0.0); // placeholder
						continue;
					}
					featureInfo.setRtFromBatchObserved(Double.valueOf(tag));
					if (featureInfo.getRtFromBatchObserved() > maxObservedRtVal[batch]) {
						maxObservedRtVal[batch] = featureInfo.getRtFromBatchObserved();
					}
				}
				featureInfo.setRtFromConversion(0.0); // placeholder
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				continue;
			}
			featuresByIntensity.add(featureInfo);
		}
		return featuresByIntensity;
	}

	private String writeLatticeFile(int batch1, int batch2, String batch1Tag, String batch2Tag,
			List<List<BatchMatchFeatureInfo>> featuresByBatch, String outputDirectory, Integer rtToUse,
			Integer nPoolSamples) {
		List<BatchMatchFeatureInfo> batch1Features = featuresByBatch.get(batch1);
		List<BatchMatchFeatureInfo> batch2Features = featuresByBatch.get(batch2);
		List<BatchMatchFeatureInfo> intenseBatch1ByMass = grabTopAndSort(batch1Features, nPoolSamples);
		List<BatchMatchFeatureInfo> intenseBatch2ByMass = grabTopAndSort(batch2Features, nPoolSamples);
		List<BatchMatchFeatureInfo> allBatch1ByMass = sortByMass(batch1Features);
		List<BatchMatchFeatureInfo> allBatch2ByMass = sortByMass(batch2Features);
		List<RtPair> rtPairsForLattice = crossReferenceForRTPairs(intenseBatch1ByMass, allBatch2ByMass, false, rtToUse);
		rtPairsForLattice.addAll(crossReferenceForRTPairs(intenseBatch2ByMass, allBatch1ByMass, true, rtToUse));

		// add pairs at both ends of RT spectrum
		RtPair rtPairStart = new RtPair();
		rtPairStart.setRt1(0.0);
		rtPairStart.setRt2(0.0);
		rtPairsForLattice.add(rtPairStart);

		RtPair rtPairEnd = new RtPair();
		rtPairEnd.setRt1(getAppropriateMaxRt(batch1, rtToUse));
		rtPairEnd.setRt2(getAppropriateMaxRt(batch2, rtToUse));
		rtPairsForLattice.add(rtPairEnd);

		rtPairsForLattice = sortAndRemoveDuplicatePairs(rtPairsForLattice);

		double diff = rtPairsForLattice.get(1).getRt1() - rtPairsForLattice.get(1).getRt2();

		if (diff > 0)
			rtPairsForLattice.set(0, new RtPair(diff, 0.0));
		else
			rtPairsForLattice.set(0, new RtPair(0.0, -1.0 * diff));

		rtPairsForLattice.add(new RtPair(100.0, 100.0));

		String rtTag = batch1Features.get(0).getAppropriateRtTag(rtToUse);
		String fileName = outputResults(batch1Tag, batch2Tag, rtPairsForLattice, outputDirectory, rtTag);
		return fileName;
	}

	private Double getAppropriateMaxRt(int batch, int rtToUse) {
		if (BatchMatchConstants.RT_FROM_BINNER_NAME.equals(rtToUse)) {
			return maxParsedRtVal[batch];
		}
		if (BatchMatchConstants.RT_FROM_BATCH_EXPECTED.equals(rtToUse)) {
			return maxExpectedRtVal[batch];
		}
		if (BatchMatchConstants.RT_FROM_BATCH_OBSERVED.equals(rtToUse)) {
			return maxObservedRtVal[batch];
		}
		return null;
	}

	private List<BatchMatchFeatureInfo> grabTopAndSort(List<BatchMatchFeatureInfo> features, Integer nPoolSamples) {
		List<BatchMatchFeatureInfo> mergedFeatures = new ArrayList<BatchMatchFeatureInfo>();

		int topIdx = Math.min(nPoolSamples, features.size());
		for (int i = 0; i < topIdx; i++) {
			mergedFeatures.add(features.get(i));
		}
		mergedFeatures = sortByMass(mergedFeatures);

		return mergedFeatures;
	}

	private List<BatchMatchFeatureInfo> sortByMass(List<BatchMatchFeatureInfo> features) {
		Collections.sort(features, new Comparator<BatchMatchFeatureInfo>() {
			@Override
			public int compare(BatchMatchFeatureInfo feature1, BatchMatchFeatureInfo feature2) {
				if (feature1 == null || feature1.getMass() == null)
					return 1;

				return feature1.getMass().compareTo(feature2.getMass());
			}
		});
		return features;
	}

	private List<RtPair> crossReferenceForRTPairs(List<BatchMatchFeatureInfo> set1, List<BatchMatchFeatureInfo> set2,
			Boolean flipOrder, Integer rtToUse) {
		List<RtPair> rtPairs = new ArrayList<RtPair>();

		for (int i = 0; i < set1.size(); i++) {
			Double targetMass = set1.get(i).getMass();
			if (targetMass == null)
				continue;
			Double targetRt = set1.get(i).getSpecifiedRt(rtToUse);

			Double smallestMassDiff = Double.MAX_VALUE;
			int bestMatch = -1;
			for (int j = 0; j < set2.size(); j++) {
				Double massDiff = Math.abs(set2.get(j).getMass() - targetMass);
				Double rtDiff = Math.abs(set2.get(j).getSpecifiedRt(rtToUse) - targetRt);
				if ((massDiff < BatchMatchConstants.MASS_TOL) && (rtDiff < BatchMatchConstants.RT_TOL)) {
					if (massDiff < smallestMassDiff) {
						smallestMassDiff = massDiff;
						bestMatch = j;
					}
				}
			}

			if (bestMatch != -1) {
				RtPair rtPair = new RtPair();
				if (flipOrder) {
					rtPair.setRt1(set2.get(bestMatch).getSpecifiedRt(rtToUse));
					rtPair.setRt2(targetRt);
				} else {
					rtPair.setRt1(targetRt);
					rtPair.setRt2(set2.get(bestMatch).getSpecifiedRt(rtToUse));
				}
				rtPairs.add(rtPair);
			}
		}
		return rtPairs;
	}

	private List<RtPair> sortAndRemoveDuplicatePairs(List<RtPair> rtPairs) {
		Collections.sort(rtPairs, new Comparator<RtPair>() {
			@Override
			public int compare(RtPair pair1, RtPair pair2) {
				return (((Double) (1000000.0 * pair1.getRt1() + pair1.getRt2()))
						.compareTo(((Double) (1000000.0 * pair2.getRt1() + pair2.getRt2()))));
			}
		});

		for (int i = rtPairs.size() - 1; i > 0; --i) {
			Double diff1 = Math.abs(rtPairs.get(i).getRt1() - rtPairs.get(i - 1).getRt1());
			Double diff2 = Math.abs(rtPairs.get(i).getRt2() - rtPairs.get(i - 1).getRt2());
			if (diff1 < BatchMatchConstants.EPSILON && diff2 < BatchMatchConstants.EPSILON) {
				rtPairs.remove(i);
			}
		}
		return rtPairs;
	}

	private String outputResults(String batch1Tag, String batch2Tag, List<RtPair> rtPairs, String outputDirectory,
			String rtTag) {

		String batch1Header = String.format("%2s", batch1Tag).replace(' ', '0');
		String batch2Header = String.format("%2s", batch2Tag).replace(' ', '0');

		String outputFileName = outputDirectory + BatchMatchConstants.FILE_SEPARATOR
				+ String.format("%s_Lattice_%s_%s.csv", rtTag, batch1Tag, batch2Tag);
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
			bos.write(
					(String.format("Batch%s, Batch%s", batch1Header, batch2Header) + BatchMatchConstants.LINE_SEPARATOR)
							.getBytes());
			for (RtPair rtPair : rtPairs) {
				String line = String.format("%7.5f, %7.5f, %7.5f", rtPair.getRt1(), rtPair.getRt2(), rtPair.getDiff());
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return outputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}
}
