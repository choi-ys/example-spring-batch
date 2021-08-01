package io.example.springbatch.part5_item_writer;

import io.example.springbatch.part4_external_repository_reader_job.CustomItemReader;
import io.example.springbatch.part4_external_repository_reader_job.domain.PersonDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : arura
 * @date : 2021-08-02 오전 4:23
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class FlatFileItemWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileItemWriterJob() throws Exception {
        return jobBuilderFactory.get("flatFileItemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.flatFileItemWriterStep())
                .build();
    }

    @Bean
    public Step flatFileItemWriterStep() throws Exception {
        return stepBuilderFactory.get("flatFileItemWriterStep")
                .<PersonDto, PersonDto>chunk(10)
                .reader(itemReader())
                .writer(csvFileItemWriter())
                .build();
    }

    private ItemWriter csvFileItemWriter() throws Exception {
        // csv파일에 작성할 feild 추출
        BeanWrapperFieldExtractor<PersonDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"id", "name", "age", "address"});
        
        // 각 필드의 데이터를 하나의 라인에 작성하기 위한 구분 값 설정
        DelimitedLineAggregator<PersonDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<PersonDto> csvFileItemWriter = new FlatFileItemWriterBuilder<PersonDto>()
                .name("csvFileItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test-output.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("id,이름,나이,주소"))
                .footerCallback(writer -> writer.write("-----------------\n"))
                .append(true)
                .build();
        csvFileItemWriter.afterPropertiesSet();

        return csvFileItemWriter;
    }

    public ItemReader itemReader(){
        return new CustomItemReader(getItems());
    }

    private List getItems() {
        List<PersonDto> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new PersonDto(
                    i+1,
                    "test name" +i,
                    30+i,
                    "test address"
            ));
        }
        return items;
    }
}