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

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 * @param <D>
 */
public abstract class GenericTypeDetails<D extends GenericDefinition> {
    public abstract D definition();

    public abstract Type erasure();

    public abstract boolean isNarrowed();

    public abstract boolean isRaw();

    public abstract Type narrow(List<Type> typeArguments) throws CodeModelException;

    public abstract List<Type> typeArguments();

    public abstract Type asType();

    /**
     * Type of enclosing definition that defines a context for current type.
     *
     * @return Type of enclosing definition or null for types with package-level or static member definitions.
     */
    @Nullable
    public abstract Type capturedEnclosingType();

    final TypeEnvironment definitionEnvironment() {
        Type enclosingType = capturedEnclosingType();
        TypeEnvironment.Builder builder;
        if (enclosingType == null)
            builder = TypeEnvironment.createBuilder();
        else
            builder = TypeEnvironment.createBuilder(enclosingType.getGenericTypeDetails().definitionEnvironment());
        GenericDefinition definition = definition();
        Iterator<TypeParameter> typeParameters = definition.typeParameters().all().iterator();
        Iterator<Type> typeArguments = typeArguments().iterator();
        while (typeParameters.hasNext() && typeArguments.hasNext()) {
            TypeParameter typeParameter = typeParameters.next();
            Type typeArgument = typeArguments.next();
            builder.put(typeParameter.name(), typeArgument);
        }
        return builder.build();
    }

}
