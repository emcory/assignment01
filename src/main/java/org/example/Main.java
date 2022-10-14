package org.example;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    //holds all sorted movies
    public static ArrayList<String> sorted_movies = new ArrayList<>();
    public static void readFile(String filepath) {
        try {
            // Create an object of file reader
            FileReader filereader = new FileReader(filepath);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            csvReader.readNext();

            // read line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                //uses binary search to find index to sort alphabetically
                int index = binarySearch(sorted_movies, nextRecord[1]);
                sorted_movies.add(index, nextRecord[1]);
            }
        }
        catch (IOException | CsvValidationException e) {
            System.out.println(e);
        }
    }
    public static int binarySearch(ArrayList<String> movies, String movie_to_add) {
        int middle, low = 0, high = movies.size() - 1;

        if (movies.size() == 0) {
            return 0;
        }

        while (true) {
            middle = (high + low) / 2;          //this gets the middle value/index
            if (movies.get(middle).compareTo(movie_to_add) < 0) { //this means it would be in the high section
                low = middle + 1;               //+1 to exclude low since it has already been checked
                if (high < low) {               //if low becomes greater than high after low = mid + 1, return mid + 1
                    return middle + 1;
                }
            } else {                            //else, it's in the lower section
                high = middle - 1;              //-1 to exclude high since it has been checked
                if (high < low) {
                    return middle;
                }
            }
        }
    }
    public static void main(String[] args) {
        readFile(args[0]);

        for (int i = 0; i < sorted_movies.size(); i++) {
            System.out.println(sorted_movies.get(i));
        }
        System.out.println(sorted_movies.size());
    }
}