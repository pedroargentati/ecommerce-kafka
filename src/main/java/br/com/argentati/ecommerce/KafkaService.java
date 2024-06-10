package br.com.argentati.ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaService<T> implements Closeable {

	private final KafkaConsumer<String, T> consumer;
	private final ConsumerFunction<T> parse;

	private KafkaService(ConsumerFunction<T> parse, String groupId, Class<T> type, Map<String, String> properties) {
		this.parse = parse;
		this.consumer = new KafkaConsumer<>(getProperties(type, groupId, properties));
	}

	public KafkaService(String groupId, String topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
		this(parse, groupId, type, properties);
		consumer.subscribe(Collections.singleton(topic));
	}

	public KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Class<T> type, Map<String, String> properties) {
		this(parse, groupId, type, properties);
		consumer.subscribe(topic);
	}

	public void run() {
		while (true) {
			var<String, T> records = consumer.poll(Duration.ofMillis(100)); // irá escutar a cada 100ms.
			if (!records.isEmpty()) {
				System.out.println(records.count() + " registros foram encontrados.");
				for (var record : records) {
					parse.consume(record);
				}
			}
		}
	}

	private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
		var properties = new Properties();

		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()); // desserializador
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, Uuid.randomUuid().toString());
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
		
		// configuração extra de properties.
		properties.putAll(overrideProperties);

		return properties;
	}

	@Override
	public void close() {
		consumer.close();
	}
}
