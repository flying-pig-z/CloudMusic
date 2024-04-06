package com.flyingpig.cloudmusic.songlist.controller;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class TestController {
    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody String userInput) {
        if (userInput.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User input cannot be empty");
        }

        // 构造请求体
        String requestBody = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + userInput + "\"}], \"stream\": true}";

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer YOUR_OPENAI_API_KEY"); // 替换成你的 OpenAI API 密钥

        // 发送 POST 请求到目标 API
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = new RestTemplate().exchange("https://api.secant.top", HttpMethod.POST, requestEntity, String.class);

        return responseEntity;
    }

}
