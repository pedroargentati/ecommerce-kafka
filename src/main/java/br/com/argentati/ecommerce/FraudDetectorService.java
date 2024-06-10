package br.com.argentati.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {

	public static void main(String[] args) throws Exception {
		var fraudService = new FraudDetectorService();
		
		try (var kafkaService = new KafkaService<Order>(FraudDetectorService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER", fraudService::parse, Order.class)) {
			kafkaService.run();
		}
	}
	
	private void parse(ConsumerRecord<String, Order> record) {
		System.out.println("----------------------------------------------------");
		System.out.println("Processando nova compra. Verificando se há fraude.");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
		System.out.println("----------------------------------------------------");
		
		System.out.println("Compra processada com sucesso.");
	}

}
