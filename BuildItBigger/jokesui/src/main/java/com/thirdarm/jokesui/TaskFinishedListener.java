package com.thirdarm.jokesui;

import com.thirdarm.jokes.backend.jokesApi.model.Joke;

import java.util.List;

/**
 * <p>Listener that notifies other classes that the task has reached <code>onPostExecute()</code>.
 * To be used within AsyncTasks, but can potentially be used elsewhere where needed.</p>
 *
 * <p>This is useful in cases in which the result of the AsyncTask is used to update the UI, while
 * also keeping the AsyncTask encapsulated in its own class, preventing the need to pass in
 * UI elements into the AsyncTask itself. The overridden listener method
 * <code>onTaskFinished()</code> called in the hosting class performs the UI updates.</p>
 *
 * <p>Steps to using this listener properly:
 * <ol>
 *  <li>Make sure the AsyncTask passes a new instance of the listener in the constructor</li>
 *  <li>When instantiating the listener, override <code>onTaskFinished()</code> to perform the
 *      requested actions in the hosting class when <code>onTaskFinished()</code> is called
 *      from within the AsyncTask</li>
 *  <li>Save the passed listener instance as a field variable in the AsyncTask</li>
 *  <li>Call <code>onTaskFinished()</code> in the AsyncTask's <code>onPostExecute()</code></li>
 *      <p>Note: Make sure to first check if the listener is <code>null</code> as some hosting
 *      classes may not need to use a listener (will pass as <code>null</code> in the AsyncTask
 *      constructor.</p>
 * </p>
 * </ol>
 */
public interface TaskFinishedListener {
    void onTaskFinished(Joke joke);
    void onTaskFinished(List<Joke> jokes);
}
