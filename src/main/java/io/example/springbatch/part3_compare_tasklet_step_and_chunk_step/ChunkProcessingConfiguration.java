package io.example.springbatch.part3_compare_tasklet_step_and_chunk_step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : choi-ys
 * @date : 2021/07/31 6:25 오후
 * @apiNote :
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ChunkProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkProcessingJob(){
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.chunkBaseStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step chunkBaseStep(@Value("#{jobParameters[chunkSize]}") String value) {
        return stepBuilderFactory.get("chunkBaseStep")
                /**
                 * <I, O> SimpleStepBuilder<I, O> chunk(int chunkSize)
                 *  - I : ItemReader에서 조회 후 반환된 Input Type
                 *  - O : ItemProcessor에서 읽어서 반환하는 Output Type
                 */
                .<String, String>chunk(StringUtils.hasText(value) ? Integer.parseInt(value) : 29) // chunkSize -> 매 회 chunk Step이 처리 하는 items 개수
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    /**
     * Batch Job의 현재 Step 수행에 필요한 데이터를 조회
     *  -
     * @return data or null
     */
    private ItemReader<String> itemReader() {
        return new ListItemReader<>(getItems());
    }

    /**
     * ItemReader에서 조회한 데이터를 가공하거나, ItemWriter로 데이터 위임 여부를 결정
     * @return
     */
    private ItemProcessor<String, String> itemProcessor() {
        return item -> item + ", Spring Batch";
    }

    /**
     * ItemReader에서 조회되어, ItemProcessor에서 가공된 데이터를 저장
     * @return
     */
    private ItemWriter<String> itemWriter() {
        return items -> log.info("chunk item size : {}", items.size());
//        return items -> items.forEach(log::info);
    }

    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + " Hello");
        }
        return items;
    }
}
