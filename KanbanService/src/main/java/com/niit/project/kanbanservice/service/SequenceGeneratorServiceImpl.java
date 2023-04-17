package com.niit.project.kanbanservice.service;

import com.niit.project.kanbanservice.domain.DbSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

@Service
public class SequenceGeneratorServiceImpl implements SequenceGeneratorService {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public int getSequenceNumber(String sequenceName) {
        // get sequence number
        Query query = new Query(Criteria.where("id").is(sequenceName));
        // update the sequence number
        Update update = new Update().inc("sequence",1);
        // find and modify sequence number
        DbSequence counter = mongoOperations.findAndModify(query,
                update,
                options().returnNew(true).upsert(true),
                DbSequence.class);
        return !Objects.isNull(counter) ? counter.getSequence() : 1;
    }
}
