package com.exasol.errorcodecrawlermavenplugin.helper;

import com.exsol.errorcodemodel.ErrorMessageDeclaration;
import com.exsol.errorcodemodel.NamedParameter;

/**
 * This class provides helper methods for {@link ErrorMessageDeclaration}
 */
public class ErrorMessageDeclarationHelper {


    /**
     * Copies {@link ErrorMessageDeclaration} prototype and adds prefix to prototype sourceFile
     * The prefix represents nested project name (without prefix report source file path is generated incorrectly for nested projects
     *
     * @param sourceFilePrefix {@link String}
     * @param prototype {@link ErrorMessageDeclaration}
     * @return {@link ErrorMessageDeclaration}
     */
    public static ErrorMessageDeclaration copy(String sourceFilePrefix, ErrorMessageDeclaration prototype) {
        ErrorMessageDeclaration.Builder builder = ErrorMessageDeclaration.builder();
        builder.declaringPackage(prototype.getDeclaringPackage());
        builder.prependMessage(prototype.getMessage());
        for (String mitigation: prototype.getMitigations()) {
            builder.appendMitigation(mitigation);
        }
        builder.identifier(prototype.getIdentifier());
        if (sourceFilePrefix == null) {
            builder.setPosition(prototype.getSourceFile(), prototype.getLine());
        } else {
            builder.setPosition(sourceFilePrefix + "/" + prototype.getSourceFile(), prototype.getLine());
        }
        for (NamedParameter parameter: prototype.getNamedParameters()) {
            builder.addParameter(parameter.getName(), parameter.getDescription());
        }
        return builder.build();
    }

    private ErrorMessageDeclarationHelper() {

    }


}
