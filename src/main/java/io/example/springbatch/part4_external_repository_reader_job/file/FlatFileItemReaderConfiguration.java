package io.example.springbatch.part4_external_repository_reader_job.file;

import io.example.springbatch.part4_external_repository_reader_job.domain.PersonDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.stream.Collectors;

/**
 * @author : arura
 * @date : 2021-08-01 오전 2:50
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class FlatFileItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileItemReaderJob() throws Exception {
        return jobBuilderFactory.get("flatFileItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(this.flatFileItemReaderStep())
                .build();
    }

    @Bean
    public Step flatFileItemReaderStep() throws Exception {
        return stepBuilderFactory.get("flatFileItemReaderStep")
                .<PersonDto, PersonDto>chunk(3)
                .reader(this.csvFileItemReader())
                .writer(itemWriter())
                .build();
    }

    private DefaultLineMapper<PersonDto> getPersonDefaultLineMapper() {
        // csv 파일을 1줄씩 읽어 대상 객체와 매핑하는 Mapper
        DefaultLineMapper<PersonDto> personDefaultLineMapper = new DefaultLineMapper<PersonDto>();

        // csv 파일과의 항목과 매핑할 객체의 필드를 설정하기 위한 Tokenizer
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();

        // DelimitedLineTokenizer에 csv 필드와 매핑할 객체의 필드명을 설정(csv 필드의 순서와 setNames()에 명시한 순서로 매핑)
        delimitedLineTokenizer.setNames("id", "name", "age", "address");
        personDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

        personDefaultLineMapper.setFieldSetMapper(fieldSet -> {
            int id = fieldSet.readInt("id");
            String name = fieldSet.readString("name");
            int age = fieldSet.readInt("age");
            String address = fieldSet.readString("address");

            return new PersonDto(id, name, age, address);
        });
        return personDefaultLineMapper;
    }

    private FlatFileItemReader<PersonDto> csvFileItemReader() throws Exception {
        FlatFileItemReader<PersonDto> csvFileItemReader = new FlatFileItemReaderBuilder<PersonDto>()
                .name("csvFileItemReader")
                .encoding("UTF-8")
                .resource(new ClassPathResource("test.csv"))
                .linesToSkip(1) // csv파일의 첫번째 라인은 읽지 않음
                .lineMapper(getPersonDefaultLineMapper())
                .build();

        csvFileItemReader.afterPropertiesSet(); // ItemReader에 필요한 필수 설정값을 검증
        return csvFileItemReader;
    }

    private ItemWriter<PersonDto> itemWriter() {
        return items -> log.info("Result : {}", items.stream()
                .map(PersonDto::getName)
                .collect(Collectors.joining(", "))
        );
    }
}