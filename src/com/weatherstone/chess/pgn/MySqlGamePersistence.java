package com.weatherstone.chess.pgn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.weatherstone.chess.engine.board.Board;
import com.weatherstone.chess.engine.board.Move;
import com.weatherstone.chess.engine.player.Player;

public class MySqlGamePersistence implements PGNPersistence {

	private final Connection dbConnection;

	private static MySqlGamePersistence INSTANCE = new MySqlGamePersistence();
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String NEXT_BEST_MOVE_QUERY = "SELECT SUBSTR(g1.moves, LENGTH('%s') + %d, INSTR(SUBSTR(g1.moves, LENGTH('%s') + %d, LENGTH(g1.moves)), ',') - 1), "
			+ "COUNT(*) FROM GameBlackWidow g1 WHERE g1.moves LIKE '%s%%' AND (outcome = '%s') GROUP BY substr(g1.moves, LENGTH('%s') + %d, "
			+ "INSTR(substr(g1.moves, LENGTH('%s') + %d, LENGTH(g1.moves)), ',') - 1) ORDER BY 2 DESC";

	private MySqlGamePersistence() {
		this.dbConnection = createDBConnection();
		createGameTable();
		createIndex("outcome", "OutcomeIndex");
		// createIndex("moves", "MoveIndex");
	}

	private static Connection createDBConnection() {
		try {
			InputStream input = new FileInputStream("config.properties");
			Properties prop = new Properties();
			prop.load(input);
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.user"), prop.getProperty("db.password"));
		} catch (final ClassNotFoundException | SQLException | FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static MySqlGamePersistence get() {
		return INSTANCE;
	}

	@Override
	public void persistGame(final Game game) {
		executePerisist(game);

	}

	@Override
	public Move getNextBestMove(final Board board, final Player player, final String gameText) {
		return queryBestMove(board, player, gameText);
	}

	private void createIndex(final String columnName, final String indexName) {
		try {
			final String sqlString = "SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_CATALOG = 'def' AND "
					+ "                 TABLE_SCHEMA = DATABASE() AND TABLE_NAME = \"GameBlackWidow\" AND INDEX_NAME = \""
					+ indexName + "\"";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet resultSet = gameStatement.getResultSet();
			if (!resultSet.isBeforeFirst()) {
				final Statement indexStatement = this.dbConnection.createStatement();
				indexStatement.execute("CREATE INDEX " + indexName + " on GameBlackWidow(" + columnName + ");\n");
				indexStatement.close();
			}
			gameStatement.close();
		} catch (final SQLException e) {
			System.out.println("CREATE INDEX " + indexName + " on GameBlackWidow(" + columnName + ");\n");
			e.printStackTrace();
		}

	}

	private void createGameTable() {
		try {
			final Statement statement = this.dbConnection.createStatement();
			statement.execute(
					"CREATE TABLE IF NOT EXISTS GameBlackWidow(id int primary key, outcome varchar(10), moves varchar(3072));");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getMaxGameRow() {
		int maxId = 0;
		try {
			final String sqlString = "SELECT MAX(ID) FROM GameBlackWidow";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet rs2 = gameStatement.getResultSet();
			if (rs2.next()) {
				maxId = rs2.getInt(1);
			}
			gameStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxId;
	}

	private void executePerisist(Game game) {
		try {
			final String gameSqlString = "INSERT INTO GameBlackWidow(id, outcome, moves) VALUES(?, ?, ?);";
			final PreparedStatement gameStatement = this.dbConnection.prepareStatement(gameSqlString);
			gameStatement.setInt(1, getMaxGameRow() + 1);
			gameStatement.setString(2, game.getWinner());
			gameStatement.setString(3, game.getMoves().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			gameStatement.executeUpdate();
			gameStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private Move queryBestMove(final Board board, final Player player, final String gameText) {
		String bestMove = "";
		String count = "0";
		try {
			final int offSet = gameText.isEmpty() ? 1 : 3;
			final String sqlString = String.format(NEXT_BEST_MOVE_QUERY, gameText, offSet, gameText, offSet, gameText,
					player.getAlliance().name(), gameText, offSet, gameText, offSet);
			System.out.println(sqlString);
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet rs2 = gameStatement.getResultSet();
			if (rs2.next()) {
				bestMove = rs2.getString(1);
				count = rs2.getString(2);
			}
			gameStatement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\tselected book move = " + bestMove + " with " + count + " hits");
		return PGNUtils.createMove(board, bestMove);
	}

}
