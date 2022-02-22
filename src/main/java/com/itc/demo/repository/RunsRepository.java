package com.itc.demo.repository;

import com.itc.demo.entities.Run;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunsRepository extends MongoRepository<Run, String > {

}
