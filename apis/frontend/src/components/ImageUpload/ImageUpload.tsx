import React, { useState, useRef } from 'react';
import { Icon } from '@iconify/react';
import './ImageUpload.css';

interface ImageUploadProps {
    value?: string | string[];
    onChange: (files: File[] | string[]) => void;
    multiple?: boolean;
    maxFiles?: number;
    accept?: string;
    placeholder?: string;
}

const ImageUpload: React.FC<ImageUploadProps> = ({
    value = [],
    onChange,
    multiple = false,
    maxFiles = 5,
    accept = "image/*",
    placeholder = "Clique para adicionar imagens ou arraste aqui"
}) => {
    const [dragOver, setDragOver] = useState(false);
    const [previews, setPreviews] = useState<string[]>(
        Array.isArray(value) ? value : value ? [value] : []
    );
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleFileSelect = (files: FileList | null) => {
        if (!files) return;

        const fileArray = Array.from(files);
        const validFiles = fileArray.filter(file => 
            file.type.startsWith('image/') && file.size <= 5 * 1024 * 1024 // 5MB max
        );

        if (validFiles.length === 0) {
            alert('Por favor, selecione apenas imagens válidas (máximo 5MB cada).');
            return;
        }

        const currentCount = previews.length;
        const newFiles = validFiles.slice(0, maxFiles - currentCount);

        if (newFiles.length < validFiles.length) {
            alert(`Máximo ${maxFiles} imagens permitidas. Apenas ${newFiles.length} foram adicionadas.`);
        }

        // Criar previews
        const newPreviews: string[] = [];
        newFiles.forEach(file => {
            const reader = new FileReader();
            reader.onload = (e) => {
                newPreviews.push(e.target?.result as string);
                if (newPreviews.length === newFiles.length) {
                    const updatedPreviews = [...previews, ...newPreviews];
                    setPreviews(updatedPreviews);
                    onChange(newFiles);
                }
            };
            reader.readAsDataURL(file);
        });
    };

    const handleDrop = (e: React.DragEvent) => {
        e.preventDefault();
        setDragOver(false);
        handleFileSelect(e.dataTransfer.files);
    };

    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault();
        setDragOver(true);
    };

    const handleDragLeave = (e: React.DragEvent) => {
        e.preventDefault();
        setDragOver(false);
    };

    const removeImage = (index: number) => {
        const newPreviews = previews.filter((_, i) => i !== index);
        setPreviews(newPreviews);
        onChange(newPreviews);
    };

    const openFileDialog = () => {
        fileInputRef.current?.click();
    };

    return (
        <div className="image-upload">
            <div
                className={`upload-area ${dragOver ? 'drag-over' : ''}`}
                onDrop={handleDrop}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onClick={openFileDialog}
            >
                <Icon icon="material-symbols:cloud-upload" style={{ fontSize: '3rem' }} />
                <p>{placeholder}</p>
                <small>
                    {multiple ? `Máximo ${maxFiles} imagens` : 'Uma imagem'} • 
                    Formatos: JPG, PNG, GIF • Máximo 5MB cada
                </small>
            </div>

            <input
                ref={fileInputRef}
                type="file"
                accept={accept}
                multiple={multiple}
                onChange={(e) => handleFileSelect(e.target.files)}
                style={{ display: 'none' }}
            />

            {previews.length > 0 && (
                <div className="image-previews">
                    {previews.map((preview, index) => (
                        <div key={index} className="image-preview">
                            <img src={preview} alt={`Preview ${index + 1}`} />
                            <button
                                type="button"
                                className="remove-image"
                                onClick={(e) => {
                                    e.stopPropagation();
                                    removeImage(index);
                                }}
                            >
                                <Icon icon="material-symbols:close" />
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ImageUpload;
