import React, { useState } from 'react';
import api from '../api/axiosConfig';

const UploadModal = ({ isOpen, onClose, currentFolderId, onUploadSuccess }) => {
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);

    if (!isOpen) return null;

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) return;
        setUploading(true);
        const formData = new FormData();
        formData.append('file', file);
        if (currentFolderId) {
            formData.append('folderId', currentFolderId);
        }

        try {
            await api.post('/files/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            onUploadSuccess();
            onClose();
            setFile(null);
        } catch (error) {
            console.error("Upload failed", error);
            alert("Upload failed");
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">Upload File</div>
                <div className="upload-area">
                    <div className="upload-icon">📁</div>
                    <p style={{ marginBottom: '1rem', color: 'var(--text-secondary)' }}>
                        Choose a file to upload to the current directory
                    </p>
                    <input type="file" onChange={handleFileChange} />
                </div>
                <div className="modal-footer">
                    <button className="btn btn-outline" onClick={onClose} disabled={uploading}>Cancel</button>
                    <button className="btn btn-primary" onClick={handleUpload} disabled={!file || uploading}>
                        {uploading ? 'Uploading...' : 'Upload'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UploadModal;
