package com.signicat.dev.repository;

import com.signicat.dev.domain.entity.UploadStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface UploadStatisticRepository extends JpaRepository<UploadStatistic, Long> {
    UploadStatistic findByIpAddressAndDate(String ipAddress, LocalDate date);
}
