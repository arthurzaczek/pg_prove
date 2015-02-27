package pg_prove;

import static pg_prove.Console.out;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class Main {
	public static void main(String[] args) {
		Console.init();

		out.println("pg_prove replacement written in java");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			out.println("** ERROR: Unable to load postgresql driver");
			e.printStackTrace();
			System.exit(2);
		}
		CmdArgs arguments = CmdArgs.parse(args);
		String url = "jdbc:postgresql:" + arguments.getDbName() + "?allowMultiQueries=true";
		String username = null;
		String password = null;

		try (Connection db = DriverManager.getConnection(url, username, password)) {
			String sql = String.join("\n", Files.readAllLines(Paths.get(arguments.getFileName())));
			Statement cmd = db.createStatement();

			boolean hasMoreResultSets = cmd.execute(sql);
			READING_QUERY_RESULTS: // label
			while (hasMoreResultSets || cmd.getUpdateCount() != -1) {
				if (hasMoreResultSets) {
					ResultSet rs = cmd.getResultSet();
					while (rs.next()) {
						String lines = rs.getString(1);
						out.println(lines);
					}
					rs.close();
				} else {
					int queryResult = cmd.getUpdateCount();
					if (queryResult == -1) {
						break READING_QUERY_RESULTS;
					}
				}

				hasMoreResultSets = cmd.getMoreResults();
			}

		} catch (SQLException e) {
			out.println("** ERROR: Database exception:");
			e.printStackTrace();
		} catch (FileNotFoundException e1) {
			out.println("** ERROR: File not found:");
			e1.printStackTrace();
		} catch (IOException e1) {
			out.println("** ERROR: I/O exception:");
			e1.printStackTrace();
		}
	}
}
