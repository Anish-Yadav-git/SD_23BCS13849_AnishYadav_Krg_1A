package com.filestorage.backend.controller;

import com.filestorage.backend.model.FileEntity;
import com.filestorage.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileEntity> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId,
            Authentication authentication) {
        
        FileEntity fileEntity = fileService.storeFile(file, folderId, authentication.getName());
        return ResponseEntity.ok(fileEntity);
    }

    @GetMapping
    public ResponseEntity<List<FileEntity>> getFiles(
            @RequestParam(required = false) Long folderId,
            Authentication authentication) {
        List<FileEntity> files = fileService.getFiles(folderId, authentication.getName());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, Authentication authentication) {
        Resource resource = fileService.loadFileAsResource(id, authentication.getName());

        String contentType = "application/octet-stream";
        try {
            contentType = java.nio.file.Files.probeContentType(resource.getFile().toPath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id, Authentication authentication) {
        fileService.deleteFile(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
