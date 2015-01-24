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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiCompare implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MultiCompare.class);
	private static final String CRLF = "\r\n";

	/** provides a list of all lines of the file */
	private List<Row> translateSequence;
	/** translation key map */
	private Map<String, List<Translate>> translateKeys;
	/** column widths for printing */
	private List<MutableInt> columnsSize;

	private File srcEng;
	private List<File> src;
	private File dstFile;

	public MultiCompare(File english, List<File> traslations, File destination) {
		srcEng = english;
		src = traslations;
		dstFile = destination;

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

		logger.info("THE END");
	}

	private void makeBase(File src) {
		InputStream ins = null; // raw byte-stream
		Reader r = null; // cooked reader
		BufferedReader br = null; // buffered for readLine()

		try {
			String s;
			logger.info("Load English file {}", src.getAbsolutePath());
			ins = new FileInputStream(src);
			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
			br = new BufferedReader(r);

			int iVal = 0;
			int iLine = 0;
			while ((s = br.readLine()) != null) {
				logger.trace("Line {}", iLine);
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
			logger.error("Errore", e);
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
			logger.info("Load traslation {}", file.getAbsolutePath());
			ins = new FileInputStream(file);
			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
			br = new BufferedReader(r);

			int iLine = 0;
			while ((s = br.readLine()) != null) {
				logger.trace("Line {}", iLine);
				Row e = new Row(s);

				if (!e.isComment()) {
					Translate t = new Translate(e.getValue());
					t.setMaker(name);

					List<Translate> list = translateKeys.get(e.getKey());
					if (list != null) {
						list.add(t);
					} else {
						// normally lines removed
						logger.warn("unknown line: {}", s);
					}
				}

				iLine++;
			}
		} catch (Exception e) {
			logger.error("Errore", e); // handle exception
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
			out = new FileOutputStream(dstFile);
			w = new OutputStreamWriter(out, "UTF-8");
			bw = new BufferedWriter(w);
			int iLine = 0;

			String oldComment = null;

			// runs all lines of the English version
			while (iLine < translateSequence.size()) {
				logger.trace("Line {}", iLine);
				Row r = translateSequence.get(iLine);

				if (r.isComment()) {
					// I avoid too many blank lines
					if (!r.getLine().equals(oldComment) || r.getLine().length() > 3) {
						bw.write(r.getLine() + CRLF);
						oldComment = r.getLine();
					}
				} else {
					List<Translate> translations = translateKeys.get(r.getKey());

					// I create the structure to understand how many values there are
					Map<String, List<Translate>> qtaTraslations = new HashMap<String, List<Translate>>();
					for (int i = 0; i < translations.size(); i++) {
						Translate translate = translations.get(i);

						List<Translate> l = qtaTraslations.get(translate.getValue());

						if (l == null) {
							l = new ArrayList<Translate>();
						}
						l.add(translate);

						qtaTraslations.put(translate.getValue(), l);
					}

					// I write only those different
					if (qtaTraslations.size() == 0) {
						// It lacks the translation so I add the original line
						bw.write(String.format("%-" + columnsSize.get(0).intValue() + "s  %-" + columnsSize.get(1).intValue() + "s\r\n", r.getKey(), r.getValue()));

					} else if (qtaTraslations.size() == 1) {

						// If there is only one translation means that we are comparing the new and the old English file
						if (translations.size() == 1) {
							// I am interested only if the value is changed
							if (!r.getValue().equals(translations.get(0).getValue())) {
								bw.write(String.format("%-" + columnsSize.get(0).intValue() + "s  %s -> %s%s", r.getKey(), translations.get(0).getValue(), r.getValue(), CRLF));
							}
						}

					} else if (qtaTraslations.size() > 1) {
						StringBuilder pLine = new StringBuilder();

						pLine.append(String.format("%-" + columnsSize.get(0).intValue() + "s  %-" + columnsSize.get(1).intValue() + "s", r.getKey(), r.getValue()));

						qtaTraslations = sortByValue(qtaTraslations);
						Set<String> keys = qtaTraslations.keySet();
						for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
							String sTraslate = (String) iter.next();
							pLine.append(" | ");

							pLine.append(sTraslate);
							if (qtaTraslations.get(sTraslate).size() > 1) {
								pLine.append("(").append(qtaTraslations.get(sTraslate).size()).append(")");
							}
						}

						pLine.append(CRLF);
						bw.write(pLine.toString());
					}
				}

				iLine++;
			}
		} catch (Exception e) {
			logger.error("Errore", e); // handle exception
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

	private static Map<String, List<Translate>> sortByValue(Map<String, List<Translate>> map) {
		Map<String, List<Translate>> result = new LinkedHashMap<>();
		Stream <Entry<String, List<Translate>>> st = map.entrySet().stream();

		st.sorted(Comparator.comparing(e -> (e.getValue().size() * -1))).forEach(e ->result.put(e.getKey(), e.getValue()));

		return result;
	}

	public static void main(String[] args) {

		// always the latest in English
		File srcEng = new File("../../../PrisonArchitect-traslate-3/English/base-language-28.txt");

		List<File> src = new ArrayList<File>();
		src.add(new File("../../../PrisonArchitect-traslate-3/English/base-language-27.txt"));
		//src.add(new File("../../Italian/mecripper/data/language/base-language.txt"));
		//src.add(new File("../../Italian/MetalCross/data/language/base-language.txt"));
		//src.add(new File("../../Italian/PaulGhost/data/language/base-language.txt"));

		File dstFile = new File("../../Italian/new_future.txt");

		MultiCompare convert = new MultiCompare(srcEng, src, dstFile);
		convert.run();
	}
}
