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
package org.terasoluna.gfw.common.codelist.i18n;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.terasoluna.gfw.common.codelist.AbstractReadWriteLockWrapper;

import com.google.common.collect.Table;

/**
 * {@link Map} implementation class which enables thread-safe operations on the map of key-value pairs contained in it.
 * @param <K> Key
 * @param <V> Value
 */
public class ReadWriteLockTableWrapper<R, C, V> extends AbstractReadWriteLockWrapper implements Table<R, C, V> {
    /**
     * backing map to be locked
     */
    private final Table<R, C, V> table;

    /**
     * Constructor with a single {@link Map} parameter.
     * @param map target map of read or write lock
     */
    public ReadWriteLockTableWrapper(Table<R, C, V> table) {
        super();
        this.table = table;
    }

    @Override
    public boolean contains(final Object rowKey, final Object columnKey) {
        return withReadLock(new LockedCallback<Boolean>() {
            @Override
            public Boolean apply() {
                return table.contains(rowKey, columnKey);
            }
        });
    }

    @Override
    public boolean containsRow(final Object rowKey) {
        return withReadLock(new LockedCallback<Boolean>() {
            @Override
            public Boolean apply() {
                return table.containsRow(rowKey);
            }
        });
    }

    @Override
    public boolean containsColumn(final Object columnKey) {
        return withReadLock(new LockedCallback<Boolean>() {
            @Override
            public Boolean apply() {
                return table.containsColumn(columnKey);
            }
        });
    }

    @Override
    public boolean containsValue(final Object value) {
        return withReadLock(new LockedCallback<Boolean>() {
            @Override
            public Boolean apply() {
                return table.containsValue(value);
            }
        });
    }

    @Override
    public V get(final Object rowKey, final Object columnKey) {
        return withReadLock(new LockedCallback<V>() {
            @Override
            public V apply() {
                return table.get(rowKey, columnKey);
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return withReadLock(new LockedCallback<Boolean>() {
            @Override
            public Boolean apply() {
                return table.isEmpty();
            }
        });
    }

    @Override
    public int size() {
        return withReadLock(new LockedCallback<Integer>() {
            @Override
            public Integer apply() {
                return table.size();
            }
        });
    }

    @Override
    public void clear() {
        withReadLock(new LockedCallback<Void>() {
            @Override
            public Void apply() {
                table.clear();
                return null;
            }
        });
    }

    @Override
    public V put(final R rowKey, final C columnKey, final V value) {
        return withReadLock(new LockedCallback<V>() {
            @Override
            public V apply() {
                return table.put(rowKey, columnKey, value);
            }
        });
    }

    @Override
    public void putAll(Table<? extends R, ? extends C, ? extends V> t) {
        withReadLock(new LockedCallback<Void>() {
            @Override
            public Void apply() {
                table.putAll(table);
                return null;
            }
        });
    }

    @Override
    public V remove(final Object rowKey, final Object columnKey) {
        return withReadLock(new LockedCallback<V>() {
            @Override
            public V apply() {
                return table.remove(rowKey, columnKey);
            }
        });
    }

    @Override
    public Map<C, V> row(final R rowKey) {
        return withReadLock(new LockedCallback<Map<C, V>>() {
            @Override
            public Map<C, V> apply() {
                return table.row(rowKey);
            }
        });
    }

    @Override
    public Map<R, V> column(final C columnKey) {
        return withReadLock(new LockedCallback<Map<R, V>>() {
            @Override
            public Map<R, V> apply() {
                return table.column(columnKey);
            }
        });
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return withReadLock(new LockedCallback<Set<Cell<R, C, V>>>() {
            @Override
            public Set<Cell<R, C, V>> apply() {
                return table.cellSet();
            }
        });
    }

    @Override
    public Set<R> rowKeySet() {
        return withReadLock(new LockedCallback<Set<R>>() {
            @Override
            public Set<R> apply() {
                return table.rowKeySet();
            }
        });
    }

    @Override
    public Set<C> columnKeySet() {
        return withReadLock(new LockedCallback<Set<C>>() {
            @Override
            public Set<C> apply() {
                return table.columnKeySet();
            }
        });
    }

    @Override
    public Collection<V> values() {
        return withReadLock(new LockedCallback<Collection<V>>() {
            @Override
            public Collection<V> apply() {
                return table.values();
            }
        });
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return withReadLock(new LockedCallback<Map<R, Map<C, V>>>() {
            @Override
            public Map<R, Map<C, V>> apply() {
                return table.rowMap();
            }
        });
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        return withReadLock(new LockedCallback<Map<C, Map<R, V>>>() {
            @Override
            public Map<C, Map<R, V>> apply() {
                return table.columnMap();
            }
        });
    }

}
