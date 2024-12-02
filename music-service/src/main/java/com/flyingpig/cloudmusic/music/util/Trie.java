package com.flyingpig.cloudmusic.music.util;

import java.util.HashMap;
import java.util.Map;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // 向 Trie 树中插入敏感词
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
    }

    // 检查字符串中是否包含敏感词
    public boolean containsSensitiveWord(String text) {
        TrieNode node = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            node = node.children.get(c);
            if (node == null) {
                return false;
            }
            if (node.isEndOfWord) {
                return true;
            }
        }
        return false;
    }

    // Trie 节点
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
    }
}
