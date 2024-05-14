package com.signicat.dev.repository.test;

import com.signicat.dev.domain.entity.UploadStatistic;
import com.signicat.dev.repository.UploadStatisticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UploadStatisticRepositoryTest {

    @Autowired
    private UploadStatisticRepository uploadStatisticRepository;

    @Test
    public void whenFindByIpAddressAndDate_thenReturnUploadStatistic() {
        // given
        UploadStatistic uploadStatistic = new UploadStatistic();
        uploadStatistic.setIpAddress("192.168.1.1");
        uploadStatistic.setDate(LocalDate.now());
        uploadStatistic.setFileCount(2);
        uploadStatisticRepository.save(uploadStatistic);

        // when
        Optional<UploadStatistic> found = uploadStatisticRepository
                .findByIpAddressAndDate(uploadStatistic.getIpAddress(),
                        uploadStatistic.getDate());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getIpAddress()).isEqualTo(uploadStatistic.getIpAddress());
        assertThat(found.get().getDate()).isEqualTo(uploadStatistic.getDate());
        assertThat(found.get().getFileCount()).isEqualTo(uploadStatistic.getFileCount());
    }

    @Test
    public void whenFindByIpAddressAndDate_thenReturnNull() {
        // given
        UploadStatistic uploadStatistic = new UploadStatistic();
        uploadStatistic.setIpAddress("192.168.1.2");
        uploadStatistic.setDate(LocalDate.now());
        uploadStatistic.setFileCount(1);
        uploadStatisticRepository.save(uploadStatistic);

        // when
        Optional<UploadStatistic> found = uploadStatisticRepository
                .findByIpAddressAndDate("192.168.1.1", LocalDate.now());

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void whenFindByNullIpAddressAndDate_thenReturnNull() {
        // given
        UploadStatistic uploadStatistic = new UploadStatistic();
        uploadStatistic.setIpAddress("192.168.1.2");
        uploadStatistic.setDate(LocalDate.now());
        uploadStatisticRepository.save(uploadStatistic);

        // when
        Optional<UploadStatistic> found = uploadStatisticRepository
                .findByIpAddressAndDate(null, null);

        // then
        assertThat(found).isEmpty();
    }

}