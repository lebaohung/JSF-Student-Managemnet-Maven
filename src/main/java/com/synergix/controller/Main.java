package com.synergix.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(1, true);
        map.put(2, false);
        map.put(3, false);
        map.put(4, true);

        List<Integer> list = map.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        for (Integer i : list) {
            System.out.println(i);
        }
    }
}
