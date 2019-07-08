package com.example.kerne.potato.temporarystorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.kerne.potato.Species;

public class SpeciesDBHelper extends SQLiteOpenHelper {
    private Context mContext = null;

//    private static volatile SpeciesDBHelper sInstance = null;
//
//    public static SpeciesDBHelper getInstance() {
//        if (sInstance == null) {
//            synchronized (SpeciesDBHelper.class) {
//                if (sInstance == null) {
//                    sInstance = new SpeciesDBHelper();
//                }
//            }
//        }
//        return sInstance;
//    }

    public static final String CREATE_SPECIES_TABLE = "create table SpeciesTable ("  //创建存储品种信息的表
//            + "id text primary key, "  //id字段
            + "speciesId text,"  //品种id字段 (+++++++++++)
            + "blockId text,"
            + "experimentType text," //实验类型
//            + "isStoredTemporarily integer,"  //是否被暂存过
            + "plantingDate text,"  //播种期字段
            + "emergenceDate text,"  //出苗期字段
            + "sproutRate integer,"  //出苗率字段
            + "squaringStage text,"  //现蕾期字段
            + "blooming text,"  //开花期字段
            + "leafColour text,"  //叶颜色字段
            + "corollaColour text,"  //花冠色字段
            + "flowering text,"  //花繁茂性字段
            + "stemColour text,"  //茎色字段
            + "openpollinated text,"  //天然结实性字段 12
            + "maturingStage text,"  //成熟期字段
            + "growingPeriod integer,"  //生育日数字段
            + "uniformityOfTuberSize text,"  //块茎整齐度字段
            + "tuberShape text,"  //薯型字段
            + "skinSmoothness text,"  //薯皮光滑度字段
            + "eyeDepth text,"  //芽眼深浅字段
            + "skinColour text,"  //皮色字段
            + "fleshColour text,"  //肉色字段
            + "isChoozen text,"  //是否入选字段
            + "remark text,"  //备注字段
            + "harvestNum integer,"  //收获株数字段
            + "lmNum integer,"  //大中薯数字段
            + "lmWeight real,"  //大中薯重字段
            + "sNum integer,"  //小薯数字段
            + "sWeight real,"  //小薯重字段
            + "commercialRate integer,"  //商品薯率字段
            + "plotYield1 float,"  //小区产量1字段
            + "plotYield2 float,"  //小区产量2字段
            + "plotYield3 float,"  //小区产量3字段
            + "acreYield float,"  //亩产量字段
            + "bigPlantHeight1 float,"  //十株株高字段
            + "bigPlantHeight2 float,"
            + "bigPlantHeight3 float,"
            + "bigPlantHeight4 float,"
            + "bigPlantHeight5 float,"
            + "bigPlantHeight6 float,"
            + "bigPlantHeight7 float,"
            + "bigPlantHeight8 float,"
            + "bigPlantHeight9 float,"
            + "bigPlantHeight10 float,"
            + "plantHeightAvg float,"  //平均株高字段
            + "bigBranchNumber1 integer,"  //十株的分支数字段
            + "bigBranchNumber2 integer,"
            + "bigBranchNumber3 integer,"
            + "bigBranchNumber4 integer,"
            + "bigBranchNumber5 integer,"
            + "bigBranchNumber6 integer,"
            + "bigBranchNumber7 integer,"
            + "bigBranchNumber8 integer,"
            + "bigBranchNumber9 integer,"
            + "bigBranchNumber10 integer,"
            + "branchNumberAvg float,"  //平均分支数字段
            + "bigYield1 float," //大薯十株测产字段
            + "bigYield2 float,"
            + "bigYield3 float,"
            + "bigYield4 float,"
            + "bigYield5 float,"
            + "bigYield6 float,"
            + "bigYield7 float,"
            + "bigYield8 float,"
            + "bigYield9 float,"
            + "bigYield10 float,"
            + "smalYield1 float," //小薯十株测产
            + "smalYield2 float,"
            + "smalYield3 float,"
            + "smalYield4 float,"
            + "smalYield5 float,"
            + "smalYield6 float,"
            + "smalYield7 float,"
            + "smalYield8 float,"
            + "smalYield9 float,"
            + "smalYield10 float,"
            + "img1 text," //图片路径字段
            + "img2 text,"
            + "img3 text,"
            + "img4 text,"
            + "img5 text,"
            + "primary key(speciesId, blockId)"  //speciesId和blockId组成复合主键
            + ")";

    public static final String CREATE_LOCALSPECIES = "create table LocalSpecies ("
            + "speciesid text primary key,"
            + "name text)";

    public static final String CREATE_BIGFARMLIST = "create table BigfarmList ("
            + "bigfarmId text primary key,"
            + "name text,"
            + "description text,"
            + "img text,"
            + "year integer,"
            + "uri text"
            + ")";

    public static final String CREATE_FARMLIST = "create table FarmList ("
            + "farmlandId text primary key,"
            + "deleted text,"
            + "name text,"
            + "length integer,"
            + "width integer,"
            + "type text,"
            + "bigfarmId text)";

    public static final String CREATE_EXPERIMENTFIELD = "create table ExperimentField ("
            + "id text primary key,"
            + "name text,"
            + "deleted text,"
            + "expType text,"
            + "moveX integer,"
            + "moveY integer,"
            + "moveX1 integer,"
            + "moveY1 integer,"
            + "num text,"
            + "color text,"
            + "farmlandId text,"
            + "rows integer,"
            + "speciesList text)";

    public static final String CREATE_SPECIESLIST = "create table SpeciesList ("
            + "blockId text primary key,"
            + "fieldId text,"
            + "speciesId text,"
            + "x integer,"
            + "y integer)";

    public static final String CREATE_SPECIES_SEQUENCE = "create table SpeciesSequence ("
            + "id integer primary key autoincrement,"
            + "fieldId text,"
            + "NumofRows integer,"
            + "ContentofColumn text)";

    public SpeciesDBHelper(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql_prepare = "drop table SpeciesTable if exists(select * from sys.databases where name='SpeciesTable')";
//        db.execSQL(sql_prepare);
        db.execSQL(CREATE_SPECIES_TABLE);
        db.execSQL(CREATE_LOCALSPECIES);
        db.execSQL(CREATE_BIGFARMLIST);
        db.execSQL(CREATE_FARMLIST);
        db.execSQL(CREATE_EXPERIMENTFIELD);
        db.execSQL(CREATE_SPECIESLIST);
        db.execSQL(CREATE_SPECIES_SEQUENCE);
        //Toast.makeText(mContext, "创建品种信息表成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists SpeciesTable");
        db.execSQL("drop table if exists LocalSpecies");
        db.execSQL("drop table if exists BigfarmList");
        db.execSQL("drop table if exists FarmList");
        db.execSQL("drop table if exists ExperimentField");
        db.execSQL("drop table if exists SpeciesList");
        db.execSQL("drop table if exists SpeciesSequence");
        onCreate(db);
    }
}
