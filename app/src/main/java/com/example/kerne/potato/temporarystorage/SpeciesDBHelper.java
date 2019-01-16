package com.example.kerne.potato.temporarystorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpeciesDBHelper extends SQLiteOpenHelper {
    private Context mContext = null;

    public static final String CREATE_SPECIES_TABLE = "create table SpeciesTable ("  //创建存储品种信息的表
            + "id integer primary key autoincrement, "  //id字段
            + "plotId text,"  //小区id字段 (+++++++++++)
            + "sowing_period text,"  //播种期字段
            + "emergence_period text,"  //出苗期字段
            + "rate_of_emergence integer,"  //出苗率字段
            + "squaring_period text,"  //现蕾期字段
            + "flowering_period text,"  //开花期字段
            + "leaf_color text,"  //叶颜色字段
            + "corolla_colors text,"  //花冠色字段
            + "plant_flourish text,"  //花繁茂性字段
            + "stem_color text,"  //茎色字段
            + "natural_fecundity text,"  //天然结实性字段
            + "mature_period text,"  //成熟期字段
            + "growing_days integer,"  //生育日数字段
            + "tuber_uniformity text,"  //块茎整齐度字段
            + "tuber_shape text,"  //薯型字段
            + "potato_skin_smoothness text,"  //薯皮光滑度字段
            + "eye text,"  //芽眼深浅字段
            + "skin_color text,"  //皮色字段
            + "flesh_color text,"  //肉色字段
            + "whether_to_be_included text,"  //是否入选字段
            + "remark text,"  //备注字段
            + "num_of_harvested_plants integer,"  //收获株数字段
            + "num_of_large_and_medium_potatoes integer,"  //大中薯数字段
            + "weight_of_large_and_medium_potatoes integer,"  //大中薯重字段
            + "num_of_small_potatoes integer,"  //小薯数字段
            + "weight_of_small_potatoes integer,"  //小薯重字段
            + "rate_of_economic_potato integer,"  //商品薯率字段
            + "small_section_yield1 integer,"  //小区产量1字段
            + "small_section_yield2 integer,"  //小区产量2字段
            + "small_section_yield3 integer,"  //小区产量3字段
            + "per_mu_yield integer,"  //亩产量字段
            + "ten_plant_height integer,"  //十株株高字段
            + "average_plant_height integer,"  //平均株高字段
            + "branch_num_of_ten_plants integer,"  //十株的分支数字段
            + "average_branch_num integer,"  //平均分支数字段
            + "yield_monitoring_of_ten_plants integer," //十株测产字段
            + "pic_path text)"; //图片路径字段

    public SpeciesDBHelper(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql_prepare = "if exists(select * from sys.object where name='SpeciesTable') drop table SpeciesTable";
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
