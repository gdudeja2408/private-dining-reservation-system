package com.example.privatedining.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class EmbeddedMongoConfig {

    private MongodExecutable mongodExecutable;
    private MongoClient mongoClient;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        int port = Network.getFreeServerPort();

        MongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();

        System.out.println("✅ Embedded MongoDB started on port " + port);

        mongoClient = MongoClients.create("mongodb://localhost:" + port);
        return new MongoTemplate(mongoClient, "private_dining_db");
    }


    @PreDestroy
    public void stopMongo() {
        if (mongoClient != null) mongoClient.close();
        if (mongodExecutable != null) mongodExecutable.stop();
        System.out.println("🛑 Embedded MongoDB stopped.");
    }
}
