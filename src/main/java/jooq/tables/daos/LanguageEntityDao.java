/*
 * This file is generated by jOOQ.
 */
package jooq.tables.daos;


import java.util.List;

import javax.annotation.Generated;

import jooq.tables.LanguageEntity;
import jooq.tables.records.LanguageEntityRecord;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LanguageEntityDao extends DAOImpl<LanguageEntityRecord, jooq.tables.pojos.LanguageEntity, Integer> {

    /**
     * Create a new LanguageEntityDao without any configuration
     */
    public LanguageEntityDao() {
        super(LanguageEntity.LANGUAGE_ENTITY, jooq.tables.pojos.LanguageEntity.class);
    }

    /**
     * Create a new LanguageEntityDao with an attached configuration
     */
    public LanguageEntityDao(Configuration configuration) {
        super(LanguageEntity.LANGUAGE_ENTITY, jooq.tables.pojos.LanguageEntity.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer getId(jooq.tables.pojos.LanguageEntity object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<jooq.tables.pojos.LanguageEntity> fetchById(Integer... values) {
        return fetch(LanguageEntity.LANGUAGE_ENTITY.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public jooq.tables.pojos.LanguageEntity fetchOneById(Integer value) {
        return fetchOne(LanguageEntity.LANGUAGE_ENTITY.ID, value);
    }

    /**
     * Fetch records that have <code>slug IN (values)</code>
     */
    public List<jooq.tables.pojos.LanguageEntity> fetchBySlug(String... values) {
        return fetch(LanguageEntity.LANGUAGE_ENTITY.SLUG, values);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<jooq.tables.pojos.LanguageEntity> fetchByName(String... values) {
        return fetch(LanguageEntity.LANGUAGE_ENTITY.NAME, values);
    }

    /**
     * Fetch records that have <code>isGateway IN (values)</code>
     */
    public List<jooq.tables.pojos.LanguageEntity> fetchByIsgateway(Integer... values) {
        return fetch(LanguageEntity.LANGUAGE_ENTITY.ISGATEWAY, values);
    }

    /**
     * Fetch records that have <code>anglicizedName IN (values)</code>
     */
    public List<jooq.tables.pojos.LanguageEntity> fetchByAnglicizedname(String... values) {
        return fetch(LanguageEntity.LANGUAGE_ENTITY.ANGLICIZEDNAME, values);
    }
}
