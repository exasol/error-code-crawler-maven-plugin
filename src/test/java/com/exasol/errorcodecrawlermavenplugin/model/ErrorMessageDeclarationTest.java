package com.exasol.errorcodecrawlermavenplugin.model;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class ErrorMessageDeclarationTest {
    @Test
    void test() {
        EqualsVerifier.simple().forClass(ErrorMessageDeclaration.class).verify();
    }
}
