package co.gi9.ilsaul.games.pa.multiCompare;

import java.io.File;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class Parameters {
	@Option(name="-c", forbids={"-t,-m, -u"}, aliases="--compare", usage="compare english versions")
	public boolean compEnglish = false;

	@Option(name="-t", forbids={"-c,-m, -u"}, aliases="--translations", usage="compare the translated versions")
	public boolean compTranslate = false;

	@Option(name="-m", forbids={"-t,-c, -u"}, aliases="--makefile", usage="Create game file with traslation")
	public boolean makeTranslationFile = false;

	@Option(name="-u", forbids={"-t,-c, -m"}, aliases="--untranslated", usage="list of untranslated keys")
	public boolean untranslation = false;

	@Option(name="-h", aliases="--help", usage="print this message")
	public boolean help;

	@Option(name="-o", aliases="--output", required=true, metaVar="<file>", usage="output to this file")
	public File dstFile;

	@Option(name="-e", aliases="--english", required=true, metaVar="<file>", usage="english file version")
	public File srcEng;

	@Argument(metaVar="<file> [<file> [<file>]]", required=true, usage="If the key is found in more than one file, key in the most right file is used")
	public File src[];
	//public List<File> src;
}
