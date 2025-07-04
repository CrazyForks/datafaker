package net.datafaker.service;

import net.datafaker.service.files.EnFile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public final class FakeValuesGrouping implements FakeValuesInterface {
    private static final FakeValuesGrouping ENGLISH_FAKE_VALUE_GROUPING = new FakeValuesGrouping();
    private final Map<String, Collection<FakeValuesInterface>> fakeValues = new HashMap<>();

    static {
        EnFile.getFiles().forEach(file -> {
            ENGLISH_FAKE_VALUE_GROUPING.add(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, file.getFile(), file.getPath())));
        });
    }

    public FakeValuesGrouping() {
    }

    public FakeValuesGrouping(FakeValues values) {
        add(values);
    }

    public void add(FakeValuesInterface fakeValue) {
        if (fakeValue instanceof FakeValues) {
            ((FakeValues) fakeValue).getPaths().forEach(p ->
            fakeValues.computeIfAbsent(p, key -> new HashSet<>())
                .add(fakeValue));
        } else if (fakeValue instanceof FakeValuesGrouping) {
            fakeValues.putAll(((FakeValuesGrouping) fakeValue).fakeValues);
        } else {
            throw new RuntimeException(fakeValues.getClass().getName() + " not supported (please raise an issue)");
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map get(String key) {
        Map result = null;
        for (FakeValuesInterface fakeValues : fakeValues.getOrDefault(key, Collections.emptyList())) {
            if (result == null) {
                result = fakeValues.get(key);
            } else {
                final Map newResult = fakeValues.get(key);
                result.putAll(newResult);
            }
        }
        return result;
    }

    public static FakeValuesGrouping getEnglishFakeValueGrouping() {
        return ENGLISH_FAKE_VALUE_GROUPING;
    }
}
