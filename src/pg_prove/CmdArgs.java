package pg_prove;

import static pg_prove.Console.out;

import java.io.File;
import java.io.PrintWriter;

public class CmdArgs {
	private CmdArgs() {

	}

	private String dbName;
	public String getDbName() {
		return dbName;
	}
	
	private String host = "localhost";
	public String getHost() {
		return host;
	}
	
	private int port = 5432;
	public int getPort() {
		return port;
	}

	private String fileName;
	public boolean hasFileName() {
		return fileName != null && !"".equals(fileName);
	}

	public File getFileName() {
		return new File(fileName);
	}

	private boolean printHelp;
	public boolean isPrintHelp() {
		return printHelp;
	}

	private String outputFileName;
	public String getOutputFileName() {
		return outputFileName;
	}
	
	private String username;
	public String getUsername() {
		return username;
	}

	private String password;
	public String getPassword() {
		return password;
	}

	public static CmdArgs parse(String[] args) {
		CmdArgs result = new CmdArgs();
		for (int idx = 0; idx < args.length; idx++) {
			String arg = args[idx];
			if ("-d".equals(arg) || "--dbname".equals(arg)) {
				result.dbName = args[++idx];
			} else if ("-h".equals(arg) || "--host".equals(arg)) {
				result.host = args[++idx];
			} else if ("-p".equals(arg) || "--port".equals(arg)) {
				result.port = Integer.parseInt(args[++idx]);
			} else if ("-H".equals(arg) || "--help".equals(arg)) {
				result.printHelp = true;
			} else if ("-O".equals(arg) || "--output".equals(arg)) {
				result.outputFileName = args[++idx];
			} else if ("-U".equals(arg) || "--username".equals(arg)) {
				result.username = args[++idx];
			} else if ("--password".equals(arg)) {
				result.password = args[++idx];
			} else if (idx == args.length - 1) {
				// Last arg
				result.fileName = arg;
				if (Helper.isNullOrEmpty(result.outputFileName)) {
					File f = new File(result.fileName);
					result.outputFileName = new File(f.getParent(), "results-" + Helper.stripExtension(f.getName()) + ".xml").getPath();
				}
			} else {
				out.println(String.format("** WARNING: Unknown command line arg %s", arg));
			}
		}
		return result;
	}

	public static void printHelp(PrintWriter out) {
		out.println("pg_prove replacement written in java.");
		out.println("Most options are implemented");
		out.println();
		out.println("Usage: pg_prove [options] {file|wildcard|directory}");
		out.println();
		out.println("-d,  --dbname DBNAME        Database to which to connect.");
		out.println("-U,  --username USERNAME    User with which to connect.");
		out.println("     --password PASSWORD    Password with which to connect. DANGER! Password will be passed clear text!");
		out.println("-h,  --host HOST            Host to which to connect.");
		out.println("-p,  --port PORT            Port to which to connect.");
		out.println("TODO: -R   --runtests             Run xUnit test using runtests().");
		out.println("TODO: -s,  --schema SCHEMA        Schema in which to find xUnit tests.");
		out.println("TODO: -x,  --match REGEX          Regular expression to find xUnit tests.");
		out.println();
		out.println("TODO: -r,  --recurse              Recursively descend into directories.");
		out.println("-O,  --output               Name of JUnit output xml file. Defaults to 'results-{filename}.xml'.");
		out.println();
		out.println("TODO: -v,  --verbose              Print all test lines.");
		out.println();
		out.println("-H,  --help                 Print a usage statement and exit.");
		out.println("TODO: -V,  --version              Print the version number and exit.");
	}
}
