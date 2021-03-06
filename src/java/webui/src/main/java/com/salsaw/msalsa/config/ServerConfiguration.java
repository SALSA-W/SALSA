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
package com.salsaw.msalsa.config;

import java.io.File;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public class ServerConfiguration {
	// FILEDS
	private final File clustalW;
	private final File clustalO;
	private final String temporaryFilePath;
	private final String mailUsername;
	private final String mailPassword;
	private final String mailSmtpAuth;
	private final boolean mailSmtpStartTtlsEnable;
	private final String mailSmtpHost;
	private final int mailSmtpPort;
	private final int cleanDaysValidityJob;
	private final String sitePublisher;
    private final String googleAnalyticsPropertyID;
    private final Integer threadPoolMaxNumber;
	
	// CONSTRUCTOR
	ServerConfiguration(
			String clustalWPath,
			String clustalOPath,
			String temporaryFilePath,
			String mailUsername,
			String mailPassword,
			String mailSmtpAuth,
			String mailSmtpStartTtlsEnable,
			String mailSmtpHost,
			String mailSmtpPort,
			String cleanDaysValidityJob,
			String sitePublisher,
            String googlePropertyID,
            String threadPoolMaxNumber){
		
		this.clustalW = new File(clustalWPath);
		this.clustalO = new File(clustalOPath);		
		this.temporaryFilePath = temporaryFilePath;
		this.mailUsername = mailUsername;
		this.mailPassword = mailPassword;
		this.mailSmtpAuth = mailSmtpAuth;
		this.mailSmtpStartTtlsEnable = Boolean.parseBoolean(mailSmtpStartTtlsEnable);
		this.mailSmtpHost = mailSmtpHost;
		this.mailSmtpPort = Integer.parseInt(mailSmtpPort);
		this.cleanDaysValidityJob = Integer.parseInt(cleanDaysValidityJob);
		this.sitePublisher = sitePublisher;
        this.googleAnalyticsPropertyID = googlePropertyID;
        this.threadPoolMaxNumber = Integer.parseInt(threadPoolMaxNumber);
	}
	
	// GET
	public File getClustalW() {
		return this.clustalW;
	}
	
	public File getClustalO() {
		return this.clustalO;
	}
	
	public String getTemporaryFilePath() {
		return this.temporaryFilePath;
	}
	
	public String getMailUsername() {
		return this.mailUsername;
	}
	
	public String getMailPassword() {
		return this.mailPassword;
	}
	
	public int getCleanDaysValidityJob() {
		return this.cleanDaysValidityJob;
	}
	
	public String getSitePublisher() {
		return this.sitePublisher;
	}
        
	public String getGoogleAnalyticsPropertyID() {
		return this.googleAnalyticsPropertyID;
	}
	
	public Integer getThreadPoolMaxNumber() {
		return this.threadPoolMaxNumber;
	}
	
	public String getMailSmtpAuth() {
		return mailSmtpAuth;
	}

	public boolean isMailSmtpStartTtlsEnable() {
		return mailSmtpStartTtlsEnable;
	}

	public String getMailSmtpHost() {
		return mailSmtpHost;
	}

	public int getMailSmtpPort() {
		return mailSmtpPort;
	}
}

