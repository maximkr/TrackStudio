package com.trackstudio.app;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

@Ignore
public class SliderTest {
    @Test
    public void whenSize() {
        Slider<String> slider = new Slider<String>(
                Arrays.asList("1", "2", "3"), 3, new ArrayList<>(), 0, 6
        ) {
            @Override
            public int getPagesCount() {
                return 3;
            }
        };
        String pages = slider.drawSlider("", "", "");
        System.out.println(pages);
    }
}