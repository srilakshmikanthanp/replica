package com.srilakshmikanthanp.replica.integration;

import com.srilakshmikanthanp.replica.integration.database.DatabaseConfig;
import com.srilakshmikanthanp.replica.integration.event.EventConfig;
import com.srilakshmikanthanp.replica.integration.inbox.InboxConfig;
import com.srilakshmikanthanp.replica.integration.outbox.OutboxConfig;
import com.srilakshmikanthanp.replica.integration.outbox.backfill.BackfillConfig;
import com.srilakshmikanthanp.replica.integration.station.StationConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DatabaseConfig.class, EventConfig.class, StationConfig.class, OutboxConfig.class, BackfillConfig.class, InboxConfig.class})
public class IntegrationTestConfig {
}
