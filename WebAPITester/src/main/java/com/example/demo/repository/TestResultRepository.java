package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestResult;

@Repository
public interface TestResultRepository extends CrudRepository<TestResult, Integer> {

}
