package se.green.webshop.creatingNewCustomer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/WelcomeNewUser")
public class Welcome extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		request.setAttribute("page_to_include", "empty.html");
		request.setAttribute("body_message", "The user with username " + request.getParameter("user_name") + " is created <br/>"
				+ "The letter with the activation link is send to your email. <br/>" + "Remember to activate!");
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
