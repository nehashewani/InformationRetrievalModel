package com.information.retrieval.tests;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class Tests {


    public void test(Set<String> titles)    {
        File folder = new File("Parsed Text");
        Set<String> fileTitles = new HashSet<>();
        File[] files = folder.listFiles();
        System.out.println("Files length: " + files.length);
        for ( File f : files)   {
            fileTitles.add(f.getName());
        }

        titles.forEach( (title) -> {
            if ( !fileTitles.contains(title) )
                System.out.println(title);
        });
    }
}
