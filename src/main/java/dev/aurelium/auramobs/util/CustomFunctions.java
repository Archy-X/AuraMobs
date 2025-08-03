package dev.aurelium.auramobs.util;

import net.objecthunter.exp4j.function.Function;

import java.util.ArrayList;
import java.util.List;

public class CustomFunctions {

    public static List<Function> getCustomFunctions() {
        List<Function> functions = new ArrayList<>();

        // Returns a random number between min (args[0]) and max (args[1])
        // Usage: random_between(min, max)
        functions.add(new Function("random_between", 2) {
            @Override
            public double apply(double... args) {
                return args[0] + Math.random() * (args[1] - args[0]);
            }
        });

        // Returns a random float between 0 and 1
        // Usage: rand()
        functions.add(new Function("rand", 0) {
            @Override
            public double apply(double... args) {
                return Math.random();
            }
        });

        // Clamps a value between a minimum and maximum
        // Usage: clamp(value, min, max)
        // Example: clamp(12, 0, 10) -> returns 10
        functions.add(new Function("clamp", 3) {
            @Override
            public double apply(double... args) {
                return Math.max(args[1], Math.min(args[0], args[2]));
            }
        });

        // Performs linear interpolation between a and b using t (0.0 to 1.0)
        // Usage: lerp(a, b, t)
        // Example: lerp(10, 20, 0.5) -> returns 15
        functions.add(new Function("lerp", 3) {
            @Override
            public double apply(double... args) {
                return args[0] + (args[1] - args[0]) * args[2];
            }
        });

        // Computes the logarithm of a value with a custom base
        // Usage: logn(base, value)
        // Example: logn(2, 8) -> returns 3
        functions.add(new Function("logn", 2) {
            @Override
            public double apply(double... args) {
                return Math.log(args[1]) / Math.log(args[0]);
            }
        });

        // Rounds a value to the nearest whole number
        // Usage: round(x)
        // Example: round(4.6) -> returns 5
        functions.add(new Function("round", 1) {
            @Override
            public double apply(double... args) {
                return Math.round(args[0]);
            }
        });

        return functions;
    }

}
