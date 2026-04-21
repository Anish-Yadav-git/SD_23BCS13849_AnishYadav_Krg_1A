package com.filestorage.backend.service;

import com.filestorage.backend.model.Folder;
import com.filestorage.backend.model.User;
import com.filestorage.backend.repository.FolderRepository;
import com.filestorage.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    public Folder createFolder(String name, Long parentFolderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = new Folder();
        folder.setName(name);
        folder.setUser(user);

        if (parentFolderId != null) {
            Folder parent = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            
            if (!parent.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to create folder in this parent folder");
            }
            folder.setParentFolder(parent);
        }

        return folderRepository.save(folder);
    }

    public List<Folder> getFolders(Long parentFolderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (parentFolderId == null) {
            return folderRepository.findByUserAndParentFolderIsNull(user);
        } else {
            Folder parent = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            if (!parent.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to this folder");
            }
            return folderRepository.findByUserAndParentFolder(user, parent);
        }
    }

    public void deleteFolder(Long folderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (!folder.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this folder");
        }

        folderRepository.delete(folder);
    }
}
