package org.eclipse.aether.util.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
public class FutureResult<T> implements Future<T>
{
    private final T value;
    
    public FutureResult( T value )
    {
        this.value = value;
    }

    @Override
    public boolean cancel( boolean mayInterruptIfRunning )
    {
        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return false;
    }

    @Override
    public boolean isDone()
    {
        return true;
    }

    @Override
    public T get()
    {
        return value;
    }

    @Override
    public T get( long timeout, TimeUnit unit )
    {
        return value;
    }
}
