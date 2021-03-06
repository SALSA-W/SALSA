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
package com.salsaw.msalsa.clustal;

/**
 * Determines the outputs that ClustalW produces.
 * 
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public enum ClustalWTreeOputputFormat {
	/**
	 * Newick/PHYLIP format tree file (Default)
	 */
	PHYLIP("phylip"),	
	/**
	 * Clustal format file in addition to the PHYLIP tree 	
	 */
	NEIGHBOR_JOINING("nj"),
	/**
	 *  Distance matrix file in addition to the PHYLIP tree
	 */
	DISTANCE("dist"),
	/**
	 * NEXUS format file in addition to the PHYLIP tree
	 */
	NEXUS("nexus"),
    ;

    private final String text;

    ClustalWTreeOputputFormat(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
