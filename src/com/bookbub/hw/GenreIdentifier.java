package com.bookbub.hw;

import com.eaio.stringsearch.BoyerMooreHorspoolRaita;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by adi on 12/9/15.
 *
 * Encapsulates the core functionality
 */
public class GenreIdentifier {

    private static JSONParser parser = new JSONParser();
    private final static Logger LOGGER = Logger.getLogger(GenreIdentifier.class.getSimpleName());
    // Uses Boyer Moore string search instead of default java search
    private final static BoyerMooreHorspoolRaita bmhr = new BoyerMooreHorspoolRaita();

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java -jar bookbub.jar </path/to/booksJson> </path/to/keyWordsCsv>");
            System.exit(-1);
        }
        String booksJson = args[0];
        String keywordsCsv = args[1];

        //First parse the input files
        List<Book> books = parseBooksJson(booksJson);
        Map<String, List<KeyWordValuePair>> keyWordValuePairs = parseKeywordsCsv(keywordsCsv);

        try {
            // Get results aka calculate scores
            List<Result> results = getResults(books, keyWordValuePairs);

            // prints results in alphabetical order of book titles
            Collections.sort(results);
            for (Result result : results) {
                System.out.println(result.getTitle());
                // Below snippet prints the top three genres in descending values of scores
                Deque<Result.GenreScore> tmp = new ArrayDeque<>(Constants.NUM_TOP_RESULTS);
                while (!result.getTop().isEmpty()) {
                    tmp.offer(result.getTop().poll());
                }
                while (!tmp.isEmpty()) {
                    Result.GenreScore genreScore = tmp.removeLast();
                    System.out.println(genreScore.getGenre() + ", " + genreScore.getScore());
                }
                System.out.println();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Something went wrong", e);
        }

    }

    private static List<Result> getResults(List<Book> books, Map<String, List<KeyWordValuePair>> keyWordValuePairs) {
        List<Result> ret = new ArrayList<>();
        for (Book book : books) {
            String title = book.getTitle();
            String desc = book.getDescription();
            Result result = new Result(title);

            for (Map.Entry<String, List<KeyWordValuePair>> entry : keyWordValuePairs.entrySet()) {
                String genre = entry.getKey();
                int totalHits = 0;
                int uniqueHits = 0;
                double avg = 0;
                double score = 0;
                for (KeyWordValuePair keyWordValuePair : entry.getValue()) {
                    String keyword = keyWordValuePair.getKeyword().trim();
                    // Boyer Moore string search
                    int pos = bmhr.searchString(desc, keyword);
                    int hits = 0;
                    while (pos != -1) {
                        hits++;
                        pos = bmhr.searchString(desc, pos+1, desc.length(), keyword);
                    }
                    if (hits != 0) {
                        uniqueHits++;
                        avg += keyWordValuePair.getValue();
                        totalHits += hits;
                    }
                }
                if (uniqueHits != 0) {
                    avg /= uniqueHits;
                }
                score = totalHits * avg;
                Result.GenreScore genreScore = result.new GenreScore(genre, score);
                result.add(genreScore);
            }

            ret.add(result);
        }
        return ret;
    }

    private static Map<String, List<KeyWordValuePair>> parseKeywordsCsv(String keywordsCsv) {
        Map<String, List<KeyWordValuePair>> ret = new HashMap<>();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(keywordsCsv));
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(Constants.CSV_DELIMITER);
                if (arr.length != 3) {
                    throw new Exception("Invalid keywords file. Some required values are missing or the file is improperly formatted." +
                            "Each line should contain a triplet <genre>, <keyword>, <score>");
                }
                String genre = arr[0];
                String keyword = arr[1];
                double value = 0;
                try {
                    value = Double.parseDouble(arr[2]);
                } catch (NumberFormatException nfe) {
                    LOGGER.log(Level.FINE, "Value is not a number, skipping line: " + line, nfe);
                    continue;
                }

                List<KeyWordValuePair> pairs = ret.get(genre);
                if (pairs == null) {
                    pairs = new ArrayList<>();
                }
                pairs.add(new KeyWordValuePair(keyword, value));
                ret.put(genre, pairs);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't read keywords file", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error closing buffered reader", e);
                }
            }
        }
        return ret;
    }

    private static List<Book> parseBooksJson(String booksJson) {
        List<Book> ret = new ArrayList<>();
        try {
            JSONArray books = (JSONArray) parser.parse(new FileReader(booksJson));
            Iterator<JSONObject> iter = books.iterator();
            while (iter.hasNext()) {
                JSONObject jsonObj = iter.next();
                String title = (String) jsonObj.get(Constants.TITLE_ELEM);
                String desc = (String) jsonObj.get(Constants.DESCRIPTION_ELEM);
                if (title == null || desc == null) {
                    throw new NullPointerException("Didn't find title or description for the book: " + jsonObj.toJSONString());
                }
                Book book = new Book(title, desc);
                ret.add(book);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't read books file", e);
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Can't parse books file", e);
        }

        return ret;
    }


}
