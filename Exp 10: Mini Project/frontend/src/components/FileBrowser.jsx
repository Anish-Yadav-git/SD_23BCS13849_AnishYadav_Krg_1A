import React, { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import UploadModal from './UploadModal';
import CreateFolderModal from './CreateFolderModal';

const FileBrowser = () => {
    const [folders, setFolders] = useState([]);
    const [files, setFiles] = useState([]);
    const [currentFolderId, setCurrentFolderId] = useState(null);
    const [path, setPath] = useState([{ id: null, name: 'My Drive' }]);
    
    const [isUploadOpen, setIsUploadOpen] = useState(false);
    const [isFolderOpen, setIsFolderOpen] = useState(false);
    const [loading, setLoading] = useState(true);

    const fetchData = async () => {
        setLoading(true);
        try {
            const params = currentFolderId ? { parentFolderId: currentFolderId } : {};
            const [foldersRes, filesRes] = await Promise.all([
                api.get('/folders', { params }),
                api.get('/files', { params: currentFolderId ? { folderId: currentFolderId } : {} })
            ]);
            setFolders(foldersRes.data);
            setFiles(filesRes.data);
        } catch (error) {
            console.error("Failed to fetch data", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [currentFolderId]);

    const navigateToFolder = (folderId, folderName) => {
        setCurrentFolderId(folderId);
        setPath([...path, { id: folderId, name: folderName }]);
    };

    const navigateUp = (index) => {
        const newPath = path.slice(0, index + 1);
        setPath(newPath);
        setCurrentFolderId(newPath[newPath.length - 1].id);
    };

    const handleDeleteFolder = async (e, id) => {
        e.stopPropagation();
        if(window.confirm("Are you sure you want to delete this folder and all its contents?")) {
            try {
                await api.delete(`/folders/${id}`);
                fetchData();
            } catch (err) {
                alert("Failed to delete folder");
            }
        }
    };

    const handleDeleteFile = async (e, id) => {
        e.stopPropagation();
        if(window.confirm("Are you sure you want to delete this file?")) {
            try {
                await api.delete(`/files/${id}`);
                fetchData();
            } catch (err) {
                alert("Failed to delete file");
            }
        }
    };

    const handleDownload = async (e, id, name) => {
        e.stopPropagation();
        try {
            const response = await api.get(`/files/download/${id}`, { responseType: 'blob' });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', name);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        } catch (err) {
            alert("Failed to download file");
        }
    };

    const formatSize = (bytes) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };

    return (
        <div>
            <div className="toolbar">
                <div className="breadcrumbs">
                    {path.map((item, index) => (
                        <React.Fragment key={item.id || 'root'}>
                            <span 
                                className={`breadcrumb-item ${index === path.length - 1 ? 'breadcrumb-active' : ''}`}
                                onClick={() => navigateUp(index)}
                            >
                                {item.name}
                            </span>
                            {index < path.length - 1 && <span className="breadcrumb-separator">/</span>}
                        </React.Fragment>
                    ))}
                </div>
                <div className="actions">
                    <button className="btn btn-outline" onClick={() => setIsFolderOpen(true)}>
                        + New Folder
                    </button>
                    <button className="btn btn-primary" onClick={() => setIsUploadOpen(true)}>
                        + Upload File
                    </button>
                </div>
            </div>

            {loading ? (
                <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>Loading...</div>
            ) : (
                <div className="grid-view">
                    {folders.map(folder => (
                        <div key={`folder-${folder.id}`} className="item-card" onClick={() => navigateToFolder(folder.id, folder.name)}>
                            <div className="item-icon folder-icon">📁</div>
                            <div className="item-name" title={folder.name}>{folder.name}</div>
                            <div className="item-meta">Folder</div>
                            <div className="item-actions">
                                <button className="btn btn-danger" onClick={(e) => handleDeleteFolder(e, folder.id)}>Delete</button>
                            </div>
                        </div>
                    ))}

                    {files.map(file => (
                        <div key={`file-${file.id}`} className="item-card">
                            <div className="item-icon">📄</div>
                            <div className="item-name" title={file.name}>{file.name}</div>
                            <div className="item-meta">{formatSize(file.size)}</div>
                            <div className="item-actions">
                                <button className="btn btn-primary" onClick={(e) => handleDownload(e, file.id, file.name)}>Download</button>
                                <button className="btn btn-danger" onClick={(e) => handleDeleteFile(e, file.id)}>Delete</button>
                            </div>
                        </div>
                    ))}

                    {folders.length === 0 && files.length === 0 && (
                        <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
                            This folder is empty
                        </div>
                    )}
                </div>
            )}

            <UploadModal 
                isOpen={isUploadOpen} 
                onClose={() => setIsUploadOpen(false)} 
                currentFolderId={currentFolderId}
                onUploadSuccess={fetchData}
            />

            <CreateFolderModal 
                isOpen={isFolderOpen} 
                onClose={() => setIsFolderOpen(false)} 
                currentFolderId={currentFolderId}
                onCreateSuccess={fetchData}
            />
        </div>
    );
};

export default FileBrowser;
