/*
 * This file is generated by jOOQ.
 */
package jooq;


import javax.annotation.Generated;

import jooq.tables.LanguageEntity;
import jooq.tables.UserEntity;
import jooq.tables.UserLanguagesEntity;
import jooq.tables.UserPreferencesEntity;
import jooq.tables.records.LanguageEntityRecord;
import jooq.tables.records.UserEntityRecord;
import jooq.tables.records.UserLanguagesEntityRecord;
import jooq.tables.records.UserPreferencesEntityRecord;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<LanguageEntityRecord, Integer> IDENTITY_LANGUAGE_ENTITY = Identities0.IDENTITY_LANGUAGE_ENTITY;
    public static final Identity<UserEntityRecord, Integer> IDENTITY_USER_ENTITY = Identities0.IDENTITY_USER_ENTITY;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<LanguageEntityRecord> PK_LANGUAGE_ENTITY = UniqueKeys0.PK_LANGUAGE_ENTITY;
    public static final UniqueKey<UserEntityRecord> PK_USER_ENTITY = UniqueKeys0.PK_USER_ENTITY;
    public static final UniqueKey<UserLanguagesEntityRecord> PK_USER_LANGUAGES_ENTITY = UniqueKeys0.PK_USER_LANGUAGES_ENTITY;
    public static final UniqueKey<UserPreferencesEntityRecord> PK_USER_PREFERENCES_ENTITY = UniqueKeys0.PK_USER_PREFERENCES_ENTITY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<LanguageEntityRecord, Integer> IDENTITY_LANGUAGE_ENTITY = Internal.createIdentity(LanguageEntity.LANGUAGE_ENTITY, LanguageEntity.LANGUAGE_ENTITY.ID);
        public static Identity<UserEntityRecord, Integer> IDENTITY_USER_ENTITY = Internal.createIdentity(UserEntity.USER_ENTITY, UserEntity.USER_ENTITY.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<LanguageEntityRecord> PK_LANGUAGE_ENTITY = Internal.createUniqueKey(LanguageEntity.LANGUAGE_ENTITY, "pk_LANGUAGE ENTITY", LanguageEntity.LANGUAGE_ENTITY.ID);
        public static final UniqueKey<UserEntityRecord> PK_USER_ENTITY = Internal.createUniqueKey(UserEntity.USER_ENTITY, "pk_USER ENTITY", UserEntity.USER_ENTITY.ID);
        public static final UniqueKey<UserLanguagesEntityRecord> PK_USER_LANGUAGES_ENTITY = Internal.createUniqueKey(UserLanguagesEntity.USER_LANGUAGES_ENTITY, "pk_USER LANGUAGES ENTITY", UserLanguagesEntity.USER_LANGUAGES_ENTITY.USERFK, UserLanguagesEntity.USER_LANGUAGES_ENTITY.LANGUAGEFK, UserLanguagesEntity.USER_LANGUAGES_ENTITY.ISSOURCE);
        public static final UniqueKey<UserPreferencesEntityRecord> PK_USER_PREFERENCES_ENTITY = Internal.createUniqueKey(UserPreferencesEntity.USER_PREFERENCES_ENTITY, "pk_USER PREFERENCES ENTITY", UserPreferencesEntity.USER_PREFERENCES_ENTITY.USERFK);
    }
}
