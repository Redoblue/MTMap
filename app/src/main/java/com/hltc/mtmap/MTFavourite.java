package com.hltc.mtmap;

import com.hltc.mtmap.orm.DaoSession;
import de.greenrobot.dao.DaoException;

import com.hltc.mtmap.orm.MTFavouriteDao;
import com.hltc.mtmap.orm.MTGrainDao;
import com.hltc.mtmap.orm.MTUserDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table MTFAVOURITE.
 */
public class MTFavourite {

    private long id;
    private long userId;
    private long grainId;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MTFavouriteDao myDao;

    private MTUser mTUser;
    private Long mTUser__resolvedKey;

    private MTGrain mTGrain;
    private Long mTGrain__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MTFavourite() {
    }

    public MTFavourite(long id) {
        this.id = id;
    }

    public MTFavourite(long id, long userId, long grainId) {
        this.id = id;
        this.userId = userId;
        this.grainId = grainId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMTFavouriteDao() : null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGrainId() {
        return grainId;
    }

    public void setGrainId(long grainId) {
        this.grainId = grainId;
    }

    /** To-one relationship, resolved on first access. */
    public MTUser getMTUser() {
        long __key = this.userId;
        if (mTUser__resolvedKey == null || !mTUser__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MTUserDao targetDao = daoSession.getMTUserDao();
            MTUser mTUserNew = targetDao.load(__key);
            synchronized (this) {
                mTUser = mTUserNew;
                mTUser__resolvedKey = __key;
            }
        }
        return mTUser;
    }

    public void setMTUser(MTUser mTUser) {
        if (mTUser == null) {
            throw new DaoException("To-one property 'userId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.mTUser = mTUser;
            userId = mTUser.getUserId();
            mTUser__resolvedKey = userId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public MTGrain getMTGrain() {
        long __key = this.grainId;
        if (mTGrain__resolvedKey == null || !mTGrain__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MTGrainDao targetDao = daoSession.getMTGrainDao();
            MTGrain mTGrainNew = targetDao.load(__key);
            synchronized (this) {
                mTGrain = mTGrainNew;
                mTGrain__resolvedKey = __key;
            }
        }
        return mTGrain;
    }

    public void setMTGrain(MTGrain mTGrain) {
        if (mTGrain == null) {
            throw new DaoException("To-one property 'grainId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.mTGrain = mTGrain;
            grainId = mTGrain.getGrainId();
            mTGrain__resolvedKey = grainId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
