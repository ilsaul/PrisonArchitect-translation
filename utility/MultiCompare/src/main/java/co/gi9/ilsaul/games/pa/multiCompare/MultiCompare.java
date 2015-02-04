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
import java.util.Arrays;
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

import javax.management.RuntimeErrorException;

import org.apache.commons.lang.mutable.MutableInt;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It can work in many way:<ol>
 * <li>Compare multiple transtation files</li>
 * <li>Compare 2 english versions</li>
 * </ol>
 * If you specified 2 file the procedure think you are compare english versions, else compare several translations.<br><br>
 *
 * @author ilSaul <ilsaul2@gmail.com>
 * @version 1.2 (02-02-2015)
 */
public class MultiCompare implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(MultiCompare.class);
	private static final String CRLF = "\r\n";

	public File dstFile;
	public File srcEng;
	public List<File> src;

	/** provides a list of all lines of the file */
	private List<Row> translateSequence;
	/** translation key map */
	private Map<String, List<Translate>> translateKeys;
	/** column widths for printing */
	private List<MutableInt> columnsSize;

	private boolean missOnly;

	public MultiCompare(File destination, File english, File...traslations) {
		srcEng = english;
		src = Arrays.asList(traslations);
		dstFile = destination;

		translateSequence = new LinkedList<Row>();
		translateKeys = new HashMap<String, List<Translate>>();
		columnsSize = new LinkedList<MutableInt>();
		columnsSize.add(new MutableInt(0)); // Keyword
		columnsSize.add(new MutableInt(0)); // english traslation
		columnsSize.add(new MutableInt(0)); // italian traslation

		missOnly = false;
	}



	@Override
	public void run() {
		makeBase(srcEng);

		for (Iterator<File> iter = src.iterator(); iter.hasNext();) {
			File file = (File) iter.next();

			loadTraslates(file);
		}

		writeReport(dstFile);

		logger.info("THE END");
	}

	public void runCompareTraslation() {
		if (src.size() <= 1) {
			logger.error("in comparison of the translations you must have multiple arguments");
			throw new RuntimeErrorException(null, "in comparison of the translations you must have multiple arguments");
		}
		run();
	}

	public void runMakeTranslationFile() {
		makeBase(srcEng);

		for (Iterator<File> iter = src.iterator(); iter.hasNext();) {
			File file = (File) iter.next();

			loadSingleTraslate(file);
		}

		writeGameFile(dstFile);

		logger.info("THE END");
	}

	public void runCompareEnglish() {
		if (src.size() > 1) {
			logger.error("In comparing English versions can only have an argument");
			throw new RuntimeErrorException(null, "In comparing English versions can only have an argument");
		}
		run();
	}

	private void runUntranslatedKeys() {
		if (src.size() <= 1) {
			logger.error("in comparison of the translations you must have multiple arguments");
			throw new RuntimeErrorException(null, "in comparison of the translations you must have multiple arguments");
		}
		missOnly = true;

		run();
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

	/**
	 * Load several traslates and mantain any of that separate
	 * @param file
	 */
	private void loadTraslates(File file) {
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
				logger.trace("Row [Key: {} Value: {}]", e.getKey(), e.getValue());

				if (!e.isComment()) {
					Translate t = new Translate(e.getValue());
					t.setMaker(name);
					logger.trace("Translate [Value: {}]", t.getValue());

					List<Translate> list = translateKeys.get(e.getKey());
					if (list != null) {
						list.add(t);
						logger.trace("Translate [Key: {} size: {}]", e.getKey(), list.size());
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

	/**
	 * Load traslate. If find a key replace the traslation
	 * @param file
	 */
	private void loadSingleTraslate(File file) {
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
				logger.trace("Row [Key: {} Value: {}]", e.getKey(), e.getValue());

				if (!e.isComment()) {
					Translate t = new Translate(e.getValue());
					t.setMaker(name);
					logger.trace("Translate [Value: {}]", t.getValue());

					List<Translate> list = translateKeys.get(e.getKey());
					if (list != null) {
						list.clear(); // Only last traslate
						list.add(t);
						logger.trace("Replace Translate [Key: {} size: {}]", e.getKey(), list.size());
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

	/**
	 * Print to a file the difference of traslation
	 * @param dstFile2
	 */
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
						oldComment = null;

					} else if (qtaTraslations.size() == 1) {

						// If there is only one translation means that we are comparing the new and the old English file
						if (translations.size() == 1) {
							// I am interested only if the value is changed
							if (!r.getValue().equals(translations.get(0).getValue())) {
								if (!missOnly) {
									bw.write(String.format("%-" + columnsSize.get(0).intValue() + "s  %s -> %s%s", r.getKey(), translations.get(0).getValue(), r.getValue(), CRLF));
								}
								oldComment = null;
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
						oldComment = null;
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

	/**
	 * Create the file for the mod translate
	 * @param dstFile2
	 */
	private void writeGameFile(File dstFile2) {
		OutputStream out = null;
		Writer w = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(dstFile);
			w = new OutputStreamWriter(out, "UTF-8");
			bw = new BufferedWriter(w);
			int iLine = 0;

			//String oldComment = null;

			// runs all lines of the English version
			while (iLine < translateSequence.size()) {
				logger.trace("Line {}", iLine);
				Row r = translateSequence.get(iLine);

				if (r.isComment()) {
					logger.trace("Comment {}", r.getLine());
					bw.write(r.getLine() + CRLF);
				} else {
					List<Translate> translations = translateKeys.get(r.getKey());

					// I create the structure to understand how many values there are
					//Map<String, List<Translate>> qtaTraslations = new HashMap<String, List<Translate>>();
					for (int i = 0; i < translations.size(); i++) {
						Translate translate = translations.get(i); // MUST 1 only

						String s = r.getLineWithNewValue(translate.getValue());

						logger.trace("original   {}", r.getLine());
						logger.trace("Traslation {}", s);
						bw.write(s + CRLF);
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
		File dstFile = null;
		File srcEng = null;
		File src[] = null;

// -c -e ../../../PrisonArchitect-traslate-3/English/base-language-29.txt -o ../../temp.txt ../../../PrisonArchitect-traslate-3/English/base-language-28.txt
// -t
// -m -e ../../../PrisonArchitect-traslate-3/English/base-language-29.txt -o ../../gamefile.txt ../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/base-language.txt ../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/New_tr.txt
//			//For test only
//			dstFile = new File("../../Italian/new_future.txt");
//
//			// always the latest in English
//			srcEng = new File("../../../PrisonArchitect-traslate-3/English/base-language-28.txt");
//
//			src = new File[3];
//			//src[0] = new File("../../../PrisonArchitect-traslate-3/English/base-language-27.txt");
//			src[0] = new File("../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/base-language.txt");
//			src[1] = new File("../../../PrisonArchitect-traslate-3/Italian/mecripper/data/language/base-language.txt");
//			src[2] = new File("../../../PrisonArchitect-traslate-3/Italian/MetalCross/data/language/base-language.txt");
//		} else {
			final Parameters params = new Parameters();
			CmdLineParser parser = new CmdLineParser(params);

			try {
				parser.parseArgument(args);
			} catch (CmdLineException e) {
				e.printStackTrace();
			}

			// print usage
			if (params.help) {
				//parser.setUsageWidth(Integer.MAX_VALUE);
				parser.printUsage(System.err);
				return;
			}

			dstFile = params.dstFile;
			srcEng = params.srcEng;
			src = params.src;
//		}

		MultiCompare convert = new MultiCompare(dstFile, srcEng, src);

		if (params.compTranslate) {
			convert.runCompareTraslation();
		} else if (params.compEnglish) {
			convert.runCompareEnglish();
		} else if (params.makeTranslationFile) {
			convert.runMakeTranslationFile();
		} else if (params.untranslation) {
			convert.runUntranslatedKeys();
		} else {
			parser.printUsage(System.err);
			return;
		}
		//convert.run();
	}
}
