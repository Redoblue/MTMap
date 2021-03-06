package com.hltc.mtmap.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hltc.mtmap.MTCategory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MTCATEGORY.
 */
public class MTCategoryDao extends AbstractDao<MTCategory, Long> {

    public static final String TABLENAME = "MTCATEGORY";
    private DaoSession daoSession;
    ;

    public MTCategoryDao(DaoConfig config) {
        super(config);
    }


    public MTCategoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MTCATEGORY' (" + //
                "'_id' INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'ICON_URL' TEXT NOT NULL );"); // 2: iconURL
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MTCATEGORY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MTCategory entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindString(2, entity.getName());
        stmt.bindString(3, entity.getIconURL());
    }

    @Override
    protected void attachEntity(MTCategory entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public MTCategory readEntity(Cursor cursor, int offset) {
        MTCategory entity = new MTCategory( //
                cursor.getLong(offset + 0), // id
                cursor.getString(offset + 1), // name
                cursor.getString(offset + 2) // iconURL
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MTCategory entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setIconURL(cursor.getString(offset + 2));
     }
     
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MTCategory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MTCategory entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }
    
    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

/**
     * Properties of entity MTCategory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property IconURL = new Property(2, String.class, "iconURL", false, "ICON_URL");
    }
    
}
