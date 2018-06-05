/*
 * Copyright (C) 2013-2017 NTT DATA Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.terasoluna.gfw.common.codelist;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * {@link Map} implementation class which enables thread-safe operations on the map of key-value pairs contained in it.
 * @param <K> Key
 * @param <V> Value
 */
public abstract class AbstractReadWriteLockWrapper {

    /**
     * Instance of {@link ReadWriteLock} implementation
     */
    private final ReadWriteLock lock;

    public AbstractReadWriteLockWrapper() {
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * A contract to represent a method call of enclosing {@link Map} instance.
     * <p>
     * Implementation of this interface must first acquire read or write lock using implementation of <br>
     * {@link ReadWriteLock} before calling the method of enclosing {@link Map}. <br>
     * </p>
     * @param <T> The return type of the method represented by the instance of implementation of this interface.
     */
    public interface LockedCallback<T> {

        /**
         * Implementation must first acquire read or write lock before calling the method represented by this contract <br>
         * @return value returned by the method call
         */
        T apply();
    }

    /**
     * Provides read locked call to a method of {@link Map}. {@link Map} is not locked exclusively. <br>
     * <p>
     * {@link Map} is the shared resource which is wrapped in this class. Access to this shared resource is regulated using
     * {@link ReadWriteLock} implementation. A read lock can be acquired by as any number of threads. A read lock does not block
     * other read locks.
     * </p>
     * <p>
     * A {@link LockedCallback} instance passed as argument to this method. It represents a method call of the {@link Map}.<br>
     * A read lock is first acquired over the {@code Map} and then using {@code callback}, method represented by
     * {@code callback} <br>
     * is executed.
     * </p>
     * @param <T> a return value type of callback method within read lock
     * @param callback An instance of {@link LockedCallback} which represents a method call of {@link Map}
     * @return the return value of the method represented by {@code callback}
     */
    public <T> T withReadLock(LockedCallback<T> callback) {
        Lock readLock = this.lock.readLock();
        T result = null;
        try {
            readLock.lock();
            result = callback.apply();
        } finally {
            readLock.unlock();
        }
        return result;
    }

    /**
     * Provides write locked call to a method of {@link Map}. {@link Map} is exclusively locked for write operation. <br>
     * <p>
     * {@link Map} is the shared resource which is wrapped in this class. Access to this shared resource is regulated <br>
     * using {@link ReadWriteLock} implementation. A write lock can be acquired by only a single thread. No read lock can be <br>
     * acquired while the resource is write locked. A write lock blocks other write locks as well as read locks. <br>
     * </p>
     * <p>
     * A {@link LockedCallback} instance passed as argument to this method. It represents a method call of the {@link Map}.<br>
     * A read lock is first acquired over the {@code Map} and then using {@code callback}, method represented by
     * {@code callback} <br>
     * is executed.<br>
     * </p>
     * @param <T> a return value type of callback method within write lock
     * @param callback An instance of {@link LockedCallback} which represents a method call of {@link Map}
     * @return the return value of the method represented by {@code callback}
     */
    public <T> T withWriteLock(LockedCallback<T> callback) {
        Lock writeLock = this.lock.writeLock();
        T result = null;
        try {
            writeLock.lock();
            result = callback.apply();
        } finally {
            writeLock.unlock();
        }
        return result;
    }

}
