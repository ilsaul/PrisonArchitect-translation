package co.gi9.ilsaul.games.pa.multiCompare;
import org.apache.commons.lang.mutable.MutableInt;


public class Row {
	private String line;

	private String key;
	private String value;
	private int startValue;
	private int endValue;

	public Row(String line) {
		this.line = line;

		if (!isComment()) {
			MutableInt pos = new MutableInt(0);
			key = getWord(line, pos);
			value = getAllFromPos(line, pos);
		} else {
			key = null;
			value = null;
		}
	}

	public String getLine() {
		return line;
	}

	public boolean isComment() {
		String s = line.trim();
		return (s.startsWith("#") || s.isEmpty() || s.length() < 3);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	private static String getWord(String line, MutableInt pos) {
		String word = "";
		boolean marker = false;
		boolean start = false;
		char ch;

		word = "";
		if (line.length() > 0) {
			do {
				ch = line.charAt(pos.intValue());
				start |=  ((ch != ' ') || (ch != '\t'));
				if (ch == '"') marker = !marker;

				if ((marker && start) || (start && (ch != ' ' && ch != '\t'))) {
					word += ch;
				}

				pos.increment();
			} while ((pos.intValue() < line.length()) && (marker || !start || (ch != ' ' && ch != '\t')));
		}

		if (word.length() == 0) word = null;
		return word;
	}

	private String getAllFromPos(String line, MutableInt pos) {
		String word = "";
		boolean start = false;
		char ch;

		word = "";
		if (line.length() > 0) {
			do {
				ch = line.charAt(pos.intValue());
				start |=  ((ch != ' ') && (ch != '\t'));

				if (start || (ch != ' ' && ch != '\t')) {
					if (startValue == 0) startValue = pos.intValue();
					word += ch;
				}

				pos.increment();
			} while (pos.intValue() < line.length());

			endValue = pos.intValue();
		}

		if (word.length() == 0) word = null;
		return word;
	}

	public String getLineWithNewValue(String value) {
		if (value == null || value.isEmpty()) return line;

		String s = line.substring(0, startValue) + value + line.substring(endValue, line.length());

		return s;
	}
}
