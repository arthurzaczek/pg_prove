package pg_prove;

public class TestCase {
	private String message;
	private boolean success;

	public static TestCase parse(String lines) {
		if (lines.startsWith("ok") || lines.startsWith("not ok")) {
			TestCase s = new TestCase();
			s.message = lines;
			s.success = lines.startsWith("ok");
			return s;
		} else {
			return null;
		}
	}

	public static TestCase fail(String error) {
		TestCase s = new TestCase();
		s.message = error;
		s.success = false;
		return s;
	}

	private TestCase() {
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return success;
	}
}
