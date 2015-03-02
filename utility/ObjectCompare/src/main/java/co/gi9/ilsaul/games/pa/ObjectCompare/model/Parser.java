package co.gi9.ilsaul.games.pa.ObjectCompare.model;

import org.apache.commons.lang.mutable.MutableInt;

import co.gi9.ilsaul.games.pa.ObjectCompare.model.part.Element;

public class Parser {

	public static Element parserLine(Element parent, String line) {
		Element actualParent = parent;
		Element e = parent;
		MutableInt pos = new MutableInt(0);

		while (pos.intValue() < line.length()) {
			String word = getWord(line, pos);

			if (word != null) {
				if (word.equals("BEGIN")) {
					actualParent = e;
					e = new Element(actualParent, getWord(line, pos));
					actualParent.addObect(e);


				} else if (word.equals("END")) {
					e = e.getParent();
					actualParent = e.getParent();

				} else {
					// Property
					String key = word;
					String val = getWord(line, pos);

					e.addProperty(key, val);
					//e = parent;
				}
			}
		}

		return e;
	}

	private static String getWord_old(String line, MutableInt pos) {
		//String tmpLine = line.trim();
		int posSpace;
		int posMark;
		do {
			posSpace = line.indexOf(' ', pos.intValue());
			posMark = line.indexOf('"', pos.intValue());
			if (posSpace == pos.intValue()) pos.increment();
		} while ((pos.intValue() >= posSpace) && (line.length() > pos.intValue()));

		String word = null;
		if (pos.intValue() < line.length()) {
			word = line.substring(pos.intValue(), posSpace);
			pos.setValue(posSpace);
		}

		return word;
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
				start |=  (ch != ' ');
				if (ch == '"') marker = !marker;

				if ((marker && start) || (start && ch != ' ')) {
					word += ch;
				}

				pos.increment();
			} while ((pos.intValue() < line.length()) && (marker || !start || ch != ' '));
		}

		if (word.length() == 0) word = null;
		return word;
	}
}
