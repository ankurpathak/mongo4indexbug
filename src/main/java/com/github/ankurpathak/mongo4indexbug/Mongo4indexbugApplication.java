package com.github.ankurpathak.mongo4indexbug;

import com.mongodb.bulk.BulkWriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Mongo4indexbugApplication {

    public static void main(String[] args) {
        SpringApplication.run(Mongo4indexbugApplication.class, args);
    }

}

@Component
class TestCLR implements CommandLineRunner{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        mongoTemplate.dropCollection("names");
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Name.class);
        Name nameTemp = new Name("Rama");
      //  List<Name> names = Arrays.asList(nameTemp, nameTemp, nameTemp);
        List<Name> names = Arrays.asList(nameTemp);
        ops.insert(names);

        try{
            BulkWriteResult hello = ops.execute();

            System.out.println();
        }catch (Exception ex){
            System.out.println();
        }

    }
}


@Document(collection = "names")
class Name {
    private String id;

    @Indexed(name = "nameIdx", unique = true, sparse = true)
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Name(String name) {
        this.name = name;
    }

    public Name() {
    }
}


interface INameRepository extends MongoRepository<Name, String>{

}
