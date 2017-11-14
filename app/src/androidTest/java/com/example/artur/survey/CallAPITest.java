package com.example.artur.survey;

import android.os.AsyncTask;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CallAPITest {
    @Test
    public void useAppContext() throws Exception {

        AsyncTask<String, Void, String> execute = new HttpPostRequest().execute("http://free-ig-videoviews.com/app/api/", "7171717171");
        try {
            Log.i("code",execute.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals("200 OK",execute.get());
    }
}
