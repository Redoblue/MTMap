package com.hltc.mtmap.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hltc.mtmap.MFriend;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MFRIEND.
 */
public class MFriendDao extends AbstractDao<MFriend, Long> {

    public static final String TABLENAME = "MFRIEND";
    private DaoSession daoSession;
    ;

    public MFriendDao(DaoConfig config) {
        super(config);
    }


    public MFriendDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MFRIEND' (" + //
                "'USER_ID' INTEGER PRIMARY KEY NOT NULL ," + // 0: userId
                "'NICK_NAME' TEXT," + // 1: nickName
                "'FIRST_CHARACTER' TEXT," + // 2: firstCharacter
                "'PORTRAIT' TEXT," + // 3: portrait
                "'REMARK' TEXT," + // 4: remark
                "'IS_FOLDER' INTEGER);"); // 5: isFolder
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MFRIEND'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, MFriend entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUserId());

        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(2, nickName);
        }

        String firstCharacter = entity.getFirstCharacter();
        if (firstCharacter != null) {
            stmt.bindString(3, firstCharacter);
        }

        String portrait = entity.getPortrait();
        if (portrait != null) {
            stmt.bindString(4, portrait);
        }

        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(5, remark);
        }

        Boolean isFolder = entity.getIsFolder();
        if (isFolder != null) {
            stmt.bindLong(6, isFolder ? 1l: 0l);
        }
    }

    @Override
    protected void attachEntity(MFriend entity) {
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
    public MFriend readEntity(Cursor cursor, int offset) {
        MFriend entity = new MFriend( //
                cursor.getLong(offset + 0), // userId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // nickName
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // firstCharacter
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // portrait
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // remark
                cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0 // isFolder
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, MFriend entity, int offset) {
        entity.setUserId(cursor.getLong(offset + 0));
        entity.setNickName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFirstCharacter(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPortrait(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRemark(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsFolder(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
     }
     
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(MFriend entity, long rowId) {
        entity.setUserId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(MFriend entity) {
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
     * Properties of entity MFriend.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property UserId = new Property(0, long.class, "userId", true, "USER_ID");
        public final static Property NickName = new Property(1, String.class, "nickName", false, "NICK_NAME");
        public final static Property FirstCharacter = new Property(2, String.class, "firstCharacter", false, "FIRST_CHARACTER");
        public final static Property Portrait = new Property(3, String.class, "portrait", false, "PORTRAIT");
        public final static Property Remark = new Property(4, String.class, "remark", false, "REMARK");
        public final static Property IsFolder = new Property(5, Boolean.class, "isFolder", false, "IS_FOLDER");
    }
    
}