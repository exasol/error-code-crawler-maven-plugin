package com.exasol.errorcodecrawlermavenplugin.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ErrorCodeParserTest {
    private static final ErrorCodeParser ERROR_CODE_READER = new ErrorCodeParser();

    @Test
    void testReadValidCode() throws ErrorIdentifier.SyntaxException {
        assertThat(ERROR_CODE_READER.parse("E-EXA-1"), equalTo(new ErrorIdentifier(ErrorIdentifier.Type.E, "EXA", 1)));
    }

    @Test
    void testReadValidCodeWithSubTag() throws ErrorIdentifier.SyntaxException {
        assertThat(ERROR_CODE_READER.parse("F-EXA-E1-1"),
                equalTo(new ErrorIdentifier(ErrorIdentifier.Type.F, "EXA-E1", 1)));
    }

    @Test
    void testInvalidSyntax() {
        final ErrorIdentifier.SyntaxException exception = assertThrows(ErrorIdentifier.SyntaxException.class,
                () -> ERROR_CODE_READER.parse("Q-EXA-X"));
        assertThat(exception.getMessage(), equalTo("E-ECM-10: The error code 'Q-EXA-X' has an invalid format."));
    }

    @Test
    void testReadWrongType() {
        final ErrorIdentifier.SyntaxException exception = assertThrows(ErrorIdentifier.SyntaxException.class,
                () -> ERROR_CODE_READER.parse("Q-EXA-1"));
        assertThat(exception.getMessage(),
                equalTo("E-ECM-11: Illegal error code 'Q-EXA-1'. The codes must start with 'W-', 'E-' or 'F-'."));
    }
}
