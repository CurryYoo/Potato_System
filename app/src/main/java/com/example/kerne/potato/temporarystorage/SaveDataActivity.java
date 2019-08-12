package com.example.kerne.potato.temporarystorage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.SpaceConsumer;
import com.example.kerne.potato.MainActivity;
import com.example.kerne.potato.R;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.kerne.potato.Util.ChangeStatusBar.setStatusBarColor;
import static com.example.kerne.potato.Util.CustomToast.showShortToast;
import static com.example.kerne.potato.Util.ThumbImage.getImageThumbnail;
import static com.example.kerne.potato.temporarystorage.RealPath.getRealPathFromUri;
import static com.example.kerne.potato.temporarystorage.Util.getAverage;
import static com.example.kerne.potato.temporarystorage.Util.getGrowingDays;
import static com.example.kerne.potato.temporarystorage.Util.showDatePickerDialog;
import static com.example.kerne.potato.temporarystorage.Util.watchOnlineLargePhoto;

public class SaveDataActivity extends AppCompatActivity {

    //叶颜色拍照
    public static final int TAKE_PHOTO_COLOR = 1;
    //解析日期
//    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //花冠色拍照
    public static final int TAKE_PHOTO_COROLLA_COLOR = 2;
    //花繁茂性拍照
    public static final int TAKE_PHOTO_PLANT_FLOURISH = 3;
    //茎色拍照
    public static final int TAKE_PHOTO_STEM_COLORS = 4;
    //天然结实性拍照
    public static final int TAKE_PHOTO_NATURAL_FECUNDITY = 5;
    //块茎整齐度拍照
    public static final int TAKE_PHOTO_TUBER_UNIFORMITY = 6;
    //薯型拍照
    public static final int TAKE_PHOTO_TUBER_SHAPE = 7;
    //薯皮光滑度拍照
    public static final int TAKE_PHOTO_POTATO_SKIN_SMOOTHNESS = 8;
    //芽眼深浅拍照
    public static final int TAKE_PHOTO_EYE = 9;
    //皮色拍照
    public static final int TAKE_PHOTO_SKIN_COLOR = 10;
    //肉色拍照
    public static final int TAKE_PHOTO_FLESH_COLOR = 11;
    //从相册选择叶颜色
    public static final int SELECT_PHOTO_COLOR = 12;
    //从相册选择花冠色
    public static final int SELECT_PHOTO_COROLLA_COLORS = 13;
    //从相册选择花繁茂性
    public static final int SELECT_PHOTO_PLANT_FLOURISH = 14;
    //从相册选择茎色
    public static final int SELECT_PHOTO_STEM_COLOR = 15;
    //从相册选择天然结实性
    public static final int SELECT_PHOTO_NATURAL_FECUNDITY = 16;

    public static final int DATA_OK = 0;
    //解析两位小数
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    String TAG = "SaveDataActivity";
    //blockId
    String blockId;
    @BindView(R.id.left_one_button)
    ImageView leftOneButton;
    @BindView(R.id.left_one_layout)
    LinearLayout leftOneLayout;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.right_two_button)
    ImageView rightTwoButton;
    @BindView(R.id.right_two_layout)
    LinearLayout rightTwoLayout;
    @BindView(R.id.right_one_button)
    ImageView rightOneButton;
    @BindView(R.id.right_one_layout)
    LinearLayout rightOneLayout;
    EditText edtSpeciesID = null;
    //    private String commitId = "initial id";
    //实验类型
    EditText edtExperimentType = null;
    TextView edtSowingPeriodInput = null;
    TextView edtEmergencePeriod = null;
    EditText edtRateOfEmergence = null;
    TextView edtSquaringPeriod = null;
    TextView edtFloweringPeriod = null;
    TextView edtMaturePeriod = null;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    LinearLayout mLinearLayout;
    View view_basic;
    View view_height;
    View view_branch;
    View view_big;
    View view_small;
    @BindView(R.id.Pre_Load)
    LinearLayout PreLoad;
    @BindView(R.id.swipe_layout)
    ScrollView swipeLayout;
    //需要暂存的各字段
    //品种id
    private String speciesId;
    private String expType;
    private Spinner spnLeafColor = null;
    private Spinner spnCorollaColors = null;
    private Spinner spnPlantFlourish = null;
    private Spinner spnStemColor = null;
    private Spinner spnNaturalFecundity = null;
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
    //大薯十株株高，也就是十株株高
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
    //大薯平均株高，也就是平均株高
    private EditText edtAveragePlantHeightOfBigPotato = null;
    //计算大薯平均株高
//    private Button btnComputeAveragePlantHeightOfBigPotato = null;
    //大薯十株分支数，也就是十株分支数
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
    //    大薯平均分支数，也就是平均分支数
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
    private ImageView ivShowColor = null;
    private Uri imageUriColor = null;
    private String pathColor = null; //图片文件路径
    private ImageView ivShowCorollaColor = null;
    private Uri imageUriCorollaColor = null;
    private String pathCorollaColor = null; //图片文件路径
    private ImageView ivShowPlantFlourish = null;
    private Uri imageUriPlantFlourish = null;
    private String pathPlantFlourish = null; //图片文件路径
    private ImageView ivShowStemColors = null;
    private Uri imageUriStemColors = null;
    private String pathStemColors = null; //图片文件路径
    private ImageView ivShowNaturalFecundity = null;
    private Uri imageUriNaturalFecundity = null;
    private String pathNaturalFecundity = null; //图片文件路径
    private ImageView ivShowTuberUniformity = null;
    private Uri imageUriTuberUniformity = null;
    private ImageView ivShowTuberShape = null;
    private Uri imageUriTuberShape = null;
    private ImageView ivShowPotatoSkinSmoothness = null;
    private Uri imageUriPotatoSkinSmoothness = null;
    private ImageView ivShowEye = null;
    private Uri imageUriEye = null;
    private ImageView ivShowSkinColor = null;
    private Uri imageUriSkinColor = null;
    private ImageView ivShowFleshColor = null;
    private Uri imageUriFleshColor = null;
    //暂存功能
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.left_one_layout:
                    finish();
                    break;
                case R.id.right_one_layout:
                    Intent intent = new Intent(SaveDataActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.right_two_layout:
                    final SweetAlertDialog saveDialog = new SweetAlertDialog(SaveDataActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            .setContentText(getString(R.string.save_data_tip))
                            .setConfirmText("确定")
                            .setCancelText("取消")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    updateDataLocally();
                                    editor.putBoolean("update_pick_data", true);
                                    editor.apply();
                                }
                            });
                    saveDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    saveDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //叶颜色拍照并显示
                case R.id.imb_colors:
                    String fileNameString = System.currentTimeMillis() + ".jpg";
                    File outputImage = null;
                    outputImage = new File(getExternalCacheDir(), fileNameString);
                    pathColor = outputImage.getAbsolutePath();
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
                        Log.d(TAG, "onClick: img" + imageUriColor);
                    } else {
                        imageUriColor = Uri.fromFile(outputImage);
                    }
//                    Log.d("Uriiiiiii", pathColor + " || " + imageUriColor);
                    //启动相机程序
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriColor);
                    startActivityForResult(intent, TAKE_PHOTO_COLOR);
                    break;
                //从相册选择叶颜色照片
                case R.id.btn_select_from_album_colors:
                    selectPhotoFromAlbum(SELECT_PHOTO_COLOR);
                    break;
                //叶颜色查看大图
                case R.id.imv_colors:
                    watchOnlineLargePhoto(SaveDataActivity.this, imageUriColor, "叶颜色");
                    break;
                //花冠色拍照并显示
                case R.id.imb_corolla_colors:
                    File outputImageCorollaColors = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                pathCorollaColor = outputImageCorollaColors.getAbsolutePath();
//                Log.d(TAG, "onClick: img "+"corolla" + pathCorollaColor);
                    try {
                        outputImageCorollaColors.createNewFile();
                        pathCorollaColor = outputImageCorollaColors.getAbsolutePath();
                        Log.d(TAG, "onClick: img " + "corolla" + pathCorollaColor);
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
                    watchOnlineLargePhoto(SaveDataActivity.this, imageUriCorollaColor, "花冠色");
                    break;
                //从相册选择花冠色照片
                case R.id.btn_select_from_album_corolla_colors:
                    selectPhotoFromAlbum(SELECT_PHOTO_COROLLA_COLORS);
                    break;
                //花繁茂性拍照并显示
                case R.id.imb_plant_flourish:
                    File outputImagePlantFlourish = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                    pathPlantFlourish = outputImagePlantFlourish.getAbsolutePath();
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
                    watchOnlineLargePhoto(SaveDataActivity.this, imageUriPlantFlourish, "花繁茂性");
                    break;
                //从相册选择花繁茂性照片
                case R.id.btn_select_from_album_plant_flourish:
                    selectPhotoFromAlbum(SELECT_PHOTO_PLANT_FLOURISH);
                    break;
                //茎色拍照并显示
                case R.id.imb_stem_color:
                    File outputImageStemColor = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                    pathStemColors = outputImageStemColor.getAbsolutePath();
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
                    watchOnlineLargePhoto(SaveDataActivity.this, imageUriStemColors, "茎色");
                    break;
                //从相册选择茎色照片
                case R.id.btn_select_from_album_stem_color:
                    selectPhotoFromAlbum(SELECT_PHOTO_STEM_COLOR);
                    break;
                //天然结实性拍照并显示
                case R.id.imb_natural_fecundity:
                    File outputImageNaturalFecundity = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
                    pathNaturalFecundity = outputImageNaturalFecundity.getAbsolutePath();
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
                    watchOnlineLargePhoto(SaveDataActivity.this, imageUriNaturalFecundity, "天然结实性");
                    break;
                //从相册选择天然结实性照片
                case R.id.btn_select_from_album_natural_fecundity:
                    selectPhotoFromAlbum(SELECT_PHOTO_NATURAL_FECUNDITY);
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
                    showDatePickerDialog(SaveDataActivity.this, edtSowingPeriodInput);
                    break;
                case R.id.emergence_period:
                    showDatePickerDialog(SaveDataActivity.this, edtEmergencePeriod);
                    break;
                case R.id.squaring_period:
                    showDatePickerDialog(SaveDataActivity.this, edtSquaringPeriod);
                    break;
                case R.id.flowering_period:
                    showDatePickerDialog(SaveDataActivity.this, edtFloweringPeriod);
                    break;
                case R.id.mature_period:
                    showDatePickerDialog(SaveDataActivity.this, edtMaturePeriod);
                    break;
                //计算生育日数
                case R.id.btn_compute_growing_days:
                    String sowingDate = edtSowingPeriodInput.getText().toString();
                    String matureDate = edtMaturePeriod.getText().toString();
//                sowingDate = edtSowingPeriodInput.getText().toString();
//                matureDate = edtMaturePeriod.getText().toString();
                    try {
                        edtGrowingDays.setText(getGrowingDays(SaveDataActivity.this, sowingDate, matureDate));
                    } catch (ParseException e) {
                        showShortToast(SaveDataActivity.this, getString(R.string.toast_date_error));
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                //计算商品薯率
                case R.id.btn_compute_rate_of_economic_potato:
                    float edtFloatNumOfLargeAndMediumPotatoesContent = Float.parseFloat(edtNumOfLargeAndMediumPotatoes.getText().toString().isEmpty() ? "0" :
                            edtNumOfLargeAndMediumPotatoes.getText().toString());
                    float edtFloatNumOfSmallPotatoesContent = Float.parseFloat(edtNumOfSmallPotatoes.getText().toString().isEmpty() ? "0" :
                            edtNumOfSmallPotatoes.getText().toString());
                    float rateOfEconomicPotato = edtFloatNumOfLargeAndMediumPotatoesContent / (edtFloatNumOfLargeAndMediumPotatoesContent + edtFloatNumOfSmallPotatoesContent) * 100;
//                edtRateOfEconomicPotato.setText(decimalFormat.format(rateOfEconomicPotato));
                    edtRateOfEconomicPotato.setText(String.valueOf(Math.round(rateOfEconomicPotato)));
                    break;
                //计算亩产量
                case R.id.btn_compute_average_per_mu_yield:
                    ArrayList<String> strings2 = new ArrayList<>();
                    strings2.add(edtSmallSectionYield1.getText().toString().isEmpty() ? "0" : edtSmallSectionYield1.getText().toString());
                    strings2.add(edtSmallSectionYield2.getText().toString().isEmpty() ? "0" : edtSmallSectionYield2.getText().toString());
                    strings2.add(edtSmallSectionYield3.getText().toString().isEmpty() ? "0" : edtSmallSectionYield3.getText().toString());
                    edtPerMuYield.setText(getAverage(strings2));
                    break;
//            计算十株平均株高
                case R.id.btn_compute_average_plant_height:
                    ArrayList<String> strings1 = new ArrayList<String>();
                    strings1.add(edtBigPotatoHeight1.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight1.getText().toString());
                    strings1.add(edtBigPotatoHeight2.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight2.getText().toString());
                    strings1.add(edtBigPotatoHeight3.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight3.getText().toString());
                    strings1.add(edtBigPotatoHeight4.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight4.getText().toString());
                    strings1.add(edtBigPotatoHeight5.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight5.getText().toString());
                    strings1.add(edtBigPotatoHeight6.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight6.getText().toString());
                    strings1.add(edtBigPotatoHeight7.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight7.getText().toString());
                    strings1.add(edtBigPotatoHeight8.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight8.getText().toString());
                    strings1.add(edtBigPotatoHeight9.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight9.getText().toString());
                    strings1.add(edtBigPotatoHeight10.getText().toString().isEmpty() ? "0" : edtBigPotatoHeight10.getText().toString());
                    edtAveragePlantHeightOfBigPotato.setText(getAverage(strings1));
                    break;
                //计算十株平均分支数
                case R.id.btn_compute_average_branch_num:
                    ArrayList<String> strings = new ArrayList<String>();
                    strings.add(edtBranchNumOfBigPotato1.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato1.getText().toString());
                    strings.add(edtBranchNumOfBigPotato2.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato2.getText().toString());
                    strings.add(edtBranchNumOfBigPotato3.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato3.getText().toString());
                    strings.add(edtBranchNumOfBigPotato4.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato4.getText().toString());
                    strings.add(edtBranchNumOfBigPotato5.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato5.getText().toString());
                    strings.add(edtBranchNumOfBigPotato6.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato6.getText().toString());
                    strings.add(edtBranchNumOfBigPotato7.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato7.getText().toString());
                    strings.add(edtBranchNumOfBigPotato8.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato8.getText().toString());
                    strings.add(edtBranchNumOfBigPotato9.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato9.getText().toString());
                    strings.add(edtBranchNumOfBigPotato10.getText().toString().isEmpty() ? "0" : edtBranchNumOfBigPotato10.getText().toString());
                    edtAverageBranchNumOfBigPotato.setText(getAverage(strings));
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, R.color.primary_background);
        setContentView(R.layout.activity_commit);
        ButterKnife.bind(this);
        sp = getSharedPreferences("update_flag", Context.MODE_PRIVATE);
        editor = sp.edit();

        mLinearLayout = findViewById(R.id.mianliner);

        //仿iOS下拉留白
        SmartSwipe.wrap(swipeLayout)
                .addConsumer(new SpaceConsumer())
                .enableVertical();
        //从上一层品种id和实验类型
        Intent intent_speciesId = getIntent();
        speciesId = intent_speciesId.getStringExtra("speciesId");
        expType = intent_speciesId.getStringExtra("expType");
        blockId = intent_speciesId.getStringExtra("blockId");

        initToolBar();

        //延迟加载视图
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                initView();
                initData();
                PreLoad.setVisibility(View.GONE);
            }
        }, 50); //延迟ms
    }

    private void initView() {
        view_basic = LayoutInflater.from(SaveDataActivity.this).inflate(R.layout.item_basicinfo, null);
        InfoItemBar mbar_basic = new InfoItemBar(SaveDataActivity.this, getString(R.string.item_bar_basic));
        mbar_basic.addView(view_basic);
        mbar_basic.setShow(true);
        mLinearLayout.addView(mbar_basic);
        view_height = LayoutInflater.from(SaveDataActivity.this).inflate(R.layout.item_height, null);
        InfoItemBar mbar_height = new InfoItemBar(SaveDataActivity.this, getString(R.string.item_bar_height));
        mbar_height.addView(view_height);
        mbar_height.setShow(true);
        mLinearLayout.addView(mbar_height);
        view_branch = LayoutInflater.from(SaveDataActivity.this).inflate(R.layout.item_branch, null);
        InfoItemBar mbar_branch = new InfoItemBar(SaveDataActivity.this, getString(R.string.item_bar_branch));
        mbar_branch.addView(view_branch);
        mbar_branch.setShow(true);
        mLinearLayout.addView(mbar_branch);
        view_big = LayoutInflater.from(SaveDataActivity.this).inflate(R.layout.item_big, null);
        InfoItemBar mbar_big = new InfoItemBar(SaveDataActivity.this, getString(R.string.item_bar_big));
        mbar_big.addView(view_big);
        mbar_big.setShow(true);
        mLinearLayout.addView(mbar_big);
        view_small = LayoutInflater.from(SaveDataActivity.this).inflate(R.layout.item_small, null);
        InfoItemBar mbar_small = new InfoItemBar(SaveDataActivity.this, getString(R.string.item_bar_small));
        mbar_small.addView(view_small);
        mbar_small.setShow(true);
        mLinearLayout.addView(mbar_small);

        //品种Id
        edtSpeciesID = findViewById(R.id.edt_species_id);
        edtSpeciesID.setText(speciesId);
        edtSpeciesID.setSelection(speciesId.length());

        //实验类型
        edtExperimentType = findViewById(R.id.edt_experiment_type);
        edtExperimentType.setText(expType);

        //播种期
        edtSowingPeriodInput = findViewById(R.id.sowing_period_input);
//        edtSowingPeriodInput.setInputType(InputType.TYPE_NULL);
        edtSowingPeriodInput.setOnClickListener(onClickListener);

        //出苗期
        edtEmergencePeriod = findViewById(R.id.emergence_period);
//        edtEmergencePeriod.setInputType(InputType.TYPE_NULL);
        edtEmergencePeriod.setOnClickListener(onClickListener);

        //出苗率
        edtRateOfEmergence = findViewById(R.id.edt_rate_of_emergence);

        //现蕾期
        edtSquaringPeriod = findViewById(R.id.squaring_period);
//        edtSquaringPeriod.setInputType(InputType.TYPE_NULL);
        edtSquaringPeriod.setOnClickListener(onClickListener);

        //开花期
        edtFloweringPeriod = findViewById(R.id.flowering_period);
//        edtFloweringPeriod.setInputType(InputType.TYPE_NULL);
        edtFloweringPeriod.setOnClickListener(onClickListener);

        //叶颜色
        spnLeafColor = findViewById(R.id.spn_colors);
        spnLeafColor.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //叶颜色拍照
        ImageButton imbTakePhotoColor = (ImageButton) findViewById(R.id.imb_colors);
        imbTakePhotoColor.setOnClickListener(onClickListener);
        ivShowColor = (ImageView) findViewById(R.id.imv_colors);
        ivShowColor.setOnClickListener(onClickListener);
        //从相册选择叶颜色照片
        Button btnSelectPhotoFromAlbumColors = (Button) findViewById(R.id.btn_select_from_album_colors);
        btnSelectPhotoFromAlbumColors.setOnClickListener(onClickListener);

        //花冠色
        spnCorollaColors = (Spinner) findViewById(R.id.corolla_colors);
        spnCorollaColors.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //花冠色拍照
        ImageButton imbTakePhotoCorollaColors = (ImageButton) findViewById(R.id.imb_corolla_colors);
        imbTakePhotoCorollaColors.setOnClickListener(onClickListener);
        ivShowCorollaColor = (ImageView) findViewById(R.id.imv_corolla_colors);
        ivShowCorollaColor.setOnClickListener(onClickListener);
        //从相册选择花冠色照片
        Button btnSelectPhotoFromAlbumCorollaColors = (Button) findViewById(R.id.btn_select_from_album_corolla_colors);
        btnSelectPhotoFromAlbumCorollaColors.setOnClickListener(onClickListener);

        //花繁茂性
        spnPlantFlourish = (Spinner) findViewById(R.id.plant_flourish);
        spnPlantFlourish.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //花繁茂性拍照
        ImageButton imbTakePhotoPlantFlourish = (ImageButton) findViewById(R.id.imb_plant_flourish);
        imbTakePhotoPlantFlourish.setOnClickListener(onClickListener);
        ivShowPlantFlourish = (ImageView) findViewById(R.id.imv_plant_flourish);
        ivShowPlantFlourish.setOnClickListener(onClickListener);
        //从相册选择花繁茂性照片
        Button btnSelectPhotoFromAlbumPlantFlourish = (Button) findViewById(R.id.btn_select_from_album_plant_flourish);
        btnSelectPhotoFromAlbumPlantFlourish.setOnClickListener(onClickListener);

        //茎色
        spnStemColor = (Spinner) findViewById(R.id.stem_color);
        spnStemColor.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //茎色拍照
        ImageButton imbTakePhotoStemColor = (ImageButton) findViewById(R.id.imb_stem_color);
        imbTakePhotoStemColor.setOnClickListener(onClickListener);
        ivShowStemColors = (ImageView) findViewById(R.id.imv_stem_color);
        ivShowStemColors.setOnClickListener(onClickListener);
        //从相册选择茎色照片
        Button btnSelectPhotoFromAlbumStemColor = (Button) findViewById(R.id.btn_select_from_album_stem_color);
        btnSelectPhotoFromAlbumStemColor.setOnClickListener(onClickListener);

        //天然结实性
        spnNaturalFecundity = (Spinner) findViewById(R.id.natural_fecundity);
        spnNaturalFecundity.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //天然结实性拍照
        ImageButton imbTakePhotoNaturalFecundity = (ImageButton) findViewById(R.id.imb_natural_fecundity);
        imbTakePhotoNaturalFecundity.setOnClickListener(onClickListener);
        ivShowNaturalFecundity = (ImageView) findViewById(R.id.imv_natural_fecundity);
        ivShowNaturalFecundity.setOnClickListener(onClickListener);
        //从相册选择天然结实性照片
        Button btnSelectPhotoFromAlbumNaturalFecundity = (Button) findViewById(R.id.btn_select_from_album_natural_fecundity);
        btnSelectPhotoFromAlbumNaturalFecundity.setOnClickListener(onClickListener);

        //成熟期
        edtMaturePeriod = findViewById(R.id.mature_period);
//        edtMaturePeriod.setInputType(InputType.TYPE_NULL);
        edtMaturePeriod.setOnClickListener(onClickListener);

        //生育日数
        edtGrowingDays = (EditText) findViewById(R.id.growing_days);
        //计算生育日数
        Button btnComputeRateOfGrowingDays = (Button) findViewById(R.id.btn_compute_growing_days);
        btnComputeRateOfGrowingDays.setOnClickListener(onClickListener);

        //块茎整齐度
        spnTuberUniformity = (Spinner) findViewById(R.id.tuber_uniformity);
        spnTuberUniformity.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //块茎整齐度拍照
//        ImageButton imbTakePhotoTuberUniformity = (ImageButton) findViewById(R.id.imb_tuber_uniformity);
//        imbTakePhotoTuberUniformity.setOnClickListener(onClickListener);
//        ivShowTuberUniformity = (ImageView) findViewById(R.id.imv_tuber_uniformity);
//        ivShowTuberUniformity.setOnClickListener(onClickListener);

        //薯型
        spnTuberShape = (Spinner) findViewById(R.id.tuber_shape);
        spnTuberShape.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //薯型拍照
//        ImageButton imbTakePhotoTuberShape = (ImageButton) findViewById(R.id.imb_tuber_shape);
//        imbTakePhotoTuberShape.setOnClickListener(onClickListener);
//        ivShowTuberShape = (ImageView) findViewById(R.id.imv_tuber_shape);
//        ivShowTuberShape.setOnClickListener(onClickListener);

        //薯皮光滑度
        spnPotatoSkinSmoothness = (Spinner) findViewById(R.id.potato_skin_smoothness);
        spnPotatoSkinSmoothness.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //薯皮光滑度拍照
//        ImageButton imbTakePhotoPotatoSkinSmoothness = (ImageButton) findViewById(R.id.imb_potato_skin_smoothness);
//        imbTakePhotoPotatoSkinSmoothness.setOnClickListener(onClickListener);
//        ivShowPotatoSkinSmoothness = (ImageView) findViewById(R.id.imv_potato_skin_smoothness);
//        ivShowPotatoSkinSmoothness.setOnClickListener(onClickListener);

        //芽眼深浅
        spnEye = (Spinner) findViewById(R.id.eye);
        spnEye.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //芽眼深浅拍照
//        ImageButton imbTakePhotoEye = (ImageButton) findViewById(R.id.imb_eye);
//        imbTakePhotoEye.setOnClickListener(onClickListener);
//        ivShowEye = (ImageView) findViewById(R.id.imv_potato_skin_smoothness);
//        ivShowEye.setOnClickListener(onClickListener);

        //皮色
        spnSkinColor = (Spinner) findViewById(R.id.skin_color);
        spnSkinColor.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //皮色拍照
//        ImageButton imbTakePhotoSkinColor = (ImageButton) findViewById(R.id.imb_skin_color);
//        imbTakePhotoSkinColor.setOnClickListener(onClickListener);
//        ivShowSkinColor = (ImageView) findViewById(R.id.imv_skin_color);
//        ivShowSkinColor.setOnClickListener(onClickListener);

        //肉色
        spnFleshColor = (Spinner) findViewById(R.id.flesh_color);
        spnFleshColor.setPopupBackgroundResource(R.drawable.bg_spinner_drop_down);
        //肉色拍照
//        ImageButton imbTakePhotoFleshColor = (ImageButton) findViewById(R.id.imb_flesh_color);
//        imbTakePhotoFleshColor.setOnClickListener(onClickListener);
//        ivShowFleshColor = (ImageView) findViewById(R.id.imv_flesh_color);
//        ivShowFleshColor.setOnClickListener(onClickListener);

        //是否入选
        rgWhetherToBeIncluded = (RadioGroup) findViewById(R.id.whether_to_be_included);
//        rgWhetherToBeIncluded.check(R.id.no);

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
        //计算商品薯率
        Button btnComputeRateOfEconomicPotato = (Button) findViewById(R.id.btn_compute_rate_of_economic_potato);
        btnComputeRateOfEconomicPotato.setOnClickListener(onClickListener);

        //小区产量1
        edtSmallSectionYield1 = (EditText) findViewById(R.id.small_section_yield1);

        //小区产量2
        edtSmallSectionYield2 = (EditText) findViewById(R.id.small_section_yield2);

        //小区产量3
        edtSmallSectionYield3 = (EditText) findViewById(R.id.small_section_yield3);

        //亩产量
        edtPerMuYield = (EditText) findViewById(R.id.per_mu_yield);
        //计算亩产量
        Button btnComputePerMuYield = (Button) findViewById(R.id.btn_compute_average_per_mu_yield);
        btnComputePerMuYield.setOnClickListener(onClickListener);
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
        btnComputeAveragePlantHeight.setOnClickListener(onClickListener);

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
        btnComputeAverageBranchNum.setOnClickListener(onClickListener);
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
    }

    private void initData() {

        //数据存储
        dbHelper = new SpeciesDBHelper(this, "SpeciesTable.db", null, 14);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        //如果品种信息不存在，进行初始化
        //如果品种信息存在，进行展示
        Cursor cursor = sqLiteDatabase.query("SpeciesTable", null,  "blockId=?", new String[]{blockId}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                //播种期
                String plantingDate = cursor.getString(3);
                edtSowingPeriodInput.setText(plantingDate);
                //出苗期
                String emergenceDate = cursor.getString(4);
                edtEmergencePeriod.setText(emergenceDate);
                //出苗率
                int sproutRate = cursor.getInt(5);
                edtRateOfEmergence.setText(String.valueOf(sproutRate));
                //现蕾期
                String squaringStage = cursor.getString(6);
                edtSquaringPeriod.setText(squaringStage);
                //开花期
                String blooming = cursor.getString(7);
                edtFloweringPeriod.setText(blooming);

                //叶颜色
                String leafColour = cursor.getString(8);
                Log.d(TAG, "onCreate: " + "leafColour:" + leafColour);
                SpinnerAdapter leafColourAdapter = spnLeafColor.getAdapter();
//                int k= apsAdapter.getCount();

                for (int j = 0; j < leafColourAdapter.getCount(); j++) {
                    if (leafColour.equals(leafColourAdapter.getItem(j).toString())) {
                        spnLeafColor.setSelection(j, true);
                        break;
                    }
                }

                //花冠色
                String corollaColour = cursor.getString(9);
                SpinnerAdapter corollaColourAdapter = spnCorollaColors.getAdapter();
                Log.d(TAG, "onCreate: " + "corollaColour:" + corollaColour);
                for (int j = 0; j < corollaColourAdapter.getCount(); j++) {
                    if (corollaColour.equals(corollaColourAdapter.getItem(j).toString())) {
                        spnCorollaColors.setSelection(j, true);
                        break;
                    }
                }
                //花繁茂性
                String flowering = cursor.getString(10);
                SpinnerAdapter floweringAdapter = spnPlantFlourish.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < floweringAdapter.getCount(); j++) {
                    if (flowering.equals(floweringAdapter.getItem(j).toString())) {
                        spnPlantFlourish.setSelection(j, true);
                        break;
                    }
                }
                //茎色
                String stemColour = cursor.getString(11);
                SpinnerAdapter stemColourAdapter = spnStemColor.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < stemColourAdapter.getCount(); j++) {
                    if (stemColour.equals(stemColourAdapter.getItem(j).toString())) {
                        spnStemColor.setSelection(j, true);
                        break;
                    }
                }
                //天然结实性
                String openpollinated = cursor.getString(12);
                SpinnerAdapter openpollinatedAdapter = spnNaturalFecundity.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < openpollinatedAdapter.getCount(); j++) {
                    if (openpollinated.equals(openpollinatedAdapter.getItem(j).toString())) {
                        spnNaturalFecundity.setSelection(j, true);
                        break;
                    }
                }

//                //图片加载
//                String img1 = cursor.getString(cursor.getColumnIndex("img1"));
//                Log.d("img1", img1);
//                if (img1 != null) {
//                    Bitmap bit = null;
//
//                    bit = getImageThumbnail(img1, 50, 50);
//
//                    ivShowColor.setImageBitmap(bit);
//                }
//                String img2 = cursor.getString(cursor.getColumnIndex("img2"));
//                if (img2 != null) {
//                    Bitmap bit = null;
//
//                    bit = getImageThumbnail(img2, 50, 50);
//
//                    ivShowCorollaColor.setImageBitmap(bit);
//                }
//                String img3 = cursor.getString(cursor.getColumnIndex("img3"));
//                if (img3 != null) {
//                    Bitmap bit = null;
//
//                    bit = getImageThumbnail(img3, 50, 50);
//
//                    ivShowPlantFlourish.setImageBitmap(bit);
//                }
//                String img4 = cursor.getString(cursor.getColumnIndex("img4"));
//                if (img4 != null) {
//                    Bitmap bit = null;
//
//                    bit = getImageThumbnail(img4, 50, 50);
//
//                    ivShowStemColors.setImageBitmap(bit);
//                }
//                String img5 = cursor.getString(cursor.getColumnIndex("img5"));
//                if (img5 != null) {
//                    Bitmap bit = null;
//
//                    bit = getImageThumbnail(img5, 50, 50);
//
//                    ivShowNaturalFecundity.setImageBitmap(bit);
//                }

                //成熟期
                String maturingStage = cursor.getString(13);
                edtMaturePeriod.setText(maturingStage);
//                Log.d("SaveDataActivity", "onCreate: hahaha" + " " + blockId + " " + speciesId +
//                        " speciesId" + speciesIdtemp + " sproutRate:" + sproutRate + " growingPeriod:" + growingPeriod);
                //生育日数
                int growingPeriod = cursor.getInt(14);
                edtGrowingDays.setText(String.valueOf(growingPeriod));
                //块茎整齐度
                String uniformityOfTuberSize = cursor.getString(15);
                SpinnerAdapter uniformityOfTuberSizeAdapter = spnTuberUniformity.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < uniformityOfTuberSizeAdapter.getCount(); j++) {
                    if (uniformityOfTuberSize.equals(uniformityOfTuberSizeAdapter.getItem(j).toString())) {
                        spnTuberUniformity.setSelection(j, true);
                        break;
                    }
                }
                //薯型
                String tuberShape = cursor.getString(16);
                SpinnerAdapter tuberShapeAdapter = spnTuberShape.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < tuberShapeAdapter.getCount(); j++) {
                    if (tuberShape.equals(tuberShapeAdapter.getItem(j).toString())) {
                        spnTuberShape.setSelection(j, true);
                        break;
                    }
                }
                //薯皮光滑度
                String skinSmoothness = cursor.getString(17);
                SpinnerAdapter skinSmoothnessAdapter = spnPotatoSkinSmoothness.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < skinSmoothnessAdapter.getCount(); j++) {
                    if (skinSmoothness.equals(skinSmoothnessAdapter.getItem(j).toString())) {
                        spnPotatoSkinSmoothness.setSelection(j, true);
                        break;
                    }
                }
                //芽眼深浅
                String eyeDepth = cursor.getString(18);
                SpinnerAdapter eyeDepthAdapter = spnEye.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < eyeDepthAdapter.getCount(); j++) {
                    if (eyeDepth.equals(eyeDepthAdapter.getItem(j).toString())) {
                        spnEye.setSelection(j, true);
                        break;
                    }
                }
                //皮色
                String skinColour = cursor.getString(19);
                SpinnerAdapter skinColourAdapter = spnSkinColor.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < skinColourAdapter.getCount(); j++) {
                    if (skinColour.equals(skinColourAdapter.getItem(j).toString())) {
                        spnSkinColor.setSelection(j, true);
                        break;
                    }
                }
                //肉色
                String fleshColour = cursor.getString(20);
                SpinnerAdapter fleshColourAdapter = spnFleshColor.getAdapter();
//                Log.d(TAG, "onCreate: "+"corollaColour:"+corollaColour);
                for (int j = 0; j < fleshColourAdapter.getCount(); j++) {
                    if (fleshColour.equals(fleshColourAdapter.getItem(j).toString())) {
                        spnFleshColor.setSelection(j, true);
                        break;
                    }
                }
                //是否入选
                String isChoozenString = cursor.getString(21);
                if (isChoozenString.equals("0")) {
                    rgWhetherToBeIncluded.check(R.id.no);
                } else {
                    rgWhetherToBeIncluded.check(R.id.yes);
                }
                //备注
                String remark = cursor.getString(22);
                edtRemark.setText(remark);
                //收获株数
                int harvestNum = cursor.getInt(23);
                edtNumOfHarvestedPlants.setText(String.valueOf(harvestNum));
                //大中薯数
                int lmNum = cursor.getInt(24);
                edtNumOfLargeAndMediumPotatoes.setText(String.valueOf(lmNum));
                //大中薯重
                float lmWeight = cursor.getFloat(25);
                edtWeightOfLargeAndMediumPotatoes.setText(String.valueOf(lmWeight));
                //小薯数
                int sNum = cursor.getInt(26);
                edtNumOfSmallPotatoes.setText(String.valueOf(sNum));
                //小薯重
                float sWeight = cursor.getFloat(27);
                edtWeightOfSmallPotatoes.setText(String.valueOf(sWeight));
                //商品薯率
                int commercialRate = cursor.getInt(28);
                edtRateOfEconomicPotato.setText(String.valueOf(commercialRate));
                //小区产量1
                float plotYield1 = cursor.getFloat(29);
                edtSmallSectionYield1.setText(String.valueOf(plotYield1));
                //小区产量2
                float plotYield2 = cursor.getFloat(30);
                edtSmallSectionYield2.setText(String.valueOf(plotYield2));
                //小区产量3
                float plotYield3 = cursor.getFloat(31);
                edtSmallSectionYield3.setText(String.valueOf(plotYield3));
                //亩产量
                float acreYield = cursor.getFloat(32);
                edtPerMuYield.setText(String.valueOf(acreYield));
                //十株株高
                float bigPlantHeight1 = cursor.getFloat(33);
                edtBigPotatoHeight1.setText(String.valueOf(bigPlantHeight1));
                float bigPlantHeight2 = cursor.getFloat(34);
                edtBigPotatoHeight2.setText(String.valueOf(bigPlantHeight2));
                float bigPlantHeight3 = cursor.getFloat(35);
                edtBigPotatoHeight3.setText(String.valueOf(bigPlantHeight3));
                float bigPlantHeight4 = cursor.getFloat(36);
                edtBigPotatoHeight4.setText(String.valueOf(bigPlantHeight4));
                float bigPlantHeight5 = cursor.getFloat(37);
                edtBigPotatoHeight5.setText(String.valueOf(bigPlantHeight5));
                float bigPlantHeight6 = cursor.getFloat(38);
                edtBigPotatoHeight6.setText(String.valueOf(bigPlantHeight6));
                float bigPlantHeight7 = cursor.getFloat(39);
                edtBigPotatoHeight7.setText(String.valueOf(bigPlantHeight7));
                float bigPlantHeight8 = cursor.getFloat(40);
                edtBigPotatoHeight8.setText(String.valueOf(bigPlantHeight8));
                float bigPlantHeight9 = cursor.getFloat(41);
                edtBigPotatoHeight9.setText(String.valueOf(bigPlantHeight9));
                float bigPlantHeight10 = cursor.getFloat(42);
                edtBigPotatoHeight10.setText(String.valueOf(bigPlantHeight10));
                //平均株高
                float plantHeightAvg = cursor.getFloat(43);
                edtAveragePlantHeightOfBigPotato.setText(String.valueOf(plantHeightAvg));
                //十株分支数
                int bigBranchNumber1 = cursor.getInt(44);
                edtBranchNumOfBigPotato1.setText(String.valueOf(bigBranchNumber1));
                int bigBranchNumber2 = cursor.getInt(45);
                edtBranchNumOfBigPotato2.setText(String.valueOf(bigBranchNumber2));
                int bigBranchNumber3 = cursor.getInt(46);
                edtBranchNumOfBigPotato3.setText(String.valueOf(bigBranchNumber3));
                int bigBranchNumber4 = cursor.getInt(47);
                edtBranchNumOfBigPotato4.setText(String.valueOf(bigBranchNumber4));
                int bigBranchNumber5 = cursor.getInt(48);
                edtBranchNumOfBigPotato5.setText(String.valueOf(bigBranchNumber5));
                int bigBranchNumber6 = cursor.getInt(49);
                edtBranchNumOfBigPotato6.setText(String.valueOf(bigBranchNumber6));
                int bigBranchNumber7 = cursor.getInt(50);
                edtBranchNumOfBigPotato7.setText(String.valueOf(bigBranchNumber7));
                int bigBranchNumber8 = cursor.getInt(51);
                edtBranchNumOfBigPotato8.setText(String.valueOf(bigBranchNumber8));
                int bigBranchNumber9 = cursor.getInt(52);
                edtBranchNumOfBigPotato9.setText(String.valueOf(bigBranchNumber9));
                int bigBranchNumber10 = cursor.getInt(53);
                edtBranchNumOfBigPotato10.setText(String.valueOf(bigBranchNumber10));
                //平均分支数
                float branchNumberAvg = cursor.getFloat(54);
                edtAverageBranchNumOfBigPotato.setText(String.valueOf(branchNumberAvg));
                //大薯十株测产
                float bigYield1 = cursor.getFloat(55);
                edtYieldMonitoringOfBigPotato1.setText(String.valueOf(bigYield1));
                float bigYield2 = cursor.getFloat(56);
                edtYieldMonitoringOfBigPotato2.setText(String.valueOf(bigYield2));
                float bigYield3 = cursor.getFloat(57);
                edtYieldMonitoringOfBigPotato3.setText(String.valueOf(bigYield3));
                float bigYield4 = cursor.getFloat(58);
                edtYieldMonitoringOfBigPotato4.setText(String.valueOf(bigYield4));
                float bigYield5 = cursor.getFloat(59);
                edtYieldMonitoringOfBigPotato5.setText(String.valueOf(bigYield5));
                float bigYield6 = cursor.getFloat(60);
                edtYieldMonitoringOfBigPotato6.setText(String.valueOf(bigYield6));
                float bigYield7 = cursor.getFloat(61);
                edtYieldMonitoringOfBigPotato7.setText(String.valueOf(bigYield7));
                float bigYield8 = cursor.getFloat(62);
                edtYieldMonitoringOfBigPotato8.setText(String.valueOf(bigYield8));
                float bigYield9 = cursor.getFloat(63);
                edtYieldMonitoringOfBigPotato9.setText(String.valueOf(bigYield9));
                float bigYield10 = cursor.getFloat(64);
                edtYieldMonitoringOfBigPotato10.setText(String.valueOf(bigYield10));
                //小薯十株测产
                float smalYield1 = cursor.getFloat(65);
                edtYieldMonitoringOfSmallPotato1.setText(String.valueOf(smalYield1));
                float smalYield2 = cursor.getFloat(66);
                edtYieldMonitoringOfSmallPotato2.setText(String.valueOf(smalYield2));
                float smalYield3 = cursor.getFloat(67);
                edtYieldMonitoringOfSmallPotato3.setText(String.valueOf(smalYield3));
                float smalYield4 = cursor.getFloat(68);
                edtYieldMonitoringOfSmallPotato4.setText(String.valueOf(smalYield4));
                float smalYield5 = cursor.getFloat(69);
                edtYieldMonitoringOfSmallPotato5.setText(String.valueOf(smalYield5));
                float smalYield6 = cursor.getFloat(70);
                edtYieldMonitoringOfSmallPotato6.setText(String.valueOf(smalYield6));
                float smalYield7 = cursor.getFloat(71);
                edtYieldMonitoringOfSmallPotato7.setText(String.valueOf(smalYield7));
                float smalYield8 = cursor.getFloat(72);
                edtYieldMonitoringOfSmallPotato8.setText(String.valueOf(smalYield8));
                float smalYield9 = cursor.getFloat(73);
                edtYieldMonitoringOfSmallPotato9.setText(String.valueOf(smalYield9));
                float smalYield10 = cursor.getFloat(74);
                edtYieldMonitoringOfSmallPotato10.setText(String.valueOf(smalYield10));
                //叶颜色图片
                pathColor = cursor.getString(75);
//                Log.d(TAG, "onCreate: "+ "imgColor:" + imgColor);
//                    content://com.example.kerne.potato.fileprovider/potato_images/Android/data/com.example.kerne.potato/cache/1554976338705.jpg
//                    /storage/emulated/0/Android/data/com.example.kerne.potato/cache/1554976338705.jpg
                if (pathColor != null) {
                    imageUriColor = Uri.parse("content://com.example.kerne.potato.fileprovider/potato_images/" + pathColor.replace("/storage/emulated/0/", ""));
                    Bitmap bitmapColor = null;
                    Log.d("pathColor", pathColor);
                    try {
                        bitmapColor = getImageThumbnail(pathColor, 50, 50);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onCreate: img" + "NullPointerException" + bitmapColor);
                        e.printStackTrace();
                    }
                    ivShowColor.setImageBitmap(bitmapColor);
                }
                //花冠色图片
                pathCorollaColor = cursor.getString(76);
                Log.d(TAG, "onCreate: " + "imgCorollaColors:" + pathCorollaColor);
                if (pathCorollaColor != null) {
                    imageUriCorollaColor = Uri.parse("content://com.example.kerne.potato.fileprovider/potato_images/" + pathCorollaColor.replace("/storage/emulated/0/", ""));
                    Bitmap bitmapCorollaColors = null;
                    try {
                        bitmapCorollaColors = getImageThumbnail(pathCorollaColor, 50, 50);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onCreate: img" + "NullPointerException" + bitmapCorollaColors);
                        e.printStackTrace();
                    }
                    ivShowCorollaColor.setImageBitmap(bitmapCorollaColors);
                }
                //花繁茂性图片
                pathPlantFlourish = cursor.getString(77);
                if (pathPlantFlourish != null) {
                    imageUriPlantFlourish = Uri.parse("content://com.example.kerne.potato.fileprovider/potato_images/" + pathPlantFlourish.replace("/storage/emulated/0/", ""));
                    Bitmap bitmapPlantFlourish = null;
                    try {
                        bitmapPlantFlourish = getImageThumbnail(pathPlantFlourish, 50, 50);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    ivShowPlantFlourish.setImageBitmap(bitmapPlantFlourish);
                }
                //茎色图片
                pathStemColors = cursor.getString(78);
                if (pathStemColors != null) {
                    imageUriStemColors = Uri.parse("content://com.example.kerne.potato.fileprovider/potato_images/" + pathStemColors.replace("/storage/emulated/0/", ""));
                    Bitmap bitmapStemColors = null;
                    try {
                        bitmapStemColors = getImageThumbnail(pathStemColors, 50, 50);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    ivShowStemColors.setImageBitmap(bitmapStemColors);
                }
                //天然结实性图片
                pathNaturalFecundity = cursor.getString(79);
                if (pathNaturalFecundity != null) {
                    imageUriNaturalFecundity = Uri.parse("content://com.example.kerne.potato.fileprovider/potato_images/" + pathNaturalFecundity.replace("/storage/emulated/0/", ""));
                    Bitmap bitmapNaturalFecundity = null;
                    try {
                        bitmapNaturalFecundity = getImageThumbnail(pathNaturalFecundity, 50, 50);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    ivShowNaturalFecundity.setImageBitmap(bitmapNaturalFecundity);
                }

                Log.d("SaveDataActivity", "onCreate: haha" + cursor);
                cursor.moveToNext();
            }
        } else {
            Log.d("SaveDataActivity", "onCreate: cursor<=0" + cursor);
//            savaDataLocally();
        }
        cursor.close();
    }

    @SuppressLint("SetTextI18n")
    private void initToolBar() {
        titleText.setText(getText(R.string.species_data_pick));
        leftOneButton.setBackgroundResource(R.drawable.left_back);
        rightOneButton.setBackgroundResource(R.drawable.homepage);
        rightTwoButton.setBackgroundResource(R.drawable.no_save);

        leftOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        rightOneLayout.setBackgroundResource(R.drawable.selector_trans_button);
        rightTwoLayout.setBackgroundResource(R.drawable.selector_trans_button);

        leftOneLayout.setOnClickListener(toolBarOnClickListener);
        rightOneLayout.setOnClickListener(toolBarOnClickListener);
        rightTwoLayout.setOnClickListener(toolBarOnClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            leftOneLayout.setTooltipText(getResources().getText(R.string.back_left));
            rightOneLayout.setTooltipText(getResources().getText(R.string.home_page));
            rightTwoLayout.setTooltipText(getResources().getText(R.string.save_data));
        }
    }

    //暂存数据到本地数据库
    private void savaDataLocally() {
        ContentValues contentValues = assembleData();
        if (contentValues == null) {
            showShortToast(SaveDataActivity.this, getString(R.string.toast_input_error));
            return;
        }
        sqLiteDatabase.insert("SpeciesTable", null, contentValues);
        contentValues.clear();
    }

    //更新本地数据
    private void updateDataLocally() {
//        String id = edtSpeciesID.getText().toString();
        ContentValues contentValues = assembleData();
        Cursor cursor = sqLiteDatabase.query("SpeciesTable", null, "blockId=?", new String[]{blockId}, null, null, null);
        if (cursor.getCount() > 0) {
            sqLiteDatabase.update("SpeciesTable", contentValues, "blockId=?", new String[]{blockId});
        }
        else {
            sqLiteDatabase.insert("SpeciesTable", null, contentValues);
        }
        cursor.close();
//        sqLiteDatabase.delete("SpeciesTable", "blockId=?", new String[]{blockId});
//        sqLiteDatabase.insert("SpeciesTable", null, contentValues);
        if (contentValues != null) {
            contentValues.clear();
        }
        showShortToast(this, getString(R.string.toast_save_data_complete));
    }

    //组装数据
    private ContentValues assembleData() {
//                dbHelper.getWritableDatabase();
//                String edtSpeciesIDContent = edtSpeciesID.getText().toString();
//                if (edtSpeciesIDContent.isEmpty()) {
//                    Toast.makeText(this, "请输入Id", Toast.LENGTH_LONG).show();
//                    break;
//                }
//                if (commitId.equals(edtSpeciesIDContent)) {
//                    Toast.makeText(this, "请输入不同Id", Toast.LENGTH_LONG).show();
//                    break;
//                } else {
//                    commitId = edtSpeciesIDContent;
//                }

        ContentValues contentValues = null;
        try {
            contentValues = new ContentValues();
            //开始组装数据
            //品种id
            contentValues.put("speciesId", edtSpeciesID.getText().toString());
            //实验类型
            contentValues.put("experimentType", edtExperimentType.getText().toString());
            //blockId
            contentValues.put("blockId", blockId);
            //播种期
            contentValues.put("plantingDate", edtSowingPeriodInput.getText().toString());
            //出苗期
            contentValues.put("emergenceDate", edtEmergencePeriod.getText().toString());
            //出苗率
            String edtRateOfEmergenceContent = edtRateOfEmergence.getText().toString();
            int edtRateOfEmergenceContentParseInt = Integer.parseInt(edtRateOfEmergenceContent.isEmpty() ? "0" : edtRateOfEmergenceContent);
            if (edtRateOfEmergenceContentParseInt > 100) {
                showShortToast(SaveDataActivity.this, getString(R.string.toast_rateOfEmergenceContentParseInt));
                return null;
            } else {
                contentValues.put("sproutRate", edtRateOfEmergenceContentParseInt);
            }
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
            //是否入选 对应isChoozen字段
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
            contentValues.put("isChoozen", checkedRadioButtonContent);
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
            contentValues.put("lmWeight", Float.parseFloat(edtWeightOfLargeAndMediumPotatoesContent.isEmpty() ? "0.00" : edtWeightOfLargeAndMediumPotatoesContent));
            //小薯数
            String edtNumOfSmallPotatoesContent = edtNumOfSmallPotatoes.getText().toString();
            contentValues.put("sNum", Integer.parseInt(edtNumOfSmallPotatoesContent.isEmpty() ? "0" : edtNumOfSmallPotatoesContent));
            //小薯重
            String edtWeightOfSmallPotatoesContent = edtWeightOfSmallPotatoes.getText().toString();
            contentValues.put("sWeight", Float.parseFloat(edtWeightOfSmallPotatoesContent.isEmpty() ? "0.00" : edtWeightOfSmallPotatoesContent));
            //商品薯率
            String edtRateOfEconomicPotatoContent = edtRateOfEconomicPotato.getText().toString();
            contentValues.put("commercialRate", Integer.parseInt(edtRateOfEconomicPotatoContent.isEmpty() ? "0" : edtRateOfEconomicPotatoContent));
            //小区产量1
            String edtSmallSectionYield1Content = edtSmallSectionYield1.getText().toString();
            contentValues.put("plotYield1", Float.parseFloat(edtSmallSectionYield1Content.isEmpty() ? "0.00" : edtSmallSectionYield1Content));
            //小区产量2
            String edtSmallSectionYield2Content = edtSmallSectionYield2.getText().toString();
            contentValues.put("plotYield2", Float.parseFloat(edtSmallSectionYield2Content.isEmpty() ? "0" : edtSmallSectionYield2Content));
            //小区产量3
            String edtSmallSectionYield3Content = edtSmallSectionYield3.getText().toString();
            contentValues.put("plotYield3", Float.parseFloat(edtSmallSectionYield3Content.isEmpty() ? "0" : edtSmallSectionYield3Content));
            //亩产量
            String edtPerMuYieldContent = edtPerMuYield.getText().toString();
            contentValues.put("acreYield", Float.parseFloat(edtPerMuYieldContent.isEmpty() ? "0" : edtPerMuYieldContent));
            //大薯十株株高
            //bigPlantHeight1
            String edtPlantHeightOfBigPotatoContent1 = edtBigPotatoHeight1.getText().toString();
            contentValues.put("bigPlantHeight1", Float.parseFloat(edtPlantHeightOfBigPotatoContent1.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent1));
            //bigPlantHeight2
            String edtPlantHeightOfBigPotatoContent2 = edtBigPotatoHeight2.getText().toString();
            contentValues.put("bigPlantHeight2", Float.parseFloat(edtPlantHeightOfBigPotatoContent2.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent2));
            //bigPlantHeight3
            String edtPlantHeightOfBigPotatoContent3 = edtBigPotatoHeight3.getText().toString();
            contentValues.put("bigPlantHeight3", Float.parseFloat(edtPlantHeightOfBigPotatoContent3.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent3));
            //bigPlantHeight4
            String edtPlantHeightOfBigPotatoContent4 = edtBigPotatoHeight4.getText().toString();
            contentValues.put("bigPlantHeight4", Float.parseFloat(edtPlantHeightOfBigPotatoContent4.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent4));
            //bigPlantHeight5
            String edtPlantHeightOfBigPotatoContent5 = edtBigPotatoHeight5.getText().toString();
            contentValues.put("bigPlantHeight5", Float.parseFloat(edtPlantHeightOfBigPotatoContent5.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent5));
            //bigPlantHeight6
            String edtPlantHeightOfBigPotatoContent6 = edtBigPotatoHeight6.getText().toString();
            contentValues.put("bigPlantHeight6", Float.parseFloat(edtPlantHeightOfBigPotatoContent6.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent6));
            //bigPlantHeight7
            String edtPlantHeightOfBigPotatoContent7 = edtBigPotatoHeight7.getText().toString();
            contentValues.put("bigPlantHeight7", Float.parseFloat(edtPlantHeightOfBigPotatoContent7.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent7));
            //bigPlantHeight8
            String edtPlantHeightOfBigPotatoContent8 = edtBigPotatoHeight8.getText().toString();
            contentValues.put("bigPlantHeight8", Float.parseFloat(edtPlantHeightOfBigPotatoContent8.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent8));
            //bigPlantHeight9
            String edtPlantHeightOfBigPotatoContent9 = edtBigPotatoHeight9.getText().toString();
            contentValues.put("bigPlantHeight9", Float.parseFloat(edtPlantHeightOfBigPotatoContent9.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent9));
            //bigPlantHeight10
            String edtPlantHeightOfBigPotatoContent10 = edtBigPotatoHeight10.getText().toString();
            contentValues.put("bigPlantHeight10", Float.parseFloat(edtPlantHeightOfBigPotatoContent10.isEmpty() ? "0" : edtPlantHeightOfBigPotatoContent10));

            //平均株高
            String edtAveragePlantHeightContentOfBigPotato = edtAveragePlantHeightOfBigPotato.getText().toString();
            contentValues.put("plantHeightAvg", Float.parseFloat(edtAveragePlantHeightContentOfBigPotato.isEmpty() ? "0" : edtAveragePlantHeightContentOfBigPotato));
            //大薯十株分支数
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
            contentValues.put("branchNumberAvg", Float.parseFloat(edtAverageBranchNumOfBigPotatoContent.isEmpty() ? "0" : edtAverageBranchNumOfBigPotatoContent));
            //十株测产
            //大薯十株测产
            String edtYieldMonitoringOfBigPotatoContent1 = edtYieldMonitoringOfBigPotato1.getText().toString();
            contentValues.put("bigYield1", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent1.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent1));
            String edtYieldMonitoringOfBigPotatoContent2 = edtYieldMonitoringOfBigPotato2.getText().toString();
            contentValues.put("bigYield2", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent2.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent2));
            String edtYieldMonitoringOfBigPotatoContent3 = edtYieldMonitoringOfBigPotato3.getText().toString();
            contentValues.put("bigYield3", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent3.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent3));
            String edtYieldMonitoringOfBigPotatoContent4 = edtYieldMonitoringOfBigPotato4.getText().toString();
            contentValues.put("bigYield4", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent4.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent4));
            String edtYieldMonitoringOfBigPotatoContent5 = edtYieldMonitoringOfBigPotato5.getText().toString();
            contentValues.put("bigYield5", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent5.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent5));
            String edtYieldMonitoringOfBigPotatoContent6 = edtYieldMonitoringOfBigPotato6.getText().toString();
            contentValues.put("bigYield6", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent6.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent6));
            String edtYieldMonitoringOfBigPotatoContent7 = edtYieldMonitoringOfBigPotato7.getText().toString();
            contentValues.put("bigYield7", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent7.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent7));
            String edtYieldMonitoringOfBigPotatoContent8 = edtYieldMonitoringOfBigPotato8.getText().toString();
            contentValues.put("bigYield8", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent8.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent8));
            String edtYieldMonitoringOfBigPotatoContent9 = edtYieldMonitoringOfBigPotato9.getText().toString();
            contentValues.put("bigYield9", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent9.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent9));
            String edtYieldMonitoringOfBigPotatoContent10 = edtYieldMonitoringOfBigPotato10.getText().toString();
            contentValues.put("bigYield10", Float.parseFloat(edtYieldMonitoringOfBigPotatoContent10.isEmpty() ? "0" : edtYieldMonitoringOfBigPotatoContent10));
            //小薯十株测产
            String edtYieldMonitoringOfSmallPotatoContent1 = edtYieldMonitoringOfSmallPotato1.getText().toString();
            contentValues.put("smalYield1", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent1.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent1));
            String edtYieldMonitoringOfSmallPotatoContent2 = edtYieldMonitoringOfSmallPotato2.getText().toString();
            contentValues.put("smalYield2", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent2.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent2));
            String edtYieldMonitoringOfSmallPotatoContent3 = edtYieldMonitoringOfSmallPotato3.getText().toString();
            contentValues.put("smalYield3", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent3.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent3));
            String edtYieldMonitoringOfSmallPotatoContent4 = edtYieldMonitoringOfSmallPotato4.getText().toString();
            contentValues.put("smalYield4", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent4.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent4));
            String edtYieldMonitoringOfSmallPotatoContent5 = edtYieldMonitoringOfSmallPotato5.getText().toString();
            contentValues.put("smalYield5", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent5.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent5));
            String edtYieldMonitoringOfSmallPotatoContent6 = edtYieldMonitoringOfSmallPotato6.getText().toString();
            contentValues.put("smalYield6", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent6.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent6));
            String edtYieldMonitoringOfSmallPotatoContent7 = edtYieldMonitoringOfSmallPotato7.getText().toString();
            contentValues.put("smalYield7", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent7.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent7));
            String edtYieldMonitoringOfSmallPotatoContent8 = edtYieldMonitoringOfSmallPotato8.getText().toString();
            contentValues.put("smalYield8", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent8.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent8));
            String edtYieldMonitoringOfSmallPotatoContent9 = edtYieldMonitoringOfSmallPotato9.getText().toString();
            contentValues.put("smalYield9", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent9.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent9));
            String edtYieldMonitoringOfSmallPotatoContent10 = edtYieldMonitoringOfSmallPotato10.getText().toString();
            contentValues.put("smalYield10", Float.parseFloat(edtYieldMonitoringOfSmallPotatoContent10.isEmpty() ? "0" : edtYieldMonitoringOfSmallPotatoContent10));

            contentValues.put("img1", pathColor);
            contentValues.put("img2", pathCorollaColor);
            contentValues.put("img3", pathPlantFlourish);
            contentValues.put("img4", pathStemColors);
            contentValues.put("img5", pathNaturalFecundity);

            contentValues.put("isUpdate", 0);

        } catch (NumberFormatException e) {
            showShortToast(this, getString(R.string.toast_check_data_form));
            e.printStackTrace();
        }
        return contentValues;
    }

    private void selectPhotoFromAlbum(int selectType) {
        if (ContextCompat.checkSelfPermission(SaveDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SaveDataActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, selectType);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //叶颜色显示照片
            case TAKE_PHOTO_COLOR:
                onResultOfPhoto(resultCode, pathColor, ivShowColor);
                break;
            //花冠色显示照片
            case TAKE_PHOTO_COROLLA_COLOR:
                onResultOfPhoto(resultCode, pathCorollaColor, ivShowCorollaColor);
                break;
            //花繁茂性显示照片
            case TAKE_PHOTO_PLANT_FLOURISH:
                onResultOfPhoto(resultCode, pathPlantFlourish, ivShowPlantFlourish);
                break;
            //茎色显示照片
            case TAKE_PHOTO_STEM_COLORS:
                onResultOfPhoto(resultCode, pathStemColors, ivShowStemColors);
                break;
            //天然结实性显示照片
            case TAKE_PHOTO_NATURAL_FECUNDITY:
                onResultOfPhoto(resultCode, pathNaturalFecundity, ivShowNaturalFecundity);
                break;
//            //块茎整齐度显示照片
//            case TAKE_PHOTO_TUBER_UNIFORMITY:
//                onResultOfPhoto(resultCode, imageUriTuberUniformity, ivShowTuberUniformity);
//                break;
//            //薯型显示照片
//            case TAKE_PHOTO_TUBER_SHAPE:
//                onResultOfPhoto(resultCode, imageUriTuberShape, ivShowTuberShape);
//                break;
//            //薯皮光滑度显示照片
//            case TAKE_PHOTO_POTATO_SKIN_SMOOTHNESS:
//                onResultOfPhoto(resultCode, imageUriPotatoSkinSmoothness, ivShowPotatoSkinSmoothness);
//                break;
//            //芽眼深浅显示照片
//            case TAKE_PHOTO_EYE:
//                onResultOfPhoto(resultCode, imageUriEye, ivShowEye);
//                break;
//            //皮色显示照片
//            case TAKE_PHOTO_SKIN_COLOR:
//                onResultOfPhoto(resultCode, imageUriSkinColor, ivShowSkinColor);
//                break;
//            //肉色显示照片
//            case TAKE_PHOTO_FLESH_COLOR:
//                onResultOfPhoto(resultCode, imageUriFleshColor, ivShowFleshColor);
//                break;
            //从相册选择叶颜色图片
            case SELECT_PHOTO_COLOR:
                if (data != null) {
                    imageUriColor = data.getData();
                    pathColor = getRealPathFromUri(SaveDataActivity.this, imageUriColor);
                    //Log.d("Uriiiii2", imageUriColor + " || " + pathColor);
                    if (imageUriColor != null) {
                        Bitmap bit = null;

                        bit = getImageThumbnail(pathColor, 50, 50);

                        ivShowColor.setImageBitmap(bit);
                    }
                }

                break;
            //从相册选择花冠色图片
            case SELECT_PHOTO_COROLLA_COLORS:
                if (data != null) {
                    imageUriCorollaColor = data.getData();
                    pathCorollaColor = getRealPathFromUri(SaveDataActivity.this, imageUriCorollaColor);
                    if (imageUriCorollaColor != null) {
                        Bitmap bit = null;
                        bit = getImageThumbnail(pathCorollaColor, 50, 50);
                        ivShowCorollaColor.setImageBitmap(bit);
                    }
                }

                break;
            //从相册选择花繁茂性图片
            case SELECT_PHOTO_PLANT_FLOURISH:
                if (data != null) {
                    imageUriPlantFlourish = data.getData();
                    pathPlantFlourish = getRealPathFromUri(SaveDataActivity.this, imageUriPlantFlourish);
                    if (imageUriPlantFlourish != null) {
                        Bitmap bit = null;

                        bit = getImageThumbnail(pathPlantFlourish, 50, 50);

                        ivShowPlantFlourish.setImageBitmap(bit);
                    }
                }

                break;
            //从相册选择茎色图片
            case SELECT_PHOTO_STEM_COLOR:
                if (data != null) {
                    imageUriStemColors = data.getData();
                    pathStemColors = getRealPathFromUri(SaveDataActivity.this, imageUriStemColors);
                    if (imageUriStemColors != null) {
                        Bitmap bit = null;
                        bit = getImageThumbnail(pathStemColors, 50, 50);
                        ivShowStemColors.setImageBitmap(bit);
                    }
                }

                break;
            //从相册选择天然结实性图片
            case SELECT_PHOTO_NATURAL_FECUNDITY:
                if (data != null) {
                    imageUriNaturalFecundity = data.getData();
                    pathNaturalFecundity = getRealPathFromUri(SaveDataActivity.this, imageUriNaturalFecundity);
                    if (imageUriNaturalFecundity != null) {
                        Bitmap bit = null;
                        bit = getImageThumbnail(pathNaturalFecundity, 50, 50);
                        ivShowNaturalFecundity.setImageBitmap(bit);
                    }
                }

                break;
            default:
                break;
        }
    }

    private void onResultOfPhoto(int resultCode, String path, ImageView ivShowPicture) {
        if (resultCode == RESULT_OK) {
            //在应用中显示图片
            Bitmap bitmap = null;
//            try {
//                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            bitmap = getImageThumbnail(path, 50, 50);
            ivShowPicture.setImageBitmap(bitmap);
        }
    }

//
//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()) {
//            case R.id.sowing_period_input:
//                showDatePickerDialog(SaveDataActivity.this, edtSowingPeriodInput);
//                break;
//            case R.id.emergence_period:
//                showDatePickerDialog(SaveDataActivity.this, edtEmergencePeriod);
//                break;
//            default:
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 6:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
                    intent1.setType("image/*");
                    startActivityForResult(intent1, 6);
                } else {
                    showShortToast(SaveDataActivity.this, getString(R.string.toast_no_permission));
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        saveImageToGallery(Context., bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}