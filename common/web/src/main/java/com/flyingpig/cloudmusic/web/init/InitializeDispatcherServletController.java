package com.flyingpig.cloudmusic.web.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.flyingpig.cloudmusic.web.config.WebAutoConfiguration.INITIALIZE_PATH;

@Slf4j(topic = "Initialize DispatcherServlet")
@RestController
public final class InitializeDispatcherServletController {

    @GetMapping(INITIALIZE_PATH)
    public void initializeDispatcherServlet() {
        log.info("Initialized the dispatcherServlet to improve the first response time of the interface...");
    }
}