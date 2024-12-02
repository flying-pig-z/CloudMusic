package com.flyingpig.cloudmusic.music.mq;

import com.flyingpig.cloudmusic.music.dataobject.entity.Music;
import com.flyingpig.cloudmusic.music.util.Trie;

import java.util.List;

public class SensitiveWordFilter {

    private Trie trie;

    public SensitiveWordFilter(List<String> sensitiveWords) {
        trie = new Trie();
        // 将所有敏感词插入到 Trie 树中
        for (String word : sensitiveWords) {
            trie.insert(word);
        }
    }

    // 检查歌曲信息中的敏感词
    public boolean hasSensitiveWords(Music music) {
        // 检查歌曲名称
        if (trie.containsSensitiveWord(music.getName())) {
            return true;
        }
        // 检查歌曲介绍
        if (music.getIntroduce() != null && trie.containsSensitiveWord(music.getIntroduce())) {
            return true;
        }
        // 检查歌手名称
        if (music.getSingerName() != null && trie.containsSensitiveWord(music.getSingerName())) {
            return true;
        }
        return false;
    }

    // 替换歌曲信息中的敏感词
    public String replaceSensitiveWords(String text) {
        for (int i = 0; i < text.length(); i++) {
            for (int j = i + 1; j <= text.length(); j++) {
                if (trie.containsSensitiveWord(text.substring(i, j))) {
                    text = text.substring(0, i) + "***" + text.substring(j);
                    break;
                }
            }
        }
        return text;
    }
}
