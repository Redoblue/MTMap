package com.hltc.mtmap.orm.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hltc.mtmap.orm.models.MTUser;
import com.hltc.mtmap.orm.DaoSession;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MTUSER.
*/
public class MTUserDao extends AbstractDao<MTUser, Long> {

    public static final String TABLENAME = "MTUSER";

    /**
     * Properties of entity MTUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Nickname = new Property(1, String.class, "nickname", false, "NICKNAME");
        public final static Property CreateTime = new Property(2, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property Phone = new Property(3, String.class, "phone", false, "PHONE");
        public final static Property AvatarURL = new Property(4, String.class, "avatarURL", false, "AVATAR_URL");
        public final static Property RawAvatarURL = new Property(5, String.class, "rawAvatarURL", false, "RAW_AVATAR_URL");
        public final static Property CoverURL = new Property(6, String.class, "coverURL", false, "COVER_URL");
    }

    private DaoSession daoSession;


    public MTUserDao(DaoConfig config) {
        super(config);
    }
    
    public MTUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MTUSER' (" + //
                "'_id' INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "'NICKNAME' TEXT NOT NULL ," + // 1: nickname
                "'CREATE_TIME' INTEGER NOT NULL ," + // 2: createTime
                "'PHONE' TEXT NOT NULL ," + // 3: phone
                "'AVATAR_URL' TEXT NOT NULL ," + // 4: avatarURL
                "'RAW_AVATAR_URL' TEXT NOT NULL ," + // 5: rawAvatarURL
                "'COVER_URL' TEXT NOT NULL );"); // 6: coverURL
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MTUSER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MTUser entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindString(2, entity.getNickname());
        stmt.bindLong(3, entity.getCreateTime().getTime());
        stmt.bindString(4, entity.getPhone());
        stmt.bindString(5, entity.getAvatarURL());
        stmt.bindString(6, entity.getRawAvatarURL());
        stmt.bindString(7, entity.getCoverURL());
    }

    @Override
    protected void attachEntity(MTUser entity) {
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
    public MTUser readEntity(Cursor cursor, int offset) {
        MTUser entity = new MTUser( //
            cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // nickname
            new java.util.Date(cursor.getLong(offset + 2)), // createTime
            cursor.getString(offset + 3), // phone
            cursor.getString(offset + 4), // avatarURL
            cursor.getString(offset + 5), // rawAvatarURL
            cursor.getString(offset + 6) // coverURL
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MTUser entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setNickname(cursor.getString(offset + 1));
        entity.setCreateTime(new java.util.Date(cursor.getLong(offset + 2)));
        entity.setPhone(cursor.getString(offset + 3));
        entity.setAvatarURL(cursor.getString(offset + 4));
        entity.setRawAvatarURL(cursor.getString(offset + 5));
        entity.setCoverURL(cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MTUser entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MTUser entity) {
        if(entity != null) {
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
    
}