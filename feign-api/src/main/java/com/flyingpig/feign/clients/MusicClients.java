package com.flyingpig.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("music-service")
@RequestMapping("/musics")
public interface MusicClients {

}
