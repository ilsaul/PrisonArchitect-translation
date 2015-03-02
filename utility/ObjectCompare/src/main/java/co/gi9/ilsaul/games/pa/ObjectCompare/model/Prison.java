package co.gi9.ilsaul.games.pa.ObjectCompare.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import co.gi9.ilsaul.games.pa.ObjectCompare.model.part.Element;

public class Prison {
	//Map<String, String> sections;
	private Element root;

	public Prison() {
		//sections = new HashMap<String, String>();
		root = new Element(null, "root");
	}

	public void load(File selectedFile) {
		InputStream ins = null; // raw byte-stream
		Reader r = null; // cooked reader
		BufferedReader br = null; // buffered for readLine()
		try {
			String s;
			Element now = root;
			ins = new FileInputStream(selectedFile);
			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
			br = new BufferedReader(r);
			while ((s = br.readLine()) != null) {
				now = Parser.parserLine(now, s);
				//sections.put(key, value) Parser.parserLine(s);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage()); // handle exception
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (r != null) {
				try {
					r.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (ins != null) {
				try {
					ins.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
		}

		System.out.println(root.getObjects().size());
		for (Iterator<Element> iter = root.getObjects().iterator(); iter.hasNext();) {
			Element e = (Element) iter.next();
			System.out.println(e.getName());
		}
	}

}
