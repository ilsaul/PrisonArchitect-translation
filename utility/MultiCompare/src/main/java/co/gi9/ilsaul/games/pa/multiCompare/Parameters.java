package co.gi9.ilsaul.games.pa.multiCompare;

import java.io.File;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Parameters {
	@Option(name="-c", forbids={"-t"}, aliases="--compare", usage="compare english versions")
	public boolean compEnglish = false;

	@Option(name="-t", forbids={"-c"}, aliases="--translations", usage="compare the translated versions")
	public boolean compTranslate = false;

	@Option(name="-h", aliases="--help", usage="print this message")
	public boolean help;

	@Option(name="-o", aliases="--output", required=true, metaVar="<file>", usage="output to this file")
	public File dstFile;

	@Option(name="-e", aliases="--english", required=true, metaVar="<file>", usage="english version")
	public File srcEng;



	@Argument(metaVar="<file> [<file> [<file>]]", required=true)
	public File src[];
	//public List<File> src;
}
