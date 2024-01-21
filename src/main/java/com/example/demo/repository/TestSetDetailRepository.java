package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TestSetDetail;

import jakarta.transaction.Transactional;

@Repository
public interface TestSetDetailRepository extends CrudRepository<TestSetDetail, Integer>{
	@Transactional
	public void deleteBytestSetId(Integer testSetId);

}
