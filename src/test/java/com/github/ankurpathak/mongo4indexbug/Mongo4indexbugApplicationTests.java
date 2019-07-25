package com.github.ankurpathak.mongo4indexbug;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(initializers = {Mongo4indexbugApplicationTests.Initializer.class})

public class Mongo4indexbugApplicationTests {

    @ClassRule
    public static MongoDbContainer mongo = new MongoDbContainer();

    @Autowired
    private INameRepository nameRepository;

    @Test(expected = DuplicateKeyException.class)
    public void contextLoads() {
        Name name = new Name("Ankur");
        nameRepository.save(name);
        name.setId(null);
        nameRepository.save(name);
        name.setId(null);
        nameRepository.save(name);
    }





    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    String.format("spring.data.mongodb.uri=mongodb://%s:%d/test", mongo.getContainerIpAddress(), mongo.getPort())
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
