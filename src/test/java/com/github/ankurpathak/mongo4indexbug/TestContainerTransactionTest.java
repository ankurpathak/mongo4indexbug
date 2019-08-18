package com.github.ankurpathak.mongo4indexbug;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Network;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {TestContainerTransactionTest.Initializer.class})
public class TestContainerTransactionTest {

    @ClassRule
    public static Network network = Network.newNetwork();

    @ClassRule
    public static MongoDbContainer mongo = new MongoDbContainer().
    withCommand("--replSet rs")
    ;





    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IUserService userService;



    @BeforeClass
    public static void setUpAll () throws Exception{
        mongo.execInContainer("/bin/bash", "-c", "mongo --eval 'printjson(rs.initiate())' --quiet");
        mongo.execInContainer("/bin/bash", "-c",
                "until mongo --eval \"printjson(rs.isMaster())\" | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done");
    }

    @Test
    public void testContainerTest() throws  Exception{

        assertThat(mongoTemplate).isNotNull();
    }

    @Test
    public void testConatinerTransaction(){
        userService.save(new User("Ankur Pathak", 12));
    }

    @Test
    @Transactional
    public void whenPerformMongoTransaction_thenSuccess() {
        userService.save(new User("John", 30));
        userService.save(new User("Ringo", 35));
        Query query = new Query().addCriteria(Criteria.where("name").is("John"));
        List<User> users = mongoTemplate.find(query, User.class);

        assertThat(users.size()).isEqualTo(1);
    }



    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    String.format("spring.data.mongodb.uri=mongodb://%s:%d/test", mongo.getContainerIpAddress(), mongo.getPort())
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
