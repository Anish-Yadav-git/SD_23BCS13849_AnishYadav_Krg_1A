package com.filestorage.backend.repository;

import com.filestorage.backend.model.Folder;
import com.filestorage.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserAndParentFolderIsNull(User user);
    List<Folder> findByUserAndParentFolder(User user, Folder parentFolder);
}
