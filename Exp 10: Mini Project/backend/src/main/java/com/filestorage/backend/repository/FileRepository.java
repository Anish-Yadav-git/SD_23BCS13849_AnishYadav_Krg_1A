package com.filestorage.backend.repository;

import com.filestorage.backend.model.FileEntity;
import com.filestorage.backend.model.Folder;
import com.filestorage.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByUserAndFolderIsNull(User user);
    List<FileEntity> findByUserAndFolder(User user, Folder folder);
}
