package com.gallery.fineart.mfineart.service.s3;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface S3Service {

    String uploadFile(MultipartFile file) throws IOException;

    String getFileUrl(String fileName);

    List<String> listFilesWithPrefix(String prefix);
}
