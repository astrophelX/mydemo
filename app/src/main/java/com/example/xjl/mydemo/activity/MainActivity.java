package com.example.xjl.mydemo.activity;
/**
 * ......................我佛慈悲....................
 * ......................_oo0oo_.....................
 * .....................o8888888o....................
 * .....................88" . "88....................
 * .....................(| -_- |)....................
 * .....................0\  =  /0....................
 * ...................___/`---'\___..................
 * ..................' \\|     |// '.................
 * ................./ \\|||  :  |||// \..............
 * .............../ _||||| -卍-|||||- \..............
 * ..............|   | \\\  -  /// |   |.............
 * ..............| \_|  ''\---/''  |_/ |.............
 * ..............\  .-\__  '-'  ___/-. /.............
 * ............___'. .'  /--.--\  `. .'___...........
 * .........."" '<  `.___\_<|>_/___.' >' ""..........
 * ........| | :  `- \`.;`\ _ /`;.`/ - ` : | |.......
 * ........\  \ `_.   \_ __\ /__ _/   .-` /  /.......
 * ....=====`-.____`.___ \_____/___.-`___.-'=====....
 * ......................`=---='.....................
 * ..................佛祖开光 ,永无BUG................
 */
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xjl.mydemo.R;
import com.tzutalin.dlibtest.CameraConnectionFragment;
import com.tzutalin.dlibtest.FileUtils;
import com.example.xjl.mydemo.tools.AnimationsContainer;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.PedestrianDet;
import com.tzutalin.dlib.VisionDetRet;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1;
//    private Button start,stop;
    private ImageView imageView;
    private QMUIRoundButton circularButton1,circularButton2
            ,circularButton3;
//    private AnimationDrawable animationDrawable;
    boolean run = false;
    AnimationsContainer.FramesSequenceAnimation animation;
    //垃圾dlib jinLib 初始化
    PedestrianDet mPersonDet;
    FaceDet mFaceDet;
    protected String mTestImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.img_main);

//        start = findViewById(R.id.button_start);
//        stop = findViewById(R.id.button_stop);
//        setXml2FrameAnim2();
//        circularButton1 = (QMUIRoundButton) findViewById(R.id.button_start);
//        circularButton2 = (QMUIRoundButton) findViewById(R.id.btn_record);
//        circularButton3 = (QMUIRoundButton) findViewById(R.id.btn_camera);

        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(animation == null)
                    //播放动画
                    animation = AnimationsContainer.getInstance(R.array.loading_anim_smile, 100).createProgressDialogAnim(imageView);//
                if(!switchBtn()){
                    animation.start();
                }else {
                    animation.stop();
                }
            }
        });
        circularButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyCameraActivity.class));
//                getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.mycontainer_mod, CameraConnectionFragment.newInstance())
//                        .commit();
                //想办法把悬浮窗弄过来
            }
        });
        circularButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    //管理按钮切换
    private boolean switchBtn(){
        boolean returnV = run;
        run = !run;

        circularButton1.setText(run == false ? "开始" : "停止");
        return returnV;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mTestImgPath = cursor.getString(columnIndex);
                cursor.close();
                if (mTestImgPath != null) {
                    runDetectAsync(mTestImgPath);
                    //Toast.makeText(this, "Img Path:" + mTestImgPath, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
    protected void runDetectAsync(@NonNull String imgPath) {
        final String targetPath = Constants.getFaceShapeModelPath();
        if (!new File(targetPath).exists()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Copy landmark model to " + targetPath, Toast.LENGTH_SHORT).show();
                }
            });
            FileUtils.copyFileFromRawToOthers(getApplicationContext(), R.raw.shape_predictor_68_face_landmarks, targetPath);
        }
        // Init
        if (mPersonDet == null) {
            mPersonDet = new PedestrianDet();
        }
        if (mFaceDet == null) {
            mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
        }

        List<VisionDetRet> faceList = mFaceDet.detect(imgPath);
        if (faceList.size() > 0) {

        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "No face", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
