package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Endpoint;

@Repository
public interface EndpointRepository extends CrudRepository<Endpoint, Integer>{

}
