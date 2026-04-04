package com.twintech.shl_tyar.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Driver document entity representing uploaded documents for driver applications.
 * Stores file metadata and references to the physical files.
 * 
 * Requirements: 2.1, 2.2
 */
@Entity
@Table(name = "driver_documents", indexes = {
    @Index(name = "idx_driver_documents_application_id", columnList = "application_id"),
    @Index(name = "idx_driver_documents_document_type", columnList = "document_type")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private DriverApplication application;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Soft delete timestamp. When set, the document is considered deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Check if the document is deleted (soft delete).
     * 
     * @return true if document is soft-deleted
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
}
