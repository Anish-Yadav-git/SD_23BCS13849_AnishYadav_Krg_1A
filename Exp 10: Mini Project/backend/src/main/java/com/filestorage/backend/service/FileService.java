package com.filestorage.backend.service;

import com.filestorage.backend.model.FileEntity;
import com.filestorage.backend.model.Folder;
import com.filestorage.backend.model.User;
import com.filestorage.backend.repository.FileRepository;
import com.filestorage.backend.repository.FolderRepository;
import com.filestorage.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    private final Path fileStorageLocation;
    
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FileService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileEntity storeFile(MultipartFile file, Long folderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            if (!folder.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized to upload to this folder");
            }
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uuidName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(uuidName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(originalFileName);
            fileEntity.setType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setPath(uuidName); // We store the UUID name
            fileEntity.setUser(user);
            fileEntity.setFolder(folder);

            return fileRepository.save(fileEntity);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(Long fileId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileEntity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to access this file");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileEntity.getName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileEntity.getName(), ex);
        }
    }

    public List<FileEntity> getFiles(Long folderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (folderId == null) {
            return fileRepository.findByUserAndFolderIsNull(user);
        } else {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));
            if (!folder.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Unauthorized access to this folder");
            }
            return fileRepository.findByUserAndFolder(user, folder);
        }
    }

    public void deleteFile(Long fileId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileEntity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this file");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(fileEntity.getPath()).normalize();
            Files.deleteIfExists(filePath);
            fileRepository.delete(fileEntity);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + fileEntity.getName(), ex);
        }
    }
}
