package com.information.retrieval.tests;

import com.information.retrieval.text.transformation.indexing.DocumentFrequencyIndex;
import com.information.retrieval.text.transformation.pojo.DocumentFrequencyPosting;
import com.information.retrieval.text.transformation.indexing.InvertedIndex;
import com.information.retrieval.text.transformation.pojo.Posting;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentFrequencyIndexTest {

    @Test
    public void createIndexTest()   {
        final String FILELOCATION = "tests";
        Map<String , Set<Posting>> expectedTermPostingMapper = createTestMap();
        InvertedIndex invertedIndex = new DocumentFrequencyIndex(1);
        invertedIndex.createIndex(FILELOCATION);
        Map<String, Set<Posting>> actualTermPostingMapper = invertedIndex.index();

        //TODO: Need to complete this test
        actualTermPostingMapper.entrySet().forEach((entry) -> {
            assertTrue( "Key not present" + entry.getKey() , expectedTermPostingMapper.containsKey(entry.getKey()));

        });
    }

    // Create the test map. Weird and long boring process. Need to come up with a solution
    // A File would be good which has something like
    // term (<DocId><Frequency>)......(<DocId><Frequency>)
    // and then parse it
    private Map<String,Set<Posting>> createTestMap() {
        Map<String, Set<Posting>> termPostingMapper = new HashMap<>();

        termPostingMapper.put("SampleDocument1" , new HashSet<>());
        termPostingMapper.get("SampleDocument1").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("SampleDocument2" , new HashSet<>());
        termPostingMapper.get("SampleDocument2").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("This", new HashSet<>());
        termPostingMapper.get("This").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("is" , new HashSet<>());
        termPostingMapper.get("is").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("a" , new HashSet<>());
        termPostingMapper.get("a").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("test" , new HashSet<>());
        termPostingMapper.get("test").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("Dont", new HashSet<>());
        termPostingMapper.get("Dont").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("worry", new HashSet<>());
        termPostingMapper.get("worry").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("Go", new HashSet<>());
        termPostingMapper.get("Go").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("ahead", new HashSet<>());
        termPostingMapper.get("ahead").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("and", new HashSet<>());
        termPostingMapper.get("and").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.get("and").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("modify", new HashSet<>());
        termPostingMapper.get("modify").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.put("it", new HashSet<>());
        termPostingMapper.get("it").add(new DocumentFrequencyPosting("SampleDocument1", 1));
        termPostingMapper.get("it").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("Do", new HashSet<>());
        termPostingMapper.get("Do").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("you", new HashSet<>());
        termPostingMapper.get("you").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("think", new HashSet<>());
        termPostingMapper.get("think").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("Java", new HashSet<>());
        termPostingMapper.get("Java").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("can", new HashSet<>());
        termPostingMapper.get("can").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("read", new HashSet<>());
        termPostingMapper.get("read").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        termPostingMapper.put("understand", new HashSet<>());
        termPostingMapper.get("understand").add(new DocumentFrequencyPosting("SampleDocument2", 1));
        return termPostingMapper;
    }
}
