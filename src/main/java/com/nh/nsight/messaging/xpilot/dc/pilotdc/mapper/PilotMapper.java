package com.nh.nsight.messaging.xpilot.dc.pilotdc.mapper;

import com.nh.nsight.messaging.xpilot.dc.pilotdc.Pilot;
import com.nh.nsight.messaging.xpilot.dc.pilotdc.dto.PilotDDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PilotMapper {

    Pilot selectByPilotId(@Param("pilotId") String pilotId);

    List<Pilot> selectList(PilotDDTO criteria);

    int insert(Pilot pilot);

    boolean existsByPilotId(@Param("pilotId") String pilotId);
}
