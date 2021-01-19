package com.exasol.errorcodecrawlermavenplugin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.exasol.errorcodecrawlermavenplugin.model.ErrorCode;

class ErrorCodeReaderTest {

    private static final ErrorCodeReader ERROR_CODE_READER = new ErrorCodeReader();

    @Test
    void testReadValidCode() throws CrawlFailedException {
        assertThat(ERROR_CODE_READER.read("E-EXA-1", ""), equalTo(new ErrorCode(ErrorCode.Type.E, "EXA", 1)));
    }

    @Test
    void testReadValidCodeWithSubTag() throws CrawlFailedException {
        assertThat(ERROR_CODE_READER.read("F-EXA-E1-1", ""), equalTo(new ErrorCode(ErrorCode.Type.F, "EXA-E1", 1)));
    }

    @Test
    void testInvalidSyntax() {
        final CrawlFailedException exception = assertThrows(CrawlFailedException.class,
                () -> ERROR_CODE_READER.read("Q-EXA-X", "mySource:1"));
        assertThat(exception.getFinding().getMessage(),
                equalTo("E-ECM-10: The error code 'Q-EXA-X' has an invalid format. (mySource:1)"));
    }

    @Test
    void testReadWrongType() {
        final CrawlFailedException exception = assertThrows(CrawlFailedException.class,
                () -> ERROR_CODE_READER.read("Q-EXA-1", "mySource:1"));
        assertThat(exception.getFinding().getMessage(), equalTo(
                "E-ECM-11: Illegal error code 'Q-EXA-1'. The codes must start with 'W-', 'E-' or 'F-'. (mySource:1)"));
    }
}
