package com.thirdarm.jokesLib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/** The object model for the data we are sending through endpoints */
public class Joke {

    private String joke;
    private String name;
    private int id;
    private int[] categoryIds;
    private int frequency;

    // TODO: Add an AUTHORS field
    // TODO: Utilize frequency as a means of influencing random joke selection to view
    // TODO: Utilize categoryIds


    public Joke(String joke, String name, int id, int[] categoryIds) {
        this.joke = joke;
        this.name = name;
        this.id = id;
        this.categoryIds = categoryIds;
        frequency = 0;
    }

    public String getJoke() {
        return this.joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getJokeName() { return this.name; }

    public void setJokeName(String name) { this.name = name; }

    public int getJokeId() { return this.id; }

    public void setJokeId(int id) { this.id = id; }

    public int[] getCategoryIds() { return this.categoryIds; }

    public void setCategoryIds(int[] ids) { this.categoryIds = ids; }

    public int getJokeFrequency() { return this.frequency; }

    public void setJokeFrequency(int i) { this.frequency = i; }
}