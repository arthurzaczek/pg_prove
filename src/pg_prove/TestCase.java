package pg_prove;

public class TestCase {
	private String message;
	private boolean success;

	public TestCase(String message, boolean success)
	{
		this.message = message;
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return success;
	}
}
