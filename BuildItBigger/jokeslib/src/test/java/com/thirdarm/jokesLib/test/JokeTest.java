package com.thirdarm.jokesLib.test;

import com.thirdarm.jokesLib.Joke;

import org.junit.Test;

/**
 * Class that tests usage of the Joke class
 */
public class JokeTest {

    String testJoke = "joke";
    String testName = "test";
    int testId = 1;
    int[] testCategoryIds = new int[]{1, 2, 3};
    int testJokeFrequency = 1;

    Joke joke = new Joke(testJoke, testName, testId, testCategoryIds);

    @Test
    public void checkJokeString() {
        assert joke.getJoke().equals(testJoke);
    }

    @Test
    public void checkJokeName() {
        assert joke.getJokeName().equals(testName);
    }

    @Test
    public void checkJokeId() {
        assert joke.getJokeId() == testId;
    }

    @Test
    public void checkJokeCategoryIds() {
        for (int i = 0; i < joke.getCategoryIds().length; i++) {
            assert joke.getCategoryIds()[i] == testCategoryIds[i];
        }
    }

    @Test
    public void checkJokeFrequency() {
        assert joke.getJokeFrequency() == 0;

        joke.setJokeFrequency(testJokeFrequency);

        assert joke.getJokeFrequency() == testJokeFrequency;
    }
}
