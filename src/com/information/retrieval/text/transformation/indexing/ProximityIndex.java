package com.information.retrieval.text.transformation.indexing;

import com.information.retrieval.fileio.FileUtility;
import com.information.retrieval.text.retrieval.stopping.Stopping;
import com.information.retrieval.text.transformation.exception.IndexCreationFailedException;
import com.information.retrieval.text.transformation.factory.TokenizerFactory;
import com.information.retrieval.text.transformation.pojo.DocumentFrequencyPosting;
import com.information.retrieval.text.transformation.pojo.ParsedHtmlPage;
import com.information.retrieval.text.transformation.pojo.Posting;
import com.information.retrieval.text.transformation.pojo.ProximityInfo;
import com.information.retrieval.text.transformation.tokenize.Tokenizer;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProximityIndex implements InvertedIndex {

    private int nGram;
    private Map<String, Set<Posting>> index;
    private Map<String, Integer> documentLengthMapper;

    public ProximityIndex(int nGram) {
        this.nGram = nGram;
        index = new HashMap<>();
    }


    public void nGram(int n) {
        this.nGram = n;
    }

    public Map<String, Set<Posting>> index() {
        return index;
    }

    public void createIndex(String fileLocation) {
        File parsedHTMLFileFolder = new File(fileLocation);
        File[] files = parsedHTMLFileFolder.listFiles();

        // Handles the tokenizer logic according to N-Gram
        TokenizerFactory tb = new TokenizerFactory(this.nGram);
        Tokenizer tokenizer = tb.createTokenizer();

        Arrays.asList(files).stream().forEach(file -> {
            ParsedHtmlPage htmlPage = getParsedHtmlPage(file);

//            StringBuilder tokenizeContent = new StringBuilder(htmlPage.getTitle() + SPACE);
            StringBuilder tokenizeContent = new StringBuilder();
            tokenizeContent.append(htmlPage.getTextualContent());

            // Create tokens according to N-Gram policy
            List<String> tokens = new ArrayList<>(tokenizer.tokenize(tokenizeContent.toString()));

            // To store the number of occurrences of a token
            // in this specific document.
            Map<String, Integer> tokenCountMapper = new ConcurrentHashMap<>();

            tokens.stream().forEach(token -> {

                tokenCountMapper.putIfAbsent(token, 0);
                tokenCountMapper.put(token, tokenCountMapper.get(token) + 1);
            });

            // For each token present in this document
            tokenCountMapper.entrySet()
                    .stream()
                    .forEach(stringIntegerEntry ->
                    {
                        String token = stringIntegerEntry.getKey();

                        // Handles the index map logic
                        index.putIfAbsent(token, new HashSet<>());
                        // Creates the posting for this document
                        // for each token present in this document

                        List<Integer> tokenIndex = new LinkedList<>();

                        for ( int i = 0 ; i < tokens.size() ; i++)  {
                            if ( tokens.get(i).equals(token)) {
                                tokenIndex.add(i);
                            }
                        }

//                        while (tokens.indexOf(token) > -1) {
//
//                            tokenIndex.add(tokens.indexOf(token));
//                            tokens.remove(token);
//
//                        }

                        index.get(token)
                                .add(new ProximityInfo(htmlPage.getTitle(), tokenCountMapper.get(token), tokenIndex));
                    });
        });
    }

    @Override
    public Map<String, Integer> documentLengthMapper() {
        if (documentLengthMapper == null) {
            documentLengthMapper = new HashMap<>();
            index().entrySet()
                    .forEach((stringSetEntry ->
                    {
                        stringSetEntry.getValue().forEach(posting ->
                        {
                            documentLengthMapper.putIfAbsent(posting.documentID(), 0);
                            int previousValue = documentLengthMapper.get(posting.documentID());
                            documentLengthMapper.put(posting.documentID(), previousValue + posting.frequency());
                        });
                    }));
        }

        return documentLengthMapper;
    }

    @Override
    public Map<String, Integer> documentLengthMapperWithStopping(Stopping stopping) {
        if ( documentLengthMapper == null) {
            documentLengthMapper = new HashMap<>();
            index().entrySet()
                    .stream()
                    .filter(stringSetEntry -> !stopping.stopList().contains(stringSetEntry.getKey()))
                    .forEach((stringSetEntry ->
                    {
                        stringSetEntry.getValue().forEach(posting ->
                        {
                            documentLengthMapper.putIfAbsent(posting.documentID(), 0);
                            int previousValue = documentLengthMapper.get(posting.documentID());
                            documentLengthMapper.put(posting.documentID(), previousValue + posting.frequency());
                        });
                    }));
        };

        return documentLengthMapper;
    }



    /**
     * Reads the file in a location and creates a ParsedHtmlPage
     *
     * @param file
     * @return An instance of ParsedHtmlPage
     */
    private ParsedHtmlPage getParsedHtmlPage(File file) {
        ParsedHtmlPage parsedHtmlPage = null;
        try {
            FileUtility.CustomFileReader customFileReader = new FileUtility.
                    CustomFileReader(file.getAbsolutePath().replace(".txt", ""));
            //String title = customFileReader.readLineFromFile();
            String title = file.getName();
            StringBuilder textualContent = new StringBuilder();
            customFileReader.readLinesFromFile().forEach(line -> textualContent.append(line + " "));
            parsedHtmlPage = new ParsedHtmlPage(title, textualContent.toString());
        } catch (
                Exception e)

        {
            e.printStackTrace();
            throw new IndexCreationFailedException(this);
        }
        return parsedHtmlPage;
    }

    @Override
    public void writeToFile(String fileLocation) {
        try {
            FileUtility.CustomFileWriter cfw = new FileUtility.CustomFileWriter(fileLocation);
            index().entrySet().forEach(stringSetEntry ->
            {
                StringBuilder sb = new StringBuilder();
                sb.append(stringSetEntry.getKey())
                        .append(" -> ");

                stringSetEntry.getValue().forEach((posting ->
                {
                    sb.append("(" + posting.documentID() + "," + posting.frequency() + "," + posting.wordPos());
                }));
                sb.append("\n");

                cfw.writeLineToFile(sb.toString());
            });
            cfw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
