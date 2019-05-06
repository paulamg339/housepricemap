package com.example.googlemaps;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)

public class ExampleTest {
    MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().resume().get();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity); //make sure the activity is not null when we build it
    }

    //Test to check that the right activity is launches after pressing a button
    //We can do this using Shadow Applications
    @Test
    public void continueShouldLaunchMap() {


        //define the expected results
        Intent expectedIntent = new Intent(activity, MapActivity.class);

        //click the map button
        activity.findViewById(R.id.btnMap).callOnClick();

        //get the actual results
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();
        //check if the expected results match the actual resultsse
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }


}


