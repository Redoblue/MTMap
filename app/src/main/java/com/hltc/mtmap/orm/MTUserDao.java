package com.hltc.mtmap.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hltc.mtmap.MTUser;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MTUSER.
 */
public class MTUserDao extends AbstractDao<MTUser, Long> {

    public static final String TABLENAME = "MTUSER";
    private DaoSession daoSession;
    ;

    public MTUserDao(DaoConfig config) {
        super(config);
    }


    public MTUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MTUSER' (" + //
                "'USER_ID' INTEGER PRIMARY KEY NOT NULL ," + // 0: userId
                "'NICK_NAME' TEXT," + // 1: nickName
                "'CREATE_TIME' TEXT," + // 2: createTime
                "'PHONE' TEXT," + // 3: phone
                "'PORTRAIT' TEXT," + // 4: portrait
                "'COVER_IMG' TEXT," + // 5: coverImg
                "'SIGNATURE' TEXT," + // 6: signature
                "'REMARK' TEXT," + // 7: remark
                "'FIRST_CHARACTER' TEXT);"); // 8: firstCharacter
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
        stmt.bindLong(1, entity.getUserId());

        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(2, nickName);
        }

        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(3, createTime);
        }

        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(4, phone);
        }

        String portrait = entity.getPortrait();
        if (portrait != null) {
            stmt.bindString(5, portrait);
        }

        String coverImg = entity.getCoverImg();
        if (coverImg != null) {
            stmt.bindString(6, coverImg);
        }

        String signature = entity.getSignature();
        if (signature != null) {
            stmt.bindString(7, signature);
        }

        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(8, remark);
        }

        String firstCharacter = entity.getFirstCharacter();
        if (firstCharacter != null) {
            stmt.bindString(9, firstCharacter);
        }
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
                cursor.getLong(offset + 0), // userId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nickName
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // createTime
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // phone
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // portrait
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // coverImg
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // signature
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // remark
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // firstCharacter
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MTUser entity, int offset) {
        entity.setUserId(cursor.getLong(offset + 0));
        entity.setNickName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreateTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPhone(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPortrait(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCoverImg(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSignature(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRemark(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setFirstCharacter(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
     
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MTUser entity, long rowId) {
        entity.setUserId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MTUser entity) {
        if (entity != null) {
            return entity.getUserId();
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
     * Properties of entity MTUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property UserId = new Property(0, long.class, "userId", true, "USER_ID");
        public final static Property NickName = new Property(1, String.class, "nickName", false, "NICK_NAME");
        public final static Property CreateTime = new Property(2, String.class, "createTime", false, "CREATE_TIME");
        public final static Property Phone = new Property(3, String.class, "phone", false, "PHONE");
        public final static Property Portrait = new Property(4, String.class, "portrait", false, "PORTRAIT");
        public final static Property CoverImg = new Property(5, String.class, "coverImg", false, "COVER_IMG");
        public final static Property Signature = new Property(6, String.class, "signature", false, "SIGNATURE");
        public final static Property Remark = new Property(7, String.class, "remark", false, "REMARK");
        public final static Property FirstCharacter = new Property(8, String.class, "firstCharacter", false, "FIRST_CHARACTER");
    }
    
}