package com.hltc.mtmap.app;

import com.hltc.mtmap.orm.DaoMaster;
import com.hltc.mtmap.orm.DaoSession;
import com.hltc.mtmap.orm.model.MTUser;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

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
    public List<MTUser> getAllUsers() {
        QueryBuilder qb = daoSession.getMTUserDao().queryBuilder();
        return qb.listLazy();
    }

//    public List<MTGrain> getAllVisibleGrains() {
//        QueryBuilder qb = getDaoSession().getMTGrainDao().queryBuilder();
//        qb.where(MTGrainDao.Properties.IsIngored.eq("false"));
//        return qb.list();
//    }


}
