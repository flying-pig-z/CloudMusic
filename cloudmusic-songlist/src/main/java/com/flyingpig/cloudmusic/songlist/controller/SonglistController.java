package com.flyingpig.cloudmusic.songlist.controller;


import com.flyingpig.cloudmusic.songlist.service.SonglistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/songlists")
public class SonglistController {
    @Autowired
    private SonglistService songlistService;



}
