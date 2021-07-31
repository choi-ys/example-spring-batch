package io.example.springbatch.part4_custom_reader_with_pojo;

import lombok.Getter;

@Getter
public class Person {

    private int id;
    private String name;
    private int age;
    private String address;

    public Person(int id, String name, int age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
}
