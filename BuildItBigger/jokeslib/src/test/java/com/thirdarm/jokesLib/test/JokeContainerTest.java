package com.thirdarm.jokesLib.test;

import com.thirdarm.jokesLib.Constants;
import com.thirdarm.jokesLib.Joke;
import com.thirdarm.jokesLib.JokesContainer;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Class that tests usage of the Joke Container class
 */
public class JokeContainerTest {

    String testJoke = "joke";
    String testName = "test";
    int testId = 1;
    int testId2 = 2;
    int[] testCategoryIds = new int[]{1, 2, 3};
    int[] testCategoryIds2 = new int[] {4, 5, 6};
    int testJokeFrequency = 1;

    Joke joke = new Joke(testJoke, testName, testId, testCategoryIds);
    Joke joke2 = new Joke(testJoke, testName, testId2, testCategoryIds2);
    JokesContainer container = new JokesContainer(false);

    @Test
    public void testCheckIfJokeIdExists() {
        // TEST 1: Check if id of an added joke exists
        container.addJokeWithId(testJoke, testName, testId, testCategoryIds, false);
        assert (container.checkIfJokeIdExists(testId));

        // TEST 2: Check if reselecting a new id works
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, false) == null);
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, true) != null);
    }

    @Test
    public void testCreateJokeId() {
        // Make sure that the id does not already exist by filling joke ids up until the
        // id limit - 1
        for (int i = 0; i < Constants.NUM_JOKE_IDS - 1; i++) {
            container.addJokeWithId(testJoke, testName, i, testCategoryIds, false);
        }
        // Test whether createJokeId() can retrieve the remaining id
        assert (container.createJokeId() < Constants.NUM_JOKE_IDS);
    }

    @Test
    public void testAddJokeNoId() {
        // TEST 1: Basic usage, using skeletal addJoke() signature
        try {
            container.addJoke(testJoke, testName, testCategoryIds);
        } catch (Exception e) {
            assert false;
        }
        // TEST 2: Basic usage, using object addJoke() signature
        try {
            container.addJoke(joke, false);
        } catch (Exception e) {
            assert false;
        }
        // TEST 3: Make sure that adding a duplicate joke results in null if reselect is false
        assert (container.addJoke(joke, false) == null);
        // TEST 4: Make sure that adding a duplicate joke does not result in null if reselect is
        // true
        assert (container.addJoke(joke, true) != null);
        // TEST 5: Fill up the container to max and make sure a failing subsequent addition,
        // regardless of method signature.
        for (int i = 0; i < Constants.NUM_JOKE_IDS; i++) {
            container.addJokeWithId(testJoke, testName, i, testCategoryIds, false);
        }
        assert (container.addJoke(testJoke, testName, testCategoryIds) == null);
        assert (container.addJoke(joke2, false) == null);
        assert (container.addJoke(joke2, true) == null);
    }

    @Test
    public void testAddJokeWithId() {
        // TEST 1: Basic usage
        try {
            container.addJokeWithId(testJoke, testName, testId, testCategoryIds, false);
        } catch (Exception e) {
            assert false;
        }
        // TEST 2: Make sure that adding a duplicate joke results in null if reselect is false
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, false) == null);
        // TEST 3: Make sure that adding a duplicate joke does not result in null if reselect is
        // true
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, true) != null);
        // TEST 4: Fill up the container to max and make sure a failing subsequent addition,
        // regardless if reselect is true or false
        for (int i = 0; i < Constants.NUM_JOKE_IDS; i++) {
            container.addJokeWithId(testJoke, testName, i, testCategoryIds, false);
        }
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, false) == null);
        assert (container.addJokeWithId(testJoke, testName, testId, testCategoryIds, true) == null);
    }

    @Test
    public void testRemoveJoke() {
        // TEST 1: If container is empty, make sure to return null
        assert (container.removeJoke(testId) == null);
        // TEST 2: Remove the joke of specified id
        container.addJoke(joke, false);
        assert (container.getContainerSize() == 1);
        assert (container.removeJoke(joke.getJokeId()) != null);
        assert (container.getContainerSize() == 0);
        // TEST 3: Return null if specified id matches no joke in the container
        assert (container.removeJoke(testId2) == null);
    }

    @Test
    public void testGetJokeById() {
        // TEST 1: Test if container is empty, return null
        assert (container.getJokeById(testId) == null);
        // TEST 2: Make sure if a joke has the id, return the joke with that id
        container.addJoke(joke, false);
        assert container.getJokeById(testId).equals(joke);
        // TEST 3: If none of the jokes have the id, make sure to return null
        assert (container.getJokeById(testId2) == null);
    }

    @Test
    public void testGetJokesByCategoryId() {
        // TEST 1: Test if container is empty, return null;
        assert (container.getJokesByCategoryId(0) == null);
        // TEST 2: Make sure if joke has the category ids, return the joke with that id
        container.addJoke(joke, false);
        for (int i : testCategoryIds) {
            assert container.getJokesByCategoryId(i).contains(joke);
        }
        // TEST 3: If none of the jokes have the category id, make sure to return null
        for (int i : testCategoryIds2) {
            assert (container.getJokesByCategoryId(i) == null);
        }
    }

    @Test
    public void testGetRandomJoke() {
        // TEST 1: Test if container is empty, return null
        assert (container.getRandomJoke() == null);
        // TEST 2: Add 2 jokes and test if getRandomJoke() gets either of the two
        container.addJoke(joke, false);
        container.addJoke(joke2, false);
        Joke randomJoke = container.getRandomJoke();
        assert (randomJoke.equals(joke) || randomJoke.equals(joke2));
    }

    @Test
    public void testGetContainerSize() {
        // TEST 1: Make sure that if the container is empty, return 0
        assert container.getContainerSize() == 0;
        // TEST 2: Add one joke, and make sure to return 1
        container.addJoke(joke, false);
        assert container.getContainerSize() == 1;
    }

    @Test
    public void testGetAllJokes() {
        // TEST 1: Make sure that if container is empty, return null
        assert (container.getAllJokes() == null);
        // TEST 2: Add two jokes, and make sure both jokes are returned
        ArrayList<Joke> jokes = new ArrayList<>();
        jokes.add(joke);
        jokes.add(joke2);
        container.addJoke(joke, false);
        container.addJoke(joke2, false);
        assert container.getAllJokes().equals(jokes);
    }
}
