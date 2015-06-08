package pg_prove;

import static pg_prove.Console.out;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

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

		TestRunner runner = new TestRunner(arguments);
		runner.run();
		try {
			exportJUnitResult(runner.getTests(), arguments.getOutputFileName());
		} catch (IOException e) {
			out.println("** ERROR: I/O exception while writing JUnit result file:");
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);

	}

	private static void exportJUnitResult(Collection<TestCase> tests, String outFile) throws IOException {
		if (!Helper.isNullOrEmpty(outFile)) {
			int failed = (int) tests.stream().filter(t -> !t.isSuccess()).count();

			try (BufferedWriter junitOut = new BufferedWriter(new FileWriter(outFile))) {
				junitOut.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				junitOut.write(String
						.format("<testsuite name=\"%s\" errors=\"0\" failures=\"%d\" hostname=\"empty\" skipped=\"0\" tests=\"%d\" time=\"1\" timestamp=\"2014-01-01T08:00:00\">\n",
								Helper.stripExtension(outFile), failed, tests.size()));
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
