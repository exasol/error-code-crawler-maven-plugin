package com.exasol.errorcodecrawlermavenplugin.crawler;

import static com.exasol.errorcodecrawlermavenplugin.crawler.PositionFormatter.formatPosition;

import com.exasol.errorreporting.ExaError;

import spoon.reflect.code.*;
import spoon.reflect.declaration.CtField;

/**
 * This class reads an the value of an argument.
 */
class ArgumentReader {
    private final String methodSignature;

    /**
     * Create a new instance of {@link ArgumentReader}.
     * 
     * @param methodSignature method signature of the method this argument is passed to; used in exception message
     */
    ArgumentReader(final String methodSignature) {
        this.methodSignature = methodSignature;
    }

    /**
     * Read the value of an argument.
     * 
     * @param messageArgument argument
     * @return string value
     * @throws InvalidSyntaxException if the argument is not a literal, constant or concatenation of the two
     */
    public String readStringArgumentValue(final CtExpression<?> messageArgument) throws InvalidSyntaxException {
        if (isLiteral(messageArgument)) {
            final Object literalValue = ((CtLiteral<?>) messageArgument).getValue();
            return literalValue.toString();
        } else if (isConcatenation(messageArgument)) {
            final CtBinaryOperator<?> binaryOperator = (CtBinaryOperator<?>) messageArgument;
            return readStringArgumentValue(binaryOperator.getLeftHandOperand())
                    + readStringArgumentValue(binaryOperator.getRightHandOperand());
        } else if (isFieldAccess(messageArgument)) {
            final CtFieldRead<?> fieldRead = (CtFieldRead<?>) messageArgument;
            final CtField<?> field = fieldRead.getVariable().getDeclaration();
            if (field.isStatic() && field.isFinal()) {
                return readStringArgumentValue(field.getDefaultExpression());
            }
        }
        throw getInvalidSyntaxException(messageArgument);
    }

    private InvalidSyntaxException getInvalidSyntaxException(final CtExpression<?> messageArgument) {
        return new InvalidSyntaxException(ExaError.messageBuilder("E-ECM-16")
                .message("Invalid parameter for {{method|uq}} call. ({{position|uq}})", this.methodSignature,
                        formatPosition(messageArgument.getPosition()))
                .mitigation("Only literals, string-constants and concatenation of these two are supported.")
                .toString());
    }

    private boolean isFieldAccess(final CtExpression<?> messageArgument) {
        return messageArgument instanceof CtFieldRead;
    }

    private boolean isConcatenation(final CtExpression<?> messageArgument) {
        return messageArgument instanceof CtBinaryOperator
                && ((CtBinaryOperator<?>) messageArgument).getKind().equals(BinaryOperatorKind.PLUS);
    }

    private boolean isLiteral(final CtExpression<?> messageArgument) {
        return messageArgument instanceof CtLiteral;
    }
}
