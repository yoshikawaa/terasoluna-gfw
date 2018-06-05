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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.terasoluna.gfw.common.codelist.AbstractCodeList;
import org.terasoluna.gfw.common.codelist.ReloadableCodeList;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

/**
 * Abstract extended implementation of {@link AbstractCodeList}. Adds Internationalization support to {@link AbstractCodeList}
 * by implementing {I18nCodeList} interface.
 */
public abstract class AbstractI18nCodeList extends AbstractCodeList implements
                                           I18nCodeList, ReloadableCodeList, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final ReadWriteLockTableWrapper<Locale, String, String> cachedTable = createTable();
    private volatile Table<Locale, String, String> exposedTable = null;
    
    /**
     * Lazy initialization flag
     */
    private boolean lazyInit = false;

    /**
     * supplier to return a {@link LinkedHashMap} object.
     */
    private static final Supplier<LinkedHashMap<String, String>> LINKED_HASH_MAP_SUPPLIER = new Supplier<LinkedHashMap<String, String>>() {
        @Override
        public LinkedHashMap<String, String> get() {
            return Maps.newLinkedHashMap();
        }
    };

    /**
     * <p>
     * Returns a codelist as map for the default locale ({@link Locale#getDefault()}). <br>
     * if there is no codelist for the locale, returns an empty map.
     * </p>
     * @see org.terasoluna.gfw.common.codelist.CodeList#asMap()
     */
    @Override
    public Map<String, String> asMap() {
        return asMap(Locale.getDefault());
    }
    
    @Override
    public Map<String, String> asMap(Locale locale) {
        Assert.notNull(locale, "locale is null");
        
        if (exposedTable == null) {
            refresh();
        }
        return exposedTable.row(locale);
    }

    /**
     * Flag that determines whether the codelist information needs to be eager fetched. <br>
     * @param lazyInit flag
     */
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public final void refresh() {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh codelist codeListId={}", getCodeListId());
        }
        synchronized (cachedTable) {
            cachedTable.clear();
            cachedTable.putAll(retrieveTable());
            exposedTable = ImmutableTable.copyOf(cachedTable);
        }
    }

    /**
     * <p>
     * check whether codeListTable is initialized.
     * </p>
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        if (!lazyInit) {
            refresh();
        }
    }

    /**
     * create table which consist of {@link LinkedHashMap} factory.
     * @return table
     */
    protected ReadWriteLockTableWrapper<Locale, String, String> createTable() {
        Map<Locale, Map<String, String>> backingMap = Maps.newLinkedHashMap();
        ReadWriteLockTableWrapper<Locale, String, String> table = new ReadWriteLockTableWrapper<Locale, String, String>(Tables.newCustomTable(backingMap,
                LINKED_HASH_MAP_SUPPLIER));
        return table;
    }
    
    abstract protected Table<Locale, String, String> retrieveTable();
    
}
