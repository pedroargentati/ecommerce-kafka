package br.com.argentati.ecommerce;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		try (var dispatcher = new KafkaDispatcher()) {
			for (int i = 0; i < 10; i++) {
				var key = UUID.randomUUID().toString();
				var value = key + "67523,7894589742";
				dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

				var email = "Obrigado pela compra! Nós estamos processando ela no momento.";
				dispatcher.send("ECOMMERCE_NEW_ORDER", key, email);
			}
		}

	}
}
