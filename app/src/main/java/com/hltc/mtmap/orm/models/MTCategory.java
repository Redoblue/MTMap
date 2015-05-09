package com.hltc.mtmap.orm.models;

import java.util.List;
import com.hltc.mtmap.orm.DaoSession;
import de.greenrobot.dao.DaoException;

import com.hltc.mtmap.orm.dao.MTCategoryDao;
import com.hltc.mtmap.orm.dao.MTGrainDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table MTCATEGORY.
 */
public class MTCategory {

    private long id;
    /** Not-null value. */
    private String name;
    /** Not-null value. */
    private String iconURL;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MTCategoryDao myDao;

    private List<MTGrain> grains2Category;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MTCategory() {
    }

    public MTCategory(long id) {
        this.id = id;
    }

    public MTCategory(long id, String name, String iconURL) {
        this.id = id;
        this.name = name;
        this.iconURL = iconURL;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMTCategoryDao() : null;
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
    public String getIconURL() {
        return iconURL;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MTGrain> getGrains2Category() {
        if (grains2Category == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MTGrainDao targetDao = daoSession.getMTGrainDao();
            List<MTGrain> grains2CategoryNew = targetDao._queryMTCategory_Grains2Category(id);
            synchronized (this) {
                if(grains2Category == null) {
                    grains2Category = grains2CategoryNew;
                }
            }
        }
        return grains2Category;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetGrains2Category() {
        grains2Category = null;
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