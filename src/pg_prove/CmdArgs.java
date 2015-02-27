package pg_prove;

import static pg_prove.Console.out;

public class CmdArgs {
	private CmdArgs() {

	}

	private String dbName;

	public String getDbName() {
		return dbName;
	}

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public static CmdArgs parse(String[] args) {
		CmdArgs result = new CmdArgs();
		for (int idx = 0; idx < args.length; idx++) {
			String arg = args[idx];
			if ("-d".equals(arg) || "--dbname".equals(arg)) {
				result.dbName = args[++idx];
			} else if (idx == args.length - 1) {
				// Last arg
				result.fileName = arg;
			} else {
				out.println(String.format(
						"** WARNING: Unknown command line arg %s", arg));
			}
		}
		return result;
	}
}
