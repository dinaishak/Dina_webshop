package se.green.webshop.creatingNewCustomer;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import se.green.webshop.helpClasses.Initializer;
import se.green.webshop.helpClasses.MailClient;
import se.green.webshop.helpClasses.Validator;
import se.group2.webbshop.exceptions.RepositoryException;
import se.group2.webbshop.repository.CustomerRepository;
import se.group2.webbshop.service.BCrypt;
import se.group2.webbshop.service.ECommerceService;

@WebServlet("/NEWCreatingNewUserServlet")
public class NEWCreatingNewUserServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String firstName = request.getParameter("first_name");
		String lastName = request.getParameter("last_name");
		String email = request.getParameter("email");
		String userName = request.getParameter("user_name");
		String password = request.getParameter("password");
		String passwordCopy = request.getParameter("password2");
		String tel = request.getParameter("phone");
		String city = request.getParameter("city");
		String zip = request.getParameter("zip");
		String streetAddress = request.getParameter("street_address");
		String captcha = request.getParameter("captcha");

		request.setAttribute("first_name", firstName);
		request.setAttribute("last_name", lastName);
		request.setAttribute("email", email);
		request.setAttribute("user_name", userName);
		request.setAttribute("password", password);
		request.setAttribute("password2", passwordCopy);
		request.setAttribute("phone", tel);
		request.setAttribute("city", city);
		request.setAttribute("zip", zip);
		request.setAttribute("street_address", streetAddress);

		request.setAttribute("page_to_include", "jsp/customer/NEWSignUp.jsp");

		ECommerceService service = (ECommerceService) getServletContext().getAttribute(Initializer.ECOMMERCE_MANAGER_KEY);
		CustomerRepository cr = service.getCustomerRepository();

		HttpSession session = request.getSession(false);
		String captchaToTry = "";
		if (session != null && session.getAttribute("captcha") != null)
		{
			captchaToTry = (String) session.getAttribute("captcha");
		}

		HashMap<String, String> errorMessages = new HashMap<String, String>();
		try
		{
			errorMessages = new Validator(firstName, lastName, email, userName, password, passwordCopy, tel, city, zip, streetAddress,
					captcha, captchaToTry, cr).validateNewUser();
			request.setAttribute("error_messages", errorMessages);
			if (errorMessages.isEmpty())
			{
				String criptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

				int customerId = service.createNewCustomer(firstName, lastName, email, userName, criptedPassword, tel, city, zip,
						streetAddress);

				session.invalidate();

				String activationMessage = "http://" + getServletContext().getInitParameter("db_host") + ":8080/"
						+ "GreenWebShop/activate_user?user_id=" + customerId;

				RequestDispatcher dispatcher = request.getRequestDispatcher("WelcomeNewUser");
				dispatcher.include(request, response);

				new MailClient().processRequest(request, response, getServletContext().getInitParameter("admin_email"), email,
						"confirmation", activationMessage, getServletContext().getInitParameter("admin_email"), getServletContext()
								.getInitParameter("admin_email_password"));
			} else
			{
				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				dispatcher.forward(request, response);
			}
		} catch (RepositoryException e1)
		{
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
			e1.printStackTrace();
		} catch (Exception e)
		{
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
			e.printStackTrace();
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doPost(req, resp);
	}

}
