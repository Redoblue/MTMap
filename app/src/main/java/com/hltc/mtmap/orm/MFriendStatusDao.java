package com.hltc.mtmap.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.hltc.mtmap.MFriendStatus;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MFRIEND_STATUS.
 */
public class MFriendStatusDao extends AbstractDao<MFriendStatus, Long> {

    public static final String TABLENAME = "MFRIEND_STATUS";
    private DaoSession daoSession;
    ;

    public MFriendStatusDao(DaoConfig config) {
        super(config);
    }


    public MFriendStatusDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'MFRIEND_STATUS' (" + //
                "'USER_ID' INTEGER PRIMARY KEY NOT NULL ," + // 0: userId
                "'USER_PORTRAIT' TEXT," + // 1: userPortrait
                "'NICK_NAME' TEXT," + // 2: nickName
                "'TEXT' TEXT," + // 3: text
                "'STATUS' TEXT);"); // 4: status
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MFRIEND_STATUS'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, MFriendStatus entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getUserId());

        String userPortrait = entity.getUserPortrait();
        if (userPortrait != null) {
            stmt.bindString(2, userPortrait);
        }

        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(3, nickName);
        }

        String text = entity.getText();
        if (text != null) {
            stmt.bindString(4, text);
        }

        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
    }

    @Override
    protected void attachEntity(MFriendStatus entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public MFriendStatus readEntity(Cursor cursor, int offset) {
        MFriendStatus entity = new MFriendStatus( //
                cursor.getLong(offset + 0), // userId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userPortrait
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nickName
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // text
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // status
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, MFriendStatus entity, int offset) {
        entity.setUserId(cursor.getLong(offset + 0));
        entity.setUserPortrait(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNickName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setText(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(MFriendStatus entity, long rowId) {
        entity.setUserId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(MFriendStatus entity) {
        if (entity != null) {
            return entity.getUserId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Properties of entity MFriendStatus.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property UserId = new Property(0, long.class, "userId", true, "USER_ID");
        public final static Property UserPortrait = new Property(1, String.class, "userPortrait", false, "USER_PORTRAIT");
        public final static Property NickName = new Property(2, String.class, "nickName", false, "NICK_NAME");
        public final static Property Text = new Property(3, String.class, "text", false, "TEXT");
        public final static Property Status = new Property(4, String.class, "status", false, "STATUS");
    }

}
