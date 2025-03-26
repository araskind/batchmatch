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

package edu.umich.med.mrc2.batchmatch.data;

import java.io.File;

import org.jdom2.Element;

import edu.umich.med.mrc2.batchmatch.data.store.BatchMatchInputObjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.XmlStorable;

public class BatchMatchInputObject implements XmlStorable, Comparable<BatchMatchInputObject>{

	private int batchNumber;
	private File binnerizedDataFile;
	private File peakAreasFile;
	private boolean isTargetBatch;
		
	public BatchMatchInputObject(){
		super();
		// TODO Auto-generated constructor stub
	}

	public BatchMatchInputObject(
			int batchNumber, 
			File binnerizedDataFile, 
			File peakAreasFile,
			boolean isTargetBatch) {
		super();
		this.batchNumber = batchNumber;
		this.binnerizedDataFile = binnerizedDataFile;
		this.peakAreasFile = peakAreasFile;
		this.isTargetBatch = isTargetBatch;
	}
	
	public int getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(int batchNumber) {
		this.batchNumber = batchNumber;
	}

	public File getBinnerizedDataFile() {
		return binnerizedDataFile;
	}

	public void setBinnerizedDataFile(File binnerizedDataFile) {
		this.binnerizedDataFile = binnerizedDataFile;
	}

	public File getPeakAreasFile() {
		return peakAreasFile;
	}

	public void setPeakAreasFile(File peakAreasFile) {
		this.peakAreasFile = peakAreasFile;
	}

	public boolean isTargetBatch() {
		return isTargetBatch;
	}

	public void setTargetBatch(boolean isTargetBatch) {
		this.isTargetBatch = isTargetBatch;
	}

	@Override
	public int compareTo(BatchMatchInputObject o) {
		return Integer.compare(batchNumber, o.getBatchNumber());
	}
	
    @Override
    public boolean equals(Object obj) {

		if (obj == this)
			return true;

        if (obj == null)
            return false;

        if (!BatchMatchInputObject.class.isAssignableFrom(obj.getClass()))
            return false;

        final BatchMatchInputObject other = (BatchMatchInputObject) obj;

        if (binnerizedDataFile.equals(other.getBinnerizedDataFile()))
            return false;
        
        if (peakAreasFile.equals(other.getPeakAreasFile()))
            return false;

        return true;
    }
	
    @Override
    public int hashCode() {
        return binnerizedDataFile.hashCode() + peakAreasFile.hashCode();
    }

    //	TODO
    public BatchMatchInputObject(Element xmlElement) {
    	
    }
    
	@Override
	public Element getXmlElement() {

		Element batchMatchInputObjectElement = 
				new Element(BatchMatchInputObjectFields.BatchMatchInputObject.name());
	
		batchMatchInputObjectElement.setAttribute(
				BatchMatchInputObjectFields.BatchNumber.name(), Integer.toString(batchNumber));
		
		batchMatchInputObjectElement.setAttribute(
				BatchMatchInputObjectFields.IsTarget.name(), Boolean.toString(isTargetBatch));
		
		Element peakAreasFileElement = 
				new Element(BatchMatchInputObjectFields.PeakAreasFile.name());
		if(peakAreasFile != null) 
			peakAreasFileElement.setText(peakAreasFile.getName());
		
		batchMatchInputObjectElement.addContent(peakAreasFileElement);
		
		Element binnerizedDataFileElement = 
				new Element(BatchMatchInputObjectFields.BinnerizedDataFile.name());
		if(binnerizedDataFile != null) 
			binnerizedDataFileElement.setText(binnerizedDataFile.getName());
		
		batchMatchInputObjectElement.addContent(binnerizedDataFileElement);
		
		return batchMatchInputObjectElement;
	}
}










