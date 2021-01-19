package com.exasol.errorcodecrawlermavenplugin.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ErrorCodeTest {
    @Test
    void testEquals() {
        EqualsVerifier.simple().forClass(ErrorCode.class).verify();
    }

    @Test
    void testToString() {
        assertThat(new ErrorCode(ErrorCode.Type.W, "EXA", 1).toString(), equalTo("W-EXA-1"));
    }
}