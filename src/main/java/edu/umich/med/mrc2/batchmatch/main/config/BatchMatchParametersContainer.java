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

package edu.umich.med.mrc2.batchmatch.main.config;

import org.jdom2.Element;

import edu.umich.med.mrc2.batchmatch.data.enums.MassErrorType;
import edu.umich.med.mrc2.batchmatch.data.store.XmlStorable;
import edu.umich.med.mrc2.batchmatch.project.AlignmentSettings;

public class BatchMatchParametersContainer implements XmlStorable{
	
	public static final String xmlNode = "BatchMatchParameters";
	
	private double massTolerance;
	private MassErrorType massErrorType;
	private double rtTolerance;
	private double annealingStretchFactor;
	private double maxSDfromCurve;
	private double minSeparation;
	private double upperDeltaRTexclusionLimit;
	private double lowerDeltaRTexclusionLimit;
	private int defaultLatticeSize;
	
	public BatchMatchParametersContainer() {
		super();
		setDefaultValues();
	}

	private void setDefaultValues() {
		
		massTolerance = 5.0d;
		massErrorType = MassErrorType.mDa;
		rtTolerance = 1.0d;
		annealingStretchFactor = 1.7d;
		maxSDfromCurve = 0.1d;
		minSeparation = 0.1d;
		upperDeltaRTexclusionLimit = 1.7d;
		lowerDeltaRTexclusionLimit = 0.0d;
		defaultLatticeSize = 30;
	}

	public double getMassTolerance() {
		return massTolerance;
	}

	public void setMassTolerance(double massTolerance) {
		this.massTolerance = massTolerance;
	}

	public MassErrorType getMassErrorType() {
		return massErrorType;
	}

	public void setMassErrorType(MassErrorType massErrorType) {
		this.massErrorType = massErrorType;
	}

	public double getRtTolerance() {
		return rtTolerance;
	}

	public void setRtTolerance(double rtTolerance) {
		this.rtTolerance = rtTolerance;
	}

	public double getAnnealingStretchFactor() {
		return annealingStretchFactor;
	}

	public void setAnnealingStretchFactor(double annealingStretchFactor) {
		this.annealingStretchFactor = annealingStretchFactor;
	}

	public double getMaxSDfromCurve() {
		return maxSDfromCurve;
	}

	public void setMaxSDfromCurve(double maxSDfromCurve) {
		this.maxSDfromCurve = maxSDfromCurve;
	}

	public double getMinSeparation() {
		return minSeparation;
	}

	public void setMinSeparation(double minSeparation) {
		this.minSeparation = minSeparation;
	}

	public double getUpperDeltaRTexclusionLimit() {
		return upperDeltaRTexclusionLimit;
	}

	public void setUpperDeltaRTexclusionLimit(double upperDeltaRTexclusionLimit) {
		this.upperDeltaRTexclusionLimit = upperDeltaRTexclusionLimit;
	}

	public double getLowerDeltaRTexclusionLimit() {
		return lowerDeltaRTexclusionLimit;
	}

	public void setLowerDeltaRTexclusionLimit(double lowerDeltaRTexclusionLimit) {
		this.lowerDeltaRTexclusionLimit = lowerDeltaRTexclusionLimit;
	}

	public int getDefaultLatticeSize() {
		return defaultLatticeSize;
	}

	public void setDefaultLatticeSize(int defaultLatticeSize) {
		this.defaultLatticeSize = defaultLatticeSize;
	}

	@Override
	public Element getXmlElement() {

		Element batchMatchParametersContainerElement = new Element(xmlNode);	
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.MASS_TOLERANCE.name(), Double.toString(massTolerance));		
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.MASS_TOLERANCE_TYPE.name(), massErrorType.name());
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.RT_TOLERANCE.name(), Double.toString(rtTolerance));
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.ANNEALING_STRETCH_FACTOR.name(), Double.toString(annealingStretchFactor));
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.MAX_SD_FROM_CURVE.name(), Double.toString(maxSDfromCurve));
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.MIN_SEPARATION.name(), Double.toString(minSeparation));
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.EXCLUDE_DELTA_RT_ABOVE.name(), Double.toString(upperDeltaRTexclusionLimit));
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.EXCLUDE_DELTA_RT_BELOW.name(), Double.toString(lowerDeltaRTexclusionLimit));			
		batchMatchParametersContainerElement.setAttribute(
				AlignmentSettings.DEFAULT_LATTICE_SIZE.name(), Integer.toString(defaultLatticeSize));
		
		return batchMatchParametersContainerElement;
	}
	
	public BatchMatchParametersContainer(Element batchMatchParametersContainerElement) {
		
		massTolerance = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.MASS_TOLERANCE.name()));
		massErrorType = 
				MassErrorType.getTypeByName(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.MASS_TOLERANCE_TYPE.name()));
		rtTolerance = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.RT_TOLERANCE.name()));
		annealingStretchFactor = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.ANNEALING_STRETCH_FACTOR.name()));
		maxSDfromCurve = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.MAX_SD_FROM_CURVE.name()));
		minSeparation = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.MIN_SEPARATION.name()));
		upperDeltaRTexclusionLimit = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.EXCLUDE_DELTA_RT_ABOVE.name()));
		lowerDeltaRTexclusionLimit = 
				Double.parseDouble(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.EXCLUDE_DELTA_RT_BELOW.name()));
		defaultLatticeSize = 
				Integer.parseInt(batchMatchParametersContainerElement.getAttributeValue(
						AlignmentSettings.DEFAULT_LATTICE_SIZE.name()));
	}
}













