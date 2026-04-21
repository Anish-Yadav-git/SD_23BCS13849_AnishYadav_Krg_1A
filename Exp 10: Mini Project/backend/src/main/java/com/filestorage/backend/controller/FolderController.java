package com.filestorage.backend.controller;

import com.filestorage.backend.model.Folder;
import com.filestorage.backend.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping
    public ResponseEntity<Folder> createFolder(@RequestBody FolderRequest folderRequest, Authentication authentication) {
        Folder folder = folderService.createFolder(folderRequest.getName(), folderRequest.getParentFolderId(), authentication.getName());
        return ResponseEntity.ok(folder);
    }

    @GetMapping
    public ResponseEntity<List<Folder>> getFolders(@RequestParam(required = false) Long parentFolderId, Authentication authentication) {
        List<Folder> folders = folderService.getFolders(parentFolderId, authentication.getName());
        return ResponseEntity.ok(folders);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolder(@PathVariable Long id, Authentication authentication) {
        folderService.deleteFolder(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
}

class FolderRequest {
    private String name;
    private Long parentFolderId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getParentFolderId() { return parentFolderId; }
    public void setParentFolderId(Long parentFolderId) { this.parentFolderId = parentFolderId; }
}
