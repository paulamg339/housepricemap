package com.example.googlemaps;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Test
    public void onCreate() {
    }

    @Test
    public void isCorrectVersion() {
    }

    @Test
    public void convertFahrenheitToCelsius() throws Exception{
        float input = 212;
        float output = 122;
        float expected = 100;
        double delta = .1;

        //ConverterUtil converterUtil = new ConverterUtil();

        assertEquals(expected, output, delta);
    }
}