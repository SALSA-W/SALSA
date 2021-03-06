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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import com.salsaw.msalsa.cli.SalsaAlgorithmExecutor;
import com.salsaw.msalsa.config.ConfigurationManager;
import com.salsaw.msalsa.services.ObjectSerializer;
import com.salsaw.msalsa.webui.exceptions.AlignmentExecutionException;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public class AlignmentResult {
	// CONSTANTS
	public static final String ERROR_FILE_NAME = "error.ser";
	
	// FIELDS
	private final UUID id;
	
	private String msalsaAligmentFastaFilePath;
	private String msalsaAligmentClustalFilePath; 
	private String msalsaPhylogeneticTreeFilePath;
	
	// CONSTRUCTOR
	public AlignmentResult(UUID id) throws ClassNotFoundException, Exception
	{
		if (id == null){
			throw new IllegalArgumentException("id");
		}
		
		this.id = id;
		initSalsaData(id.toString());
	}
	
	// GET / SET
	public UUID getId() {
		return this.id;
	}
	
	public String getAligmentFastaFilePath() {
		return this.msalsaAligmentFastaFilePath;
	}
	
	public String getAligmentClustalFilePath() {
		return this.msalsaAligmentClustalFilePath;
	}
	
	public String getPhylogeneticTreeFilePath() {
		return this.msalsaPhylogeneticTreeFilePath;
	}
	
	/**
	 * Load result file path from the file system 
	 * 
	 * @param idProccedRequest
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws AlignmentExecutionException 
	 */
	private void initSalsaData(String idProccedRequest) throws ClassNotFoundException, AlignmentExecutionException, FileNotFoundException, IOException {
		// Get the folder where the files are stored
		File processedRequestFolder = new File(Paths.get(
				ConfigurationManager.getInstance().getServerConfiguration().getTemporaryFilePath(),
				idProccedRequest).toString());
		File[] listOfFiles = processedRequestFolder.listFiles();
		
		String msalsaFastaAligmentFilePath = null;
		String msalsaClustalAligmentFilePath = null;
		String msalsaPhylogeneticTreeFilePath = null;		
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	// Search SALSA alignment and tree files
		        if (file.getName().endsWith(SalsaAlgorithmExecutor.SALSA_ALIGMENT_FASTA_SUFFIX)){
		        	msalsaFastaAligmentFilePath = file.getAbsolutePath();
		        	continue;
		        }
		        if (file.getName().endsWith(SalsaAlgorithmExecutor.SALSA_ALIGMENT_CLUSTAL_SUFFIX)){
		        	msalsaClustalAligmentFilePath = file.getAbsolutePath();
		        	continue;
		        }
		        if (file.getName().endsWith(SalsaAlgorithmExecutor.SALSA_TREE_SUFFIX)){
		        	msalsaPhylogeneticTreeFilePath = file.getAbsolutePath();
		        	continue;
		        }
				if (file.getName().equals(ERROR_FILE_NAME)) {
					// If errors are present throw the exception thrown inside the alignment process
					String aligmentErrorPath = file.getAbsolutePath();
					ObjectSerializer<Exception> exceptionSerializer = new ObjectSerializer<>(aligmentErrorPath);
					throw new AlignmentExecutionException(exceptionSerializer.deserialize());
				}
		    }
		}
		
		if (msalsaFastaAligmentFilePath == null){
			throw new IllegalStateException("Unable to find file " + SalsaAlgorithmExecutor.SALSA_ALIGMENT_FASTA_SUFFIX + " for UUID " + idProccedRequest);
		}
		
		this.msalsaAligmentFastaFilePath = msalsaFastaAligmentFilePath;
		this.msalsaAligmentClustalFilePath = msalsaClustalAligmentFilePath;
		this.msalsaPhylogeneticTreeFilePath = msalsaPhylogeneticTreeFilePath;
	}
}
