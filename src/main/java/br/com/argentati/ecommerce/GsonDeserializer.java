package br.com.argentati.ecommerce;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDeserializer<T> implements Deserializer<T> {
	
	public static final String TYPE_CONFIG = "br.com.argentati.ecommerce.type_config";
	private Class<T> type;
	
	private final Gson gson = new GsonBuilder().create();
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		String typeName = String.valueOf(configs.get(TYPE_CONFIG));
		try {
			this.type = (Class<T>) Class.forName(typeName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Tipo de desserializaçao não existe.");
		}
	}

	@Override
	public T deserialize(String topic, byte[] bytes) {
		return gson.fromJson(new String(bytes), type);
	}

}
