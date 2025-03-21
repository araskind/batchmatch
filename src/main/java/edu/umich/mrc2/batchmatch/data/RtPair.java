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

package edu.umich.mrc2.batchmatch.data;

public class RtPair {
	private double rt1;
	private double rt2;

	// Pair
	public RtPair() {
	}

	public RtPair(double rt1, double rt2) {
		this.rt1 = rt1;
		this.rt2 = rt2;
	}

	public double getRt1() {
		return rt1;
	}

	public void setRt1(double rt1) {
		this.rt1 = rt1;
	}

	public double getRt2() {
		return rt2;
	}

	public void setRt2(double rt2) {
		this.rt2 = rt2;
	}

	public double getDiff() {
		return rt2 - rt1;
	}

	public double getReverseDiff() {
		return rt1 - rt2;
	}
	
	public void flipRT() {
		
		double newRt1 = rt2;
		double newRt2 = rt1;
		
		rt1 = newRt1;
		rt2 = newRt2;
	}
	
	public double getAverageRT() {
		return (rt1 + rt2) / 2.0d;
	}
	
	public String toString() {
		return String.format("%.3f ~ %.3f", rt1, rt2);
	}
	
	public boolean isEquivalentWithTolerance(RtPair other, double tolerance) {
		
		//	TODO
		
		return false;
	}
}
