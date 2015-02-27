package pg_prove;

import java.io.PrintWriter;

public class Console {
	public static void init() {
		if (System.console() != null) {
			out = System.console().writer();
		} else {
			out = new PrintWriter(System.out, true);
		}
	}

	public static PrintWriter out;
}
