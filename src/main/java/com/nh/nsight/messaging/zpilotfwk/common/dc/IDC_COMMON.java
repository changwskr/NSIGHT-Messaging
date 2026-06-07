package com.nh.nsight.messaging.zpilotfwk.common.dc;

import com.nh.nsight.messaging.zpilotfwk.common.dc.dto.SpCommon7001BIZDDTO;

import java.util.List;

/**
 * SP_COMMON 업무 데이터 DC(Data Component) 계약 — CRUD.
 */
public interface IDC_COMMON {

    SpCommon7001BIZDDTO create(SpCommon7001BIZDDTO dto);

    SpCommon7001BIZDDTO get(Long id);

    List<SpCommon7001BIZDDTO> search(SpCommon7001BIZDDTO condition);

    long count(SpCommon7001BIZDDTO condition);

    SpCommon7001BIZDDTO update(SpCommon7001BIZDDTO dto);

    void delete(Long id);
}
