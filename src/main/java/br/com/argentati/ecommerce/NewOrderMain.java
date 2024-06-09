package br.com.argentati.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		try (var orderDispatcher = new KafkaDispatcher<Order>()) {
			try (var emailDispatcher = new KafkaDispatcher<String>()) {
				for (int i = 0; i < 10; i++) {
					var userId = UUID.randomUUID().toString();
					var orderId = UUID.randomUUID().toString();
					var amount = Math.random() * 5000 + 1;

					var order = new Order(userId, orderId, new BigDecimal(amount));
					orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

					var email = "Obrigado pela compra! Nós estamos processando ela no momento.";
					emailDispatcher.send("ECOMMERCE_NEW_ORDER", userId, email);
				}
			}
		}
	}
}
