package com.salsaw.msalsa.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.salsaw.msalsa.services.AlignmentRequestManager;
import com.salsaw.msalsa.services.ServletExceptionManager;

/**
 * Servlet implementation class AlignmentStatusServlet
 */
@WebServlet("/alignment-status")
public class AlignmentStatusServlet extends HttpServlet {
	// CONSTANTS
	/**
	 * 
	 */
	private static final long serialVersionUID = 8769462891168921812L;
	public static final String ID_PARAMETER = "id";	
	static final Logger logger = LogManager.getLogger(AlignmentStatusServlet.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			UUID idRequest = readAndValidateProcessId(request, response);
			if (idRequest == null) {
				// The input data are invalid
				ServletExceptionManager.manageErrorMessageException("Invalid input data", request, response);
			}
			else {
				if (AlignmentRequestManager.getInstance().IsRequestCompleted(idRequest) == false) {
					// Redirect the request to loading
					RequestDispatcher requestDispatcher = request.getRequestDispatcher("/loading.jsp");
					requestDispatcher.forward(request, response);
				} else {

					String aligmentResultServlet = "/" + AlignmentResultServlet.class.getSimpleName() + "?"
							+ ID_PARAMETER + URLEncoder.encode(idRequest.toString(), StandardCharsets.UTF_8.toString());
					// Redirect the request to result servlet
					RequestDispatcher requestDispatcher = request.getRequestDispatcher(aligmentResultServlet);
					requestDispatcher.forward(request, response);
				}
			}		
		}  catch (Exception e) {
			ServletExceptionManager.manageException(e, request, response);
		}
	}

	public static UUID readAndValidateProcessId(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// For response code logic see
		// http://www.restapitutorial.com/httpstatuscodes.html

		// Read the request id
		String idRequestString = request.getParameter(ID_PARAMETER);

		if (idRequestString == null) {
			logger.warn(ID_PARAMETER + " not set");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}

		UUID idRequest = UUID.fromString(idRequestString);
		if (AlignmentRequestManager.getInstance().RequestExists(idRequest) == false) {
			logger.warn(idRequestString + " not exists");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		return idRequest;
	}
}
