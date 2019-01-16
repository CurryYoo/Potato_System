package com.example.kerne.potato.temporarystorage;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
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
import java.util.Calendar;

public class SaveDataActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //需要暂存的各字段
    EditText edtSpeciesID = null;
    private String commitId = "initial id";
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
    private EditText edtTenPlantHeight = null;
    private EditText edtAveragePlantHeight = null;
    private EditText edtBranchNumOfTenPlants = null;
    private EditText edtAverageBranchNum = null;
    private EditText edtYieldMonitoringOfTenPlants = null;

    //拍照功能
    public static final int TAKE_PHOTO = 1;
    private ImageView ivShowPicture = null;
    private Uri imageUri;
    private File outputImage;
    private String fileNameString;
    //叶颜色拍照
    public static final int TAKE_PHOTO_COLOR = 2;
    private ImageView ivShowColor = null;
    private Uri imageUriColor;
    private File outputImageColor;
    private String fileNameStringColor;

    //暂存功能
    private SpeciesDBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    //小区id
    private String plotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Action bar显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_commit_data);

        //品种Id，判断是否重复存储
        edtSpeciesID = (EditText) findViewById(R.id.edt_species_id);

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
        ImageButton imbTakePhotoColor = (ImageButton) findViewById(R.id.imb_colors);
        imbTakePhotoColor.setOnClickListener(this);
        ivShowColor = (ImageView) findViewById(R.id.imv_colors);

        //花冠色
        spnCorollaColors = (Spinner) findViewById(R.id.corolla_colors);

        //花繁茂性
        spnPlantFlourish = (Spinner) findViewById(R.id.plant_flourish);

        //茎色
        spnStemColor = (Spinner) findViewById(R.id.stem_color);

        //天然结实性
        spnNaturalFecundity = (Spinner) findViewById(R.id.natural_fecundity);


        //成熟期
        edtMaturePeriod = (EditText) findViewById(R.id.mature_period);
        edtMaturePeriod.setInputType(InputType.TYPE_NULL);
        edtMaturePeriod.setOnClickListener(this);

        //生育日数
        edtGrowingDays = (EditText) findViewById(R.id.growing_days);

        //块茎整齐度
        spnTuberUniformity = (Spinner) findViewById(R.id.tuber_uniformity);

        //薯型
        spnTuberShape = (Spinner) findViewById(R.id.tuber_shape);

        //薯皮光滑度
        spnPotatoSkinSmoothness = (Spinner) findViewById(R.id.potato_skin_smoothness);

        //芽眼深浅
        spnEye = (Spinner) findViewById(R.id.eye);

        //皮色
        spnSkinColor = (Spinner) findViewById(R.id.skin_color);

        //肉色
        spnFleshColor = (Spinner) findViewById(R.id.flesh_color);

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

        //十株株高
        edtTenPlantHeight = (EditText) findViewById(R.id.ten_plant_height);

        //平均株高
        edtAveragePlantHeight = (EditText) findViewById(R.id.average_plant_height);

        //十株的分支数
        edtBranchNumOfTenPlants = (EditText) findViewById(R.id.branch_num_of_ten_plants);

        //平均的分支数
        edtAverageBranchNum = (EditText) findViewById(R.id.average_branch_num);

        //十株测产
        edtYieldMonitoringOfTenPlants = (EditText) findViewById(R.id.yield_monitoring_of_ten_plants);

        Button btnSaveOffline = (Button) findViewById(R.id.save_offline);
        btnSaveOffline.setOnClickListener(this);

        Button btnTakePhoto = (Button) findViewById(R.id.take_photo);
        btnTakePhoto.setOnClickListener(this);
        ivShowPicture = (ImageView) findViewById(R.id.iv_show_picture);

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
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            default:
        }
        return true;
    }

    public File appDir = new File(Environment.getExternalStorageDirectory(), "Potato");

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
                Intent intent_plotId = getIntent();
                plotId = intent_plotId.getStringExtra("plotId");

                ContentValues contentValues = new ContentValues();
                //开始组装数据
                //小区id
                contentValues.put("plotId", plotId);
                //播种期
                contentValues.put("sowing_period", edtSowingPeriodInput.getText().toString());
                //出苗期
                contentValues.put("emergence_period", edtEmergencePeriod.getText().toString());
                //出苗率
                String edtRateOfEmergenceContent = edtRateOfEmergence.getText().toString();
                contentValues.put("rate_of_emergence", Integer.parseInt(edtRateOfEmergenceContent.isEmpty() ? "0" : edtRateOfEmergenceContent));
                //现蕾期
                contentValues.put("squaring_period", edtSquaringPeriod.getText().toString());
                //开花期
                contentValues.put("flowering_period", edtFloweringPeriod.getText().toString());
                //叶颜色
                contentValues.put("leaf_color", spnLeafColor.getSelectedItem().toString());
                //花冠色
                contentValues.put("corolla_colors", spnCorollaColors.getSelectedItem().toString());
                //花繁茂性
                contentValues.put("plant_flourish", spnPlantFlourish.getSelectedItem().toString());
                //茎色
                contentValues.put("stem_color", spnStemColor.getSelectedItem().toString());
                //天然结实性
                contentValues.put("natural_fecundity", spnNaturalFecundity.getSelectedItem().toString());
                //成熟期
                contentValues.put("mature_period", edtMaturePeriod.getText().toString());
                //生育日数
                String edtGrowingDaysContent = edtGrowingDays.getText().toString();
                contentValues.put("growing_days", Integer.parseInt(edtGrowingDaysContent.isEmpty() ? "0" : edtGrowingDaysContent));
                //块茎整齐度
                contentValues.put("tuber_uniformity", spnTuberUniformity.getSelectedItem().toString());
                //薯型
                contentValues.put("tuber_shape", spnTuberShape.getSelectedItem().toString());
                //薯皮光滑度
                contentValues.put("potato_skin_smoothness", spnPotatoSkinSmoothness.getSelectedItem().toString());
                //芽眼深浅
                contentValues.put("eye", spnEye.getSelectedItem().toString());
                //皮色
                contentValues.put("skin_color", spnSkinColor.getSelectedItem().toString());
                //肉色
                contentValues.put("flesh_color", spnFleshColor.getSelectedItem().toString());
                //是否入选
                int checkedRadioButtonId = rgWhetherToBeIncluded.getCheckedRadioButtonId();
                String checkedRadioButtonContent = null;
                switch (checkedRadioButtonId) {
                    case R.id.no:
                        checkedRadioButtonContent = "no";
                        break;
                    case R.id.yes:
                    default:
                        checkedRadioButtonContent = "yes";
                }
                contentValues.put("whether_to_be_included", checkedRadioButtonContent);
                //备注
                contentValues.put("remark", edtRemark.getText().toString());
                //收获株数
                String edtNumOfHarvestedPlantsContent = edtNumOfHarvestedPlants.getText().toString();
                contentValues.put("num_of_harvested_plants", Integer.parseInt(edtNumOfHarvestedPlantsContent.isEmpty() ? "0" : edtNumOfHarvestedPlantsContent));
                //大中薯数
                String edtNumOfLargeAndMediumPotatoesContent = edtNumOfLargeAndMediumPotatoes.getText().toString();
                contentValues.put("num_of_large_and_medium_potatoes", Integer.parseInt(edtNumOfLargeAndMediumPotatoesContent.isEmpty() ? "0" : edtNumOfLargeAndMediumPotatoesContent));
                //大中薯重
                String edtWeightOfLargeAndMediumPotatoesContent = edtWeightOfLargeAndMediumPotatoes.getText().toString();
                contentValues.put("weight_of_large_and_medium_potatoes", Integer.parseInt(edtWeightOfLargeAndMediumPotatoesContent.isEmpty() ? "0" : edtWeightOfLargeAndMediumPotatoesContent));
                //小薯数
                String edtNumOfSmallPotatoesContent = edtNumOfSmallPotatoes.getText().toString();
                contentValues.put("num_of_small_potatoes", Integer.parseInt(edtNumOfSmallPotatoesContent.isEmpty() ? "0" : edtNumOfSmallPotatoesContent));
                //小薯重
                String edtWeightOfSmallPotatoesContent = edtWeightOfSmallPotatoes.getText().toString();
                contentValues.put("weight_of_small_potatoes", Integer.parseInt(edtWeightOfSmallPotatoesContent.isEmpty() ? "0" : edtWeightOfSmallPotatoesContent));
                //商品薯率
                String edtRateOfEconomicPotatoContent = edtRateOfEconomicPotato.getText().toString();
                contentValues.put("rate_of_economic_potato", Integer.parseInt(edtRateOfEconomicPotatoContent.isEmpty() ? "0" : edtRateOfEconomicPotatoContent));
                //小区产量1
                String edtSmallSectionYield1Content = edtSmallSectionYield1.getText().toString();
                contentValues.put("small_section_yield1", Integer.parseInt(edtSmallSectionYield1Content.isEmpty() ? "0" : edtSmallSectionYield1Content));
                //小区产量2
                String edtSmallSectionYield2Content = edtSmallSectionYield2.getText().toString();
                contentValues.put("small_section_yield2", Integer.parseInt(edtSmallSectionYield2Content.isEmpty() ? "0" : edtSmallSectionYield2Content));
                //小区产量3
                String edtSmallSectionYield3Content = edtSmallSectionYield3.getText().toString();
                contentValues.put("small_section_yield3", Integer.parseInt(edtSmallSectionYield3Content.isEmpty() ? "0" : edtSmallSectionYield3Content));
                //亩产量
                String edtPerMuYieldContent = edtPerMuYield.getText().toString();
                contentValues.put("per_mu_yield", Integer.parseInt(edtPerMuYieldContent.isEmpty() ? "0" : edtPerMuYieldContent));
                //十株株高
                String edtTenPlantHeightContent = edtTenPlantHeight.getText().toString();
                contentValues.put("ten_plant_height", Integer.parseInt(edtTenPlantHeightContent.isEmpty() ? "0" : edtTenPlantHeightContent));
                //平均株高
                String edtAveragePlantHeightContent = edtAveragePlantHeight.getText().toString();
                contentValues.put("average_plant_height", Integer.parseInt(edtAveragePlantHeightContent.isEmpty() ? "0" : edtAveragePlantHeightContent));
                //十株的分支数
                String edtBranchNumOfTenPlantsContent = edtBranchNumOfTenPlants.getText().toString();
                contentValues.put("branch_num_of_ten_plants", Integer.parseInt(edtBranchNumOfTenPlantsContent.isEmpty() ? "0" : edtBranchNumOfTenPlantsContent));
                //平均分支数
                String edtAverageBranchNumContent = edtAverageBranchNum.getText().toString();
                contentValues.put("average_branch_num", Integer.parseInt(edtAverageBranchNumContent.isEmpty() ? "0" : edtAverageBranchNumContent));
                //十株测产
                String edtYieldMonitoringOfTenPlantsContent = edtYieldMonitoringOfTenPlants.getText().toString();
                contentValues.put("yield_monitoring_of_ten_plants", Integer.parseInt(edtYieldMonitoringOfTenPlantsContent.isEmpty() ? "0" : edtYieldMonitoringOfTenPlantsContent));

                sqLiteDatabase.insert("SpeciesTable", null, contentValues);
                contentValues.clear();
                Toast.makeText(this,
                        "暂存成功，当手机在线时，请提交到远程服务器", Toast.LENGTH_LONG).show();
                break;
            case R.id.take_photo:
                //启动相机程序
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }

                //创建File对象，用于存储拍照后的图片
                fileNameString = System.currentTimeMillis() + ".jpg";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    outputImage = new File(getExternalCacheDir(), fileNameString);
                }
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(SaveDataActivity.this,
                            "com.example.kerne.potato.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            //叶颜色拍照并显示
            case R.id.imb_colors:
                fileNameStringColor = System.currentTimeMillis() + ".jpg";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    outputImageColor = new File(getExternalCacheDir(), fileNameStringColor);
                }
                try {
                    if (outputImageColor.exists()) {
                        outputImageColor.delete();
                    }
                    outputImageColor.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            File appDir = new File(Environment.getExternalStorageDirectory(), "Potato");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUriColor = FileProvider.getUriForFile(SaveDataActivity.this,
                            "com.example.kerne.potato.fileprovider", outputImageColor);
                } else {
                    imageUriColor = Uri.fromFile(outputImageColor);
                }
                //启动相机程序
                Intent intentColor = new Intent("android.media.action.IMAGE_CAPTURE");
                intentColor.putExtra(MediaStore.EXTRA_OUTPUT, imageUriColor);
                startActivityForResult(intentColor, TAKE_PHOTO_COLOR);
                break;
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
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            ivShowPicture.setImageBitmap(imageBitmap);
//        }

//        Log.d("SaveDataActivity", "onActivityResult method"+" " + (data == null));
        switch (requestCode) {
            case TAKE_PHOTO:
//                Log.d("SaveDataActivity", "switch TAKE_PHOTO");
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
//                Log.d("SaveDataActivity", "before switch TAKE_PHOTO break");
                break;
            case TAKE_PHOTO_COLOR:
//                Log.d("SaveDataActivity", "switch TAKE_PHOTO");
                if (resultCode == RESULT_OK) {
                    //在应用中显示图片
                    Bitmap bitmapColor = null;
                    try {
                        bitmapColor = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUriColor));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    ivShowColor.setImageBitmap(bitmapColor);
                }
//                Log.d("SaveDataActivity", "before switch TAKE_PHOTO break");
                break;
            default:
                break;
        }
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
                editText.setText(year + "-" + month + "-" + dayOfMonth);
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

