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
package com.salsaw.msalsa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.salsaw.msalsa.algorithm.exceptions.SALSAException;
import com.salsaw.msalsa.cli.App;
import com.salsaw.msalsa.cli.SalsaParameters;
import com.salsaw.msalsa.clustal.ClustalFileMapper;
import com.salsaw.msalsa.config.ConfigurationManager;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class HomePageView extends CustomComponent implements IHomePageView, View {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Navigator navigator;
	protected static final String PROCESSED = "processed";
	
	@AutoGenerated
	private GridLayout mainLayout;	
	@AutoGenerated
	private Upload uploadInput;
	@AutoGenerated
	private Label title;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param navigator 
	 */
	public HomePageView(Navigator navigator) {
		this.navigator = navigator;
		
		buildMainLayout();
		
		// Create form with salsa parameters
		SalsaParameters salsaParameters = new SalsaParameters();
		salsaParameters.setGeneratePhylogeneticTree(true);
		BeanItem<SalsaParameters> salsaParametersBeanItem = new BeanItem<SalsaParameters>(salsaParameters);		
		SalsaParametersForm salsaParametersForm = new SalsaParametersForm(salsaParametersBeanItem);		
		
		Button toogleSalsaParametersButton = new Button("Show/Hide M-SALSA Parameters");
		toogleSalsaParametersButton.addClickListener(new Button.ClickListener() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				salsaParametersForm.setVisible(!salsaParametersForm.isVisible());
		    }
		});
		
		toogleSalsaParametersButton.setWidth("-1px");
		toogleSalsaParametersButton.setHeight("-1px");			
		mainLayout.addComponent(toogleSalsaParametersButton);
		mainLayout.setComponentAlignment(toogleSalsaParametersButton, new Alignment(48));
		
		salsaParametersForm.setWidth("-1px");
		salsaParametersForm.setHeight("-1px");
		mainLayout.addComponent(salsaParametersForm);
		mainLayout.setComponentAlignment(salsaParametersForm, new Alignment(48));
		
		setCompositionRoot(mainLayout);

		// TODO add user code here		
		
		// Implement both receiver that saves upload in a file and
		// listener for successful upload
		class AligmentUploader implements Receiver, SucceededListener {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			public File file;
		    
		    public OutputStream receiveUpload(String filename,
		                                      String mimeType) {
		        // Create upload stream
		        FileOutputStream fos = null; // Stream to write to
		        try {
		    		// Load server configuration
		    		String tmpFolder = ConfigurationManager.getInstance().getServerConfiguration().getTemporaryFilePath();
		    		
		            // Open the file for writing.
		            file = Paths.get(tmpFolder, filename).toFile();
		            fos = new FileOutputStream(file);
		        } catch (final java.io.FileNotFoundException e) {
		            new Notification("Could not open file<br/>",
		                             e.getMessage(),
		                             Notification.Type.ERROR_MESSAGE)
		                .show(Page.getCurrent());
		            return null;
		        }
		        return fos; // Return the output stream to write to
		    }

		    public void uploadSucceeded(SucceededEvent event) {
		        // TODO - start with align - test code to refactor
		    	
		    	// Get path to correct Clustal process
		    	switch (salsaParameters.getClustalType()) {
				case CLUSTAL_W:
					salsaParameters.setClustalPath(ConfigurationManager.getInstance().getServerConfiguration().getClustalW().getAbsolutePath());
					break;

				case CLUSTAL_O:
					salsaParameters.setClustalPath(ConfigurationManager.getInstance().getServerConfiguration().getClustalO().getAbsolutePath());
					break;
				}
		    	salsaParameters.setClustalWPath(ConfigurationManager.getInstance().getServerConfiguration().getClustalW().getAbsolutePath());
		    	
		    	// Create M-SALSA output file name
		    	salsaParameters.setInputFile(file.getAbsolutePath());
		    	String inputFileName = FilenameUtils.getBaseName(file.getAbsolutePath());		    	
		    	salsaParameters.setOutputFile(
		    			Paths.get(
		    			ConfigurationManager.getInstance().getServerConfiguration().getTemporaryFilePath(),
		    			inputFileName + "-msalsa-aln.fasta").toString());
		    	
		    	
		    	try {		    		
					App.callClustal(salsaParameters);
					
					// Store path where files has been generated
			    	ClustalFileMapper clustalFileMapper = new ClustalFileMapper(file.getAbsolutePath());
			    	clustalFileMapper.setAlignmentFilePath(salsaParameters.getOutputFile());
			    	clustalFileMapper.setPhylogeneticTreeFile(
			    			Paths.get(
			    			ConfigurationManager.getInstance().getServerConfiguration().getTemporaryFilePath(),
			    			inputFileName + "-msalsa-aln.ph").toString());
			    	
			    	// Show view with results
		            final PhylogeneticTreeView testTreeView = new PhylogeneticTreeView(clustalFileMapper);	                   
		            navigator.addView(PROCESSED, testTreeView);            
		            navigator.navigateTo(PROCESSED);
		            
				} catch (SALSAException | IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					new Notification("ERROR: ",
                            e.getMessage(),
                            Notification.Type.ERROR_MESSAGE)
							.show(Page.getCurrent());
				}
		    	 
		    	
	             /*
		    	for (IHomePageListener listener: listeners){
		            listener.buttonClick(file);
		    	}
	             */
		    }
		};
		
		AligmentUploader receiver = new AligmentUploader();
		
		uploadInput.setReceiver(receiver);
		uploadInput.addSucceededListener(receiver);

	}

	/* Only the presenter registers one listener... */
    List<IHomePageListener> listeners =
            new ArrayList<IHomePageListener>();

    @Override
    public void addListener(IHomePageListener listener) {
        listeners.add(listener);
    }

	@AutoGenerated
	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		mainLayout.setRows(4);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// title
		title = new Label();
		title.setImmediate(false);
		title.setWidth("-1px");
		title.setHeight("-1px");
		title.setValue("M-SALSA");
		mainLayout.addComponent(title, 0, 0);
		mainLayout.setComponentAlignment(title, new Alignment(48));
		
		// uploadInput
		uploadInput = new Upload();
		uploadInput.setCaption("Upload and process align input");
		uploadInput.setImmediate(false);
		uploadInput.setWidth("-1px");
		uploadInput.setHeight("-1px");
		mainLayout.addComponent(uploadInput, 0, 1);
		mainLayout.setComponentAlignment(uploadInput, new Alignment(48));
				
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
