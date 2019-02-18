import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author harshverma
 * NETID : HXV180001
 */

public class TokanizerExecutor {

    public static List<String> fileNamesList = new ArrayList<>();
    public static Map<String, Integer> wordCountMap = new HashMap<>();
    public static Map<String, Integer> sortedWordCoundMap = new TreeMap<>();
    public static Map<String, Integer> wordsStemMap = new HashMap<String, Integer>();
    public static Map<String, Integer> sortedStemMap = new TreeMap<String, Integer>();

    public static File folder;
    static int wordsCountWithFrequencyAsOne = 0;
    static int totalWordsCount = 0;


    public static void main(String[] args) {

        System.out.println("Program to tokanize and Stem the Documents!");

        if(args.length==0){
            System.out.println("Please Enter the Path of Cranfield Dataset folder");
            System.exit(0);
        }

        folder = new File(args[0]);
       // folder = new File("/Users/harshverma/Documents/Project_repo/IRFirstAssingment/resource/Cranfield");
        long startTime = Calendar.getInstance().getTimeInMillis();
        storeFilesFornFolderToFileList(folder);
        readFileAndStoreUpdateValues();
        long endTime = Calendar.getInstance().getTimeInMillis();
        calculateCount(wordCountMap);
        System.out.println("Before Stemming Output for Tokanization :\n_________________________________________________________");
        System.out.println("Total Number of tokens in Cranfield Collection: " + totalWordsCount);
        System.out.println("Number of unique Words in Cranfield Collection: " + wordCountMap.size());
        System.out.println("Number of words occurring only once in Cranfield Collection: " + wordsCountWithFrequencyAsOne);
        System.out.println("Average Number of Word Tokens per document in Cranfield Collection: " + totalWordsCount / fileNamesList.size() + "\n");
        System.out.println("30 most frequent words in the Cranfield text collection\n");

        sortedWordCoundMap = sortCountmap(wordCountMap);

        printWordsAsPerTheirFrequency(sortedWordCoundMap);

        System.out.println("\nAfter Stemming :\n_______________________________________________________________");
        System.out.println("Number of distinct Stems in Cranfield Collection: " + wordsStemMap.size());
        calculateCount(wordsStemMap);
        System.out.println("Number of Stems occurring only once in Cranfield Collection: "+ wordsCountWithFrequencyAsOne);
        System.out.println("Average Number of Word Stems per document in Cranfield Collection: " + totalWordsCount / fileNamesList.size() + "\n");
        System.out.println("30 most frequent stems in the Cranfield text collection");
        sortedStemMap = sortCountmap(wordsStemMap);
        printWordsAsPerTheirFrequency(sortedStemMap);

        System.out.println("\nTime taken to read files during program execution in ms: "+(endTime-startTime)+" ms");
    }


    public static void readFileAndStoreUpdateValues() {
        try {
            for (int i = 0; i < fileNamesList.size(); i++) {

                String tempPath = folder + "//" + fileNamesList.get(i);

                Scanner readFile = new Scanner(new File(tempPath));

                while (readFile.hasNextLine()) {

                    String curLine = readFile.nextLine();
                    /*If the line contains SGML tags ignore the line*/
                    if (!(curLine.contains("<") && curLine.contains(">"))) {
                        curLine = curLine.replaceAll("[-]", " ");
                        curLine = curLine.replaceAll("w'w+", " ");
                        curLine = curLine.replaceAll("\\.", "");
                        StringTokenizer stringTokenizer = new StringTokenizer(curLine);

                        while (stringTokenizer.hasMoreTokens()) {
                            String word = stringTokenizer.nextToken().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                            String tempWord=word;
                            if (word.equals(""))
                                continue;
                            else {
                                if (wordCountMap.containsKey(word)) {
                                    wordCountMap.put(word, wordCountMap.get(word) + 1);
                                } else {
                                    wordCountMap.put(word, 1);
                                }

                                // Stem of words step/
                                String stemmedWord = null;
                                Stemmer myStemmer = new Stemmer();
                                myStemmer.add(tempWord.toCharArray(),tempWord.length());
                                myStemmer.stem();
                                stemmedWord=myStemmer.toString();
                                if (wordsStemMap.containsKey(stemmedWord)) {
                                    wordsStemMap.put(stemmedWord, wordsStemMap.get(stemmedWord) + 1);
                                } else {

                                    wordsStemMap.put(stemmedWord, 1);
                                }

                            }

                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void calculateCount(Map<String, Integer> tempMap) {
        totalWordsCount = 0;
        wordsCountWithFrequencyAsOne = 0;
        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            totalWordsCount += entry.getValue();
            if (entry.getValue() == 1) {
                wordsCountWithFrequencyAsOne++;

            }
        }
    }

    public static void printWordsAsPerTheirFrequency(Map<String, Integer> printMap) {

        Iterator<Map.Entry<String, Integer>> iterator = printMap.entrySet()
                .iterator();
        for (int i = 0; iterator.hasNext() && i < 30; i++) {
            System.out.println(i + 1 + ". " + iterator.next());
        }
    }

    // The below method receives the wordCountMap/wordsStemMap and sorts it based on values and
    // stores it in a TreeMap.
    // Reference :
    // http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
    private static TreeMap<String, Integer> sortCountmap(Map<String, Integer> tempMap) {

        class My_Comapartor implements Comparator<String> {
            Map<String, Integer> map;
            public My_Comapartor(Map<String, Integer> base) {
                this.map = base;
            }
            public int compare(String firstvalue, String secondvalue) {
                if (map.get(firstvalue) >= map.get(secondvalue)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        My_Comapartor customComparator = new My_Comapartor(tempMap);
        TreeMap<String, Integer> sortedTreeMap = new TreeMap<String, Integer>(customComparator);
        sortedTreeMap.putAll(tempMap);
        return sortedTreeMap;

    }

    //Traverse the Directory Structure and add all fileNamesList to a List
    public static void storeFilesFornFolderToFileList(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                storeFilesFornFolderToFileList(fileEntry);
            } else {
                fileNamesList.add(fileEntry.getName().toString());
            }
        }
    }

}
