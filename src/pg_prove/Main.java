package pg_prove;

import static pg_prove.Console.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		Console.init();

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			out.println("** ERROR: Unable to load postgresql driver");
			e.printStackTrace();
			System.exit(1);
		}
		CmdArgs arguments = CmdArgs.parse(args);

		if (arguments.isPrintHelp() || !arguments.hasFileName()) {
			CmdArgs.printHelp(out);
			System.exit(0);
		}
		File argFile = arguments.getFileName();
		if (!argFile.exists()) {
			out.println("** ERROR: File or directory " + argFile.getName() + " not found");
			System.exit(1);
		}

		String url = "jdbc:postgresql:" + arguments.getDbName();
		String username = null;
		String password = null;
		List<TestCase> tests = new LinkedList<TestCase>();

		try (Connection db = DriverManager.getConnection(url, username, password)) {
			List<File> files;
			if (argFile.isFile()) {
				files = Arrays.asList(arguments.getFileName());
			} else if (argFile.isDirectory()) {
				files = Arrays.asList(argFile.listFiles());
			} else {
				throw new IllegalStateException("Argument " + argFile.getName() + " is neither a file or directory");
			}

			for (File file : files) {
				String sql = String.join("\n", Files.readAllLines(file.toPath()));
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
			}
			exportJUnitResult(tests, arguments.getOutputFileName());
			System.exit(0);
		} catch (SQLException e) {
			out.println("** ERROR: Database exception:");
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e1) {
			out.println("** ERROR: File not found:");
			e1.printStackTrace();
			System.exit(1);
		} catch (IOException e1) {
			out.println("** ERROR: I/O exception:");
			e1.printStackTrace();
			System.exit(1);
		}
	}

	private static void exportJUnitResult(Collection<TestCase> tests, String outFile) throws IOException {
		if (!Helper.isNullOrEmpty(outFile)) {
			int failed = (int) tests.stream().filter(t -> !t.isSuccess()).count();

			try (BufferedWriter junitOut = new BufferedWriter(new FileWriter(outFile))) {
				junitOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				junitOut.write(String
						.format("<testsuite errors=\"0\" failures=\"%d\" hostname=\"empty\" name=\"pg-prove-result\" skipped=\"0\" tests=\"%d\" time=\"1\" timestamp=\"2014-01-01T08:00:00\">\n",
								failed, tests.size()));
				for (TestCase t : tests) {
					String message = t.getMessage();
					String firstLine = message.split("\\n")[0];
					junitOut.write(String.format("    <testcase classname=\"pg_prove\" name=\"%s\" time=\"0.0\">\n", Helper.xmlEscapeText(firstLine)));
					if (!t.isSuccess()) {
						junitOut.write("        <failure message=\"The test failed\" type=\"junit.framework.AssertionFailedError\">\n");
						junitOut.write(Helper.xmlEscapeText(message));
						junitOut.write("        </failure>\n");
					}
					junitOut.write("    </testcase>\n");
				}
				junitOut.write("    <system-out><![CDATA[]]></system-out>\n");
				junitOut.write("    <system-err><![CDATA[]]></system-err>\n");
				junitOut.write("</testsuite>");

			}
		}
	}
}
