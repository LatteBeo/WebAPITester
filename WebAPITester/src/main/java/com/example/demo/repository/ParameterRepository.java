package com.example.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Parameter;

@Repository
public interface ParameterRepository extends CrudRepository<Parameter, Integer> {
	/**
	 * Delete parameters of the specified API.
	 * @param api_id API ID
	 */
	@Transactional
	public void deleteByApi_id(Integer api_id);
	/**
	 * Get all parameters of the specified API.
	 * @param api_id
	 * @return
	 */
	public List<Parameter> findByApi_id(Integer api_id);
}