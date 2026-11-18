package com.srilakshmikanthanp.replica.playground.simulation.slaveoutage;

import com.srilakshmikanthanp.replica.playground.replication.Slave;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slave
@Component
public class SlaveOutage {
  private final AtomicBoolean down = new AtomicBoolean();

  public boolean isDown() {
    return down.get();
  }

  public void setDown(boolean value) {
    down.set(value);
  }
}
