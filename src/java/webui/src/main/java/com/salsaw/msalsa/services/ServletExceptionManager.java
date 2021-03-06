/**
 * Copyright 2016 Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
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
package com.salsaw.msalsa.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Alessandro Daniele, Fabio Cesarato, Andrea Giraldin
 *
 */
public class ServletExceptionManager {

	public static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
	static final Logger logger = LogManager.getLogger(ServletExceptionManager.class);

	/**
	 * Show the {@code errorMessage} passed to user in error JSP
	 * 
	 * @param errorMessage
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void manageErrorMessageException(String errorMessage, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set error message as request attribute
		request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, errorMessage);

		request.getRequestDispatcher("/error.jsp").forward(request, response);
	}
	
	/**
	 * Log the exception and show the message of the {@code exception} passed to user in error JSP
	 * 
	 * @param exception
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void manageException(Exception exception, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.error(exception);

		manageErrorMessageException(exception.getMessage(), request, response);
	}
}
