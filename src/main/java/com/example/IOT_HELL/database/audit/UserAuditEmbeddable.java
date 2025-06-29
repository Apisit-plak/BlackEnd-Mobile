package com.example.IOT_HELL.database.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserAuditEmbeddable implements Serializable {

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_by")
    private String updateBy;



}
