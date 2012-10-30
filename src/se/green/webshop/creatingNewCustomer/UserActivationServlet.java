package se.green.webshop.creatingNewCustomer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.green.webshop.helpClasses.Initializer;
import se.group2.webbshop.exceptions.RepositoryException;
import se.group2.webbshop.model.Customer;
import se.group2.webbshop.service.ECommerceService;

@WebServlet("/activate_user")
public class UserActivationServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		ECommerceService service = (ECommerceService) getServletContext().getAttribute(Initializer.ECOMMERCE_MANAGER_KEY);

		if (request.getParameter("user_id") != null)
		{

			int customerId = Integer.parseInt(request.getParameter("user_id"));

			try
			{
				Customer customer = service.getCustomer(customerId);
				service.activateCustomer(customerId);
				System.out.println(customer + " is activated");
				response.getWriter().print("Customer account with username " + customer.getUserName() + " is activated. ");
				response.getWriter().print("Congratulations!");
			} catch (RepositoryException e)
			{
				e.printStackTrace();
			}
		} else
		{
			response.getWriter().print("Hello World!");
		}

	}

}
