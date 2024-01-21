package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestSet;

@Repository
public interface TestSetRepository extends CrudRepository<TestSet, Integer>{

}
