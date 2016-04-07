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

package com.github.sviperll.codemodel.render;

import com.github.sviperll.codemodel.ObjectType;
import com.github.sviperll.codemodel.Type;
import com.github.sviperll.codemodel.WildcardType;
import java.util.Iterator;
import java.util.Locale;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
@ParametersAreNonnullByDefault
class SimpleRendererContext implements RendererContext {
    private final LineWriter implementation;
    private final int identationLevel;

    SimpleRendererContext(LineWriter implementation) {
        this(implementation, 0);
    }
    SimpleRendererContext(LineWriter implementation, int identationLevel) {
        this.implementation = implementation;
        this.identationLevel = identationLevel;
    }
    @Override
    public void appendText(String s) {
        implementation.writeText(identationLevel, s);
    }
    @Override
    public void appendLineBreak() {
        implementation.writeLineBreak();
    }

    @Override
    public RendererContext indented() {
        return new SimpleRendererContext(implementation, identationLevel + 1);
    }

    @Override
    public void appendWhiteSpace() {
        implementation.writeWhiteSpace();
    }

    @Override
    public void appendRenderable(Renderable renderable) {
        Renderer renderer = renderable.createRenderer(this);
        renderer.render();
    }

    @Override
    public void appendQualifiedClassName(String name) {
        implementation.writeQualifiedTypeName(identationLevel, name);
    }

    @Override
    public void appendEmptyLine() {
        implementation.appendEmptyLine();
    }
}