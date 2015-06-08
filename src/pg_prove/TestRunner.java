package pg_prove;

import static pg_prove.Console.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestRunner {
	List<TestCase> tests = new LinkedList<TestCase>();
	CmdArgs arguments;

	public TestRunner(CmdArgs arguments) {
		this.arguments = arguments;
	}

	public List<TestCase> getTests() {
		return tests;
	}

	public void run() {
		File argFile = arguments.getFileName();
		if (!argFile.exists()) {
			out.println("** ERROR: File or directory " + argFile.getName() + " not found");
			System.exit(1);
		}
		List<File> files;
		if (argFile.isFile()) {
			files = Arrays.asList(arguments.getFileName());
		} else if (argFile.isDirectory()) {
			files = Arrays.asList(argFile.listFiles());
		} else {
			throw new IllegalStateException("Argument " + argFile.getName() + " is neither a file or directory");
		}

		String url = String.format("jdbc:postgresql://%s:%d/%s",
				arguments.getHost(),
				arguments.getPort(),
				arguments.getDbName());
		String username = arguments.getUsername();
		String password = arguments.getPassword();

		try (Connection db = DriverManager.getConnection(url, username, password)) {
			beginTransaction(db);
			for (File file : files) {
				StringBuilder sql = new StringBuilder();
				for (String line : Files.readAllLines(file.toPath())) {
					sql.append(line + "\n");
					if (line.trim().endsWith(";")) {
						executeStatement(db, sql);
						sql = new StringBuilder();
					}
				}
				if (sql.length() > 0) {
					executeStatement(db, sql);
				}
			}
			rollback(db);
		} catch (SQLException e) {
			out.println("** ERROR: Unable to open database:");
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e) {
			out.println("** ERROR: File not found:");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			out.println("** ERROR: I/O exception:");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void beginTransaction(Connection db) {
		try {
			Statement cmd = db.createStatement();
			cmd = db.createStatement();
			cmd.execute("BEGIN;");
		} catch (SQLException e) {
			out.println("** ERROR: unable to begin transaction ");
			e.printStackTrace();
		}
	}

	private void rollbackToSavepoint(Connection db) {
		try {
			Statement cmd = db.createStatement();
			cmd = db.createStatement();
			cmd.execute("ROLLBACK TO SAVEPOINT sp");
		} catch (SQLException e) {
			out.println("** ERROR: unable to rollback to last savepoint: ");
			e.printStackTrace();
		}
	}
	
	private void savepoint(Connection db) {
		try {
			Statement cmd = db.createStatement();
			cmd = db.createStatement();
			cmd.execute("SAVEPOINT sp");
		} catch (SQLException e) {
			// Ignore that. This may also be called outside a transaction.
		}
	}

	private void rollback(Connection db) {
		try {
			Statement cmd = db.createStatement();
			cmd = db.createStatement();
			cmd.execute("ROLLBACK;");
		} catch (SQLException e) {
			out.println("** ERROR: unable to rollback: ");
			e.printStackTrace();
		}
	}

	private void executeStatement(Connection db, StringBuilder sql) {
		try {
			Statement cmd = db.createStatement();

			boolean hasMoreResultSets = cmd.execute(sql.toString());
			while (hasMoreResultSets || cmd.getUpdateCount() != -1) {
				if (hasMoreResultSets) {
					ResultSet rs = cmd.getResultSet();
					while (rs.next()) {
						String lines = rs.getString(1);
						out.println(lines);
						TestCase c = TestCase.parse(lines);
						if (c != null) {
							tests.add(c);
						}
					}
					rs.close();
				} else {
					int queryResult = cmd.getUpdateCount();
					if (queryResult == -1) {
						continue;
					}
				}

				hasMoreResultSets = cmd.getMoreResults();
			}

			savepoint(db);
		} catch (SQLException e) {
			out.println("** ERROR: Database exception, ignoring and continue: " + e.getMessage());
			tests.add(TestCase.fail(e.getMessage()));
			rollbackToSavepoint(db);
		}
	}
}
