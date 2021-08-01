package io.example.springbatch.part4_external_repository_reader_job.jdbc;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오전 3:33
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JdbcPagingItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingItemReaderJob() throws Exception {
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.jdbcPagingItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingItemReaderStep() throws Exception {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
                .<PersonDto, PersonDto>chunk(4)
                .reader(jdbcPagingItemReader())
                .writer(itemWriter())
                .build();
    }

    private JdbcPagingItemReader jdbcPagingItemReader() throws Exception {
        JdbcPagingItemReader<PersonDto> personJdbcPagingItemReader = new JdbcPagingItemReaderBuilder<PersonDto>()
                .pageSize(3) // 가져올 row 수
                .fetchSize(2)
                .name("jdbcPagingItemReader")
                .dataSource(dataSource)
                .rowMapper((resultSet, rowNum) ->
                        new PersonDto(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getInt(3),
                                resultSet.getString(4)
                        )
                )
                .queryProvider(pagingQueryProvider())
                .build();
        personJdbcPagingItemReader.afterPropertiesSet();
        return personJdbcPagingItemReader;
    }

    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, name, age, address");
        queryProvider.setFromClause("from person_tb");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    private ItemWriter<PersonDto> itemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonDto::getName)
                .collect(Collectors.joining(", "))
        );
    }
}