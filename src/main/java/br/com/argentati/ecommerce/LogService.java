package br.com.argentati.ecommerce;

import java.time.Duration;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {
	
	// consumindo mais de um tópico.

	public static void main(String[] args) throws Exception {

		var consumer = new KafkaConsumer<String, String>(properties());
		consumer.subscribe(Pattern.compile("ECCOMERCE.*"));

		while (true) {
			var records = consumer.poll(Duration.ofMillis(100)); // irá escutar a cada 100ms.

			if (!records.isEmpty()) {
				System.out.println(records.count() + " registros foram encontrados.");
				for (var record : records) {
					System.out.println("----------------------------------------------------");
					System.out.println("LOG: " + record.topic());
					System.out.println(record.key());
					System.out.println(record.value());
					System.out.println(record.partition());
					System.out.println(record.offset());
					System.out.println("----------------------------------------------------");
				}
			}
		}

	}

	private static Properties properties() {
		var properties = new Properties();

		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // desserializador
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, LogService.class.getSimpleName());

		return properties;
	}

}
