package com.cdit.test.portal;

//Accent insensitive analyzer
public class AccentInsensitiveAnalyzer extends StopwordAnalyzerBase {
 public AccentInsensitiveAnalyzer(Version matchVersion){
     super(matchVersion, StandardAnalyzer.STOP_WORDS_SET);
 }

 @Override
 protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
     final Tokenizer source = new StandardTokenizer(matchVersion, reader);

     TokenStream tokenStream = source;
     tokenStream = new StandardFilter(matchVersion, tokenStream);
     tokenStream = new LowerCaseFilter(tokenStream);
     tokenStream = new StopFilter(matchVersion, tokenStream, getStopwordSet());
     tokenStream = new ASCIIFoldingFilter(tokenStream);
     return new TokenStreamComponents(source, tokenStream);
 }
}
