package com.flyingpig.cloudmusic.music.mq;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class SensitiveWordLoader {

    private static List<String> sensitiveWords = new ArrayList<>();

    // 初始化时加载敏感词
    @PostConstruct
    private void loadSensitiveWordsOnStartup() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("sensitive-words.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    sensitiveWords.add(line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load sensitive words", e);
        }
    }

    // 获取敏感词列表
    public static List<String> getSensitiveWords() {
        return Collections.unmodifiableList(sensitiveWords);
    }
}
