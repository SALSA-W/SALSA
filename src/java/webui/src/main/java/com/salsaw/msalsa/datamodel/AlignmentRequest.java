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
package com.salsaw.msalsa.datamodel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.salsaw.msalsa.config.ConfigurationManager;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public class AlignmentRequest {

	// FIELDS
	private final SalsaWebParameters salsaWebParameters;
	private final UUID id;

	// CONSTRUCTOR
	public AlignmentRequest(SalsaWebParameters salsaWebParameters) {
		if (salsaWebParameters == null) {
			throw new IllegalArgumentException("salsaWebParameters");
		}
		this.salsaWebParameters = salsaWebParameters;
		// Generate new GUID
		this.id = UUID.randomUUID();
	}

	// GET / SET
	public UUID getId() {
		return id;
	}

	public SalsaWebParameters getSalsaWebParameters() {
		return salsaWebParameters;
	}

	public Path getAlignmentRequestPath() {
		return Paths.get(
				ConfigurationManager.getInstance().getServerConfiguration().getTemporaryFilePath(),
				this.getId().toString());
	}
}
