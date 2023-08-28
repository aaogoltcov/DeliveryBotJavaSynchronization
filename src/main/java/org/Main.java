package org;

import java.util.*;


public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    static final private int threadSize = 1000;

    public static void main(String[] args) throws InterruptedException {
        final char letterTemplate = 'R';

        Thread summarizeThread = new Thread(() -> {
            synchronized (sizeToFreq) {
                while (!Thread.interrupted()) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }

                    List<Map.Entry<Integer, Integer>> sizeToFreqList = sizeToFreq
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                        .toList();

                    int cycleSize;

                    if (sizeToFreqList.size() < 5) {
                        cycleSize = sizeToFreqList.size();
                    } else {
                        cycleSize = 5;
                    }

                    for (int i = 0; i < cycleSize; i++) {
                        if (i == 0) {
                            System.out.println("Самое частое количество повторений " + sizeToFreqList.get(i).getKey() + " (встретилось " + sizeToFreqList.get(i).getValue() + " раз)");
                            System.out.println("Другие размеры:");
                        } else {
                            System.out.println("- " + sizeToFreqList.get(i).getKey() + " (" + sizeToFreqList.get(i).getValue() + " раз)");
                        }
                    }
                }
            }
        });

        summarizeThread.start();

        for (int k = 0; k < threadSize; k++) {
            Thread freqRoute = new Thread(() -> {
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
                        sizeToFreq.notify();
                    }
                }
            });

            freqRoute.start();
            freqRoute.join();
        }

        summarizeThread.interrupt();
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