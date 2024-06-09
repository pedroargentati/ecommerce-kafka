package br.com.argentati.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaDispatcher<T> implements Closeable {

	private final KafkaProducer<String, T> producer;

	public KafkaDispatcher() {
		this.producer = new KafkaProducer<>(properties());
	}

	private static Properties properties() {
		var properties = new Properties();

		// Aonde o Kafka vai se conectar
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");

		// Serializador a ser usado para transformar a mensagem e a chave de strings
		// para bytes
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());

		return properties;
	}

	public void send(String topic, String key, T value) throws InterruptedException, ExecutionException {
		// Registro que será enviada. Tópico / valores
		var record = new ProducerRecord<>(topic, key, value);

		// enviando uma mensagem que será armazenado no Kafka.
		producer.send(record, callback()).get(); // como o send é assíncrono o get() força a espera (sync)
	}

	private static Callback callback() {
		return (data, exeption) -> {
			// caso a exception seja diferente de nula, logar.
			if (exeption != null) {
				exeption.printStackTrace();
				return;
			}
			System.out.println("Enviando com sucesso: " + data.topic() + ":::partition" + data.partition() + "/ offset"
					+ data.offset() + "/" + data.timestamp());
		};
	}

	@Override
	public void close() {
		producer.close();
	}

}
