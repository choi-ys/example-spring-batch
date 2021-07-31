package io.example.springbatch.part4_custom_reader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오전 2:38
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class JavaObjectItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job javaItemReaderJob() {
        return jobBuilderFactory.get("javaItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.javaObjectItemReaderStep())
                .build();
    }

    @Bean
    public Step javaObjectItemReaderStep() {
        return stepBuilderFactory.get("javaObjectItemReaderStep")
                .<Person, Person>chunk(10)
                .reader(new CustomItemReader<>(getItem()))
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<Person> itemWriter() {
        return items -> log.info(items.stream()
                .map(Person::getName)
                .collect(Collectors.joining(", "))
        );
    }

    private List<Person> getItem() {
        List<Person> items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            items.add(new Person(i+1, "test name" + i, i+30, "test address"));
        }
        return items;
    }
}