import org.apache.commons.lang.mutable.MutableInt;


public class Row {
	private String line;

	private String key;
	private String value;

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

	private static String getAllFromPos(String line, MutableInt pos) {
		String word = "";
		boolean start = false;
		char ch;

		word = "";
		if (line.length() > 0) {
			do {
				ch = line.charAt(pos.intValue());
				start |=  ((ch != ' ') && (ch != '\t'));

				if (start || (ch != ' ' && ch != '\t')) {
					word += ch;
				}

				pos.increment();
			} while (pos.intValue() < line.length());
		}

		if (word.length() == 0) word = null;
		return word;
	}
}
