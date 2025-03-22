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

import java.util.Map;
import java.util.TreeMap;

import edu.umich.med.mrc2.batchmatch.data.enums.RTValueSource;

public class BatchMatchFeatureInfo {

	private double mass = -1.0d;
	private double intensity = -1.0d;
	private int batch = -1;
	private Map<RTValueSource,Double>rtMap;
	
	public BatchMatchFeatureInfo() {
		super();
		rtMap = new TreeMap<RTValueSource,Double>();
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}
	
	public void setRTofType(double rt, RTValueSource type) {
		rtMap.put(type, rt);
	}
	
	public double getRTofType(RTValueSource type) {
		
		if(rtMap.containsKey(type))
			return rtMap.get(type);
		else
			return -1.0d;
	}
	
	public double getObservedRt() {
		
		if(rtMap.containsKey(RTValueSource.FROM_BATCH_OBSERVED))
			return rtMap.get(RTValueSource.FROM_BATCH_OBSERVED);
		else
			return -1.0d;
	}
}
