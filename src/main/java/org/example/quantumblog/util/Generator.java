package org.example.quantumblog.util;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;

@Service
public class Generator {
    private HashSet<Integer> generatedNumbers = new HashSet<>();

    //生成六位随机验证码
    public static String generateRandomCode() {
        Random random = new Random();
        int number = 100_000 + random.nextInt(900_000);
        return String.valueOf(number);
    }

    //ID生成器
    public int generateUniqueNumber(String username, long timestamp) {
        Random random = new Random(username.hashCode() + timestamp);
        int number;
        do {
            number = 1_000_000_0 + random.nextInt(9_000_000_0);
        } while (generatedNumbers.contains(number));
        generatedNumbers.add(number);
        return number;
    }
}
