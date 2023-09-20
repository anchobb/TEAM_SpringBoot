package com.example.demo.domain.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class QnARepositoryTest {

    @Autowired
    private QnARepository boardRepository;

    @Test
    public void test1(){
           int count =  boardRepository.countWhereContentKeyword("a");
           System.out.println("count : " + count);
    }


}