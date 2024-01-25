package com.changenode.mainclasses;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class TestClass {
    public static void main(String[] args) {
        Random rand = new Random();
        double x = 0;
        for (int n = 0; n < 100; n++) {
            x = x + rand.nextDouble(10);
            coords[n] = x;
        }

        System.out.println(Arrays.toString(coords));

        double bound = coords[coords.length-1] + 100;

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSSS");

        for (int n = 0; n < 1000; n++) {
            double test = rand.nextDouble(bound) - 50;
            long startTime = System.currentTimeMillis();
            System.out.println(sdf.format(new Date(System.currentTimeMillis()).getTime())+" [TEST] #"+n+" value: "+test+" result: "+findClosestMatch(test));
            System.out.println("ELAPSED: "+(System.currentTimeMillis() - startTime));
        }

    }
    static double[] coords = new double[100];
    public static int findClosestMatch(double target) {
        int left = 0;
        int right = coords.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (coords[mid] == target) {
                return mid;
            } else if (coords[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // At this point, left and right pointers have crossed
        // Find the index of the closest match among left and right elements
        if (left > 0 && (left == coords.length || Math.abs(coords[left - 1] - target) < Math.abs(coords[left] - target))) {
            return left - 1;
        } else {
            return left;
        }
    }
}
