package com.information.retrieval.fileio;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A Utility class which provides API and Wrapper classes for performing FILE I/O
 * operations
 */

public class FileUtility {

    /**
     * Utility method to append text to the file
     *
     * @param text
     * @param fileName
     */
    public static void appendToFile(String text, String fileName) {
        try (FileWriter fw = new FileWriter(fileName + ".txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method to append text to the file
     *
     * @param text
     * @param fileName
     */
    public static void writeToFile(String text, String fileName) {
        try (FileWriter fw = new FileWriter(fileName + ".txt", false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use this class to read lines of text stored in a file.
     * Always call close() after the lines of text has been finished reading.
     */
    public static class CustomFileReader {
        BufferedReader bufferedReader;

        /**
         * Creates a buffered reader for the path of file
         * specified by @param fileLocation
         *
         * @param fileLocation
         * @throws IOException
         * @returns this
         */
        public CustomFileReader(String fileLocation) throws Exception {
            try {
                bufferedReader = new BufferedReader(new FileReader(fileLocation + ".txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Reads a line of text using the buffered reader.
         *
         * @return String
         * @throws IOException
         */

        public String readLineFromFile() throws IOException {
            String line = bufferedReader.readLine();
            return line;
        }

        public Stream<String> readLinesFromFile()  {
            return bufferedReader.lines();
        }

        /**
         * Important: Do not forget to call this method.
         *
         * @throws IOException
         */
        public void close() throws IOException {
            bufferedReader.close();
        }
    }

    /**
     * Use this class to write lines of text stored in a file.
     * Always call close() after the lines of text has been finished reading.
     */
    public static class CustomFileWriter {
        BufferedWriter bufferedWriter;

        /**
         * Creates a buffered reader for the path of file
         * specified by @param fileLocation
         *
         * @param fileLocation
         * @throws IOException
         * @returns this
         */
        public CustomFileWriter(String fileLocation) throws Exception {
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(fileLocation + ".txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Writes a line of text using the buffered writer
         *
         * @param text should end with "\n" newLine character
         * @return String
         * @throws IOException
         */
        public void writeLineToFile(String text) {
            try {
                bufferedWriter.write(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Important: Do not forget to call this method
         *
         * @throws IOException
         */
        public void close() throws IOException {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    /**
     * Utility method to write text to the file
     *
     * @param object
     * @param fileName
     */
    public static void writeToFile(Object object, String fileName) {
        try (FileWriter fw = new FileWriter(fileName + ".txt", false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(object);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to read java objects from file
     *
     * @param fileLocation
     * @return
     */
    public static List<Object> readObjectsFromFile(String fileLocation) {

        List<Object> objects = new ArrayList<>();
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(fileLocation + ".txt"));
            Object obj = null;
            while ((obj = inputStream.readUnshared()) != null) {
                objects.add(obj);
            }
            inputStream.close();
        } catch (EOFException eofException) {

        } catch (IOException e) {
            e.printStackTrace();
            ;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objects;
    }

    /**
     * Use this class to store object(s)
     * to the file.
     * Don't forget to call close() after writing operation
     * has been finished.
     */

    public static class CustomObjectWriter {
        ObjectOutputStream objectOutputStream;

        public CustomObjectWriter(String fileLocation) throws IOException {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileLocation));
        }

        public void writeObjectToFile(Object object) throws IOException {
            objectOutputStream.writeUnshared(object);
        }

        /**
         * Always call this method to complete the write operation
         *
         * @throws IOException
         */
        public void flush() throws IOException {
            objectOutputStream.flush();
        }
    }

}
