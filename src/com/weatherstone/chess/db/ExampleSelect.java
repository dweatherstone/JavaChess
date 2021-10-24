package com.weatherstone.chess.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ExampleSelect {

	public static void main(String[] args) {
		try {
			//Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/ChessGames", "root", "chimneys23");
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM Player");
			while (rs.next()) {
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
