package io.example.springbatch.part4_external_repository_reader_job.repository;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonEntityRepository extends JpaRepository<PersonEntity, Long> {

    Page<PersonEntity> findByAgeBefore(int age, Pageable pageable);
}
