/**
 * Copyright 2015 Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
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
 */
package com.salsaw.salsa.algorithm;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public final class Tree {
	// FIELDS
	private final Node root;
	private final Node[] leaves;
	private final int insertedSequences;
	

	// METHODS
	/**
	 * The parameters are the name of the file containing the tree in the Newick notation and the number of sequences
	 * @param fileName
	 * @param numberOfSequences
	 */
	public Tree(final String fileName, int numberOfSequences){
		
		this.insertedSequences = 0;
		//file.open(fileName);

		//if(!file.is_open()) throw("ERROR: file "+string(fileName)+" can't be open.");

		this.leaves=new Node[numberOfSequences];
		
		this.root=createNode(null);

		//file.close();		
		
		// TODO Report code from c
	}
	
	/**
	 * Re-root the tree
	 */
	public final void changeRoot(){
		// TODO Report code from c
	}
	
	/**
	 * Take as input the sequences' names and the weights array,
	 * it modifies the weights and returns the their sum.
	 * @param names
	 * @param weights
	 * @return
	 */
	public final float generateWeights(String[] names, float[] weights){
		// TODO Report code from c
		return 0;
	}
	
	public final void printTree(){
		// TODO Report code from c
	}
	
	private final String readName(){
		// TODO Report code from c
		return null;
	}
	
	private final Node createNode(Node parent){
		// TODO Report code from c
		return null;
	}
	
	private final float leafWeight(Node leaf){
		// TODO Report code from c
		return 0;
	}
	
}
