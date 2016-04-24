/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.thirdarm.jokes.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.thirdarm.jokesLib.Constants;
import com.thirdarm.jokesLib.Joke;
import com.thirdarm.jokesLib.JokesContainer;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "jokesApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.jokes.thirdarm.com",
    ownerName = "backend.jokes.thirdarm.com",
    packagePath=""
  )
)

/**
 *  This endpoint class allows us to modify and access a JokesContainer object, which itself
 *  contains all jokes data. The JokesContainer is an object found in the Java Jokes Library. The
 *  methods in this class parallel most of the methods in the JokesContainer class.
 */
public class JokesEndpoint {

    JokesContainer container = new JokesContainer(true);

    /**
     * Adds a joke to the joke list
     * @param joke The joke string
     * @param name String id of the joke
     * @param categoryIds Categories joke belongs to
     * @return The added joke. <code>Null</code> if none added.
     */
    @ApiMethod(name = "addJoke")
    public Joke addJoke(@Named("joke") String joke, @Named("name") String name,
                        @Named("categoryIds") int[] categoryIds) {
        return container.addJoke(joke, name, categoryIds);
    }

    /**
     * Adds a joke to the joke list, with specified id
     * @param joke The joke string
     * @param name String id of the joke
     * @param id Integer id of the joke
     * @param categoryIds Categories joke belongs to
     * @return The added joke, <code>Null</code> if none added.
     */
    @ApiMethod(name = "addJokeWithId")
    public Joke addJokeWithId(@Named("joke") String joke, @Named("name") String name,
                        @Named("id") int id, @Named("categoryIds") int[] categoryIds,
                        @Named("reselect") boolean reselect) {
        return container.addJokeWithId(joke, name, id, categoryIds, reselect);
    }

//    /**
//     * Adds a joke to the joke list
//     * @param joke The joke object
//     * @return The added joke
//     */
//    @ApiMethod(name = "addJokeObject")
//    public Joke addJokeObject(Joke joke) {
//        return container.addJoke(joke);
//    }

    /**
     * Removes a joke from the joke list, with specified id
     * @param id Integer id of the joke
     * @return The removed joke. <code>Null</code> if none removed.
     */
    @ApiMethod(name = "removeJoke")
    public Joke removeJoke(@Named("id") int id) {
        return container.removeJoke(id);
    }

    /**
     * GET the joke corresponding to the provided id
     * @param id The id of the joke
     * @return The joke corresponding to the provided id. <code>Null</code> if not found.
     */
    @ApiMethod(name = "getJokeById")
    public Joke getJokeById(@Named("id") int id) {
        return container.getJokeById(id);
    }

    /**
     * GET the jokes corresponding to the provided category id
     * @param categoryId The id corresponding to the category of the joke
     * @return List of jokes with the given category id. <code>Null</code> if none found.
     */
    @ApiMethod(name = "getJokesByCategoryId")
    public ArrayList<Joke> getJokesByCategoryId(@Named("categoryId") int categoryId) {
        return container.getJokesByCategoryId(categoryId);
    }

    /**
     * Get a random joke from the jokes list
     * @return A random joke. <code>Null</code> if list is empty.
     */
    @ApiMethod(name = "getRandomJoke")
    public Joke getRandomJoke() {
        return container.getRandomJoke();
    }

    /**
     * GET ALL the jokes!
     * @return The jokes contained in the Endpoint. <code>Null</code> if list is empty.
     */
    @ApiMethod(name = "getAllJokes")
    public ArrayList<Joke> getAllJokes() {
        return container.getAllJokes();
    }

    /**
     * GET the jokes container
     * @return The jokes container
     */
    @ApiMethod(name = "getJokesContainer")
    public JokesContainer getJokesContainer() { return container; }

    /**
     * Reset the joke container. Bring back the original jokes!
     * @return The jokes container
     */
    @ApiMethod(name = "resetJokesContainer")
    public JokesContainer resetJokesContainer() {
        container = new JokesContainer(true);
        return container;
    }
}
