package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    //holds all movies. code could be expanded to search by movie and print actors
    public static ArrayList<Movie> movies = new ArrayList<>();
    //holds all actors
    public static ArrayList<Actor> sorted_actors = new ArrayList<>();

    //helper function that builds a string until it hits a quotation mark. used to parse json
    public static String extractString(String full_string) {
        StringBuilder build = new StringBuilder();
        for (int j = 0; j < full_string.length(); j++) {
            if (full_string.charAt(j) != '\"') {
                build.append(full_string.charAt(j));
            } else {
                break;
            }
        }
        return build.toString();
    }
    public static void readFile(String filepath) throws FileNotFoundException, UnsupportedEncodingException {
        // Create an object of file reader
        //FileReader filereader = new FileReader(filepath);

        // create csvReader object
        CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
        String[] nextRecord;

        //skip the first line
        try {
            nextRecord = csvReader.readNext();
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
        String movie_name;

        // read line by line
        while (nextRecord != null) {
            try {
                nextRecord = csvReader.readNext();

                //movie name is always at index 1
                movie_name = nextRecord[1];
                Movie movie = new Movie(movie_name);
                movies.add(movie);
                Actor actor = new Actor(null);

                String[] split_names = nextRecord[2].split("\"character\": \"");

                for (int i = 1; i < split_names.length; i++) {
                    //getting character from json
                    String character_build = extractString(split_names[i]);

                    //getting name from json NEED TO ACCEPT SPECIAL CHARACTERS
                    String[] get_name = split_names[i].split("\"name\": \"");
                    String name_build = null;
                    if (get_name.length > 1) {
                        name_build = extractString(get_name[1]);
                    }
                        //set databases
                    MovieStats movie_stats = new MovieStats(movie_name, character_build);

                    //need to change binary search for arraylist<object> and make movie sorted a movie class
                    int index = binarySearch(sorted_actors, name_build, false);
                    if (index != -1) { //if index == -1, means actor already exists
                        Actor new_actor = new Actor(name_build);
                        new_actor.addMovie(movie_stats);
                        sorted_actors.add(index, new_actor);
                    } else {
                        //now that we know the actor exists, find the index to get the actor
                        index = binarySearch(sorted_actors, name_build, true);
                        actor = sorted_actors.get(index);
                        actor.addMovie(movie_stats);
                    }
                }

            } catch (IOException | CsvValidationException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    //took this code from my binary search lab. copied and pasted but changed names
    public static int binarySearch(ArrayList<Actor> actors, String actor_name, boolean lookup) {
        int middle, low = 0, high = actors.size() - 1;

        //if the size is zero, put it in the zeroth index
        if (actors.size() == 0) {
            return 0;
        }

        while (true) {
            middle = (high + low) / 2;          //this gets the middle value/index
            if (actors.get(middle).getName().equalsIgnoreCase(actor_name)) {      //check to see if each variable is = to target
                if (!lookup) {
                    return -1;
                } else {
                    return middle;
                }
            } else if (actors.get(high).getName().equalsIgnoreCase(actor_name)) {
                if (!lookup) {
                    return -1;
                } else {
                    return high;
                }
            } else if (actors.get(low).getName().equalsIgnoreCase(actor_name)) {
                if (!lookup) {
                    return -1;
                } else {
                    return low;
                }
            } else if (actors.get(middle).getName().compareToIgnoreCase(actor_name) < 0) { //this means it would be in the high section
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
    public static void main(String[] args) throws FileNotFoundException {
        //read the file
        try {
            readFile(args[0]);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(movies.size());
        for (int i = 0; i < sorted_actors.size(); i++) {
            System.out.println(sorted_actors.get(i).getName());
        }
        //get user input
        Scanner input = new Scanner(System.in);
        System.out.print("Welcome to the Movie Wall!\n" +
                "Enter the name of an actor (or \"EXIT\" to quit): ");
        String lookup_name = input.nextLine();

        //loop until user inputs exit
        while (!lookup_name.equalsIgnoreCase("EXIT")) {
            //look for actor with the closest name with lookup = true
            int actor_index = binarySearch(sorted_actors, lookup_name, true);
            String name = sorted_actors.get(actor_index).getName();

            //if the name is equal to the input, print the movies the actor has been in
            if (name.equalsIgnoreCase(lookup_name)) {
                sorted_actors.get(actor_index).printMovies();
            } else {
                System.out.println("Actor doesn't exits in this wall. Did you mean \"" + name + "\"?");
            }

            //loop through again
            System.out.print("Enter the name of an actor (or \"EXIT\" to quit): ");
            lookup_name = input.nextLine();
        }
        //exit
        System.out.println("Thanks for using the Movie Wall!");
    }
}