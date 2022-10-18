package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Actor {
    String actor_name;
    ArrayList<MovieStats> movie_appearance = new ArrayList<>();

    public Actor(String name) {
        this.actor_name = name;
    }

    public void addMovie(MovieStats movie) {
        this.movie_appearance.add(movie);
    }

    public String getName() {
        return this.actor_name;
    }

    public void printMovies() {
        for (MovieStats movieStats : movie_appearance) {
            PrintWriter printWriter = new PrintWriter(System.out,true);
            printWriter.println("* Movie: " + movieStats.movie_name + " as " + movieStats.character);
        }
    }


}
