/*
 * Copyright (c) 2016, Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.sviperll.codemodel;

import com.github.sviperll.codemodel.render.Renderable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
@ParametersAreNonnullByDefault
public abstract class ExecutableBuilder extends GenericDefinitionBuilder<NestingBuilder> {
    private final VariableScope scope = VariableScope.createTopLevel();
    private final BlockBuilder body = BlockBuilder.createWithBracesForced(scope.createNested());
    private final List<VariableDeclaration> parameters = new ArrayList<>();
    private final List<Type> throwsList = new ArrayList<>();
    private final BuiltDefinition definition = new BuiltDefinition();

    private final TypeContainer typeContainer;
    private final NestingBuilder residence;

    ExecutableBuilder(NestingBuilder residence) {
        super(residence);
        if (residence.residence().getNesting().isStatic()) {
            typeContainer = new TypeContainer(null);
        } else {
            typeContainer = null;
        }
        this.residence = residence;
        
    }

    abstract boolean isConstructor();

    abstract MethodDefinitionDetails getMethodDefinitionDetails();

    public void addParameter(Type type, String name) throws CodeModelException {
        name = scope.makeIntroducable(name);
        scope.introduce(name);
        Parameter parameter = new Parameter(false, type, name);
        parameters.add(parameter);
    }

    public void addFinalParameter(Type type, String name) throws CodeModelException {
        name = scope.makeIntroducable(name);
        scope.introduce(name);
        Parameter parameter = new Parameter(true, type, name);
        parameters.add(parameter);
    }

    public void throwsException(Type type) throws CodeModelException {
        if (!type.isObjectType())
            throw new CodeModelException("Only object type can be an exception");
        if (type.getObjectDetails().definition().isGeneric())
            throw new CodeModelException("Generic class can't be used as throwable exception");
        throwsList.add(type);
    }

    @Override
    public ExecutableDefinition definition() {
        return definition;
    }

    public BlockBuilder body() {
        return body;
    }

    private class BuiltDefinition extends ExecutableDefinition {
        @Override
        public List<VariableDeclaration> parameters() {
            return Collections.unmodifiableList(parameters);
        }

        @Override
        public List<Type> throwsList() {
            return Collections.unmodifiableList(throwsList);
        }

        @Override
        Renderable body() {
            return body;
        }

        @Override
        public Type rawType() {
            if (residence.residence().getNesting().isStatic()) {
                return typeContainer.type;
            } else {
                throw new UnsupportedOperationException("Parent instance type is required");
            }
        }

        @Override
        public Type rawType(Type parentInstanceType) {
            if (residence.residence().getNesting().isStatic()) {
                throw new UnsupportedOperationException("Type is static memeber, no parent is expected.");
            } else {
                TypeContainer typeContainer = new TypeContainer(parentInstanceType.getObjectDetails());
                return typeContainer.type;
            }
        }

        @Override
        public boolean isConstructor() {
            return ExecutableBuilder.this.isConstructor();
        }

        @Override
        public boolean isMethod() {
            return !ExecutableBuilder.this.isConstructor();
        }

        @Override
        public MethodDefinitionDetails getMethodDetails() {
            return ExecutableBuilder.this.getMethodDefinitionDetails();
        }

        @Override
        public Residence residence() {
            return residence.residence();
        }

        @Override
        public CodeModel getCodeModel() {
            return residence.getCodeModel();
        }

        @Override
        public Type internalType() {
            Type rawType;
            if (residence.residence().getNesting().isStatic()) {
                rawType = rawType();
            } else {
                rawType = rawType(residence.residence().getNesting().parent().internalType());
            }
            List<Type> internalTypeArguments = typeParameters().asInternalTypeArguments();
            try {
                return rawType.getExecutableDetails().narrow(internalTypeArguments);
            } catch (CodeModelException ex) {
                throw new RuntimeException("No parameter-argument mismatch is guaranteed to ever happen", ex);
            }
        }

        @Override
        public TypeParameters typeParameters() {
            return ExecutableBuilder.this.typeParameters();
        }
    }

    private class TypeContainer {
        private final ObjectTypeDetails parentInstanceType;
        private Type type = Type.executable(new BuiltTypeDetails());

        TypeContainer(ObjectTypeDetails parentInstanceType) {
            this.parentInstanceType = parentInstanceType;
        }

        private class BuiltTypeDetails extends RawExecutableTypeDetails {

            @Override
            public Type asType() {
                return type;
            }

            @Override
            public ExecutableDefinition definition() {
                return definition;
            }

            @Override
            public List<VariableDeclaration> parameters() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<Type> throwsList() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Type returnType() {
                if (definition.isConstructor())
                    return parentInstanceType.asType();
                else {
                    return wrapType(definition.getMethodDetails().returnType());
                }
            }

            private Type wrapType(Type type) {
                if (parentInstanceType != null)
                    type = type.inEnvironment(parentInstanceType.definitionEnvironment());
                return type;
            }

            @Override
            public Type capturedEnclosingType() {
                return parentInstanceType == null ? null : parentInstanceType.asType();
            }
        }
    }

    private static class Parameter extends VariableDeclaration {

        private final boolean isFinal;
        private final Type type;
        private final String name;

        public Parameter(boolean isFinal, Type type, String name) throws CodeModelException {
            if (type.isVoid())
                throw new CodeModelException("void is not allowed here");
            this.isFinal = isFinal;
            this.type = type;
            this.name = name;
        }

        @Override
        public boolean isFinal() {
            return isFinal;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        Expression getInitialValue() {
            throw new UnsupportedOperationException();
        }
    }

}
