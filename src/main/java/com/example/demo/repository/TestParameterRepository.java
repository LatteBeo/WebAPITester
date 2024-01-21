package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestParameter;

import jakarta.transaction.Transactional;

@Repository
public interface TestParameterRepository extends CrudRepository<TestParameter, Integer>{
	public List<TestParameter> findByTest_id(int test_id);
	/**
	 * Delete test parameters whose the specified test has.
	 * @param test_id Test ID
	 */
	@Transactional
	public void deleteBytest_id(int test_id);
}
