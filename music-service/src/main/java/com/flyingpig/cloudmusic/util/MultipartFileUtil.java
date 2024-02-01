package com.flyingpig.cloudmusic.util;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileUtil {
    public static boolean isMusicFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        //并没有进行Mine类型检验
//        String contentType = file.getContentType();
//
//        if (contentType != null && contentType.startsWith("audio/")) {
//            return true;
//        }
        String[] parts = fileName.split("\\.");
        if (parts.length > 0) {
            String extension = parts[parts.length - 1];
            return "mp3".equalsIgnoreCase(extension) || "wav".equalsIgnoreCase(extension)
                    || "ogg".equalsIgnoreCase(extension) || "m4a".equalsIgnoreCase(extension)
                    || "flac".equalsIgnoreCase(extension) || "wma".equalsIgnoreCase(extension);
        }

        return false;
    }

    public static boolean isImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String[] parts = fileName.split("\\.");
        if (parts.length > 0) {
            String extension = parts[parts.length - 1];
            return "jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)
                    || "png".equalsIgnoreCase(extension) || "gif".equalsIgnoreCase(extension);
        }
        return false;
    }
}
