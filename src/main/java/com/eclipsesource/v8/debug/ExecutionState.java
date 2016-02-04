/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.v8.debug;

import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

/**
 * Represents the current execution state at a break.
 * The execution state provides methods for inspecting
 * the stack, variables and scopes.
 *
 * The ExecutionState should not be persisted as it
 * will be released when the debugger continues.
 *
 */
public class ExecutionState implements Releasable {

    private static final String PREPARE_STEP = "prepareStep";
    private static final String FRAME_COUNT = "frameCount";

    private V8Object v8Object;

    ExecutionState(final V8Object v8Object) {
        this.v8Object = v8Object.twin();
    }

    /**
     * Returns the current stack frame count.
     *
     * @return The stack frame count.
     */
    public int frameCount() {
        return v8Object.executeIntegerFunction(FRAME_COUNT, null);
    }

    /**
     * Indicates to the debugger how to proceed. If not called,
     * the debugger will continue running until the next breakpoint
     * is hit.
     *
     * @param action The step action to use.
     */
    public void prepareState(final StepAction action) {
        V8Array parameters = new V8Array(v8Object.getRuntime());
        parameters.push(action.index);
        try {
            v8Object.executeVoidFunction(PREPARE_STEP, parameters);
        } finally {
            parameters.release();
        }
    }

    @Override
    public void release() {
        if ((v8Object != null) && !v8Object.isReleased()) {
            v8Object.release();
            v8Object = null;
        }
    }

}