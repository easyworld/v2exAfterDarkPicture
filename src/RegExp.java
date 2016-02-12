import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExp {
	public static String[] exec(String regExp, String string) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(string);
		String[] result = null;
		int count = 0;
		if (m.find()) {
			count = m.groupCount();
			if (count == 0)
				return new String[] { m.group() };
			result = new String[count];
			for (int i = 0; i < count; i++)
				result[i] = m.group(i + 1);
		}
		return result;
	}

	public static boolean test(String regExp, String string) {
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(string);
		return m.find();
	}

	public static void main(String[] args) {
		String[] aa = exec("(\\d) / (\\d)", "10 / 20");
		System.out.println(test("\\w", "s"));
	}
}
