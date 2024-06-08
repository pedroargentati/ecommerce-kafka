package br.com.argentati.ecommerce;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class NewOrderMain {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		var producer = new KafkaProducer<String, String>(properties());
		
		var key = UUID.randomUUID().toString();
		var value = key + "67523,7894589742";
		
		// Registro que será enviada. Tópico / valores
		var record = new ProducerRecord<String, String>("ECOMMERCE_NEW_ORDER", key, value);
		
		var email = "Obrigado pela compra! Nós estamos processando ela no momento.";
		var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", key, email);
		
		// enviando uma mensagem que será armazenado no Kafka.
		producer.send(record, callback()).get(); // como o send é assíncrono o get() força a espera (sync)
		producer.send(emailRecord).get();
		
	}

	private static Callback callback() {
		return (data, exeption) -> {
			// caso a exception seja diferente de nula, logar.
			if (exeption != null) {
				exeption.printStackTrace();
				return;
			}
			System.out.println("Enviando com sucesso: " + data.topic() + ":::partition" + data.partition() + "/ offset" + data.offset() + "/" + data.timestamp());
		};
	}
	
	private static Properties properties() {
		var properties = new Properties();
		
		// Aonde o Kafka vai se conectar
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		// Serializador a ser usado para transformar a mensagem e a chave de strings para bytes
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		return properties; 
	}

}
