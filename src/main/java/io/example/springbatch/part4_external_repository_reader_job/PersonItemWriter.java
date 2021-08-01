package io.example.springbatch.part4_external_repository_reader_job;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonDto;
import io.example.springbatch.part4_external_repository_reader_job.domain.PersonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.stream.Collectors;

@Slf4j
public class PersonItemWriter {

    public static ItemWriter<PersonDto> personDtoItemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonDto::getName)
                .collect(Collectors.joining(", "))
        );
    }

    public static ItemWriter<PersonEntity> personEntityItemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonEntity::getName)
                .collect(Collectors.joining(", "))
        );
    }
}
