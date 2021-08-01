package io.example.springbatch.part4_external_repository_reader_job.domain;

import lombok.Getter;

@Getter
public class PersonDto {

    private int id;
    private String name;
    private int age;
    private String address;

    public PersonDto(int id, String name, int age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
