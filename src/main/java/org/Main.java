package org;

import java.util.*;
import java.util.stream.Stream;


public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        final char letterTemplate = 'R';

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                List<Character> templateLetters = new ArrayList<>();

                for (int j = 0; j < route.length(); j++) {
                    char letter = route.charAt(j);

                    if (letter == letterTemplate) {
                        templateLetters.add(letter);
                    }
                }

                final int templateLetterSize = templateLetters.size();

                if (templateLetterSize > 0) {
                    synchronized (sizeToFreq) {
                        if (sizeToFreq.containsKey(templateLetterSize)) {
                            sizeToFreq.put(templateLetterSize, sizeToFreq.get(templateLetterSize) + 1);
                        } else {
                            sizeToFreq.put(templateLetterSize, 1);
                        }
                    }
                }
            }).start();
        }

        List<Map.Entry<Integer, Integer>> sizeToFreqList = sizeToFreq
            .entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
            .toList();

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                System.out.println("Самое частое количество повторений " + sizeToFreqList.get(i).getKey() + " (встретилось " + sizeToFreqList.get(i).getValue() + " раз)");
                System.out.println("Другие размеры:");
            } else {
                System.out.println("- " + sizeToFreqList.get(i).getKey() + " (" + sizeToFreqList.get(i).getValue() + " раз)");
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}