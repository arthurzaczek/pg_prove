package pg_prove;

import static pg_prove.Console.out;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

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
		String url = "jdbc:postgresql:" + arguments.getDbName();
		String username = null;
		String password = null;
		List<TestCase> tests = new LinkedList<TestCase>();

		try (Connection db = DriverManager.getConnection(url, username, password)) {
			String sql = String.join("\n", Files.readAllLines(Paths.get(arguments.getFileName())));
			Statement cmd = db.createStatement();

			boolean hasMoreResultSets = cmd.execute(sql);
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

			exportJUnitResult(tests, "results-" + arguments.getFileName() + ".xml");
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

	private static void exportJUnitResult(Collection<TestCase> tests, String outFile) throws IOException {
		if (!Helper.isNullOrEmpty(outFile)) {
			int failed = (int) tests.stream().filter(t -> !t.isSuccess()).count();

			try (BufferedWriter junitOut = new BufferedWriter(new FileWriter(outFile))) {
				junitOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				junitOut.write(String
						.format("<testsuite errors=\"0\" failures=\"%d\" hostname=\"empty\" name=\"checkproject-result\" skipped=\"0\" tests=\"%d\" time=\"1\" timestamp=\"2014-01-01T08:00:00\">\n",
								failed, tests.size()));
				int counter = 1;
				for (TestCase t : tests) {
					String message = t.getMessage();
					String firstLine = message.split("\\n")[0];
					junitOut.write(String.format("    <testcase classname=\"checkproject\" name=\"%s\" time=\"0.0\">\n",
							Helper.xmlEscapeText(firstLine)));
					if (!t.isSuccess()) {
						junitOut.write("        <failure message=\"The test failed\" type=\"junit.framework.AssertionFailedError\">\n");
						junitOut.write(Helper.xmlEscapeText(message));
						junitOut.write("        </failure>\n");
					}
					junitOut.write("    </testcase>\n");
					counter++;
				}
				junitOut.write("    <system-out><![CDATA[]]></system-out>\n");
				junitOut.write("    <system-err><![CDATA[]]></system-err>\n");
				junitOut.write("</testsuite>");

			}
		}
	}
}
