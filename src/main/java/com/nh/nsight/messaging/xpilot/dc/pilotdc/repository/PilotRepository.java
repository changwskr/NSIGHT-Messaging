package com.nh.nsight.messaging.xpilot.dc.pilotdc.repository;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.Pilot;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;

import java.util.List;

public interface PilotRepository {

    Pilot findByPilotId(String pilotId);

    List<Pilot> findList(PilotDDTO criteria);

    int insert(Pilot pilot);

    boolean existsByPilotId(String pilotId);
}
