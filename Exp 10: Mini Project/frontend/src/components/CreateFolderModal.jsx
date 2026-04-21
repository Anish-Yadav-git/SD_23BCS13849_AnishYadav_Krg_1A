import React, { useState } from 'react';
import api from '../api/axiosConfig';

const CreateFolderModal = ({ isOpen, onClose, currentFolderId, onCreateSuccess }) => {
    const [name, setName] = useState('');
    const [creating, setCreating] = useState(false);

    if (!isOpen) return null;

    const handleCreate = async () => {
        if (!name.trim()) return;
        setCreating(true);

        try {
            await api.post('/folders', {
                name: name.trim(),
                parentFolderId: currentFolderId
            });
            onCreateSuccess();
            onClose();
            setName('');
        } catch (error) {
            console.error("Create folder failed", error);
            alert("Create folder failed");
        } finally {
            setCreating(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">Create New Folder</div>
                <div className="input-group">
                    <label>Folder Name</label>
                    <input 
                        type="text" 
                        value={name} 
                        onChange={(e) => setName(e.target.value)} 
                        autoFocus
                    />
                </div>
                <div className="modal-footer">
                    <button className="btn btn-outline" onClick={onClose} disabled={creating}>Cancel</button>
                    <button className="btn btn-primary" onClick={handleCreate} disabled={!name.trim() || creating}>
                        {creating ? 'Creating...' : 'Create'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateFolderModal;
