package com.nh.nsight.messaging.xpilot.dc.pilotdc.repository;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.Pilot;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.mapper.PilotMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PilotRepositoryImpl implements PilotRepository {

    private final PilotMapper pilotMapper;

    public PilotRepositoryImpl(PilotMapper pilotMapper) {
        this.pilotMapper = pilotMapper;
    }

    @Override
    public Pilot findByPilotId(String pilotId) {
        return pilotMapper.selectByPilotId(pilotId);
    }

    @Override
    public List<Pilot> findList(PilotDDTO criteria) {
        return pilotMapper.selectList(criteria);
    }

    @Override
    public int insert(Pilot pilot) {
        return pilotMapper.insert(pilot);
    }

    @Override
    public boolean existsByPilotId(String pilotId) {
        return pilotMapper.existsByPilotId(pilotId);
    }
}
