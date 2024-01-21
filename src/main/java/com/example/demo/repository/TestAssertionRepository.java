package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestAssertion;

import jakarta.transaction.Transactional;

@Repository
public interface TestAssertionRepository extends CrudRepository<TestAssertion, Integer>{
	public List<TestAssertion> findByTest_id(int test_id);
	/**
	 * Delete test parameters whose the specified test has.
	 * @param test_id Test ID
	 */
	@Transactional
	public void deleteBytest_id(int test_id);
}
