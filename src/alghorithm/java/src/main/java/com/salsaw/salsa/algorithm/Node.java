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

import com.salsaw.salsa.algorithm.exceptions.SALSAException;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public final class Node {

	// FIELDS
	private final String name;
	private Node left;
	private Node right;
	private Node parent;

	private float distance;
	private int descendantLeaves;

	/**
	 * It represent the sum of distances between this node and its descendant
	 * leaves
	 */
	private float distancesSum;
	private float difference;

	// METHODS
	public Node(String name, Node left, Node right, Node parent, float distance) {

		this.name = name;
		this.left = left;
		this.right = right;
		this.parent = parent;

		this.distance = distance;

		// In this way, descendentLeafs is correct
		setRight(right);
	}

	private final void setRight(Node right) {
		this.right = right;

		this.descendantLeaves = 0;
		this.distancesSum = 0;
		if (this.right != null) {
			this.descendantLeaves = this.right.descendantLeaves;
			this.distancesSum = this.right.distancesSum + this.right.distance
					* this.right.descendantLeaves;
		}
		if (this.left != null) {
			this.descendantLeaves += this.left.descendantLeaves;
			this.distancesSum += this.left.distancesSum + this.left.distance
					* this.left.descendantLeaves;
		}

		if (this.descendantLeaves == 0) {
			// The node is a leaf itself
			this.descendantLeaves = 1; 
		}

	}

	public final String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public final int getDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final int getDescendentLeaves() {
		// TODO Auto-generated method stub
		return 0;
	}

	public final Node getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public final Node calculatePositionOfRoot(int insertedSequences) {
		// TODO Auto-generated method stub
		return null;
	}

	public final Node addRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	// PRIVATE METHODS

	/**
	 * Right now, newParent is a son of the current node. The method invert the
	 * current parent with newParent and do a recursive call.
	 * @throws SALSAException 
	 */
	private final void invertNode(Node newParent, float newDistance) throws SALSAException {
		Node oldParent = this.parent;
		float oldDistance = this.distance;
		this.parent=newParent;
		this.distance=newDistance;

		if (this.left==newParent) {
			this.left=oldParent;
		}
		else if (this.right==newParent) {
			this.right=oldParent;
		}
		else{
			throw new SALSAException("Error while trying to invert parent with son in the tree.");
		}

		if (oldParent != null){
			// Recursive call on old parent
			oldParent.invertNode(this, oldDistance);
		}
	}

	/**
	 * Calculate the number of descendant leaves of the current node and all the
	 * descendant nodes. Called on the root of a tree, it updates the internal
	 * variable descendantLeaves on all the nodes of the tree.
	 * 
	 * @return
	 */
	private final int calculateDescendantLeaves() {
		this.descendantLeaves=0;
		if (this.left!=null){
			this.descendantLeaves=this.left.calculateDescendantLeaves();
		}
		if (this.right!=null){
			this.descendantLeaves+=this.right.calculateDescendantLeaves();
		}

		if (this.descendantLeaves==0) {
			//The node is a leaf itself
			this.descendantLeaves=1; 
		}
		return this.descendantLeaves;
	}

}
