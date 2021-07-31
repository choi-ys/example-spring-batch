package io.example.springbatch.part3_compare_tasklet_step_and_chunk_step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
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
public class TaskletLikeChunkProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskletListChunkProcessingJob(){
        return jobBuilderFactory.get("taskletListChunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskBaseStep())
                .build();
    }

    private Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet(null))
                .build();
    }

    @StepScope
    private Tasklet tasklet(@Value("#{jobParameters[chunkSize]}") String value) {
        List<String> items = getItems();
        return ((contribution, chunkContext) -> {
            StepExecution stepExecution = contribution.getStepExecution();

            String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
            String stepName = stepExecution.getStepName();

            int chunkSize = StringUtils.hasText(value) ? Integer.parseInt(value) : 10;
            int fromIndex = stepExecution.getReadCount();
            int nextIndex = fromIndex + chunkSize;
            int toIndex = nextIndex < items.size() ? nextIndex : items.size();

            if(fromIndex >= items.size()){
                return RepeatStatus.FINISHED;
            }

            items.subList(fromIndex, toIndex);
            stepExecution.setReadCount(toIndex);

            log.info("[{} : {}] chunkSize : {}, fromIndex : {}, toIndex : {}", jobName, stepName, chunkSize, fromIndex, toIndex);
            return RepeatStatus.CONTINUABLE;
        });
    }

    private List<String> getItems() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(i + " Hello");
        }
        return items;
    }
}
