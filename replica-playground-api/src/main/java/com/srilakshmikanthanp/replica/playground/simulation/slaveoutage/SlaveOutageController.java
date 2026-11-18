package com.srilakshmikanthanp.replica.playground.simulation.slaveoutage;

import com.srilakshmikanthanp.replica.playground.replication.Slave;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Slave
@RestController
@RequestMapping("/simulations/slave-outage")
@RequiredArgsConstructor
public class SlaveOutageController {
  private final SlaveOutage outage;

  @GetMapping
  SlaveOutageDto get() {
    return new SlaveOutageDto(outage.isDown());
  }

  @PutMapping
  SlaveOutageDto start() {
    outage.setDown(true);
    return get();
  }

  @DeleteMapping
  SlaveOutageDto end() {
    outage.setDown(false);
    return get();
  }
}
