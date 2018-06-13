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
import org.terasoluna.gfw.common.codelist.ReloadableCodeList;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

/**
 * Reloadable implementation of {@link I18nCodeList}<br>
 * <p>
 * {@link I18nCodeList} has a table of codelist.<br>
 * Each row is a codelist for each language and represented as <strong>unmodifiable linked hash maps</strong>.<br>
 * The key of rows is {@link Locale}.The key of columns is {@link String}(code). <br>
 * </p>
 * <p>
 * To build a table of codelist, set a map of the {@link Locale} and the corresponding {@link ReloadableCodeList}.<br>
 * <strong>To reload the table of codelist, reload the codelist for each locale, then reload this.</strong>
 * </p>
 * 
 * 
 * <h3>set by rows with {@link ReloadableI18nCodeList}</h3>
 * 
 * <pre>
 * &lt;bean id=&quot;CL_I18N_WEEK&quot;
 *     class=&quot;org.terasoluna.gfw.common.codelist.i18n.ReloadableI18nCodeList&quot;&gt;
 *     &lt;property name=&quot;rowsByCodeList&quot;&gt;
 *         &lt;util:map&gt;
 *             &lt;entry key=&quot;en&quot; value-ref=&quot;CL_PRICE_EN&quot; /&gt;
 *             &lt;entry key=&quot;ja&quot; value-ref=&quot;CL_PRICE_JA&quot; /&gt;
 *         &lt;/util:map&gt;
 *     &lt;/property&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id=&quot;AbstractJdbcCodeList&quot;
 *     class=&quot;org.terasoluna.gfw.common.codelist.JdbcCodeList&quot; abstract=&quot;true&quot;&gt;
 *     &lt;property name=&quot;jdbcTemplate&quot; ref=&quot;jdbcTemplateForCodeList&quot; /&gt;
 * &lt;/bean&gt;
 * 
 * 
 * &lt;bean id=&quot;CL_PRICE_EN&quot; parent=&quot;AbstractJdbcCodeList&quot;&gt;
 *     &lt;property name=&quot;querySql&quot;
 *         value=&quot;SELECT code, label FROM price WHERE locale = 'en' ORDER BY code&quot; /&gt;
 *     &lt;property name=&quot;valueColumn&quot; value=&quot;code&quot; /&gt;
 *     &lt;property name=&quot;labelColumn&quot; value=&quot;label&quot; /&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean id=&quot;CL_PRICE_JA&quot; parent=&quot;AbstractJdbcCodeList&quot;&gt;
 *     &lt;property name=&quot;querySql&quot;
 *         value=&quot;SELECT code, label FROM price WHERE locale = 'ja' ORDER BY code&quot; /&gt;
 *     &lt;property name=&quot;valueColumn&quot; value=&quot;code&quot; /&gt;
 *     &lt;property name=&quot;labelColumn&quot; value=&quot;label&quot; /&gt;
 * &lt;/bean&gt;
 * 
 * </pre>
 * 
 * @since 5.5.0
 * @author Atsushi Yoshikawa
 *
 */
public class ReloadableI18nCodeList extends AbstractI18nCodeList implements
                                ReloadableCodeList, InitializingBean {
    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(
            ReloadableI18nCodeList.class);
    /**
     * codelist table.
     */
    private Table<Locale, String, String> codeListTable;

    /**
     * codelist for each locale.
     */
    private Map<Locale, ReloadableCodeList> codeLists;

    /**
     * Lazy initialization flag.
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
     * returns row of codelist table.
     * </p>
     * @see org.terasoluna.gfw.common.codelist.i18n.I18nCodeList#asMap(java.util.Locale)
     */
    @Override
    public Map<String, String> asMap(Locale locale) {
        Assert.notNull(locale, "locale is null");

        // If exposedMap is null, that means it is called for the first time
        // and lazyInit must be set to true
        if (codeListTable == null) {
            refresh();
        }

        return codeListTable.row(locale);
    }

    /**
     * set ({@link ReloadableCodeList}) for each locale.<br>
     * <p>
     * The key is {@link Locale} and the value is {@link ReloadableCodeList}.<br>
     * </p>
     * @param ({@link ReloadableCodeList}) for each locale
     */
    public void setRowsByCodeList(Map<Locale, ReloadableCodeList> codeLists) {
        this.codeLists = codeLists;
    }

    /**
     * Flag that determines whether the codelist information needs to be eager fetched. <br>
     * @param lazyInit flag
     */
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    /**
     * Reloads the codelist.
     * @see org.terasoluna.gfw.common.codelist.ReloadableCodeList#refresh()
     */
    @Override
    public void refresh() {
        if (logger.isDebugEnabled()) {
            logger.debug("refresh codelist codeListId={}", getCodeListId());
        }
        Table<Locale, String, String> table = createTable();
        for (Map.Entry<Locale, ReloadableCodeList> e : codeLists.entrySet()) {
            Locale locale = e.getKey();
            Map<String, String> row = e.getValue().asMap();
            for (Map.Entry<String, String> re : row.entrySet()) {
                String value = re.getKey();
                String label = re.getValue();
                table.put(locale, value, label);
            }
        }
        this.codeListTable = Tables.unmodifiableTable(table);
    }

    /**
     * create table which consist of {@link LinkedHashMap} factory.
     * @return table
     */
    private Table<Locale, String, String> createTable() {
        Map<Locale, Map<String, String>> backingMap = Maps.newLinkedHashMap();
        Table<Locale, String, String> table = Tables.newCustomTable(backingMap,
                LINKED_HASH_MAP_SUPPLIER);
        return table;
    }

    /**
     * This method is called after the properties of the codelist are set.
     * <p>
     * Checks the lazyInit flag to determine whether the <br>
     * codelist should be refreshed after the properties are set.<br>
     * If lazyInit flag is set to true, the codelist is not refreshed immediately. <br>
     * If it is set to false, it is refreshed (values re-loaded) immediately after the <br>
     * properties are loaded<br>
     * </p>
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(codeLists, "codeLists is not initialized!");
        if (!lazyInit) {
            refresh();
        }
    }

}
