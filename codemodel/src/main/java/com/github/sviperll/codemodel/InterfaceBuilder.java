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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.Nonnull;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 * @param <B>
 */
@ParametersAreNonnullByDefault
public class InterfaceBuilder<B extends ResidenceBuilder> extends AbstractObjectBuilder<B> {

    private final String name;
    private final List<ObjectType> interfaces = new ArrayList<>();

    public InterfaceBuilder(B residence, String name) {
        super(ObjectKind.CLASS, residence);
        this.name = name;
    }

    public void extendsInterface(ObjectType type) throws CodeModelException {
        if (!type.definition().kind().isInterface())
            throw new CodeModelException("Only interfaces can be implemented");
        if (type.containsWildcards())
             throw new CodeModelException("Wildcards are not allowed in implemenents clause");
        interfaces.add(type);
    }

    @Override
    ObjectDefinition createDefinition(TypeParameters typeParameters) {
        return new BuiltDefinition(typeParameters);
    }

    private class BuiltDefinition extends AbstractObjectBuilder<B>.BuiltDefinition {

        BuiltDefinition(TypeParameters typeParameters) {
            super(typeParameters);
        }

        @Override
        public String simpleName() {
            return name;
        }

        @Override
        public boolean isFinal() {
            return false;
        }

        @Override
        public ObjectType extendsClass() {
            return getCodeModel().objectType();
        }

        @Override
        public List<ObjectType> implementsInterfaces() {
            return Collections.unmodifiableList(interfaces);
        }

        @Override
        public Collection<ConstructorDefinition> constructors() {
            return Collections.emptyList();
        }

        @Override
        public boolean isAnonymous() {
            return false;
        }
    }

}