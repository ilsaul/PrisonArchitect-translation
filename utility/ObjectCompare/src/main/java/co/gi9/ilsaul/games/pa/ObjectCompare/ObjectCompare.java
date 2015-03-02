package co.gi9.ilsaul.games.pa.ObjectCompare;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.gi9.ilsaul.games.pa.ObjectCompare.model.Prison;

public class ObjectCompare {
	private static final Logger logger = LoggerFactory.getLogger(ObjectCompare.class);
	private static final String CRLF = "\r\n";

	public File dstFile;
	public File srcEng;
	public List<File> src;

	private Prison prison;

	public ObjectCompare(File destination, File english, File...traslations) {
		srcEng = english;
		src = Arrays.asList(traslations);
		dstFile = destination;

		//translateSequence = new LinkedList<Row>();
		//translateKeys = new HashMap<String, List<Translate>>();
		//columnsSize = new LinkedList<MutableInt>();
		//columnsSize.add(new MutableInt(0)); // Keyword
		//columnsSize.add(new MutableInt(0)); // english traslation
		//columnsSize.add(new MutableInt(0)); // italian traslation

		//missOnly = false;
	}

	private void runCompareEnglish() {
//		if (src.size() != 1) {
//			logger.error("In comparing English versions can only have an argument");
//			throw new RuntimeErrorException(null, "In comparing English versions can only have an argument");
//		}

		makeBase(srcEng);

		//for (Iterator<File> iter = src.iterator(); iter.hasNext();) {
		//	File file = (File) iter.next();

			//loadTraslates(file);
	//	}

		//writeReport(dstFile);

		logger.info("THE END");
	}

	/**
	 * Load actual Enlish version of the file
	 * @param src file to be loaded
	 */
	private void makeBase(File src) {
//		InputStream ins = null; // raw byte-stream
//		Reader r = null; // cooked reader
//		BufferedReader br = null; // buffered for readLine()

		prison = new Prison();
		prison.load(src);

//		try {
//			String s;
//			logger.info("Load English file {}", src.getAbsolutePath());
//			ins = new FileInputStream(src);
//			r = new InputStreamReader(ins, "UTF-8"); // leave charset out for default
//			br = new BufferedReader(r);
//
//			int iVal = 0;
//			int iLine = 0;
//			while ((s = br.readLine()) != null) {
//				logger.trace("Line {}", iLine);
//				Row e = new Row(s);
//				translateSequence.add(e);
//
//				if (!e.isComment()) {
//					String key = e.getKey();
//					List<Translate> l =  new ArrayList<Translate>();
//					translateKeys.put(key, l);
//					columnsSize.get(0).setValue(Math.max(columnsSize.get(0).intValue(), e.getKey().length()));
//					//columnsSize.get(1).setValue(Math.max(columnsSize.get(1).intValue(), e.getValue().length()));
//					columnsSize.get(1).add( e.getValue().length());
//					iVal++;
//				}
//
//				iLine++;
//			}
//
//			columnsSize.get(1).setValue(columnsSize.get(1).longValue() / iVal);
//		} catch (Exception e) {
//			logger.error("Errore", e);
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (Throwable t) { /* ensure close happens */
//				}
//			}
//			if (r != null) {
//				try {
//					r.close();
//				} catch (Throwable t) { /* ensure close happens */
//				}
//			}
//			if (ins != null) {
//				try {
//					ins.close();
//				} catch (Throwable t) { /* ensure close happens */
//				}
//			}
//		}
	}

	public static void main(String[] args) {
		File dstFile = null;
		File srcEng = null;
		File src[] = null;

		// -c -e ../../English/biographies.txt -o ../../temp.txt ../../English/biographies-28.txt

// -c -e ../../../PrisonArchitect-traslate-3/English/base-language-29.txt -o ../../temp.txt ../../../PrisonArchitect-traslate-3/English/base-language-28.txt
// -t
// -m -e ../../../PrisonArchitect-traslate-3/English/base-language-29.txt -o ../../gamefile.txt ../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/base-language.txt ../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/New_tr.txt
// -u -e ../../../PrisonArchitect-traslate-3/English/base-language-29.txt -o ../../miss.txt ../../../PrisonArchitect-traslate-3/Italian/PaulGhost/data/language/base-language-29.txt

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

		ObjectCompare convert = new ObjectCompare(dstFile, srcEng, src);

		if (params.compEnglish) {
			convert.runCompareEnglish();
		} else {
			parser.printUsage(System.err);
			return;
		}
		//convert.run();
	}
}
