package br.com.argentati.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		try (var orderDispatcher = new KafkaDispatcher<Order>()) {
			var email = Math.random() + "@email,com";
			try (var emailDispatcher = new KafkaDispatcher<String>()) {
				for (int i = 0; i < 10; i++) {
					var orderId = UUID.randomUUID().toString();
					var amount = Math.random() * 5000 + 1;

					var order = new Order(orderId, new BigDecimal(amount), email);
					orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

					var emailCode = "Obrigado pela compra! Nós estamos processando ela no momento.";
					emailDispatcher.send("ECOMMERCE_NEW_ORDER", email, emailCode);
				}
			}
		}
	}
}
