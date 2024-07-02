package br.com.argentati.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {
	
	private final Connection connection;
	
	public CreateUserService() {
		String url = "jdbc:sqlite:target/users_database.db";
		conenction = DriverManager.getConnection(url);
		try {
			connection.createStatement().execute("CREATE TABLE Users ("
					+ "UUID VARCHAR(200) PRIMARY KEY,"
					+ "EMAIL VARCHAR(200) )");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
        var fraudService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        System.out.println("Order processed");
		var order = record.value();

    }
	
}
