package com.hltc.mtmap.orm;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * 用来生成GreenDao类的类
 */
public class Generator {
    private static final int DB_VERSION = 1;
    private static final String JAVA_PACKAGE = "com.hltc.mtmap";
    private Schema schema = new Schema(DB_VERSION, JAVA_PACKAGE);

    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        generator.schema.setDefaultJavaPackageDao(JAVA_PACKAGE + ".orm");
        generator.schema.enableKeepSectionsByDefault();
        generator.schema.enableActiveEntitiesByDefault();

        generator.addEntities();

        new DaoGenerator().generateAll(generator.schema, "./app/src/main/java");
    }

    protected void addEntities() {
        /**
         * 建立数据库
         */
        Entity mtUser = schema.addEntity("MTUser");
        mtUser.addLongProperty("userId").primaryKey().notNull();
        mtUser.addStringProperty("nickName").notNull();
        mtUser.addStringProperty("createTime").notNull();
        mtUser.addStringProperty("phone").notNull();
        mtUser.addStringProperty("portrait").notNull();
        mtUser.addStringProperty("coverImg").notNull();
        mtUser.addStringProperty("signature").notNull();

        Entity mtGrain = schema.addEntity("MTGrain");
        mtGrain.addIdProperty().primaryKey().notNull();
        mtGrain.addStringProperty("description").notNull();
        mtGrain.addStringProperty("date").notNull();
        mtGrain.addBooleanProperty("isPublic").notNull();
        mtGrain.addBooleanProperty("isIngored").notNull();
        mtGrain.addDoubleProperty("latitude").notNull();
        mtGrain.addDoubleProperty("longitude").notNull();
        Property graSiteId = mtGrain.addLongProperty("siteId").notNull().getProperty();
        Property graCatId = mtGrain.addLongProperty("categoryId").notNull().getProperty();
        Property graUsrId = mtGrain.addLongProperty("userId").notNull().getProperty();

        Entity mtSite = schema.addEntity("MTSite");
        mtSite.addIdProperty().primaryKey().notNull();
        mtSite.addStringProperty("name").notNull();
        mtSite.addStringProperty("address").notNull();
        mtSite.addFloatProperty("latitude").notNull();
        mtSite.addFloatProperty("longitude").notNull();

        Entity mtPhoto = schema.addEntity("MTPhoto");
        mtPhoto.addIdProperty().primaryKey().notNull();
        mtPhoto.addStringProperty("previewURL").notNull();
        mtPhoto.addStringProperty("thumbnailURL").notNull();
        Property phoUsrId = mtPhoto.addLongProperty("userId").notNull().getProperty();
        Property phoGraId = mtPhoto.addLongProperty("grainId").notNull().getProperty();

        Entity mtCategory = schema.addEntity("MTCategory");
        mtCategory.addIdProperty().primaryKey().notNull();
        mtCategory.addStringProperty("name").notNull();
        mtCategory.addStringProperty("iconURL").notNull();

        Entity mtComment = schema.addEntity("MTComment");
        mtComment.addIdProperty().primaryKey().notNull();
        mtComment.addStringProperty("content").notNull();
        mtComment.addDateProperty("date").notNull();
        Property toCommentId = mtComment.addLongProperty("toCommentId").getProperty();
        Property comUserId = mtComment.addLongProperty("userId").getProperty();
        Property comGrainId = mtComment.addLongProperty("grainId").notNull().getProperty();

        Entity mtFavourite = schema.addEntity("MTFavourite");
        mtFavourite.addIdProperty().primaryKey().notNull();
        Property favUserId = mtFavourite.addLongProperty("userId").notNull().getProperty();
        Property favGrainId = mtFavourite.addLongProperty("grainId").notNull().getProperty();


        /**
         * 数据库关系建立
         */
        mtUser.addToMany(mtComment, comUserId).setName("comments2User");
        mtUser.addToMany(mtFavourite, favUserId).setName("favourite2User");
        mtUser.addToMany(mtGrain, graUsrId).setName("grains2User");
        mtUser.addToMany(mtPhoto, phoUsrId).setName("photo2User");

        mtGrain.addToOne(mtSite, graSiteId);
        mtGrain.addToOne(mtUser, graUsrId);
        mtGrain.addToOne(mtCategory, graCatId);
        mtGrain.addToMany(mtComment, comGrainId).setName("comments2Grain");
        mtGrain.addToMany(mtFavourite, favGrainId).setName("favourites2Grain");
        mtGrain.addToMany(mtPhoto, phoGraId).setName("photos2Grain");

        mtComment.addToOne(mtUser, comUserId);
        mtComment.addToOne(mtGrain, comGrainId);
        mtComment.addToOne(mtComment, toCommentId).setName("parent");
        mtComment.addToMany(mtComment, toCommentId).setName("children");

        mtFavourite.addToOne(mtUser, favUserId);
        mtFavourite.addToOne(mtGrain, favGrainId);

        mtPhoto.addToOne(mtUser, phoUsrId);
        mtPhoto.addToOne(mtGrain, phoGraId);

        mtSite.addToMany(mtGrain, graSiteId).setName("grains2Site");

        mtCategory.addToMany(mtGrain, graCatId).setName("grains2Category");
    }
}
