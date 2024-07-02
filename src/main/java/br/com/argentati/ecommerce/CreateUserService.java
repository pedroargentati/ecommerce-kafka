package br.com.argentati.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {

	private final Connection connection;

	public CreateUserService() throws SQLException {
		String url = "jdbc:sqlite:target/users_database.db";
		connection = DriverManager.getConnection(url);
		connection.createStatement()
				.execute("CREATE TABLE Users (" + "UUID VARCHAR(200) PRIMARY KEY," + "EMAIL VARCHAR(200) )");
	}

	public static void main(String[] args) throws Exception {
		var createuserService = new CreateUserService();
		try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
				createuserService::parse, Order.class, Map.of())) {
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Order> record) throws SQLException {
		System.out.println("------------------------------------------");
		System.out.println("Processing new order, checking for new user");
		System.out.println(record.value());
		System.out.println("Order processed");
		var order = record.value();

		if (this.isNewUser(order.getEmail())) {
			this.insertNewUser(order.getUserId(), order.getEmail());
		}
	}

	private void insertNewUser(String uuid, String email) throws SQLException {
		var insert = connection.prepareCall("INSERT INTO Users (uuid, email) "
				+ "VALUES (?,?); ");
		insert.setString(1, uuid);
		insert.setString(2, "email");
		
		insert.execute();
		System.out.println("Usuário uuid e " + email + " adicionados.");
	}

	private boolean isNewUser(String email) throws SQLException {
		var exists = connection.prepareStatement("SELECT UUID FROM Users "
				+ "WHERE EMAIL = ? LIMIT 1;");
		
		exists.setString(1, email);
		var result = exists.executeQuery();
		
		return !result.next();
	}
}
