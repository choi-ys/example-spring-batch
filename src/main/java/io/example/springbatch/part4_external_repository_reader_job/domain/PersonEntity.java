package io.example.springbatch.part4_external_repository_reader_job.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "person_tb"
)
@SequenceGenerator(
        name = "PERSON_ENTITY_SEQ_GENERATOR",
        sequenceName = "PERSON_ENTITY_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PersonEntity {

    @Id
    @GeneratedValue(generator = "PERSON_ENTITY_SEQ_GENERATOR")
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "age", nullable = false, length = 50)
    private int age;

    @Column(name = "address", nullable = false, length = 50)
    private String address;
}
