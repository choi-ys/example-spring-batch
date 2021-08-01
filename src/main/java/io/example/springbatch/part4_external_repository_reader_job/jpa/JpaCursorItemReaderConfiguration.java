package io.example.springbatch.part4_external_repository_reader_job.jpa;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static io.example.springbatch.part4_external_repository_reader_job.PersonItemWriter.personEntityItemWriter;

/**
 * @author : arura
 * @date : 2021-08-01 오전 5:00
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JpaCursorItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jpaCursorItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jpaCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaCursorItemReaderStep() throws Exception {
        return stepBuilderFactory.get("jpaCursorItemReaderStep")
                .<PersonEntity, PersonEntity>chunk(10)
                .reader(jpaCursorItemReader())
                .writer(personEntityItemWriter())
                .build();
    }

    private JpaCursorItemReader jpaCursorItemReader() throws Exception {
        JpaCursorItemReader<PersonEntity> personJpaCursorItemReader = new JpaCursorItemReaderBuilder<PersonEntity>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from PersonEntity p")
                .build();

        personJpaCursorItemReader.afterPropertiesSet();
        return personJpaCursorItemReader;
    }
}