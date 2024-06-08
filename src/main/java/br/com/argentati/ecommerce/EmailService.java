package br.com.argentati.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService {

	public static void main(String[] args) throws Exception {
		var emailService = new EmailService();
		var kafkaService = new KafkaService(EmailService.class.getSimpleName(), "ECCOMERCE_SEND_EMAIL", emailService::parse);
		
		kafkaService.run();
	}
	
	private void parse(ConsumerRecord<String, String> record) {
		System.out.println("----------------------------------------------------");
		System.out.println("Enviando email...");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
		System.out.println("----------------------------------------------------");

		System.out.println("Email enviado com sucesso.");
	}

}
