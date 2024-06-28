package br.com.argentati.ecommerce;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {

	public static void main(String[] args) throws Exception {
		var fraudService = new FraudDetectorService();
		
		try (var kafkaService = new KafkaService<Order>(FraudDetectorService.class.getSimpleName(),
				"ECOMMERCE_NEW_ORDER",
				fraudService::parse,
				Order.class,
				Map.of())) {
			kafkaService.run();
		}
	}
	
	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();
	
	private void parse(ConsumerRecord<String, Order> record) throws InterruptedException, ExecutionException {
		System.out.println("----------------------------------------------------");
		System.out.println("Processando nova compra. Verificando se há fraude.");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
		System.out.println("----------------------------------------------------");

		var order = record.value();
		if (this.isFraud(order)) {
			// simulando uma detecção de fraude.
			System.out.println("Compra é uma Fraude!" + order);
			orderDispatcher.send("ECOMMERCEORDER_REJECTED", order.getUserId(), null);
		} else {
			System.out.println("Compra processada com sucesso." + order);
		}

	}

	private boolean isFraud(Order order) {
		return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
	}

}
