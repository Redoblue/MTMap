package com.hltc.mtmap.app;
//
//import android.content.Context;
//
//import com.hltc.mtmap.orm.DaoMaster;
//import com.hltc.mtmap.orm.DaoSession;
//import com.hltc.mtmap.orm.dao.MTCategoryDao;
//import com.hltc.mtmap.orm.dao.MTGrainDao;
//import com.hltc.mtmap.orm.dao.MTSiteDao;
//import com.hltc.mtmap.orm.models.MTGrain;
//import com.hltc.mtmap.orm.models.MTSite;
//
//import java.util.List;
//
//import de.greenrobot.dao.query.QueryBuilder;
//
///**
// * Created by merlin on 5/1/15.
// */
public class DaoManager {
//
//    private static DaoManager manager;
//    private Context context;
//
//    private DaoMaster daoMaster;
//    private DaoSession daoSession;
//
//    public static DaoManager getDaoManager(Context context) {
//        if (manager == null) {
//            manager = new DaoManager();
//            manager.context = context;
//        }
//        return manager;
//    }
//
//    public DaoMaster getDaoMaster() {
//        if (daoMaster == null) {
//            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, AppConfig.APP_DB_NAME, null);
//            daoMaster = new DaoMaster(helper.getWritableDatabase());
//        }
//        return daoMaster;
//    }
//
//    public DaoSession getDaoSession() {
//        if (daoSession == null) {
//            daoSession = getDaoMaster().newSession();
//        }
//        return daoSession;
//    }
//
//
//    /************************** 数据操作 **************************/
//
//    public List<MTGrain> getAllVisibleGrains() {
//        QueryBuilder qb = getDaoSession().getMTGrainDao().queryBuilder();
//        qb.where(MTGrainDao.Properties.IsIngored.eq("false"));
//        return qb.list();
//    }
//
//
}
