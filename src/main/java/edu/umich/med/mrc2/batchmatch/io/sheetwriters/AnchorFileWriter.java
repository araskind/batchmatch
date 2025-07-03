////////////////////////////////////////////////////////
// AnchorFileWriter.java
// Written by Jan Wigginton
// September 2019
////////////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.umich.med.mrc2.batchmatch.data.comparators.orig.RtPairComparator;
import edu.umich.med.mrc2.batchmatch.data.orig.RtPair;
import edu.umich.med.mrc2.batchmatch.main.BatchMatchConstants;
import edu.umich.med.mrc2.batchmatch.utils.orig.ListUtils;

public class AnchorFileWriter {

	Boolean writeDiffs = true;

	public AnchorFileWriter() {
		this(true);
	}

	public AnchorFileWriter(boolean writeDiffs) {
		this.writeDiffs = writeDiffs;
	}

	public void outputFileList(
			File outputDirectory, 
			List<String> fileNames) {

		List<Integer> fileIndices = new ArrayList<Integer>();
		for (int i = 0; i < fileNames.size(); i++)
			fileIndices.add(i);

		outputFileList(outputDirectory, fileNames, fileIndices);
	}

	public void outputFileList(
			File outputDirectory, 
			List<String> fileNames, 
			List<Integer> fileIndices) {
		outputFileList(outputDirectory, fileNames, fileIndices, "lattice_list.csv");
	}

	public void outputFileList(
			File outputDirectory, 
			Map<Integer, File> updatedAnchorFileNameMap, 
			String fileName) {

		List<Integer> fileIndices = ListUtils.makeListFromCollection(updatedAnchorFileNameMap.keySet());
		Collections.sort(fileIndices);

		List<String> newFileNames = new ArrayList<String>();
		for (int i = 0; i < fileIndices.size(); i++)
			newFileNames.add(updatedAnchorFileNameMap.get(fileIndices.get(i)).getAbsolutePath());

		outputFileList(outputDirectory, newFileNames, fileIndices, fileName);
	}

	public void outputFileList(
			File outputDirectory, 
			List<String> fileNames, 
			List<Integer> fileIndices, 
			String fileName) {

		Collections.sort(fileIndices);

		List<String> fileTags = new ArrayList<String>();
		for (Integer idx : fileIndices) {
			Integer tag = idx + 1;
			fileTags.add(tag.toString());
		}

		outputFileListByTag(outputDirectory, fileNames, fileTags, fileName);
	}

	public void outputFileListByTag(
			File outputDirectory, 
			List<String> fileNames, 
			List<String> fileTags,
			String fileName) {

		File outputFile = Paths.get(outputDirectory.getAbsolutePath(), fileName).toFile();
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
			
			if (fileNames.size() != fileTags.size())
				throw new IOException("Error while writing new lattice file list");

			for (int i = 0; i < fileNames.size(); i++) {
				String line = String.format("%s,%s", fileTags.get(i), fileNames.get(i));

				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public File outputResults(
			String fileNameSuffix, 
			List<RtPair> rtPairs, 
			File outputDirectory,
			String fileNamePrefix, 
			String batch1Tag, 
			String batch2Tag) {

		return outputResults(
				fileNameSuffix, rtPairs, outputDirectory, 
				fileNamePrefix, batch1Tag, batch2Tag, false);
	}

	public File outputResults(
			String fileNameSuffix, 
			List<RtPair> rtPairs, 
			File outputDirectory,
			String fileNamePrefix, 
			String batch1Tag, 
			String batch2Tag, 
			Boolean writingMasses) {
		
		File outputFile = 
				Paths.get(outputDirectory.getAbsolutePath(), 
						String.format("%s_%s.csv", fileNamePrefix, fileNameSuffix)).toFile();

		outputResults(outputFile, rtPairs, batch1Tag, batch2Tag);
		
		return outputFile;
	}

	public void outputResults(
			File outputFile, 
			List<RtPair> rtPairs) {
		outputResults(outputFile, rtPairs, "Batch01", "Batch02");
	}

	public void outputResults(
			File outputFile, 
			List<RtPair> rtPairs, 
			String batch1Tag, 
			String batch2Tag) {
		outputResults(outputFile, rtPairs, batch1Tag, batch2Tag, false);
	}

	public void outputResults(
			File outputFile, 
			List<RtPair> rtPairs, 
			String batch1Tag, 
			String batch2Tag,
			Boolean writingMasses) {

		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
			
			if (batch1Tag != null && batch2Tag != null)
				bos.write((String.format("%s, %s", batch1Tag, batch2Tag) + BatchMatchConstants.LINE_SEPARATOR)
						.getBytes());

			Double projMin = Double.MAX_VALUE, projMax = Double.MIN_VALUE;
			Double maxX = Double.MIN_VALUE, absMaxX = writingMasses ? 1300.0 : 40.0; // Double.MIN_VALUE;

			for (RtPair rtPair : rtPairs) {
				String line = String.format("%6.4f, %6.4f", rtPair.getRt1(), rtPair.getRt2());
				if (true)
					line = String.format("%6.4f, %6.4f, %6.4f", rtPair.getRt1(), rtPair.getRt2(), rtPair.getDiff());

				if (rtPair.getRt1() > absMaxX)
					continue;
				if (rtPair.getRt1() < absMaxX) {
					if (rtPair.getRt1() > maxX)
						maxX = rtPair.getRt1();

					if (rtPair.getDiff() < projMin)
						projMin = rtPair.getDiff();

					if (rtPair.getDiff() > projMax)
						projMax = rtPair.getDiff();

				}
			}
			Collections.sort(rtPairs, new RtPairComparator()); // , keyType, valueType)
			for (RtPair rtPair : rtPairs) {
				String line = String.format("%6.4f, %6.4f", rtPair.getRt1(), rtPair.getRt2());
				if (true)
					line = String.format("%6.4f, %6.4f, %6.4f", rtPair.getRt1(), rtPair.getRt2(), rtPair.getDiff());
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}

			if (!writingMasses) {
				String line = String.format("%6.4f, %6.4f, %6.4f", 100.0, 100.0, 0.0);
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	private List<RtPair> fillInEnds(
			Double startX, 
			Double lastSimulatedX, 
			Double projMin, Double projMax,
			BufferedOutputStream bos) throws IOException {

		List<RtPair> addList = new ArrayList<RtPair>();
		Double nXSteps = 2.0, yStepsPerX = 20.0;

		Double ySpan = projMax - projMin;
		projMax += .1 * ySpan;
		projMin -= .1 * ySpan;
		ySpan = projMax - projMin;

		Double yIncrement = ySpan / yStepsPerX;

		Double endX = lastSimulatedX;
		Double xSpan = endX - startX;

		Double xIncrement = xSpan / nXSteps;
		Double xSubIncrement = xIncrement / yStepsPerX;

		String line = null;
		Double nextX = startX, nextDeltaY = null, nextY;

		for (int i = 0; i < nXSteps; i++) {
			nextX = startX + i * xIncrement;
			nextDeltaY = projMin;
			nextY = nextX + nextDeltaY;

			for (int j = 0; j < yStepsPerX; j++) {
				line = String.format("%6.4f, %6.4f, %6.4f", nextX, nextY, nextY - nextX);
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
				nextX += xSubIncrement;
				nextDeltaY += yIncrement;
				nextY = nextX + nextDeltaY;
			}
		}

		return addList;
	}

	public void outputResultsWithTagsToFile(
			String fileName, 
			List<Double> pairTags, 
			List<RtPair> rtPairs,
			String outputDirectory, 
			String batch1Tag, 
			String batch2Tag, 
			Boolean printDiagnostics) {

		String outputFileName = outputDirectory + BatchMatchConstants.FILE_SEPARATOR + fileName;

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
			bos.write((String.format("%s,%s", batch1Tag, batch2Tag) + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			for (int i = 0; i < rtPairs.size(); i++) {
				RtPair rtPair = rtPairs.get(i);

				String line = String.format("%6.4f,%6.4f", rtPair.getRt1(), rtPair.getRt2());
				if (printDiagnostics && rtPair.getRt1() < 99.0)
					line = String.format("%6.4f,%6.4f,%6.4f,%6.4f", pairTags.get(i), rtPair.getRt1(), rtPair.getRt2(),
							rtPair.getRt2() - rtPair.getRt1());

				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void outputResultsToFile(String fileName, List<RtPair> rtPairs, String outputDirectory, String batch1Tag,
			String batch2Tag, Boolean printDiagnostics) {

		String outputFileName = outputDirectory + BatchMatchConstants.FILE_SEPARATOR + fileName;

		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(new File(outputFileName)));
			bos.write((String.format("%s,%s", batch1Tag, batch2Tag) + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			for (RtPair rtPair : rtPairs) {
				String line = String.format("%6.4f,%6.4f", rtPair.getRt1(), rtPair.getRt2());
				if (printDiagnostics && rtPair.getRt1() < 1300.0) {
					line = String.format("%6.4f,%6.4f,%6.4f", rtPair.getRt1(), rtPair.getRt2(),
							rtPair.getRt2() - rtPair.getRt1());
				}
				bos.write((line + BatchMatchConstants.LINE_SEPARATOR).getBytes());
			}
			bos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	*/
}
