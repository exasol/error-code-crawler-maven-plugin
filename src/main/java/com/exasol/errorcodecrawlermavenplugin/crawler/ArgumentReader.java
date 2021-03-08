package com.exasol.errorcodecrawlermavenplugin.crawler;

import static com.exasol.errorcodecrawlermavenplugin.crawler.PositionFormatter.formatPosition;

import com.exasol.errorreporting.ExaError;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtField;

/**
 * This class reads an the value of an argument.
 */
class ArgumentReader {

    /**
     * Read the value of an argument.
     * 
     * @param messageArgument argument
     * @param signature       method signature for use in exception message
     * @return string value
     * @throws InvalidSyntaxException if the argument is not a literal, constant or concatenation of the two
     */
    public String readStringArgumentValue(final CtExpression<?> messageArgument, final String signature)
            throws InvalidSyntaxException {
        if (messageArgument instanceof CtLiteral) {
            final Object literalValue = ((CtLiteral<?>) messageArgument).getValue();
            return literalValue.toString();
        } else if (messageArgument instanceof CtBinaryOperator
                && ((CtBinaryOperator<?>) messageArgument).getKind().equals(BinaryOperatorKind.PLUS)) {
            final CtBinaryOperator<?> binaryOperator = (CtBinaryOperator<?>) messageArgument;
            return readStringArgumentValue(binaryOperator.getLeftHandOperand(), signature)
                    + readStringArgumentValue(binaryOperator.getRightHandOperand(), signature);
        } else if (messageArgument instanceof CtFieldRead) {
            final CtFieldRead<?> fieldRead = (CtFieldRead<?>) messageArgument;
            final CtField<?> field = fieldRead.getVariable().getDeclaration();
            if (field.isStatic() && field.isFinal()) {
                return readStringArgumentValue(field.getDefaultExpression(), signature);
            }
        }
        throw new InvalidSyntaxException(ExaError.messageBuilder("E-ECM-16")
                .message("Invalid parameter for {{method}} call. ({{position}})").unquotedParameter("method", signature)
                .unquotedParameter("position", formatPosition(messageArgument.getPosition()))
                .mitigation("Only literals, string-constants and concatenation of these two are supported.")
                .toString());
    }
}
