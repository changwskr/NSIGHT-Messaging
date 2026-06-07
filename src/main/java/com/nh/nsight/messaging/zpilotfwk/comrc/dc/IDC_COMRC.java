package com.nh.nsight.messaging.zpilotfwk.comrc.dc;

import com.nh.nsight.messaging.zpilotfwk.comrc.dc.dto.SpComrc7201BIZDDTO;

import java.util.List;

public interface IDC_COMRC {

    SpComrc7201BIZDDTO create(SpComrc7201BIZDDTO dto);

    SpComrc7201BIZDDTO get(Long id);

    List<SpComrc7201BIZDDTO> search(SpComrc7201BIZDDTO condition);

    long count(SpComrc7201BIZDDTO condition);
}
