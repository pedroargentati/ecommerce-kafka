package br.com.argentati.ecommerce;

import org.apache.kafka.common.serialization.Serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Classe para serializar objetos em JSON e pegar seus bytes.
 * 
 * O Kafka só sabe serializar string em bytes, por isso essa classe é
 * necessária.
 * 
 * @author Pedro
 *
 * @param data <T>
 */
public class GsonSerializer<T> implements Serializer<T> {

	private final Gson gson = new GsonBuilder().create();

	@Override
	public byte[] serialize(String topic, T data) {
		return gson.toJson(data).getBytes();
	}

}
