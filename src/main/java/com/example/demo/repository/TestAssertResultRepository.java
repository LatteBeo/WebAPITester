package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestAssertResult;

@Repository
public interface TestAssertResultRepository extends CrudRepository<TestAssertResult, Integer> {
	public List<TestAssertResult> findByTestResultId(int testresultid);
}
