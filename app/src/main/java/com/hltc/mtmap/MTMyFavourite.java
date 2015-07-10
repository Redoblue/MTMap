package com.hltc.mtmap;

import com.hltc.mtmap.orm.DaoSession;

import de.greenrobot.dao.DaoException;

import com.hltc.mtmap.orm.MTMyFavouriteDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table MTMY_FAVOURITE.
 */
public class MTMyFavourite {

    private long grainId;
    private String text;
    private String createTime;
    private String siteName;
    private String address;
    private String image;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient MTMyFavouriteDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MTMyFavourite() {
    }

    public MTMyFavourite(long grainId) {
        this.grainId = grainId;
    }

    public MTMyFavourite(long grainId, String text, String createTime, String siteName, String address, String image) {
        this.grainId = grainId;
        this.text = text;
        this.createTime = createTime;
        this.siteName = siteName;
        this.address = address;
        this.image = image;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMTMyFavouriteDao() : null;
    }

    public long getGrainId() {
        return grainId;
    }

    public void setGrainId(long grainId) {
        this.grainId = grainId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
