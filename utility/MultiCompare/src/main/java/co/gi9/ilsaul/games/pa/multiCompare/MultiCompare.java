package co.gi9.ilsaul.games.pa.multiCompare;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang.mutable.MutableInt;

public class MultiCompare implements Runnable {
	private static final String CRLF = "\r\n";

	private List<Row> translateSequence;
	private Map<String, List<Translate>> translateKeys;
	private List<MutableInt> columnsSize;

	private File srcEng;
	private List<File> src;
	private File dstFile;

	public MultiCompare() {
		srcEng = new File("../../../PrisonArchitect-traslate-3/English/base-language-28.txt");


		src = new ArrayList<File>();
		src.add(new File("../../../PrisonArchitect-traslate-3/English/base-language.txt"));
		//src.add(new File("../../Italian/mecripper/data/language/base-language.txt"));
		//src.add(new File("../../Italian/MetalCross/data/language/base-language.txt"));
		//src.add(new File("../../Italian/PaulGhost/data/language/base-language.txt"));

		dstFile = new File("../../Italian/new_future.txt");

		translateSequence = new LinkedList<Row>();
		translateKeys = new HashMap<String, List<Translate>>();
		columnsSize = new LinkedList<MutableInt>();
		columnsSize.add(new MutableInt(0)); // Keyword
		columnsSize.add(new MutableInt(0)); // english traslation
		columnsSize.add(new MutableInt(0)); // italian traslation
	}

	@Override
	public void run() {
		makeBase(srcEng);

		for (Iterator<File> iter = src.iterator(); iter.hasNext();) {
			File file = (File) iter.next();

			loadTraslate(file);
		}

		writeReport(dstFile);

		System.out.println("FINE");
	}

	private void makeBase(File src) {
		InputStream ins = null; // raw byte-stream
		Reader r = null; // cooked reader
		BufferedReader br = null; // buffered for readLine()

		try {
			String s;
			ins = new FileInputStream(src);
			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
			br = new BufferedReader(r);

			int iVal = 0;
			int iLine = 0;
			while ((s = br.readLine()) != null) {
				System.out.println(iLine);
				Row e = new Row(s);
				translateSequence.add(e);

				if (!e.isComment()) {
					String key = e.getKey();
					List<Translate> l =  new ArrayList<Translate>();
					translateKeys.put(key, l);
					columnsSize.get(0).setValue(Math.max(columnsSize.get(0).intValue(), e.getKey().length()));
					//columnsSize.get(1).setValue(Math.max(columnsSize.get(1).intValue(), e.getValue().length()));
					columnsSize.get(1).add( e.getValue().length());
					iVal++;
				}

				iLine++;
			}

			columnsSize.get(1).setValue(columnsSize.get(1).longValue() / iVal);
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
	}

	private void loadTraslate(File file) {
		String name = file.getParentFile().getParentFile().getParent();
		name = name.substring(name.lastIndexOf('/') +1);

		InputStream ins = null; // raw byte-stream
		Reader r = null; // cooked reader
		BufferedReader br = null; // buffered for readLine()

		try {
			String s;
			System.out.println("Caricamento file: " + file.getAbsolutePath());
			ins = new FileInputStream(file);
			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
			br = new BufferedReader(r);

			int iLine = 0;
			while ((s = br.readLine()) != null) {
				System.out.println(iLine);

				Row e = new Row(s);

				if (!e.isComment()) {
					Translate t = new Translate(e.getValue());
					t.setMaker(name);

					List<Translate> list = translateKeys.get(e.getKey());
					if (list != null) {
						list.add(t);
					} else {
						System.out.println("Riga sconosciuta: " + s);
					}
				}

				iLine++;
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
	}

	private void writeReport(File dstFile2) {
		OutputStream out = null;
		Writer w = null;
		BufferedWriter bw = null;
		try {
//			/String s;
			out = new FileOutputStream(dstFile);
			w = new OutputStreamWriter(out, "UTF-8");
			bw = new BufferedWriter(w);
			int iLine = 0;
			while (iLine < translateSequence.size()) {
				System.out.println(iLine);

				Row r = translateSequence.get(iLine);

				if (r.isComment()) {
					bw.write(r.getLine() + CRLF);
				} else {
					List<Translate> tk = translateKeys.get(r.getKey());

					// Creo la struttura per capire quanti valori ci sono
					Map<String, List<Translate>> qta = new HashMap<String, List<Translate>>();
					for (int i = 0; i < tk.size(); i++) {
						Translate t = tk.get(i);

						List<Translate> l = qta.get(t.getValue());

						if (l == null) {
							l = new ArrayList<Translate>();
						}
						l.add(t);

						qta.put(t.getValue(), l);
					}

					// Scrivo solo quelli diversi
					if (qta.size() == 0) {
						bw.write(String.format("%-" + columnsSize.get(0).intValue() + "s  %-" + columnsSize.get(1).intValue() + "s\r\n", r.getKey(), r.getValue()));

					} else if (qta.size() > 1) {
						StringBuilder pLine = new StringBuilder();

						pLine.append(String.format("%-" + columnsSize.get(0).intValue() + "s  %-" + columnsSize.get(1).intValue() + "s", r.getKey(), r.getValue()));

						// TODO: ordinare dal più importante
						qta = sortByValue(qta);
						Set<String> keys = qta.keySet();
						for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
							String sTraslate = (String) iter.next();
							pLine.append(" | ");

							pLine.append(sTraslate);
							if (qta.get(sTraslate).size() > 1) {
								pLine.append("(").append(qta.get(sTraslate).size()).append(")");
							}
						}

						pLine.append(CRLF);
						bw.write(pLine.toString());
					}
				}

//				// 1) I casi sono tutti uguali?
//				String val = null;
//				String valPrec = null;
//				for (int i = 0; i < tk.size(); i++) {
//					Translate t = tk.get(i);
//					val = t.getValue();
//					if (valPrec == null) {
//						valPrec = val;
//					} else {
//						if (val.equals(valPrec)) {
//
//						}
//					}
//				}

				iLine++;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage()); // handle exception
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (w != null) {
				try {
					w.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Throwable t) { /* ensure close happens */
				}
			}
		}
	}


//	private Entity parserLine(String line) {
//		String s = line.trim();
//
//		//System.out.println((int) s.charAt(0));
//		//System.out.println(Integer.toHexString(s.charAt(0)));
//		//System.out.println(Character.isValidCodePoint(s.charAt(0)));
//
//		if (s.startsWith("#") || s.isEmpty() || s.length() < 3) {
//			return new Entity(s);
//		} else {
//			MutableInt pos = new MutableInt(0);
//			String key = getWord(line, pos);
//			String value = getAllFromPos(line, pos);
//
//			return new Entity(key, value);
//		}
//	}

	public static void main(String[] args) {
		MultiCompare convert = new MultiCompare();

		convert.run();
	}

	private static Map<String, List<Translate>> sortByValue(Map<String, List<Translate>> map) {
		Map<String, List<Translate>> result = new LinkedHashMap<>();
		Stream <Entry<String, List<Translate>>> st = map.entrySet().stream();

		//st.sorted(Comparator.comparing(e -> e.getValue().size())).forEach(e ->result.put(e.getKey(), e.getValue()));
		st.sorted(Comparator.comparing(e -> (e.getValue().size() * -1))).forEach(e ->result.put(e.getKey(), e.getValue()));

		return result;
	}
}
