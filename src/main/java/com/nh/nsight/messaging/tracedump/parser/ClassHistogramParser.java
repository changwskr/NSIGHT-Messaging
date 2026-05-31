package com.nh.nsight.messaging.tracedump.parser;

import com.nh.nsight.messaging.tracedump.model.ClassHistogramSnapshot;
import com.nh.nsight.messaging.tracedump.model.EvidenceFile;
import com.nh.nsight.messaging.tracedump.model.EvidenceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ClassHistogramParser {

    private static final Pattern LINE = Pattern.compile("^\\s*\\d+:\\s+(\\d+)\\s+(\\d+)\\s+(.+)$");

    public List<ClassHistogramSnapshot> parse(List<EvidenceFile> files) {
        return files.stream()
                .filter(f -> f.type() == EvidenceType.CLASS_HISTOGRAM)
                .map(this::parseOne)
                .toList();
    }

    private ClassHistogramSnapshot parseOne(EvidenceFile file) {
        List<ClassHistogramSnapshot.HistogramEntry> entries = new ArrayList<>();
        for (String raw : file.content().split("\n")) {
            Matcher matcher = LINE.matcher(raw);
            if (matcher.find()) {
                entries.add(new ClassHistogramSnapshot.HistogramEntry(
                        matcher.group(3).trim(),
                        Long.parseLong(matcher.group(1)),
                        Long.parseLong(matcher.group(2))
                ));
            }
        }
        entries.sort(Comparator.comparingLong(ClassHistogramSnapshot.HistogramEntry::bytes).reversed());
        return new ClassHistogramSnapshot(file.fileName(), entries.stream().limit(15).toList());
    }
}
