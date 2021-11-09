package tests.com.weatherstone.chess.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.io.Resources;
import com.weatherstone.chess.pgn.PGNUtils;
import com.weatherstone.chess.pgn.ParsePGNException;

class PGNParserTest {

	@Test
	void test1() throws IOException {
		doTest("pgn/t1.pgn");
	}
/**	
	@Test
	void test2() throws IOException {
		doTest("tests/pgn/t2.pgn");
	}
	
	@Test
	void test3() throws IOException {
		doTest("tests/pgn/t3.pgn");
	}
	
	@Test
	void test4() throws IOException {
		doTest("tests/pgn/t4.pgn");
	}
	
	@Test
	void test5() throws IOException {
		doTest("tests/pgn/t5.pgn");
	}
	
	@Test
	void test6() throws IOException {
		doTest("tests/pgn/t6.pgn");
	}
	
	@Test
	void test7() throws IOException {
		doTest("tests/pgn/t7.pgn");
	}
	
	@Test
	void test8() throws IOException {
		doTest("tests/pgn/t8.pgn");
	}
	
	@Test
	void test9() throws IOException {
		doTest("tests/pgn/t9.pgn");
	}
	
	void testPawnPromotion() throws IOException {
		doTest("tests/pgn/queenPromotion.pgn");
	}
	
	@Test
	void test10() throws IOException {
		doTest("tests/pgn/smallerTest.pgn");
	}
	
	@Test
	void testParens() throws ParsePGNException {
		final String gameText = "(+)-(-) (+)-(-) 1. e4 e6";
		final List<String> moves = PGNUtilities.processMoveText(gameText);
		assert(moves.size() == 2);
	}
**/	
	private static void doTest(final String testFilePath) throws IOException {
		final URL url = Resources.getResource(testFilePath);
		final File testPGNFile = new File(url.getFile());
		PGNUtils.persistPGNFile(testPGNFile);
	}

}
