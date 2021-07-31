package io.example.springbatch.part3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class TaskletProcessingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskProcessingJob(){
        return jobBuilderFactory.get("taskProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.taskBaseStep())
                .build();
    }

    private Step taskBaseStep() {
        return stepBuilderFactory.get("taskBaseStep")
                .tasklet(this.tasklet())
                .build();
    }

    private Tasklet tasklet() {
        return ((contribution, chunkContext) -> {
            List<String> items = getItems();
            items.forEach(log::info);
            return RepeatStatus.FINISHED;
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
