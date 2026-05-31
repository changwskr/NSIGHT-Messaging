package com.nh.nsight.messaging.tracedump.parser;

import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import com.nh.nsight.messaging.tracedump.model.NmtSnapshot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NmtParser {

    private static final Pattern LINE = Pattern.compile(
            "-\\s*(.+?)\\s+\\(reserved=(\\d+)KB, committed=(\\d+)KB\\)"
    );

    public List<NmtSnapshot> parse(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == EvidenceType.NMT)
                .map(this::parseOne)
                .toList();
    }

    private NmtSnapshot parseOne(EvidenceFile file) {
        List<NmtSnapshot.NmtLine> lines = new ArrayList<>();
        long totalCommitted = 0;
        Matcher matcher = LINE.matcher(file.content());
        while (matcher.find()) {
            long reserved = Long.parseLong(matcher.group(2));
            long committed = Long.parseLong(matcher.group(3));
            lines.add(new NmtSnapshot.NmtLine(matcher.group(1).trim(), committed, reserved));
            if ("Total".equalsIgnoreCase(matcher.group(1).trim())) {
                totalCommitted = committed;
            }
        }
        lines.sort(Comparator.comparingLong(NmtSnapshot.NmtLine::committedKb).reversed());
        List<NmtSnapshot.NmtLine> top = lines.stream().limit(8).toList();
        return new NmtSnapshot(file.fileName(), totalCommitted, top);
    }
}
