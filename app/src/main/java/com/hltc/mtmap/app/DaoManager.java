package com.hltc.mtmap.app;

import com.hltc.mtmap.MFriend;
import com.hltc.mtmap.MFriendStatus;
import com.hltc.mtmap.MTMyFavourite;
import com.hltc.mtmap.MTMyGrain;
import com.hltc.mtmap.orm.DaoMaster;
import com.hltc.mtmap.orm.DaoSession;

import java.util.ArrayList;
import java.util.List;

public class DaoManager {

    private static DaoManager manager = new DaoManager();

    public DaoMaster daoMaster;
    public DaoSession daoSession;

    private DaoManager() {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(
                MyApplication.getContext(), AppConfig.APP_DB_NAME, null);
        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    public static DaoManager getManager() {
        return manager;
    }

    /**
     * *********************** 数据操作 *************************
     */
    public List<MFriend> getAllFriend() {
        List<MFriend> list = new ArrayList<>();
        try {
            list = DaoManager.getManager().daoSession.getMFriendDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * get friendstatus list
     */
    public List<MFriendStatus> getAllFriendStarus() {
        List<MFriendStatus> list = new ArrayList<>();
        try {
            list = DaoManager.getManager().daoSession.getMFriendStatusDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * get friendstatus list
     */
    public List<MTMyGrain> getAllMyGrains() {
        List<MTMyGrain> list = new ArrayList<>();
        try {
            list = DaoManager.getManager().daoSession.getMTMyGrainDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * get favourites list
     */
    public List<MTMyFavourite> getAllMyFavourites() {
        List<MTMyFavourite> list = new ArrayList<>();
        try {
            list = DaoManager.getManager().daoSession.getMTMyFavouriteDao().loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
