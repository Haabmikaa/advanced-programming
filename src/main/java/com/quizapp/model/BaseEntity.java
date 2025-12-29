package com.quizapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base entity class with common fields
 * All model classes extend this base class
 */
public abstract class BaseEntity implements Serializable {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}