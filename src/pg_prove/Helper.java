package pg_prove;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Helper {
	public static String stripExtension(String str) {
		if (str == null)
			return null;

		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;

		return str.substring(0, pos);
	}

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.equals("");
	}

	public static String readNonEmptyLine(BufferedReader rd, boolean trim)
			throws IOException {
		String line = null;
		while ((line = rd.readLine()) != null) {
			if (trim)
				line = line.trim();
			if (!isNullOrEmpty(line))
				break;
		}
		return line;
	}

	public static String readAllText(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}
	
	public static String xmlEscapeText(String t) {
		if (t == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			default:
				if (c < 0x20 || c > 0x7e) {
					sb.append("&#" + ((int) c) + ";");
				} else
					sb.append(c);
			}
		}
		return sb.toString();
	}
}
