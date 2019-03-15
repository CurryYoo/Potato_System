package com.example.kerne.potato.temporarystorage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kerne.potato.MainActivity;
import com.example.kerne.potato.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

public class SaveDataActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //需要暂存的各字段
    //品种id
    private String speciesId;
    EditText edtSpeciesID = null;
    private String commitId = "initial id";
    //实验类型
    EditText edtExperimentType = null;
    EditText edtSowingPeriodInput = null;
    EditText edtEmergencePeriod = null;
    EditText edtRateOfEmergence = null;
    EditText edtSquaringPeriod = null;
    EditText edtFloweringPeriod = null;

    private Spinner spnLeafColor = null;
    private Spinner spnCorollaColors = null;
    private Spinner spnPlantFlourish = null;
    private Spinner spnStemColor = null;
    private Spinner spnNaturalFecundity = null;

    EditText edtMaturePeriod = null;

    private EditText edtGrowingDays = null;
    private Spinner spnTuberUniformity = null;
    private Spinner spnTuberShape = null;
    private Spinner spnPotatoSkinSmoothness = null;
    private Spinner spnEye = null;
    private Spinner spnSkinColor = null;
    private Spinner spnFleshColor = null;
    private RadioGroup rgWhetherToBeIncluded = null;
    private EditText edtRemark = null;
    private EditText edtNumOfHarvestedPlants = null;
    private EditText edtNumOfLargeAndMediumPotatoes = null;
    private EditText edtWeightOfLargeAndMediumPotatoes = null;
    private EditText edtNumOfSmallPotatoes = null;
    private EditText edtWeightOfSmallPotatoes = null;
    private EditText edtRateOfEconomicPotato = null;
    private EditText edtSmallSectionYield1 = null;
    private EditText edtSmallSectionYield2 = null;
    private EditText edtSmallSectionYield3 = null;
    private EditText edtPerMuYield = null;
    //大薯十株株高
    private EditText edtBigPotatoHeight1 = null;
    private EditText edtBigPotatoHeight2 = null;
    private EditText edtBigPotatoHeight3 = null;
    private EditText edtBigPotatoHeight4 = null;
    private EditText edtBigPotatoHeight5 = null;
    private EditText edtBigPotatoHeight6 = null;
    private EditText edtBigPotatoHeight7 = null;
    private EditText edtBigPotatoHeight8 = null;
    private EditText edtBigPotatoHeight9 = null;
    private EditText edtBigPotatoHeight10 = null;
    //大薯平均株高
    private EditText edtAveragePlantHeightOfBigPotato = null;
    //大薯十株分支数
    private EditText edtBranchNumOfBigPotato1 = null;
    private EditText edtBranchNumOfBigPotato2 = null;
    private EditText edtBranchNumOfBigPotato3 = null;
    private EditText edtBranchNumOfBigPotato4 = null;
    private EditText edtBranchNumOfBigPotato5 = null;
    private EditText edtBranchNumOfBigPotato6 = null;
    private EditText edtBranchNumOfBigPotato7 = null;
    private EditText edtBranchNumOfBigPotato8 = null;
    private EditText edtBranchNumOfBigPotato9 = null;
    private EditText edtBranchNumOfBigPotato10 = null;
//    大薯平均分支数
    private EditText edtAverageBranchNumOfBigPotato = null;
//    大薯十株测产
    private EditText edtYieldMonitoringOfBigPotato1 = null;
    private EditText edtYieldMonitoringOfBigPotato2 = null;
    private EditText edtYieldMonitoringOfBigPotato3 = null;
    private EditText edtYieldMonitoringOfBigPotato4 = null;
    private EditText edtYieldMonitoringOfBigPotato5 = null;
    private EditText edtYieldMonitoringOfBigPotato6 = null;
    private EditText edtYieldMonitoringOfBigPotato7 = null;
    private EditText edtYieldMonitoringOfBigPotato8 = null;
    private EditText edtYieldMonitoringOfBigPotato9 = null;
    private EditText edtYieldMonitoringOfBigPotato10 = null;


    //小薯十株株高
    private EditText edtSmallPotatoHeight1 = null;
    private EditText edtSmallPotatoHeight2 = null;
    private EditText edtSmallPotatoHeight3 = null;
    private EditText edtSmallPotatoHeight4 = null;
    private EditText edtSmallPotatoHeight5 = null;
    private EditText edtSmallPotatoHeight6 = null;
    private EditText edtSmallPotatoHeight7 = null;
    private EditText edtSmallPotatoHeight8 = null;
    private EditText edtSmallPotatoHeight9 = null;
    private EditText edtSmallPotatoHeight10 = null;
    //小薯平均株高
    private EditText edtAveragePlantHeightOfSmallPotato = null;
    //小薯十株分支数
    private EditText edtBranchNumOfSmallPotato1 = null;
    private EditText edtBranchNumOfSmallPotato2 = null;
    private EditText edtBranchNumOfSmallPotato3 = null;
    private EditText edtBranchNumOfSmallPotato4 = null;
    private EditText edtBranchNumOfSmallPotato5 = null;
    private EditText edtBranchNumOfSmallPotato6 = null;
    private EditText edtBranchNumOfSmallPotato7 = null;
    private EditText edtBranchNumOfSmallPotato8 = null;
    private EditText edtBranchNumOfSmallPotato9 = null;
    private EditText edtBranchNumOfSmallPotato10 = null;
    //    小薯平均分支数
    private EditText edtAverageBranchNumOfSmallPotato = null;
    //    小薯十株测产
    private EditText edtYieldMonitoringOfSmallPotato1 = null;
    private EditText edtYieldMonitoringOfSmallPotato2 = null;
    private EditText edtYieldMonitoringOfSmallPotato3 = null;
    private EditText edtYieldMonitoringOfSmallPotato4 = null;
    private EditText edtYieldMonitoringOfSmallPotato5 = null;
    private EditText edtYieldMonitoringOfSmallPotato6 = null;
    private EditText edtYieldMonitoringOfSmallPotato7 = null;
    private EditText edtYieldMonitoringOfSmallPotato8 = null;
    private EditText edtYieldMonitoringOfSmallPotato9 = null;
    private EditText edtYieldMonitoringOfSmallPotato10 = null;

    //叶颜色拍照
    public static final int TAKE_PHOTO_COLOR = 1;
    private ImageView ivShowColor = null;
    private Uri imageUriColor = null;
    private String pathColor = null; //图片文件路径
    //花冠色拍照
    public static final int TAKE_PHOTO_COROLLA_COLOR = 2;
    private ImageView ivShowCorollaColor = null;
    private Uri imageUriCorollaColor = null;
    //花繁茂性拍照
    public static final int TAKE_PHOTO_PLANT_FLOURISH = 3;
    private ImageView ivShowPlantFlourish = null;
    private Uri imageUriPlantFlourish = null;
    //茎色拍照
    public static final int TAKE_PHOTO_STEM_COLORS = 4;
    private ImageView ivShowStemColors = null;
    private Uri imageUriStemColors = null;
    //天然结实性拍照
    public static final int TAKE_PHOTO_NATURAL_FECUNDITY = 5;
    private ImageView ivShowNaturalFecundity = null;
    private Uri imageUriNaturalFecundity = null;
    //块茎整齐度拍照
    public static final int TAKE_PHOTO_TUBER_UNIFORMITY = 6;
    private ImageView ivShowTuberUniformity = null;
    private Uri imageUriTuberUniformity = null;
    //薯型拍照
    public static final int TAKE_PHOTO_TUBER_SHAPE = 7;
    private ImageView ivShowTuberShape = null;
    private Uri imageUriTuberShape = null;
    //薯皮光滑度拍照
    public static final int TAKE_PHOTO_POTATO_SKIN_SMOOTHNESS = 8;
    private ImageView ivShowPotatoSkinSmoothness = null;
    private Uri imageUriPotatoSkinSmoothness = null;
    //芽眼深浅拍照
    public static final int TAKE_PHOTO_EYE = 9;
    private ImageView ivShowEye = null;
    private Uri imageUriEye = null;
    //皮色拍照
    public static final int TAKE_PHOTO_SKIN_COLOR = 10;
    private ImageView ivShowSkinColor = null;
    private Uri imageUriSkinColor = null;
    //肉色拍照
    public static final int TAKE_PHOTO_FLESH_COLOR = 11;
    private ImageView ivShowFleshColor = null;
    private Uri imageUriFleshColor = null;

    //暂存功能
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_commit_data);

        //品种Id，判断是否重复存储
        edtSpeciesID = (EditText) findViewById(R.id.edt_species_id);

        //实验类型
        edtExperimentType = (EditText) findViewById(R.id.edt_experiment_type);

        //播种期
        edtSowingPeriodInput = (EditText) findViewById(R.id.sowing_period_input);
        edtSowingPeriodInput.setInputType(InputType.TYPE_NULL);
//        edtSowingPeriodInput.setOnFocusChangeListener(this);
        edtSowingPeriodInput.setOnClickListener(this);

        //出苗期
        edtEmergencePeriod = (EditText) findViewById(R.id.emergence_period);
        edtEmergencePeriod.setInputType(InputType.TYPE_NULL);
//        edtEmergencePeriod.setOnFocusChangeListener(this);
        edtEmergencePeriod.setOnClickListener(this);

        //出苗率
        edtRateOfEmergence = (EditText) findViewById(R.id.edt_rate_of_emergence);

        //现蕾期
        edtSquaringPeriod = (EditText) findViewById(R.id.squaring_period);
        edtSquaringPeriod.setInputType(InputType.TYPE_NULL);
        edtSquaringPeriod.setOnClickListener(this);

        //开花期
        edtFloweringPeriod = (EditText) findViewById(R.id.flowering_period);
        edtFloweringPeriod.setInputType(InputType.TYPE_NULL);
        edtFloweringPeriod.setOnClickListener(this);

        //叶颜色
        spnLeafColor = (Spinner) findViewById(R.id.spn_colors);
        //叶颜色拍照
        ImageButton imbTakePhotoColor = (ImageButton) findViewById(R.id.imb_colors);
        imbTakePhotoColor.setOnClickListener(this);
        ivShowColor = (ImageView) findViewById(R.id.imv_colors);
        ivShowColor.setOnClickListener(this);

        //花冠色
        spnCorollaColors = (Spinner) findViewById(R.id.corolla_colors);
        //花冠色拍照
        ImageButton imbTakePhotoCorollaColors = (ImageButton) findViewById(R.id.imb_corolla_colors);
        imbTakePhotoCorollaColors.setOnClickListener(this);
        ivShowCorollaColor = (ImageView) findViewById(R.id.imv_corolla_colors);
        ivShowCorollaColor.setOnClickListener(this);

        //花繁茂性
        spnPlantFlourish = (Spinner) findViewById(R.id.plant_flourish);
        //花繁茂性拍照
        ImageButton imbTakePhotoPlantFlourish = (ImageButton) findViewById(R.id.imb_plant_flourish);
        imbTakePhotoPlantFlourish.setOnClickListener(this);
        ivShowPlantFlourish = (ImageView) findViewById(R.id.imv_plant_flourish);
        ivShowPlantFlourish.setOnClickListener(this);

        //茎色
        spnStemColor = (Spinner) findViewById(R.id.stem_color);
        //茎色拍照
        ImageButton imbTakePhotoStemColor = (ImageButton) findViewById(R.id.imb_stem_color);
        imbTakePhotoStemColor.setOnClickListener(this);
        ivShowStemColors = (ImageView) findViewById(R.id.imv_stem_color);
        ivShowStemColors.setOnClickListener(this);

        //天然结实性
        spnNaturalFecundity = (Spinner) findViewById(R.id.natural_fecundity);
        //天然结实性拍照
        ImageButton imbTakePhotoNaturalFecundity = (ImageButton) findViewById(R.id.imb_natural_fecundity);
        imbTakePhotoNaturalFecundity.setOnClickListener(this);
        ivShowNaturalFecundity = (ImageView) findViewById(R.id.imv_natural_fecundity);
        ivShowNaturalFecundity.setOnClickListener(this);


        //成熟期
        edtMaturePeriod = (EditText) findViewById(R.id.mature_period);
        edtMaturePeriod.setInputType(InputType.TYPE_NULL);
        edtMaturePeriod.setOnClickListener(this);

        //生育日数
        edtGrowingDays = (EditText) findViewById(R.id.growing_days);

        //块茎整齐度
        spnTuberUniformity = (Spinner) findViewById(R.id.tuber_uniformity);
        //块茎整齐度拍照
//        ImageButton imbTakePhotoTuberUniformity = (ImageButton) findViewById(R.id.imb_tuber_uniformity);
//        imbTakePhotoTuberUniformity.setOnClickListener(this);
//        ivShowTuberUniformity = (ImageView) findViewById(R.id.imv_tuber_uniformity);
//        ivShowTuberUniformity.setOnClickListener(this);

        //薯型
        spnTuberShape = (Spinner) findViewById(R.id.tuber_shape);
        //薯型拍照
//        ImageButton imbTakePhotoTuberShape = (ImageButton) findViewById(R.id.imb_tuber_shape);
//        imbTakePhotoTuberShape.setOnClickListener(this);
//        ivShowTuberShape = (ImageView) findViewById(R.id.imv_tuber_shape);
//        ivShowTuberShape.setOnClickListener(this);

        //薯皮光滑度
        spnPotatoSkinSmoothness = (Spinner) findViewById(R.id.potato_skin_smoothness);
        //薯皮光滑度拍照
//        ImageButton imbTakePhotoPotatoSkinSmoothness = (ImageButton) findViewById(R.id.imb_potato_skin_smoothness);
//        imbTakePhotoPotatoSkinSmoothness.setOnClickListener(this);
//        ivShowPotatoSkinSmoothness = (ImageView) findViewById(R.id.imv_potato_skin_smoothness);
//        ivShowPotatoSkinSmoothness.setOnClickListener(this);

        //芽眼深浅
        spnEye = (Spinner) findViewById(R.id.eye);
        //芽眼深浅拍照
//        ImageButton imbTakePhotoEye = (ImageButton) findViewById(R.id.imb_eye);
//        imbTakePhotoEye.setOnClickListener(this);
//        ivShowEye = (ImageView) findViewById(R.id.imv_potato_skin_smoothness);
//        ivShowEye.setOnClickListener(this);

        //皮色
        spnSkinColor = (Spinner) findViewById(R.id.skin_color);
        //皮色拍照
//        ImageButton imbTakePhotoSkinColor = (ImageButton) findViewById(R.id.imb_skin_color);
//        imbTakePhotoSkinColor.setOnClickListener(this);
//        ivShowSkinColor = (ImageView) findViewById(R.id.imv_skin_color);
//        ivShowSkinColor.setOnClickListener(this);

        //肉色
        spnFleshColor = (Spinner) findViewById(R.id.flesh_color);
        //肉色拍照
//        ImageButton imbTakePhotoFleshColor = (ImageButton) findViewById(R.id.imb_flesh_color);
//        imbTakePhotoFleshColor.setOnClickListener(this);
//        ivShowFleshColor = (ImageView) findViewById(R.id.imv_flesh_color);
//        ivShowFleshColor.setOnClickListener(this);

        //是否入选
        rgWhetherToBeIncluded = (RadioGroup) findViewById(R.id.whether_to_be_included);

        //备注
        edtRemark = (EditText) findViewById(R.id.remark);

        //收获株数
        edtNumOfHarvestedPlants = (EditText) findViewById(R.id.num_of_harvested_plants);

        //大中薯数
        edtNumOfLargeAndMediumPotatoes = (EditText) findViewById(R.id.num_of_large_and_medium_potatoes);

        //大中薯重
        edtWeightOfLargeAndMediumPotatoes = (EditText) findViewById(R.id.weight_of_large_and_medium_potatoes);

        //小薯数
        edtNumOfSmallPotatoes = (EditText) findViewById(R.id.num_of_small_potatoes);

        //小薯重
        edtWeightOfSmallPotatoes = (EditText) findViewById(R.id.weight_of_small_potatoes);

        //商品薯率
        edtRateOfEconomicPotato = (EditText) findViewById(R.id.rate_of_economic_potato);

        //小区产量1
        edtSmallSectionYield1 = (EditText) findViewById(R.id.small_section_yield1);

        //小区产量2
        edtSmallSectionYield2 = (EditText) findViewById(R.id.small_section_yield2);

        //小区产量3
        edtSmallSectionYield3 = (EditText) findViewById(R.id.small_section_yield3);

        //亩产量
        edtPerMuYield = (EditText) findViewById(R.id.per_mu_yield);

        //大薯十株株高
        edtBigPotatoHeight1 = (EditText) findViewById(R.id.ten_plant_height1);
        edtBigPotatoHeight2 = (EditText) findViewById(R.id.ten_plant_height2);
        edtBigPotatoHeight3 = (EditText) findViewById(R.id.ten_plant_height3);
        edtBigPotatoHeight4 = (EditText) findViewById(R.id.ten_plant_height4);
        edtBigPotatoHeight5 = (EditText) findViewById(R.id.ten_plant_height5);
        edtBigPotatoHeight6 = (EditText) findViewById(R.id.ten_plant_height6);
        edtBigPotatoHeight7 = (EditText) findViewById(R.id.ten_plant_height7);
        edtBigPotatoHeight8 = (EditText) findViewById(R.id.ten_plant_height8);
        edtBigPotatoHeight9 = (EditText) findViewById(R.id.ten_plant_height9);
        edtBigPotatoHeight10 = (EditText) findViewById(R.id.ten_plant_height10);

        //大薯平均株高
        edtAveragePlantHeightOfBigPotato = (EditText) findViewById(R.id.average_plant_height);
        //计算大薯的平均株高
        Button btnComputeAveragePlantHeight = (Button) findViewById(R.id.btn_compute_average_plant_height);
        btnComputeAveragePlantHeight.setOnClickListener(this);

        //大薯十株分支数
        edtBranchNumOfBigPotato1 = (EditText) findViewById(R.id.branch_num_of_ten_plant1);
        edtBranchNumOfBigPotato2 = (EditText) findViewById(R.id.branch_num_of_ten_plant2);
        edtBranchNumOfBigPotato3 = (EditText) findViewById(R.id.branch_num_of_ten_plant3);
        edtBranchNumOfBigPotato4 = (EditText) findViewById(R.id.branch_num_of_ten_plant4);
        edtBranchNumOfBigPotato5 = (EditText) findViewById(R.id.branch_num_of_ten_plant5);
        edtBranchNumOfBigPotato6 = (EditText) findViewById(R.id.branch_num_of_ten_plant6);
        edtBranchNumOfBigPotato7 = (EditText) findViewById(R.id.branch_num_of_ten_plant7);
        edtBranchNumOfBigPotato8 = (EditText) findViewById(R.id.branch_num_of_ten_plant8);
        edtBranchNumOfBigPotato9 = (EditText) findViewById(R.id.branch_num_of_ten_plant9);
        edtBranchNumOfBigPotato10 = (EditText) findViewById(R.id.branch_num_of_ten_plant10);

        //大薯平均分支数
        edtAverageBranchNumOfBigPotato = (EditText) findViewById(R.id.average_branch_num);
        Button btnComputeAverageBranchNum = (Button) findViewById(R.id.btn_compute_average_branch_num);
        btnComputeAverageBranchNum.setOnClickListener(this);
        //大薯十株测产
        edtYieldMonitoringOfBigPotato1 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant1);
        edtYieldMonitoringOfBigPotato2 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant2);
        edtYieldMonitoringOfBigPotato3 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant3);
        edtYieldMonitoringOfBigPotato4 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant4);
        edtYieldMonitoringOfBigPotato5 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant5);
        edtYieldMonitoringOfBigPotato6 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant6);
        edtYieldMonitoringOfBigPotato7 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant7);
        edtYieldMonitoringOfBigPotato8 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant8);
        edtYieldMonitoringOfBigPotato9 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant9);
        edtYieldMonitoringOfBigPotato10 = (EditText) findViewById(R.id.yield_monitoring_of_ten_plant10);



        //小薯十株株高
        edtSmallPotatoHeight1 = (EditText) findViewById(R.id.small_height1);
        edtSmallPotatoHeight2 = (EditText) findViewById(R.id.small_height2);
        edtSmallPotatoHeight3 = (EditText) findViewById(R.id.small_height2);
        edtSmallPotatoHeight4 = (EditText) findViewById(R.id.small_height3);
        edtSmallPotatoHeight5 = (EditText) findViewById(R.id.small_height5);
        edtSmallPotatoHeight6 = (EditText) findViewById(R.id.small_height6);
        edtSmallPotatoHeight7 = (EditText) findViewById(R.id.small_height7);
        edtSmallPotatoHeight8 = (EditText) findViewById(R.id.small_height8);
        edtSmallPotatoHeight9 = (EditText) findViewById(R.id.small_height9);
        edtSmallPotatoHeight10 = (EditText) findViewById(R.id.small_height10);

        //小薯平均株高
//        edtAveragePlantHeightOfSmallPotato = (EditText) findViewById(R.id.average_small_plant_height);
        //计算小薯的平均株高
//        Button btnComputeAverageHeightOfSmallPotato = (Button) findViewById(R.id.btn_compute_average_small_plant_height);
//        btnComputeAverageHeightOfSmallPotato.setOnClickListener(this);

        //小薯十株分支数
        edtBranchNumOfSmallPotato1 = (EditText) findViewById(R.id.branch_num_of_small_plant1);
        edtBranchNumOfSmallPotato2 = (EditText) findViewById(R.id.branch_num_of_small_plant2);
        edtBranchNumOfSmallPotato3 = (EditText) findViewById(R.id.branch_num_of_small_plant3);
        edtBranchNumOfSmallPotato4 = (EditText) findViewById(R.id.branch_num_of_small_plant4);
        edtBranchNumOfSmallPotato5 = (EditText) findViewById(R.id.branch_num_of_small_plant5);
        edtBranchNumOfSmallPotato6 = (EditText) findViewById(R.id.branch_num_of_small_plant6);
        edtBranchNumOfSmallPotato7 = (EditText) findViewById(R.id.branch_num_of_small_plant7);
        edtBranchNumOfSmallPotato8 = (EditText) findViewById(R.id.branch_num_of_small_plant8);
        edtBranchNumOfSmallPotato9 = (EditText) findViewById(R.id.branch_num_of_small_plant9);
        edtBranchNumOfSmallPotato10 = (EditText) findViewById(R.id.branch_num_of_small_plant10);

        //小薯平均分支数
//        edtAverageBranchNumOfSmallPotato = (EditText) findViewById(R.id.average_small_branch_num);
//        Button btnComputeAverageBranchNumOfSmallPotato = (Button) findViewById(R.id.btn_compute_average_small_branch_num);
//        btnComputeAverageBranchNum.setOnClickListener(this);
        //小薯十株测产
        edtYieldMonitoringOfSmallPotato1 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant1);
        edtYieldMonitoringOfSmallPotato2 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant2);
        edtYieldMonitoringOfSmallPotato3 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant3);
        edtYieldMonitoringOfSmallPotato4 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant4);
        edtYieldMonitoringOfSmallPotato5 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant5);
        edtYieldMonitoringOfSmallPotato6 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant6);
        edtYieldMonitoringOfSmallPotato7 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant7);
        edtYieldMonitoringOfSmallPotato8 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant8);
        edtYieldMonitoringOfSmallPotato9 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant9);
        edtYieldMonitoringOfSmallPotato10 = (EditText) findViewById(R.id.yield_monitoring_of_small_plant10);
        //暂存监听
        Button btnSaveOffline = (Button) findViewById(R.id.save_offline);
        btnSaveOffline.setOnClickListener(this);

        //数据存储
        dbHelper = new SpeciesDBHelper(this,
                "SpeciesTable.db", null, 1);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "Search item selected", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                this.finish();
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            default:
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.save_offline:
                //暂存数据到本地数据库
//                dbHelper.getWritableDatabase();
                String edtSpeciesIDContent = edtSpeciesID.getText().toString();
                if (edtSpeciesIDContent.isEmpty()) {
                    Toast.makeText(this, "请输入Id", Toast.LENGTH_LONG).show();
                    break;
                }
                if (commitId.equals(edtSpeciesIDContent)) {
                    Toast.makeText(this, "请输入不同Id", Toast.LENGTH_LONG).show();
                    break;
                } else {
                    commitId = edtSpeciesIDContent;
                }

                //从上一层获取小区id
                Intent intent_speciesId = getIntent();
                speciesId = intent_speciesId.getStringExtra("speciesId");

                ContentValues contentValues = new ContentValues();
                //开始组装数据
                //品种id
                contentValues.put("speciesId", speciesId);
                //实验类型
                contentValues.put("experimentType", edtExperimentType.getText().toString());
                //播种期
                contentValues.put("plantingDate", edtSowingPeriodInput.getText().toString());
                //出苗期
                contentValues.put("emergenceDate", edtEmergencePeriod.getText().toString());
                //出苗率
                String edtRateOfEmergenceContent = edtRateOfEmergence.getText().toString();
                contentValues.put("sproutRate", Integer.parseInt(edtRateOfEmergenceContent.isEmpty() ? "0" : edtRateOfEmergenceContent));
                //现蕾期
                contentValues.put("squaringStage", edtSquaringPeriod.getText().toString());
                //开花期
                contentValues.put("blooming", edtFloweringPeriod.getText().toString());
                //叶颜色
                contentValues.put("leafColour", spnLeafColor.getSelectedItem().toString());
                //花冠色
                contentValues.put("corollaColour", spnCorollaColors.getSelectedItem().toString());
                //花繁茂性
                contentValues.put("flowering", spnPlantFlourish.getSelectedItem().toString());
                //茎色
                contentValues.put("stemColour", spnStemColor.getSelectedItem().toString());
                //天然结实性
                contentValues.put("openpollinated", spnNaturalFecundity.getSelectedItem().toString());
                //成熟期
                contentValues.put("maturingStage", edtMaturePeriod.getText().toString());
                //生育日数
                String edtGrowingDaysContent = edtGrowingDays.getText().toString();
                contentValues.put("growingPeriod", Integer.parseInt(edtGrowingDaysContent.isEmpty() ? "0" : edtGrowingDaysContent));
                //块茎整齐度
                contentValues.put("uniformityOfTuberSize", spnTuberUniformity.getSelectedItem().toString());
                //薯型
                contentValues.put("tuberShape", spnTuberShape.getSelectedItem().toString());
                //薯皮光滑度
                contentValues.put("skinSmoothness", spnPotatoSkinSmoothness.getSelectedItem().toString());
                //芽眼深浅
                contentValues.put("eyeDepth", spnEye.getSelectedItem().toString());
                //皮色
                contentValues.put("skinColour", spnSkinColor.getSelectedItem().toString());
                //肉色
                contentValues.put("fleshColour", spnFleshColor.getSelectedItem().toString());
                //是否入选 对应deleted字段
                int checkedRadioButtonId = rgWhetherToBeIncluded.getCheckedRadioButtonId();
                Boolean checkedRadioButtonContent;
                switch (checkedRadioButtonId) {
                    case R.id.no:
                        checkedRadioButtonContent = false;
                        break;
                    case R.id.yes:
                    default:
                        checkedRadioButtonContent = true;
                }
                contentValues.put("isChoozen",  checkedRadioButtonContent);
                //备注
                contentValues.put("remark", edtRemark.getText().toString());
                //收获株数
                String edtNumOfHarvestedPlantsContent = edtNumOfHarvestedPlants.getText().toString();
                contentValues.put("harvestNum", Integer.parseInt(edtNumOfHarvestedPlantsContent.isEmpty() ? "0" : edtNumOfHarvestedPlantsContent));
                //大中薯数
                String edtNumOfLargeAndMediumPotatoesContent = edtNumOfLargeAndMediumPotatoes.getText().toString();
                contentValues.put("lmNum", Integer.parseInt(edtNumOfLargeAndMediumPotatoesContent.isEmpty() ? "0" : edtNumOfLargeAndMediumPotatoesContent));
                //大中薯重
                String edtWeightOfLargeAndMediumPotatoesContent = edtWeightOfLargeAndMediumPotatoes.getText().toString();
                contentValues.put("lmWeight", Integer.parseInt(edtWeightOfLargeAndMediumPotatoesContent.isEmpty() ? "0" : edtWeightOfLargeAndMediumPotatoesContent));
                //小薯数
                String edtNumOfSmallPotatoesContent = edtNumOfSmallPotatoes.getText().toString();
                contentValues.put("sNum", Integer.parseInt(edtNumOfSmallPotatoesContent.isEmpty() ? "0" : edtNumOfSmallPotatoesContent));
                //小薯重
                String edtWeightOfSmallPotatoesContent = edtWeightOfSmallPotatoes.getText().toString();
                contentValues.put("sWeight", Integer.parseInt(edtWeightOfSmallPotatoesContent.isEmpty() ? "0" : edtWeightOfSmallPotatoesContent));
                //商品薯率
                String edtRateOfEconomicPotatoContent = edtRateOfEconomicPotato.getText().toString();
                contentValues.put("commercialRate", Integer.parseInt(edtRateOfEconomicPotatoContent.isEmpty() ? "0" : edtRateOfEconomicPotatoContent));
                //小区产量1
                String edtSmallSectionYield1Content = edtSmallSectionYield1.getText().toString();
                contentValues.put("plotYield1", Integer.parseInt(edtSmallSectionYield1Content.isEmpty() ? "0" : edtSmallSectionYield1Content));
                //小区产量2
                String edtSmallSectionYield2Content = edtSmallSectionYield2.getText().toString();
                contentValues.put("plotYield2", Integer.parseInt(edtSmallSectionYield2Content.isEmpty() ? "0" : edtSmallSectionYield2Content));
                //小区产量3
                String edtSmallSectionYield3Content = edtSmallSectionYield3.getText().toString();
                contentValues.put("plotYield3", Integer.parseInt(edtSmallSectionYield3Content.isEmpty() ? "0" : edtSmallSectionYield3Content));
                //亩产量
                String edtPerMuYieldContent = edtPerMuYield.getText().toString();
                contentValues.put("acreYield", Integer.parseInt(edtPerMuYieldContent.isEmpty() ? "0" : edtPerMuYieldContent));
                //大薯十株株高
                //bigPlantHeight1
                String edtPlantHeightOfBigPotatoContent1 = edtBigPotatoHeight1.getText().toString();
                contentValues.put("bigPlantHeight1", Integer.parseInt(edtPlantHeightOfBigPotatoContent1.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent1));
                //bigPlantHeight2
                String edtPlantHeightOfBigPotatoContent2 = edtBigPotatoHeight2.getText().toString();
                contentValues.put("bigPlantHeight2", Integer.parseInt(edtPlantHeightOfBigPotatoContent2.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent2));
                //bigPlantHeight3
                String edtPlantHeightOfBigPotatoContent3 = edtBigPotatoHeight3.getText().toString();
                contentValues.put("bigPlantHeight3", Integer.parseInt(edtPlantHeightOfBigPotatoContent3.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent3));
                //bigPlantHeight4
                String edtPlantHeightOfBigPotatoContent4 = edtBigPotatoHeight4.getText().toString();
                contentValues.put("bigPlantHeight4", Integer.parseInt(edtPlantHeightOfBigPotatoContent4.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent4));
                //bigPlantHeight5
                String edtPlantHeightOfBigPotatoContent5 = edtBigPotatoHeight5.getText().toString();
                contentValues.put("bigPlantHeight5", Integer.parseInt(edtPlantHeightOfBigPotatoContent5.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent5));
                //bigPlantHeight6
                String edtPlantHeightOfBigPotatoContent6 = edtBigPotatoHeight6.getText().toString();
                contentValues.put("bigPlantHeight6", Integer.parseInt(edtPlantHeightOfBigPotatoContent6.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent6));
                //bigPlantHeight7
                String edtPlantHeightOfBigPotatoContent7 = edtBigPotatoHeight7.getText().toString();
                contentValues.put("bigPlantHeight7", Integer.parseInt(edtPlantHeightOfBigPotatoContent7.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent7));
                //bigPlantHeight8
                String edtPlantHeightOfBigPotatoContent8 = edtBigPotatoHeight8.getText().toString();
                contentValues.put("bigPlantHeight8", Integer.parseInt(edtPlantHeightOfBigPotatoContent8.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent8));
                //bigPlantHeight9
                String edtPlantHeightOfBigPotatoContent9 = edtBigPotatoHeight9.getText().toString();
                contentValues.put("bigPlantHeight9", Integer.parseInt(edtPlantHeightOfBigPotatoContent9.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent9));
                //bigPlantHeight10
                String edtPlantHeightOfBigPotatoContent10 = edtBigPotatoHeight10.getText().toString();
                contentValues.put("bigPlantHeight10", Integer.parseInt(edtPlantHeightOfBigPotatoContent10.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent10));

                //大薯平均株高
                String edtAveragePlantHeightContentOfBigPotato = edtAveragePlantHeightOfBigPotato.getText().toString();
                contentValues.put("plantHeightAvg", Integer.parseInt(edtAveragePlantHeightContentOfBigPotato.isEmpty() ? "0" : edtAveragePlantHeightContentOfBigPotato));
                //大薯十株分支数
                //十株的分支数一起暂存到本地，上传时可以拆分成 bigBranchNumber1,bigBranchNumber2,bigBranchNumber3....
                //bigBranchNumber1
                String edtBranchNumOfTBigPotatoContent1 = edtBranchNumOfBigPotato1.getText().toString();
                contentValues.put("bigBranchNumber1", Integer.parseInt(edtBranchNumOfTBigPotatoContent1.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent1));
                //bigBranchNumber2
                String edtBranchNumOfTBigPotatoContent2 = edtBranchNumOfBigPotato2.getText().toString();
                contentValues.put("bigBranchNumber2", Integer.parseInt(edtBranchNumOfTBigPotatoContent2.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent2));
                //bigBranchNumber3
                String edtBranchNumOfTBigPotatoContent3 = edtBranchNumOfBigPotato3.getText().toString();
                contentValues.put("bigBranchNumber3", Integer.parseInt(edtBranchNumOfTBigPotatoContent3.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent3));
                //bigBranchNumber4
                String edtBranchNumOfTBigPotatoContent4 = edtBranchNumOfBigPotato4.getText().toString();
                contentValues.put("bigBranchNumber4", Integer.parseInt(edtBranchNumOfTBigPotatoContent4.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent4));
                //bigBranchNumber5
                String edtBranchNumOfTBigPotatoContent5 = edtBranchNumOfBigPotato5.getText().toString();
                contentValues.put("bigBranchNumber5", Integer.parseInt(edtBranchNumOfTBigPotatoContent5.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent5));
                //bigBranchNumber6
                String edtBranchNumOfTBigPotatoContent6 = edtBranchNumOfBigPotato6.getText().toString();
                contentValues.put("bigBranchNumber5", Integer.parseInt(edtBranchNumOfTBigPotatoContent5.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent5));
                //bigBranchNumber7
                String edtBranchNumOfTBigPotatoContent7 = edtBranchNumOfBigPotato7.getText().toString();
                contentValues.put("bigBranchNumber7", Integer.parseInt(edtBranchNumOfTBigPotatoContent7.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent7));
                //bigBranchNumber8
                String edtBranchNumOfTBigPotatoContent8 = edtBranchNumOfBigPotato8.getText().toString();
                contentValues.put("bigBranchNumber8", Integer.parseInt(edtBranchNumOfTBigPotatoContent8.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent8));
                //bigBranchNumber9
                String edtBranchNumOfTBigPotatoContent9 = edtBranchNumOfBigPotato9.getText().toString();
                contentValues.put("bigBranchNumber9", Integer.parseInt(edtBranchNumOfTBigPotatoContent9.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent9));
                //bigBranchNumber10
                String edtBranchNumOfTBigPotatoContent10 = edtBranchNumOfBigPotato10.getText().toString();
                contentValues.put("bigBranchNumber10", Integer.parseInt(edtBranchNumOfTBigPotatoContent10.isEmpty() ? "0" : edtBranchNumOfTBigPotatoContent10));
                //平均分支数
                String edtAverageBranchNumOfBigPotatoContent = edtAverageBranchNumOfBigPotato.getText().toString();
                contentValues.put("branchNumberAvg", Integer.parseInt(edtAverageBranchNumOfBigPotatoContent.isEmpty() ? "0" : edtAverageBranchNumOfBigPotatoContent));
                //十株测产
                //十株株高一起暂存到本地，上传时可以拆分成 bigYield1,bigYield2,bigYield3....
//                String edtYieldMonitoringOfTenPlantsContent = edtYieldMonitoringOfTenPlants.getText().toString();
//                contentValues.put("bigYield", Integer.parseInt(edtYieldMonitoringOfTenPlantsContent.isEmpty() ? "0" : edtYieldMonitoringOfTenPlantsContent));

                contentValues.put("img1", pathColor);

                sqLiteDatabase.insert("SpeciesTable", null, contentValues);
                contentValues.clear();
                Toast.makeText(this,
                        "暂存成功，当手机在线时，请提交到远程服务器", Toast.LENGTH_LONG).show();
                break;
            //叶颜色拍照并显示
            case R.id.imb_colors:
                String fileNameString = System.currentTimeMillis() + ".jpg";
                File outputImage = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    outputImage = new File(getExternalCacheDir(), fileNameString);
                    pathColor = outputImage.getAbsolutePath();
                }
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUriColor = FileProvider.getUriForFile(SaveDataActivity.this,
                            "com.example.kerne.potato.fileprovider", outputImage);
                } else {
                    imageUriColor = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriColor);
                startActivityForResult(intent, TAKE_PHOTO_COLOR);
                break;
            //叶颜色查看大图
            case R.id.imv_colors:
                watchLargePhoto(imageUriColor);
                break;
            //花冠色拍照并显示
            case R.id.imb_corolla_colors:
                File outputImageCorollaColors = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                try {
                    outputImageCorollaColors.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUriCorollaColor = FileProvider.getUriForFile(SaveDataActivity.this,
                        "com.example.kerne.potato.fileprovider", outputImageCorollaColors);
                Intent intentCorollaColors = new Intent("android.media.action.IMAGE_CAPTURE");
                intentCorollaColors.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCorollaColor);
                startActivityForResult(intentCorollaColors, TAKE_PHOTO_COROLLA_COLOR);
                break;
            //花冠色查看大图
            case R.id.imv_corolla_colors:
                watchLargePhoto(imageUriCorollaColor);
                break;
            //花繁茂性拍照并显示
            case R.id.imb_plant_flourish:
                File outputImagePlantFlourish = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                try {
                    outputImagePlantFlourish.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUriPlantFlourish = FileProvider.getUriForFile(SaveDataActivity.this,
                        "com.example.kerne.potato.fileprovider", outputImagePlantFlourish);
                Intent intentPlantFlourish = new Intent("android.media.action.IMAGE_CAPTURE");
                intentPlantFlourish.putExtra(MediaStore.EXTRA_OUTPUT, imageUriPlantFlourish);
                startActivityForResult(intentPlantFlourish, TAKE_PHOTO_PLANT_FLOURISH);
                break;
            //花繁茂性查看大图
            case R.id.imv_plant_flourish:
                watchLargePhoto(imageUriPlantFlourish);
                break;
            //茎色拍照并显示
            case R.id.imb_stem_color:
                File outputImageStemColor = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                try {
                    outputImageStemColor.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUriStemColors = FileProvider.getUriForFile(SaveDataActivity.this,
                        "com.example.kerne.potato.fileprovider", outputImageStemColor);
                Intent intentStemColor = new Intent("android.media.action.IMAGE_CAPTURE");
                intentStemColor.putExtra(MediaStore.EXTRA_OUTPUT, imageUriStemColors);
                startActivityForResult(intentStemColor, TAKE_PHOTO_STEM_COLORS);
                break;
            //茎色查看大图
            case R.id.imv_stem_color:
                watchLargePhoto(imageUriStemColors);
                break;
            //天然结实性拍照并显示
            case R.id.imb_natural_fecundity:
                File outputImageNaturalFecundity = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                try {
                    outputImageNaturalFecundity.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUriNaturalFecundity = FileProvider.getUriForFile(SaveDataActivity.this,
                        "com.example.kerne.potato.fileprovider", outputImageNaturalFecundity);
                Intent intentNaturalFecundity = new Intent("android.media.action.IMAGE_CAPTURE");
                intentNaturalFecundity.putExtra(MediaStore.EXTRA_OUTPUT, imageUriNaturalFecundity);
                startActivityForResult(intentNaturalFecundity, TAKE_PHOTO_NATURAL_FECUNDITY);
                break;
            //天然结实性查看大图
            case R.id.imv_natural_fecundity:
                watchLargePhoto(imageUriNaturalFecundity);
                break;
            //块茎整齐度拍照并显示
//            case R.id.imb_tuber_uniformity:
//                File outputImageTuberUniformity = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImageTuberUniformity.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriTuberUniformity = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImageTuberUniformity);
//                Intent intentTuberUniformity = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentTuberUniformity.putExtra(MediaStore.EXTRA_OUTPUT, imageUriTuberUniformity);
//                startActivityForResult(intentTuberUniformity, TAKE_PHOTO_TUBER_UNIFORMITY);
//                break;
//            //块茎整齐度查看大图
//            case R.id.imv_tuber_uniformity:
//                watchLargePhoto(imageUriTuberUniformity);
//                break;
//            //薯型拍照并显示
//            case R.id.imb_tuber_shape:
//                File outputImageTuberShape = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImageTuberShape.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriTuberShape = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImageTuberShape);
//                Intent intentTuberShape = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentTuberShape.putExtra(MediaStore.EXTRA_OUTPUT, imageUriTuberShape);
//                startActivityForResult(intentTuberShape, TAKE_PHOTO_TUBER_SHAPE);
//                break;
//            //薯型查看大图
//            case R.id.imv_tuber_shape:
//                watchLargePhoto(imageUriTuberShape);
//                break;
//            //薯皮光滑度拍照并显示
//            case R.id.imb_potato_skin_smoothness:
//                File outputImagePotatoSkinSmoothness = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImagePotatoSkinSmoothness.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriPotatoSkinSmoothness = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImagePotatoSkinSmoothness);
//                Intent intentPotatoSkinSmoothness = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentPotatoSkinSmoothness.putExtra(MediaStore.EXTRA_OUTPUT, imageUriPotatoSkinSmoothness);
//                startActivityForResult(intentPotatoSkinSmoothness, TAKE_PHOTO_POTATO_SKIN_SMOOTHNESS);
//                break;
//            //薯皮光滑度查看大图
//            case R.id.imv_potato_skin_smoothness:
//                watchLargePhoto(imageUriPotatoSkinSmoothness);
//                break;
//            //芽眼深浅拍照并显示
//            case R.id.imb_eye:
//                File outputImageEye = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImageEye.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriEye = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImageEye);
//                Intent intentEye = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentEye.putExtra(MediaStore.EXTRA_OUTPUT, imageUriEye);
//                startActivityForResult(intentEye, TAKE_PHOTO_EYE);
//                break;
//            //芽眼深浅查看大图
//            case R.id.imv_eye:
//                watchLargePhoto(imageUriEye);
//                break;
//            //皮色拍照并显示
//            case R.id.imb_skin_color:
//                File outputImageSkinColor = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImageSkinColor.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriEye = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImageSkinColor);
//                Intent intentSkinColor = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentSkinColor.putExtra(MediaStore.EXTRA_OUTPUT, imageUriSkinColor);
//                startActivityForResult(intentSkinColor, TAKE_PHOTO_SKIN_COLOR);
//                break;
//            //皮色查看大图
//            case R.id.imv_skin_color:
//                watchLargePhoto(imageUriSkinColor);
//                break;
//            //肉色拍照并显示
//            case R.id.imb_flesh_color:
//                File outputImageFleshColor = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                try {
//                    outputImageFleshColor.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageUriFleshColor = FileProvider.getUriForFile(SaveDataActivity.this,
//                        "com.example.kerne.potato.fileprovider", outputImageFleshColor);
//                Intent intentFleshColor = new Intent("android.media.action.IMAGE_CAPTURE");
//                intentFleshColor.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFleshColor);
//                startActivityForResult(intentFleshColor, TAKE_PHOTO_FLESH_COLOR);
//                break;
            //肉色查看大图
//            case R.id.imv_flesh_color:
//                watchLargePhoto(imageUriFleshColor);
//                break;
            case R.id.sowing_period_input:
                showDatePickerDialog(edtSowingPeriodInput);
                break;
            case R.id.emergence_period:
                showDatePickerDialog(edtEmergencePeriod);
                break;
            case R.id.squaring_period:
                showDatePickerDialog(edtSquaringPeriod);
                break;
            case R.id.flowering_period:
                showDatePickerDialog(edtFloweringPeriod);
                break;
            case R.id.mature_period:
                showDatePickerDialog(edtMaturePeriod);
                break;
            //计算十株平均株高
//            case R.id.btn_compute_average_plant_height:
//                edtAveragePlantHeight.setText(
//                        getAverage(edtTenPlantHeight.getText().toString(),"株高"));
//                break;
            //计算十株平均分支数
            case R.id.btn_compute_average_branch_num:
//                edtAverageBranchNum.setText(
//                        getAverage(edtBranchNumOfTenPlants.getText().toString(), "分支数")
//                );
                break;
            default:
        }
    }

    public String getAverage(String s,String item) {
        float sum = 0;
        float average = 0;
        if (s.isEmpty()) {
            Toast.makeText(this, "请输入十株的"+item, Toast.LENGTH_SHORT).show();
        } else {
            String[] strings = s.split("-");
            for (String e :strings
                    ) {
                sum += Integer.valueOf(e);
            }
            average = sum / strings.length;
        }
        DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(average);//format 返回的是字符串
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //叶颜色显示照片
            case TAKE_PHOTO_COLOR:
                onResultOfPhoto(resultCode,imageUriColor, ivShowColor);
                break;
            //花冠色显示照片
            case TAKE_PHOTO_COROLLA_COLOR:
                onResultOfPhoto(resultCode,imageUriCorollaColor, ivShowCorollaColor);
                break;
            //花繁茂性显示照片
            case TAKE_PHOTO_PLANT_FLOURISH:
                onResultOfPhoto(resultCode,imageUriPlantFlourish, ivShowPlantFlourish);
                break;
            //茎色显示照片
            case TAKE_PHOTO_STEM_COLORS:
                onResultOfPhoto(resultCode,imageUriStemColors, ivShowStemColors);
                break;
            //天然结实性显示照片
            case TAKE_PHOTO_NATURAL_FECUNDITY:
                onResultOfPhoto(resultCode,imageUriNaturalFecundity, ivShowNaturalFecundity);
                break;
            //块茎整齐度显示照片
            case TAKE_PHOTO_TUBER_UNIFORMITY:
                onResultOfPhoto(resultCode,imageUriTuberUniformity, ivShowTuberUniformity);
                break;
            //薯型显示照片
            case TAKE_PHOTO_TUBER_SHAPE:
                onResultOfPhoto(resultCode,imageUriTuberShape, ivShowTuberShape);
                break;
            //薯皮光滑度显示照片
            case TAKE_PHOTO_POTATO_SKIN_SMOOTHNESS:
                onResultOfPhoto(resultCode,imageUriPotatoSkinSmoothness, ivShowPotatoSkinSmoothness);
                break;
            //芽眼深浅显示照片
            case TAKE_PHOTO_EYE:
                onResultOfPhoto(resultCode,imageUriEye, ivShowEye);
                break;
            //皮色显示照片
            case TAKE_PHOTO_SKIN_COLOR:
                onResultOfPhoto(resultCode,imageUriSkinColor, ivShowSkinColor);
                break;
            //肉色显示照片
            case TAKE_PHOTO_FLESH_COLOR:
                onResultOfPhoto(resultCode,imageUriFleshColor, ivShowFleshColor);
                break;
            default:
                break;
        }
    }

    private void onResultOfPhoto(int resultCode,Uri imageUri,ImageView ivShowPicture) {
        if (resultCode == RESULT_OK) {
            //在应用中显示图片
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ivShowPicture.setImageBitmap(bitmap);
        }
    }

    private void watchLargePhoto(Uri imageUri) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View imgLargeView = layoutInflater.inflate(R.layout.dialog_watch_big_photo, null);
        final AlertDialog alertDialogShowLargeImage = new AlertDialog.Builder(this).setTitle("点击可关闭").create();
        //获取ImageView
        ImageView imvLargePhoto = (ImageView) imgLargeView.findViewById(R.id.imv_large_photo);
        //设置图片到ImageView
        imvLargePhoto.setImageURI(imageUri);
        //定义dialog
        alertDialogShowLargeImage.setView(imgLargeView);
        alertDialogShowLargeImage.show();
        //点击大图关闭dialog
        imgLargeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogShowLargeImage.cancel();
            }
        });
    }

    private File getSavePhotoFile() {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Potato");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        return file;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.sowing_period_input:
                showDatePickerDialog(edtSowingPeriodInput);
                break;
            case R.id.emergence_period:
                showDatePickerDialog(edtEmergencePeriod);
                break;
            default:
        }
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(SaveDataActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String zeroMonth = "";
                String zeroDay = "";
                if (month < 10) zeroMonth = "0"; //当月份小于10时，需要在月份前加入0，需要符合yyyy-mm-dd当格式
                if (dayOfMonth < 10) zeroDay = "0"; //同上
                editText.setText(year + "-" + zeroMonth + month + "-" + zeroDay + dayOfMonth); //yyyy-mm-dd
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        saveImageToGallery(Context., bitmap);
        Log.d("SaveDataActivity", "onPause method");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SaveDataActivity", "onDestroy method");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SaveDataActivity", "onStop method");
    }
}

