
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lexicalanalyzer.LexicalAnalyzer;

class LexicalAnalyzerTest {

	@Test
	void testLexicalAnalyzerInit() {
		LexicalAnalyzer la = new LexicalAnalyzer(null);
		assertEquals(la.symbolMap.get(' '), 16);
		assertEquals(la.symbolMap.get('\t'), 16);
		assertEquals(la.symbolMap.get('\n'), 17);
		assertEquals(la.symbolMap.get('|'), 15);
		assertEquals(la.stateMap.get(33), "invalidchar");
		assertEquals(la.tb[30][3], 30);
	}

}
