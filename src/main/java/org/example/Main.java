package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    //holds all movies. code could be expanded to search by movie and print actors
    public static ArrayList<Movie> movies = new ArrayList<>();
    //holds all actors
    public static ArrayList<Actor> sorted_actors = new ArrayList<>();

    public static void readFile(String filepath) throws FileNotFoundException, UnsupportedEncodingException {
        // Create an object of file reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8));
        String line;

        //skip the first line
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // read line by line
        while (line != null) {
            try {
                line = reader.readLine();
                String[] nextRecord = line.split(",\"\\[\\{");
                StringBuilder movie_name_build = new StringBuilder();
                int comma_count = 0;
                boolean no_actors = false; //takes care of empty actor section in csv

                //building the movie name with the string builder until open square bracket
                for (int i = 0; i < nextRecord[0].length(); i++) {
                    if (comma_count > 0 && nextRecord[0].charAt(i) != '\"') {
                        movie_name_build.append(nextRecord[0].charAt(i));
                    } else if (nextRecord[0].charAt(i) == ',') {
                        comma_count++;
                    }
                    //if it hits an open square bracket, replaces last comma and breaks. means there are no actors
                    if (nextRecord[0].charAt(i) == '[' && movie_name_build.length() > 1) {
                        movie_name_build.replace(movie_name_build.length() - 2, movie_name_build.length(), " ");
                        no_actors = true;
                        break;
                    }
                }
                //adds movie name to the list of movies and create new actor
                String movie_name = movie_name_build.toString();
                Movie movie = new Movie(movie_name);
                movies.add(movie);
                Actor actor = new Actor(null);

                if (nextRecord.length > 1 && !no_actors) {
                    //prep string to get character
                    String[] split_names = nextRecord[1].split("\"\"character\"\": \"\"");
                    for (int i = 1; i < split_names.length; i++) {
                        //getting character from json
                        int end_index = split_names[i].indexOf('\"');
                        String character_build = split_names[i].substring(0, end_index);

                        //prep string to get name
                        String[] get_name = split_names[i].split("\"\"name\"\": \"\"");
                        //getting name from json NEED TO ACCEPT SPECIAL CHARACTERS
                        String name_build = null;
                        if (get_name.length > 1) {
                            end_index = (get_name[1].indexOf('\"'));
                            name_build = get_name[1].substring(0, end_index);
                        }
                        //set movie stats with movie name and character
                        MovieStats movie_stats = new MovieStats(movie_name, character_build);

                        //if there is a name find index to insert actor so that it is sorted
                        if (name_build != null) {
                            int index = binarySearch(sorted_actors, name_build, false);
                            //if index == -1, means actor already exists
                            if (index != -1) {
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
                    }
                }
            } catch (IOException | NullPointerException e) {
                //ignore e.printStackTrace();
            }
        }
    }

    //took this code from my binary search lab. copied and pasted but changed names
    //bool lookup is true when looking for an actor in the list
    //and false when looking to see if actor is in list/where to insert
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
        PrintWriter printWriter = new PrintWriter(System.out,true);

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
                System.out.print("Actor doesn't exits in this wall. Did you mean \"" + name + "\"? Y/N: ");
                String yes_or_no = input.nextLine();
                if (yes_or_no.equalsIgnoreCase("Y")) {
                    printWriter.println(name);
                    sorted_actors.get(actor_index).printMovies();
                }
            }

            //loop through again
            System.out.print("Enter the name of an actor (or \"EXIT\" to quit): ");
            lookup_name = input.nextLine();
        }
        //exit
        System.out.println("Thanks for using the Movie Wall!");
    }
}