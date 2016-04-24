package com.thirdarm.jokesLib;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * This is the container that holds all the jokes. Now with convenience getter and setter methods.
 */
public class JokesContainer {

    // Contain ALL the jokes!
    private ArrayList<Joke> mJokes;

    /**
     * Default constructor. Add a starter pack of jokes if requested.
     * @param starterPack If true, add an initial set of the best jokes out ther.
     */
    public JokesContainer(boolean starterPack) {
        mJokes = new ArrayList<>();
        if (starterPack) {
            StarterPack sp = new StarterPack();
            ArrayList<Pair<String, String>> jokesPack = sp.getJokesPack();
            int[] categoryId = {-1};

            // Gather the jokes from the java starter pack and create a list of joke object
            for (Pair<String, String> joke : jokesPack) {
                mJokes.add(new Joke(joke.getValue(), joke.getKey(), createJokeId(), categoryId));
            }
        }
    }

    /**
     * Creates a unique joke id. Checks against list of all jokes to ensure id is unique.
     * Value of id limited by value of Constants.NUM_JOKE_IDS.
     * @return A joke id. -1 if container is already full.
     *
     * TODO: This can potentially run forever (e.g. if majority of ids are already taken). Fix.
     *  Maybe implement another search method?
     */
    public int createJokeId() {
        if (getContainerSize() == Constants.NUM_JOKE_IDS) {
            return -1;
        }
        Random random = new Random();
        while (true) {
            int i = random.nextInt(Constants.NUM_JOKE_IDS);
            if (!checkIfJokeIdExists(i)) {
                return i;
            }
        }
    }

    /**
     * Helper method that checks if a specified joke id is already taken by a joke in the container
     * @param id Id to test
     * @return True if id is taken
     */
    public boolean checkIfJokeIdExists(int id) {
        for (Joke joke : mJokes) {
            if (joke.getJokeId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a joke to the joke list. Does nothing if the container is already full.
     * @param joke The joke string
     * @param name String id of the joke
     * @param categoryIds Categories joke belongs to
     * @return The added joke
     */
    public Joke addJoke(String joke, String name, int[] categoryIds) {
        int id = createJokeId();
        if (id != -1) {
            Joke response = new Joke(joke, name, id, categoryIds);
            mJokes.add(response);
            return response;
        } else {
            return null;
        }
    }

    /**
     * Adds a joke to the joke list, if id not already taken.
     * @param joke The joke
     * @param reselect If <code>true</code>, automatically select a new id if specified id is
     *                 already taken.
     *                 If <code>false</code>, return <code>null</code>.
     * @return The added joke
     */
    public Joke addJoke(Joke joke, boolean reselect) {
        if (!checkIfJokeIdExists(joke.getJokeId())) {
            mJokes.add(joke);
            return joke;
        } else if (reselect) {
            return addJoke(joke.getJoke(), joke.getJokeName(), joke.getCategoryIds());
        } else {
            return null;
        }
    }

    /**
     * Adds a joke to the joke list, with id specified. Does nothing if the container is
     * already full.
     * @param joke The joke string
     * @param name String id of the joke
     * @param id Integer id of the joke
     * @param categoryIds Categories joke belongs to
     * @param reselect If <code>true</code>, automatically select a new id if specified id is
     *                 already taken.
     *                 If <code>false</code>, return <code>null</code>.
     * @return The added joke, or <code>null</code> if specified id is already used and
     * <code>reselect</code> is <code>false</code>.
     */
    public Joke addJokeWithId(String joke, String name, int id, int[] categoryIds, boolean reselect) {
        if (checkIfJokeIdExists(id)) {
            if (reselect) {
                id = createJokeId();
            } else {
                return null;
            }
        }
        if (id != -1) {
            Joke response = new Joke(joke, name, id, categoryIds);
            mJokes.add(response);
            return response;
        } else {
            return null;
        }
    }

    /**
     * Removes a specified joke from the container.
     * @param id The id of the joke
     * @return The removed joke. Null if no joke was removed.
     */
    public Joke removeJoke(int id) {
        for (Joke joke : mJokes) {
            if (joke.getJokeId() == id) {
                mJokes.remove(joke);
                return joke;
            }
        }
        return null;
    }

    /**
     * GET the joke corresponding to the provided id
     * @param id The id of the joke
     * @return The joke corresponding to the provided id. Null if not found.
     */
    public Joke getJokeById(int id) {
        for (Joke joke : mJokes) {
            if (joke.getJokeId() == id) {
                return joke;
            }
        }
        return null;
    }

    /**
     * GET the jokes corresponding to the provided category id.
     * @param categoryId The id corresponding to the category of the joke
     * @return List of jokes with the given category id. <code>Null</code> if list is empty.
     */
    public ArrayList<Joke> getJokesByCategoryId(int categoryId) {
        ArrayList<Joke> response = new ArrayList<>();
        for (Joke joke : mJokes) {
            int[] ids = joke.getCategoryIds();
            for (int id : ids) {
                if (id == categoryId) {
                    response.add(joke);
                    break;
                }
            }
        }
        if (response.size() == 0) {
            return null;
        }
        return response;
    }

    /**
     * GET a random joke from the jokes list.
     * @return A random joke. <code>Null</code> if container is empty.
     */
    public Joke getRandomJoke() {
        if (getContainerSize() == 0) {
            return null;
        }
        Random random = new Random();
        int i = random.nextInt(mJokes.size());
        return mJokes.get(i);
    }

    /**
     * GET ALL the jokes!
     * @return The jokes contained in the Endpoint. <code>Null</code> if container is empty.
     */
    public ArrayList<Joke> getAllJokes() {
        if (getContainerSize() == 0) {
            return null;
        }
        return mJokes;
    }

    /**
     * Get the size of the container
     * @return The number of jokes in the container
     */
    public int getContainerSize() {
        return mJokes.size();
    }

}
