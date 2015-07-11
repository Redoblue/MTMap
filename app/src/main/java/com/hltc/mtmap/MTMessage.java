package com.hltc.mtmap;

import com.hltc.mtmap.orm.DaoSession;

import de.greenrobot.dao.DaoException;

import com.hltc.mtmap.orm.MTMessageDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table MTMESSAGE.
 */
public class MTMessage {

    private long id;
    private String type;
    private Long userId;
    private String portrait;
    private String nickName;
    private String remark;
    private Long grainId;
    private String name;
    private String address;
    private String image;
    private String text;
    private String commentTxt;
    private String createTime;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient MTMessageDao myDao;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public MTMessage() {
    }

    public MTMessage(long id) {
        this.id = id;
    }

    public MTMessage(long id, String type, Long userId, String portrait, String nickName, String remark, Long grainId, String name, String address, String image, String text, String commentTxt, String createTime) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.portrait = portrait;
        this.nickName = nickName;
        this.remark = remark;
        this.grainId = grainId;
        this.name = name;
        this.address = address;
        this.image = image;
        this.text = text;
        this.commentTxt = commentTxt;
        this.createTime = createTime;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMTMessageDao() : null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getGrainId() {
        return grainId;
    }

    public void setGrainId(Long grainId) {
        this.grainId = grainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCommentTxt() {
        return commentTxt;
    }

    public void setCommentTxt(String commentTxt) {
        this.commentTxt = commentTxt;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
