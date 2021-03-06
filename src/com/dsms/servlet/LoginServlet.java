package com.dsms.servlet;

/**
* Learner Login module
*/

import java.io.*;
import javax.servlet.http.*;

import com.dsms.entity.LearnerAvailableCoursesVO;
import com.dsms.entity.LearnerCourseScheduleVO;
import com.dsms.entity.OffersVO;
import com.dsms.util.CryptWithMD5;
import com.dsms.util.DatabaseOperations;
import com.dsms.util.UtilConstants;

import javax.servlet.*;
import java.sql.*;
import java.util.List;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String username = request.getParameter("Username");
		String password0 = request.getParameter("password");
		String password = CryptWithMD5.crypt(password0);

		System.out.println("Username is" + username);
		System.out.println("Password is" + password);

		Integer learner_id = null;
		User userObject = null;
		RequestDispatcher rd = null;
		boolean successFlag = false;
		// creating connection with the database
		if (username != null && password != null) {
			learner_id = Authenticator.authenticateLearner(username, password);
			System.out.println("LearnerId"+learner_id);
			
			
			if (learner_id != null) {
				UtilConstants.setLearnerId(learner_id);
				
				///setting session
				HttpSession session = request.getSession();
				session.setAttribute("userType", "learner");
				session.setAttribute("l_id", learner_id);
				System.out.println("Valid!!");
				userObject = FetchUserObject.getUserObject(username);
				if (userObject != null) {
					successFlag = true;

				} else {
					successFlag = false;
				}

			} else {
				successFlag = false;

			}

			if (successFlag) {
				doGet(request, response);
			} else {
				rd = request.getRequestDispatcher("/index.jsp");
				request.getSession().setAttribute("loginfailed", "true");

				// request.getSession().setAttribute(arg0, arg1);
				rd.forward(request, response);
				return;
			}

		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			String username = request.getParameter("Username");
			DatabaseOperations dboper = new DatabaseOperations();
			int learnerId = dboper.getLearnerId(username);
			List<LearnerAvailableCoursesVO> courseList = dboper.getCourseDetails(learnerId);
			List<LearnerCourseScheduleVO> courseSchedule = dboper.getCourseSchedule(learnerId);
			List<OffersVO> offers = dboper.getOffers();
			if(courseSchedule.size()==0){
				request.setAttribute("courseList", courseList);
				request.setAttribute("offerList", offers);
			}
			request.setAttribute("courseSchedule", courseSchedule);

			RequestDispatcher rd = request.getRequestDispatcher("learnerDashboard.jsp");
			rd.forward(request, response);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}