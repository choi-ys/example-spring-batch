package io.example.springbatch.part1_hello_batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class HelloConfiguration {

    /**
     * batch의 실행 단위인 Job 생성 부
     */
    private final JobBuilderFactory jobBuilderFactory;
    /**
     * Job의 실행 단위인 Step 생성 부
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Job : Batch의 실행 단위 중 하나
     * JobBuilderFactory를 이용한 Job 생성 시 구성 요소
     *  - get() : Batch의 이름
     *    - Spring Batch를 실행 할 수 있는 key
     *  - incrementer() : Batch의 실행 단위
     *    - RunIdIncrementer() : Job실행 시 파라미터 ID를 자동으로 생성하여 실행 단위를 구분(Unique)
     *  - start() : Job실행 시 최초로 실행될 Step을 설정
     * @return Batch의 이름과 실행 단위, 실행 Step이 설정된 Job
     */
    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
                .incrementer(new RunIdIncrementer())
                .start(this.helloStep())
                .build();
    }

    /**
     * Step : Job의 실행 단위
     *  - get() : Step의 이름
     *  - tasklet() : step의 실행 단위 중 하나(tasklet, chunk)
     *    - tasklet : 작업을 한번에 실행 하는 단위
     *    - chunk : 작업을 여러번에 나누어 실행 하는 단위
     * @return
     */
    @Bean
    public Step helloStep() {
        return stepBuilderFactory.get("helloStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("hello spring batch");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
