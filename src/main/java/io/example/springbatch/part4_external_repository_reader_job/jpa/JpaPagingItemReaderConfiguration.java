package io.example.springbatch.part4_external_repository_reader_job.jpa;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오후 5:06
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JpaPagingItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jpaPagingItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jpaPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaPagingItemReaderStep() throws Exception {
        return stepBuilderFactory.get("jpaPagingItemReaderStep")
                .<PersonEntity, PersonEntity>chunk(2)
                .reader(jpaPagingItemReader())
                .writer(itemWriter())
                .build();
    }

    public JpaPagingItemReader<PersonEntity> jpaPagingItemReader() {
        JpaPagingItemReader<PersonEntity> jpaPagingItemReader = new JpaPagingItemReader<PersonEntity>();
        jpaPagingItemReader.setQueryString("select p from PersonEntity p where p.age < :age");

        HashMap<String, Object> map = new HashMap<>();
        map.put("age", 31);

        jpaPagingItemReader.setParameterValues(map);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(2);
        return jpaPagingItemReader;
    }

    private ItemWriter<PersonEntity> itemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonEntity::getName)
                .collect(Collectors.joining(", "))
        );
    }
}