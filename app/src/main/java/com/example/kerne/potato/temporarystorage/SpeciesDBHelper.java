package com.example.kerne.potato.temporarystorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpeciesDBHelper extends SQLiteOpenHelper {
    private Context mContext = null;

    public static final String CREATE_SPECIES_TABLE = "create table SpeciesTable ("  //创建存储品种信息的表
            + "id integer primary key autoincrement, "  //id字段
            + "speciesId text,"  //小区id字段 (+++++++++++)
            + "plantingDate text,"  //播种期字段
            + "emergenceDate text,"  //出苗期字段
            + "sproutRate integer,"  //出苗率字段
            + "squaringStage text,"  //现蕾期字段
            + "blooming text,"  //开花期字段
            + "leafColour text,"  //叶颜色字段
            + "corollaColour text,"  //花冠色字段
            + "flowering text,"  //花繁茂性字段
            + "stemColour text,"  //茎色字段
            + "openpollinated text,"  //天然结实性字段
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
            + "lmWeight integer,"  //大中薯重字段
            + "sNum integer,"  //小薯数字段
            + "sWeight integer,"  //小薯重字段
            + "commercialRate integer,"  //商品薯率字段
            + "plotYield1 integer,"  //小区产量1字段
            + "plotYield2 integer,"  //小区产量2字段
            + "plotYield3 integer,"  //小区产量3字段
            + "acreYield integer,"  //亩产量字段
            + "bigPlantHeight integer,"  //十株株高字段
            + "plantHeightAvg integer,"  //平均株高字段
            + "bigBranchNumber integer,"  //十株的分支数字段
            + "branchNumberAvg integer,"  //平均分支数字段
            + "bigYield integer," //十株测产字段
            + "img1 text)"; //图片路径字段

    public SpeciesDBHelper(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql_prepare = "drop table SpeciesTable if exists(select * from sys.databases where name='SpeciesTable')";
//        db.execSQL(sql_prepare);
//        String string = "drop table SpeciesTable;";
//        db.execSQL(string);
        db.execSQL(CREATE_SPECIES_TABLE);
//        Toast.makeText(mContext, "创建品种信息表成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
