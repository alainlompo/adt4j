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

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class FieldBuilder implements SettledBuilder<NestedResidence, NestedResidenceBuilder> {

    private final FieldDeclaration declaration = new BuiltFieldDeclaration();
    private final NestedResidenceBuilder residence;
    private final Type type;
    private final String name;
    private boolean isFinal = false;
    private boolean isInitialized = false;
    private Expression initializer = null;

    FieldBuilder(NestedResidenceBuilder residence, Type type, String name) throws CodeModelException {
        if (type.isVoid())
            throw new CodeModelException("void is not allowed here");
        this.residence = residence;
        this.type = type;
        this.name = name;
    }

    public NestedResidenceBuilder residence() {
        return residence;
    }

    public FieldDeclaration declaration() {
        return declaration;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public void initialize(Expression expression) {
        if (isInitialized)
            throw new IllegalStateException("Field already initialized");
        isInitialized = true;
        initializer = expression;
    }

    private class BuiltFieldDeclaration extends FieldDeclaration {
        @Override
        public String name() {
            return name;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public NestedResidence residence() {
            return residence.residence();
        }

        @Override
        public boolean isInitialized() {
            return isInitialized;
        }

        @Override
        public boolean isFinal() {
            return isFinal;
        }
    }
}