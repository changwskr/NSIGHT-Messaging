package com.nh.nsight.messaging.zpilotfwk.order.dc;

import com.nh.nsight.messaging.zpilotfwk.order.dc.dto.SpOrder7101BIZDDTO;

import java.util.List;

public interface IDC_ORDER {

    SpOrder7101BIZDDTO create(SpOrder7101BIZDDTO dto);

    SpOrder7101BIZDDTO get(Long id);

    List<SpOrder7101BIZDDTO> search(SpOrder7101BIZDDTO condition);

    long count(SpOrder7101BIZDDTO condition);
}
