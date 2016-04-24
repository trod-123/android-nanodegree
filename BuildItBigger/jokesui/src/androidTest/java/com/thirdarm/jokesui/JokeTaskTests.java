package com.thirdarm.jokesui;

import android.test.AndroidTestCase;

import com.thirdarm.jokes.backend.jokesApi.model.Joke;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Tests to check if the JokeTasks are working properly
 */
public class JokeTaskTests extends AndroidTestCase {

    private Joke mJoke;

    public void testGetRandomJoke() {
        final CountDownLatch signal = new CountDownLatch(1);

        GetRandomJokeTask testTask = new GetRandomJokeTask(new TaskFinishedListener() {
            @Override
            public void onTaskFinished(Joke joke) {
                signal.countDown();
                mJoke = joke;
            }

            @Override
            public void onTaskFinished(List<Joke> jokes) {

            }
        });
        testTask.execute();

        try {
            signal.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new AssertionError("There was an error: " + e);
        }
        assert mJoke instanceof Joke;
    }
}
