package com.itc.demo.repository;

import com.itc.demo.entities.Runner;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunnerRepository extends MongoRepository<Runner, String> {

}
