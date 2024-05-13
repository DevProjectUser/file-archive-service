package com.signicat.dev.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "upload_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "upload_date", columnDefinition = "DATE")
    private LocalDate date;

    @Column(name = "file_count")
    private int fileCount;
}
