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

import java.text.NumberFormat;
import java.util.List;

import org.jdom2.Element;

import edu.umich.med.mrc2.batchmatch.data.store.LatticeObjectFields;
import edu.umich.med.mrc2.batchmatch.data.store.XmlStorable;
import edu.umich.med.mrc2.batchmatch.main.config.BatchMatchConfiguration;

public class LatticeObject  implements XmlStorable{

	private BatchMatchInputObject batchOneData;
	private BatchMatchInputObject referenceBatchData;
	List<RtPair>rtPairs;
	
	public LatticeObject(
			BatchMatchInputObject batchOneData, 
			BatchMatchInputObject referenceBatchData,
			List<RtPair> rtPairs) {
		super();
		this.batchOneData = batchOneData;
		this.referenceBatchData = referenceBatchData;
		this.rtPairs = rtPairs;
	}

	public BatchMatchInputObject getBatchOneData() {
		return batchOneData;
	}

	public BatchMatchInputObject getReferenceBatchData() {
		return referenceBatchData;
	}

	public List<RtPair> getRtPairs() {
		return rtPairs;
	}

	@Override
	public Element getXmlElement() {
		
		NumberFormat rtFormat = BatchMatchConfiguration.defaultRtFormat;

		Element latticeObjectElement = 
				new Element(LatticeObjectFields.LatticeObject.name());	
		latticeObjectElement.setAttribute(
				LatticeObjectFields.ReferenceBatchIndex.name(), 
				Integer.toString(referenceBatchData.getBatchNumber()));
		latticeObjectElement.setAttribute(
				LatticeObjectFields.BatchOneIndex.name(), 
				Integer.toString(batchOneData.getBatchNumber()));
		
		Element rtPairsListElement = 
				new Element(LatticeObjectFields.RTPairList.name());
		for(RtPair pair : rtPairs) {
			
			Element rtPairElement = 
					new Element(LatticeObjectFields.RTPair.name());	
			rtPairElement.setAttribute(
					LatticeObjectFields.RT1.name(), rtFormat.format(pair.getRt1()));
			rtPairElement.setAttribute(
					LatticeObjectFields.RT2.name(), rtFormat.format(pair.getRt2()));
			
			rtPairsListElement.addContent(rtPairElement);			
		}
		latticeObjectElement.addContent(rtPairsListElement);
		
		return latticeObjectElement;
	}
}
