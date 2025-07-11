package net.datafaker.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class FakeValuesTest {

    private static final String PATH = "address";
    private final FakeValues fakeValues = FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "address.yml", PATH));

    @Test
    void getAValueReturnsAValue() {
        assertThat(fakeValues.get(PATH)).isNotNull();
    }

    @Test
    void getAValueDoesNotReturnAValue() {
        assertThat(fakeValues.get("dog")).isNull();
    }

    @Test
    void getAValueWithANonEnglishFile() {
        FakeValues frenchFakeValues = FakeValues.of(FakeValuesContext.of(Locale.FRENCH));
        assertThat(frenchFakeValues.get(PATH)).isNotNull();
    }

    @Test
    void getAValueForHebrewLocale() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.get(PATH)).isNotNull();
    }

    @Test
    void correctPathForHebrewLanguage() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.getPaths()).containsExactly("he");
    }

    @Test
    void incorrectPathForHebrewLanguage() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.getPaths()).doesNotContain("iw");
    }

    @Test
    void correctLocale() {
        FakeValues fv = FakeValues.of(FakeValuesContext.of(new Locale("uk")));
        assertThat(fv.getLocale()).isEqualTo(new Locale("uk"));
    }

    @Test
    void getAValueFromALocaleThatCantBeLoaded() {
        FakeValues fakeValues = FakeValues.of(FakeValuesContext.of(new Locale("nothing")));
        assertThat(fakeValues.get(PATH)).isNull();
    }

    @ParameterizedTest
    @MethodSource("fakeValuesProvider")
    void checkEquals(FakeValues fv1, FakeValues fv2, boolean equals) {
        if (equals) {
            assertThat(fv1).isEqualTo(fv2);
        } else {
            assertThat(fv1).isNotEqualTo(fv2);
        }
    }

    static Stream<Arguments> fakeValuesProvider() throws MalformedURLException {
        Path tmp = Paths.get("tmp");
        return Stream.of(
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), FakeValues.of(FakeValuesContext.of(Locale.CANADA)), true),
            of(null, FakeValues.of(FakeValuesContext.of(Locale.CANADA)), false),
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), null, false),
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), null, false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH)), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", null)), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), true),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())), true),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", Paths.get("tmp2").toUri().toURL())), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())), false)
        );
    }
}
