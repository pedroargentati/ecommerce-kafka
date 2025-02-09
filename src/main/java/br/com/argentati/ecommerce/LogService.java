package br.com.argentati.ecommerce;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {

	// consumindo mais de um tópico.
	public static void main(String[] args) throws Exception {
		var logService = new LogService();
		
		try (var kafkaService = new KafkaService<String>(LogService.class.getSimpleName(),
				Pattern.compile("ECOMMERCE.*"),
				logService::parse,
				String.class,
				Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
			kafkaService.run();			
		}
	}

	private void parse(ConsumerRecord<String, String> record) {
		System.out.println("----------------------------------------------------");
		System.out.println("LOG: " + record.topic());
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
		System.out.println("----------------------------------------------------");
	}
}
