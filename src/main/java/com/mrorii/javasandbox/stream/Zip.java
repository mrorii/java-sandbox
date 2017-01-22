package com.mrorii.javasandbox.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Zip {

    public static void main(String[] args) {
        List<Integer> numbers1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> numbers2 = Arrays.asList(10, 20, 30, 40, 50);

        IntStream.range(0, Math.min(numbers1.size(), numbers2.size()))
                .flatMap(i -> IntStream.of(numbers1.get(i), numbers2.get(i)))
                .forEach(System.out::println);

        List<String> strings1 = Arrays.asList("foo", "bar", "baz");
        List<String> strings2 = Arrays.asList("hoge", "piyo", "fuga");

        IntStream.range(0, Math.min(strings1.size(), strings2.size()))
                .mapToObj(i -> Stream.of(strings1.get(i), strings2.get(i)))
                .flatMap(s -> s)
                .forEach(System.out::println);
    }

}
