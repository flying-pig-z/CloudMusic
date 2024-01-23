package com.flyingpig.cloudmusic.dataobject.message;

import com.flyingpig.cloudmusic.dataobject.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicUploadMessage implements Serializable {
    private Music music;
    private byte[] coverData;
    private byte[] musicData;

    public MusicUploadMessage(Music music, MultipartFile coverFile, MultipartFile musicFile) throws IOException {
        this.music = music;
        this.coverData = coverFile.getBytes();
        this.musicData = musicFile.getBytes();
    }

    public MultipartFile getCoverFile() {
        String coverFileName = generateRandomFileName("cover.jpg");
        return new MockMultipartFile(coverFileName, coverFileName, "image/jpeg", coverData);
    }

    public MultipartFile getMusicFile() {
        String musicFileName = generateRandomFileName("music.mp3");
        return new MockMultipartFile(musicFileName, musicFileName, "audio/mpeg", musicData);
    }

    private String generateRandomFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        return UUID.randomUUID().toString() + extension;
    }
}
