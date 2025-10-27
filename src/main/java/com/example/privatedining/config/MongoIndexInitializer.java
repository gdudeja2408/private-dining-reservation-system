package com.example.privatedining.config;

import com.example.privatedining.model.Reservation;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.List;

@Configuration
public class MongoIndexInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoIndexInitializer.class);
    private final MongoTemplate mongoTemplate;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void ensureIndexes() {
        IndexOperations indexOps = mongoTemplate.indexOps(Reservation.class);

        try {
            indexOps.dropIndex("unique_confirmed_room_slot");
        } catch (Exception ignored) {
        }

        // Define compound index keys
        Document indexKeys = new Document()
                .append("roomId", 1)
                .append("reservationDate", 1)
                .append("startTime", 1)
                .append("endTime", 1);

        // Define MongoDB index options
        Document indexOptions = new Document()
                .append("unique", true)
                .append("name", "unique_confirmed_room_slot")
                .append("partialFilterExpression", new Document("status", "CONFIRMED"));

        // Create partial unique index manually
        Document createIndexCommand = new Document("createIndexes", "reservations")
                .append("indexes", List.of(
                        new Document()
                                .append("key", indexKeys)
                                .append("name", "unique_confirmed_room_slot")
                                .append("unique", true)
                                .append("partialFilterExpression", new Document("status", "CONFIRMED"))
                ));

        mongoTemplate.getDb().runCommand(createIndexCommand);
        log.info("✅ Ensured partial unique index for CONFIRMED reservations (roomId, date, start, end)");
    }
}
