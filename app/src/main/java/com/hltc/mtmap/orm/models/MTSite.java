package com.hltc.mtmap.orm.models;

import java.util.List;
import com.hltc.mtmap.orm.DaoSession;
import de.greenrobot.dao.DaoException;

import com.hltc.mtmap.orm.dao.MTGrainDao;
import com.hltc.mtmap.orm.dao.MTSiteDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table MTSITE.
 */
public class MTSite {

    private long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String address;
    private float latitude;
    private float longitude;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MTSiteDao myDao;

    private List<MTGrain> grains2Site;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MTSite() {
    }

    public MTSite(long id) {
        this.id = id;
    }

    public MTSite(long id, String name, String address, float latitude, float longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMTSiteDao() : null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    /** Not-null value. */
    public String getAddress() {
        return address;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MTGrain> getGrains2Site() {
        if (grains2Site == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MTGrainDao targetDao = daoSession.getMTGrainDao();
            List<MTGrain> grains2SiteNew = targetDao._queryMTSite_Grains2Site(id);
            synchronized (this) {
                if(grains2Site == null) {
                    grains2Site = grains2SiteNew;
                }
            }
        }
        return grains2Site;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetGrains2Site() {
        grains2Site = null;
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