package com.exasol.errorcodecrawlermavenplugin.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ErrorIdentifierTest {
    @Test
    void testEquals() {
        EqualsVerifier.simple().forClass(ErrorIdentifier.class).verify();
    }

    @Test
    void testToString() {
        assertThat(new ErrorIdentifier(ErrorIdentifier.Type.W, "EXA", 1).toString(), equalTo("W-EXA-1"));
    }
}