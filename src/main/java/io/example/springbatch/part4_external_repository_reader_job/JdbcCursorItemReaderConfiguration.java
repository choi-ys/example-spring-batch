package io.example.springbatch.part4_external_repository_reader_job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오전 3:33
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JdbcCursorItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() throws Exception {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<PersonDto, PersonDto>chunk(10)
                .reader(jdbcCursorItemReader())
                .writer(itemWriter())
                .build();
    }

    private JdbcCursorItemReader jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader<PersonDto> personJdbcCursorItemReader = new JdbcCursorItemReaderBuilder<PersonDto>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT id, name, age, address FROM person_tb")
                .rowMapper((resultSet, rowNum) ->
                        new PersonDto(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        )
                )
                .build();
        personJdbcCursorItemReader.afterPropertiesSet();
        return personJdbcCursorItemReader;
    }

    private ItemWriter<PersonDto> itemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonDto::getName)
                .collect(Collectors.joining(", "))
        );
    }
}