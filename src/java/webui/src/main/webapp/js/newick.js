﻿/**
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

com_salsaw_msalsa_JsPhyloSVG = function() {

	this.onStateChange = function() {
		phyloCanvas = new Smits.PhyloCanvas({
			newick : this.getState().newickTree
		}, 
		this.getState().divId, 
		this.getState().svgHeight, 
		this.getState().svgWidth);
	}
}