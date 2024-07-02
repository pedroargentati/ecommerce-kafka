package br.com.argentati.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 8226200295824494810L;

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() {
		super.destroy();
		orderDispatcher.close();
		emailDispatcher.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			var email = req.getParameter("email");
			var amount = req.getParameter("amount");

			var orderId = UUID.randomUUID().toString();

			var order = new Order(orderId, new BigDecimal(amount), email);
			orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

			var emailCode = "Obrigado pela compra! Nós estamos processando ela no momento.";
			emailDispatcher.send("ECOMMERCE_NEW_ORDER", email, emailCode);

			System.out.println("Nova compra enviada com sucesso.");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print("Nova compra enviada com sucesso.");
		} catch (Exception e) {
			throw new ServletException();
		}
	}

}
