package com.example.GuangMing128.camera;

import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.GuangMing128.BaseFragment;
import com.example.GuangMing128.R;
import com.example.GuangMing128.camera.ui.CameraLcdView;
import com.llvision.glass3.core.camera.client.CameraException;
import com.llvision.glass3.core.camera.client.CameraStatusListener;
import com.llvision.glass3.core.camera.client.ICameraBase;
import com.llvision.glass3.core.camera.client.ICameraClient;
import com.llvision.glass3.core.camera.client.ICameraDevice;
import com.llvision.glass3.core.camera.client.IFrameCallback;
import com.llvision.glass3.core.camera.client.IRenderFrameCallback;
import com.llvision.glass3.core.camera.client.PixelFormat;
import com.llvision.glass3.core.camera.client.RecordAudioListener;
import com.llvision.glass3.core.camera.client.RecordStatusListener;
import com.llvision.glass3.core.lcd.client.IGlassDisplay;
import com.llvision.glass3.core.lcd.client.ILCDClient;
import com.llvision.glass3.library.camera.entity.CameraRoi;
import com.llvision.glass3.library.camera.entity.RecordParameter;
import com.llvision.glass3.library.camera.entity.Size;
import com.llvision.glass3.library.utils.YuvFormat;
import com.llvision.glass3.platform.ConnectionStatusListener;
import com.llvision.glass3.platform.GlassException;
import com.llvision.glass3.platform.IGlass3Device;
import com.llvision.glass3.platform.LLVisionGlass3SDK;//负责SDK初始化、销毁和设备管理
import com.llvision.glxss.common.encoder.MediaEncoder;
import com.llvision.glxss.common.encoder.MediaVideoBufferEncoder;
import com.llvision.glxss.common.exception.BaseException;
import com.llvision.glxss.common.push.StreamParam;
import com.llvision.glxss.common.push.encoder.video.input.Frame;
import com.llvision.glxss.common.push.record.MP4RecordClient;
import com.llvision.glxss.common.push.record.RecordController;
import com.llvision.glxss.common.ui.CameraTextureView;
import com.llvision.glxss.common.ui.SurfaceCallback;
import com.llvision.glxss.common.utils.LogUtil;
import com.llvision.glxss.common.utils.ToastUtils;
import com.llvision.support.uvc.utils.FpsCounter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;

public class CameraFragment extends BaseFragment {

    private static final String TAG = CameraFragment.class.getSimpleName();    //得到类的简写名称（CameraFragment）=TAG
    private static final long SECOND_ONE = 1000;
    private static final int DEFAULT_QUALITY = 100;
    private static final String DIR_NAME = "LLVisionCamera";
    private static final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINESE);//设置日期格式

    private static final Map<Integer, Integer> mFovImageList = new TreeMap<Integer, Integer>() {
        {
            put(ICameraDevice.FOV_60, R.drawable.btn_fov_10x2x);
            put(ICameraDevice.FOV_40, R.drawable.btn_fov_15x2x);
            put(ICameraDevice.FOV_30, R.drawable.btn_fov_20x2x);
        }
    };

    private IGlass3Device mGlass3Device;//继承自IGlassDevice，包括获取设备ID等方法
    private ICameraDevice mICameraDevice;//摄像头操作接（获取录像状态，开始/暂停/停止录像，重启录像）
    private ICameraClient mCameraClient;//相机
    private ILCDClient mLcdClient;//接口
    private IGlassDisplay mGlassDisplay;

    // 接口?

    private CameraTextureView mCameraView;
    private ArrayAdapter<String> mPreviewAdapter;
    private Button bntCamera;
    private Button btnRecord;
    private Button mBtnTakePicture;
    private Button mBtnGetEc;
    private Button mBtnGetAnti;
    private Button mBtnGetAe;
    private Button mBtnSetRoi;
    private Button mBtnGetRoi;
    private RadioGroup mRgEc;
    private RadioGroup mRgAnti;
    private RadioGroup mRgAe;
    private EditText mEtRoiRight;
    private EditText mEtRoiBottom;

    private ImageView mFovIv;
    private Button mReStartRecordBtn;
    private TextView mTvShowEc;
    private TextView mTvShowAnti;
    private TextView mTvShowAe;
    private TextView mTvShowRoi;
    private EditText mEtRoiLeft;
    private EditText mEtRoiTop;
    private TextView mRenderTextView;
    private Button mRenderRecordBtn;

    private TextView responseResult;


//    private TextView mGlassResolutionTv;
//    private ImageView mGlassFovIv;
//    private TextView mGlassRecordTimeTv;
//    private TextView mActualFpsTextTv;

    private int mWidth = 1280;
    private int mHeight = 720;
    private int mFps = 30;
    private boolean mIsStop = false;
    private int mCurrentFov = ICameraDevice.FOV_DEFAULT;//3
    private int mRenderCount = 0;
    private float VIDEO_BPP = 0.50f;
    private int AUDIO_CHANNEL_COUNT = 1;
    private int AUDIO_SAMPLING_RATE = 16000;
    //初始化参数设置

    private boolean mHadDestroy = false;

    //private MediaMuxerWrapper mRenderMuxer;
    private MP4RecordClient mMp4v2Helper;
    private RecordParameter mRecordParameter;

    private static final int MSG_RESTART_RECORDING = 1;
    private static final int MSG_UPDATE_FPS = 2;
    private static final int MSG_RENDER_VIEW = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESTART_RECORDING:
                    try {
                        mICameraDevice.startRecording(mRecordStatusListener);
                    } catch (CameraException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_UPDATE_FPS:
                    try {
                        float fps = (float) msg.obj;
                        mCameraLcdView.setFpsText("fps: " + fps);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_RENDER_VIEW:
                    mRenderTextView.setText("mRenderCount = " + (mRenderCount++));
                    break;
                default:
                    break;
            }
        }
    };
    private View mOverlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCameraLcdView = CameraLcdView.createCameralcdView();
        mCameraLcdView.setRecordingMaximumTime(30);//设置录屏时间/秒
        //监听录屏时间
        mCameraLcdView.setOnRecordingRearchMaximumListener(new CameraLcdView.IReachMaximumTimeForRecordingListener() {
            @Override
            public void onReachMaximumTime() {
                try {
                    if (mICameraDevice != null && mICameraDevice.isRecording()) {
                        //如果摄像头正在录像则停止录像
                        mICameraDevice.stopRecording();
                    }
                    //发送重新开始录像信息
                    mHandler.sendEmptyMessageDelayed(MSG_RESTART_RECORDING, 500);
                } catch (CameraException e) {
                    e.printStackTrace();
                }
            }
        });

        //连接眼镜
        LLVisionGlass3SDK.getInstance().registerConnectionListener(mConnectionStatusListener);//新建连接状态监听器
        try {
            if (LLVisionGlass3SDK.getInstance().isServiceConnected()) {
                //如果设备已连接，保存设备到列表
                List<IGlass3Device> glass3DeviceList = LLVisionGlass3SDK.getInstance()
                        .getGlass3DeviceList();
                if (glass3DeviceList != null && glass3DeviceList.size() > 0) {
                    //如果设备列表不为空(设备已连接)，获取索引为0的设备
                    mGlass3Device = glass3DeviceList.get(0);
                    //获取眼镜摄像头
                    mCameraClient = (ICameraClient) LLVisionGlass3SDK.getInstance().getGlass3Client(
                            IGlass3Device.Glass3DeviceClient.CAMERA);
                    //获取眼镜显示器
                    mLcdClient = (ILCDClient) LLVisionGlass3SDK.getInstance().getGlass3Client(
                            IGlass3Device.Glass3DeviceClient.LCD);
                }
            } else {
                LogUtil.e("服务尚未连接.");
            }
        } catch (GlassException e) {
            e.printStackTrace();
        }


        //开启视频流混合，可以在视频流上叠加自定义View，调用 stopRenderCameraStream关闭
        mOverlayView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_camera_render, null);
        mRenderTextView = mOverlayView.findViewById(R.id.id_render_text);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        bntCamera = rootView.findViewById(R.id.btn_camera);
        bntCamera.setOnClickListener(mOnClickListener);
        btnRecord = rootView.findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(mOnClickListener);
        mBtnTakePicture = rootView.findViewById(R.id.btn_takepicture);
        mBtnTakePicture.setOnClickListener(mOnClickListener);
        mReStartRecordBtn = rootView.findViewById(R.id.btn_restart);
        mReStartRecordBtn.setOnClickListener(mOnClickListener);
        mFovIv = rootView.findViewById(R.id.id_fov_btn);
        mFovIv.setOnClickListener(mOnClickListener);
        mBtnGetEc = rootView.findViewById(R.id.btn_getEc);
        mBtnGetEc.setOnClickListener(mOnClickListener);
        mBtnGetAnti = rootView.findViewById(R.id.btn_getAntibanding);
        mBtnGetAnti.setOnClickListener(mOnClickListener);
        mBtnGetAe = rootView.findViewById(R.id.btn_getAe);
        mBtnGetAe.setOnClickListener(mOnClickListener);
        mBtnGetRoi = rootView.findViewById(R.id.btn_getRoi);
        mBtnGetRoi.setOnClickListener(mOnClickListener);
        mBtnSetRoi = rootView.findViewById(R.id.btn_setRoi);
        mBtnSetRoi.setOnClickListener(mOnClickListener);
        mRgEc = rootView.findViewById(R.id.rg_ec);
        mRgEc.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRgAnti = rootView.findViewById(R.id.rg_anti);
        mRgAnti.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRgAe = rootView.findViewById(R.id.rg_ae);
        mRgAe.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mEtRoiLeft = rootView.findViewById(R.id.et_roi_left);
        mEtRoiTop = rootView.findViewById(R.id.et_roi_top);
        mEtRoiRight = rootView.findViewById(R.id.et_roi_right);
        mEtRoiBottom = rootView.findViewById(R.id.et_roi_bottom);

        responseResult = rootView.findViewById(R.id.textView8);

        mTvShowEc = rootView.findViewById(R.id.tv_ecInfo);
        mTvShowAnti = rootView.findViewById(R.id.tv_AntibandingInfo);
        mTvShowAe = rootView.findViewById(R.id.tv_AeInfo);
        mTvShowRoi = rootView.findViewById(R.id.tv_showRoi);
        mCameraView = (CameraTextureView) rootView.findViewById(R.id.camera_view);
        mCameraView.setSurfaceCallback(mSurfaceCallback);
        mCameraView.setAspectRatio(mWidth / (float) mHeight);
        Spinner spinner = rootView.findViewById(R.id.id_spinner_view);

        mRenderRecordBtn = rootView.findViewById(R.id.id_render_recod_btn);
        mRenderRecordBtn.setOnClickListener(mOnClickListener);

        if (mGlass3Device != null) {
            try {
                mICameraDevice = mCameraClient.openCamera(mGlass3Device, mCameraStatusListener);
                if (mICameraDevice != null) {
                    mICameraDevice.setPreviewSize(mWidth, mHeight, mFps);
                    mICameraDevice.connect();
                }
            } catch (CameraException e) {
                e.printStackTrace();
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }
        List<Size> list = null;
        String[] previewList = null;
        if (mICameraDevice != null) {
            try {
                list = mICameraDevice.getSupportedPreviewSizeList();
                if (list != null) {
                    previewList = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        Size size = list.get(i);
                        previewList[i] = size.width + "," + size.height + "," + (int) size
                                .getCurrentFrameRate();
                    }
                }
            } catch (CameraException e) {
                e.printStackTrace();
            }
        }
        if (previewList != null) {
            if (mPreviewAdapter == null) {
                mPreviewAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, previewList);
                mPreviewAdapter.setDropDownViewResource(android.R.layout.select_dialog_item);
            }
            spinner.setAdapter(mPreviewAdapter);
            final List<Size> finalList = list;
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long
                        id) {
                    Size size = finalList.get(position);
                    int width = size.width;
                    int height = size.height;
                    int fps = (int) size.getCurrentFrameRate();
                    //is supported
                    if (width != mWidth || height != mHeight || fps != mFps) {
                        mWidth = width;
                        mHeight = height;
                        mFps = fps;
                        try {
                            if (mCameraClient != null && mICameraDevice != null && mICameraDevice
                                    .isCameraConnected()) {
                                mCameraView.setAspectRatio((float) width / (float) height);

                                mCameraLcdView.setResolutionText(width + "x" + height + " " + fps +
                                        "fps");
                                mICameraDevice.resize(width, height, fps);
                                mIFrameCallback = new FrameCallback(mHandler, responseResult);
                                mICameraDevice.setFrameCallback(mIFrameCallback, PixelFormat
                                        .PIXEL_FORMAT_NV21);
                            }
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            Toast.makeText(getActivity(), "get supported preview size list is null ", Toast
                    .LENGTH_LONG).show();
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mHadDestroy = true;
        closeGlassDisplay();
        if (mICameraDevice != null) {
            mICameraDevice.release();
        }
        if (mCameraLcdView != null) {
            mCameraLcdView.destroy();
        }
        //close render record
        if (mMp4v2Helper != null) {
            mMp4v2Helper.stop();
            mMp4v2Helper = null;
        }
        if (isRenderRecord && mICameraDevice != null) {
            try {
                mICameraDevice.stopRenderCameraStream();
            } catch (CameraException e) {
                e.printStackTrace();
            }
        }
        isRenderRecord = false;
        mRenderRecordBtn.setText("start render record");
        btnRecord.setEnabled(true);
        mReStartRecordBtn.setEnabled(true);

        super.onDestroyView();

    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        LLVisionGlass3SDK.getInstance().unRegisterConnectionListener(mConnectionStatusListener);
        mGlass3Device = null;
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_camera:
                    if (mGlass3Device != null) {
                        try {
                            if (mCameraClient == null) {
                                mCameraClient = (ICameraClient) LLVisionGlass3SDK.getInstance()
                                        .getGlass3Client(
                                                IGlass3Device.Glass3DeviceClient.CAMERA);
                            }
                            if (mICameraDevice == null || !mICameraDevice.isCameraOpened()) {

                                mICameraDevice = mCameraClient.openCamera(mGlass3Device,
                                        mCameraStatusListener);
                            }
                            if (!mICameraDevice.isCameraConnected()) {
                                mICameraDevice.setPreviewSize(mWidth, mHeight, mFps);
                                mCameraView.setAspectRatio((float) mWidth / (float) mHeight);
                                mICameraDevice.connect();
                                bntCamera.setText("Close");
                            } else {
                                mICameraDevice.disconnect();
                                bntCamera.setText("OPEN");
                                btnRecord.setText("STARTRECORD");
                            }
                        } catch (CameraException e) {
                            LogUtil.e(TAG, e);
                        } catch (BaseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_record:
                    if (mICameraDevice != null) {
                        RecordParameter parameter = new RecordParameter.RecordParameterBuilder().build();
                        try {
                            if (mICameraDevice.isCameraConnected()
                                    && !mICameraDevice.isRecording()) {
                                //设置码率
//                                parameter.setVideoBitRate(2 * 1000 * 1000);
//                                mICameraDevice.setRecordParameter(parameter);

                                mRecordStatusListener = new MyRecordStatusListener(mCameraLcdView);
                                mRecordAudioListener = new MyRecordAudioListener();
                                mICameraDevice.startRecording(mRecordStatusListener, mRecordAudioListener);
                                btnRecord.setText("CLOSERECORD");
                                mRenderRecordBtn.setEnabled(false);

                            } else if (mICameraDevice.isRecording()) {
                                parameter = mICameraDevice.getRecordParameter();
                                LogUtil.i(parameter.toString());

                                mICameraDevice.stopRecording();
                                btnRecord.setText("STARTRECORD");
                                mReStartRecordBtn.setText("PAUSERECORDING");
                                mRenderRecordBtn.setEnabled(true);
                            }

                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_restart:
                    try {
                        if (mICameraDevice.isCameraConnected() && mICameraDevice.isRecording()) {
                            if (mIsStop) {
                                mReStartRecordBtn.setText("PAUSERECORDING");
                                mICameraDevice.reStartRecording();
                                mIsStop = false;
                                mRenderRecordBtn.setEnabled(false);
                            } else {
                                mReStartRecordBtn.setText("RESTARTRECORDING");
                                mICameraDevice.pauseRecording();
                                mIsStop = true;
                                mRenderRecordBtn.setEnabled(true);
                            }

                        }
                    } catch (CameraException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.id_fov_btn:
                    if (mICameraDevice != null) {
                        mCurrentFov--;
                        try {
                            if (mCurrentFov < ICameraDevice.FOV_30) {
                                mCurrentFov = ICameraDevice.FOV_60;
                            }
                            mICameraDevice.setFov(mCurrentFov);
                            mFovIv.setImageResource(mFovImageList.get(mCurrentFov));
                            if (mCameraLcdView != null) {
                                mCameraLcdView.setGlassFovImageResource(mFovImageList.get(mCurrentFov));
                            }
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_takepicture:
                    if (mICameraDevice != null) {
                        if (mICameraDevice.isCameraConnected()) {
                            String path = getCaptureFile(".jpg").toString();
                            if (path != null && !path.equals("")) {
                                try {
                                    mICameraDevice.takePicture(path);

                                } catch (CameraException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogUtil.e(TAG, "storage path is null.");

                            }

                        }

                    }
                    break;
                case R.id.btn_getEc:
                    if (mICameraDevice != null) {
                        try {
                            int ec = mICameraDevice.getEc();
                            mTvShowEc.setText("Ec value:" + ec);
                            LogUtil.d("CameraFragment#syncEc#ec:" + ec);
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_getAntibanding:
                    if (mICameraDevice != null) {
                        try {
                            int anti = mICameraDevice.getAntibanding();
                            switch (anti) {
                                case ICameraBase.ANTIBANDING_OFF:
                                    mTvShowAnti.setText("Antibanding value:OFF");
                                    break;
                                case ICameraBase.ANTIBANDING_50HZ:
                                    mTvShowAnti.setText("Antibanding value:50HZ");
                                    break;
                                case ICameraBase.ANTIBANDING_60HZ:
                                    mTvShowAnti.setText("Antibanding value:60HZ");
                                    break;
                                default:
                                    mTvShowAnti.setText("Antibanding value:" + anti);

                            }
                            LogUtil.d("CameraFragment#getAntibanding#ant:" + anti);
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_getAe:
                    if (mICameraDevice != null) {
                        try {
                            int ae = mICameraDevice.getAe();
                            switch (ae) {
                                case ICameraBase.AE_MODE_CENTER:
                                    mTvShowAe.setText("Ae value:CENTER");
                                    break;
                                case ICameraBase.AE_MODE_ALL:
                                    mTvShowAe.setText("Ae value:ALL");
                                    break;
                                case ICameraBase.AE_MODE_ROI:
                                    mTvShowAe.setText("Ae value:ROI");
                                    break;
                                case ICameraBase.AE_MODE_SPOT:
                                    mTvShowAe.setText("Ae value:SPOT");
                                    break;
                                default:
                                    mTvShowAe.setText("Ae value:" + ae);

                            }
                            LogUtil.d("CameraFragment#getAe#Ae:" + ae);
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_setRoi:
                    String left = mEtRoiLeft.getText().toString();
                    String top = mEtRoiTop.getText().toString();
                    String right = mEtRoiRight.getText().toString();
                    String bottom = mEtRoiBottom.getText().toString();
                    if (left == null) {
                        ToastUtils.showShort(getActivity(), "Left value is null");
                        return;
                    }
                    if (left.equals("")) {
                        ToastUtils.showShort(getActivity(), "Left value is null");
                        return;
                    }
                    if (top == null) {
                        ToastUtils.showShort(getActivity(), "Top value is null");
                        return;
                    }
                    if (top.equals("")) {
                        ToastUtils.showShort(getActivity(), "Top value is null");
                        return;
                    }
                    if (right == null) {
                        ToastUtils.showShort(getActivity(), "Right value is null");
                        return;
                    }
                    if (right.equals("")) {
                        ToastUtils.showShort(getActivity(), "Right value is null");
                        return;
                    }
                    if (bottom == null) {
                        ToastUtils.showShort(getActivity(), "Bottom value is null");
                        return;
                    }
                    if (bottom.equals("")) {
                        ToastUtils.showShort(getActivity(), "Bottom value is null");
                        return;
                    }
                    if (mICameraDevice != null) {
                        try {
                            CameraRoi cameraRoi = new CameraRoi(Integer.valueOf(left)
                                    , Integer.valueOf(top), Integer.valueOf(right), Integer.valueOf(bottom));
                            mICameraDevice.setRoi(cameraRoi);
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.btn_getRoi:
                    if (mICameraDevice != null) {
                        try {
                            CameraRoi cameraRoi = mICameraDevice.getRoi();
                            mTvShowRoi.setText("roi value:" + cameraRoi.toString());
                            LogUtil.d("CameraFragment#getRoi#roi:" + cameraRoi.toString());
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case R.id.id_render_recod_btn:
                    if (mICameraDevice != null) {
                        if (!isRenderRecord) {
                            try {
                                mICameraDevice.startRenderCameraStream(mOverlayView,
                                        mIRenderFrameCallback, YuvFormat.FOURCC_NV21);
                            } catch (CameraException e) {
                                e.printStackTrace();
                            }
                            mMp4v2Helper = new MP4RecordClient();
                            mRecordParameter = getRecordParameter();
                            StreamParam param = new StreamParam();
                            param.mEnCodeType = StreamParam.EnCodeType.NV21;
                            param.videoBitRate = mRecordParameter.getVideoBitRate();
                            param.mAudioChannel = mRecordParameter.getAudioChannelCount();
                            param.samplingRate = mRecordParameter.getAudioSampleRate();
                            param.width = mWidth;
                            param.height = mHeight;
                            param.frameRate = mFps;
                            mMp4v2Helper.prepare(param);

                            String path = getCaptureFile(".mp4").toString();
                            if (path != null && !path.equals("")) {
                                try {
                                    mMp4v2Helper.start(path, new RecordController.Listener() {
                                        @Override
                                        public void onStatusChange(RecordController.Status status) {
                                            LogUtil.i(TAG, "Record tpye:" + status.name());
                                            if (status == RecordController.Status.RECORDING) {

                                            } else if (status == RecordController.Status.PAUSED) {

                                            } else if (status == RecordController.Status.RESUMED) {

                                            } else if (status == RecordController.Status.STARTED) {

                                            } else if (status == RecordController.Status.STOPPED) {

                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mMp4v2Helper.setAudioCallback(new MP4RecordClient.AudioCallback() {
                                    @Override
                                    public void init(int bufferSize) {

                                    }

                                    @Override
                                    public void onData(byte[] data, int length) {

                                    }
                                });
                            } else {
                                LogUtil.e(TAG, "storage path is null.");
                            }
                            isRenderRecord = true;
                            mRenderRecordBtn.setText("stop render record");
                            btnRecord.setEnabled(false);
                            mReStartRecordBtn.setEnabled(false);
                        } else {
                            if (mMp4v2Helper != null) {
                                mMp4v2Helper.stop();
                                mMp4v2Helper = null;
                            }
                            try {
                                mICameraDevice.stopRenderCameraStream();
                            } catch (CameraException e) {
                                e.printStackTrace();
                            }
                            isRenderRecord = false;
                            mRenderRecordBtn.setText("start render record");
                            btnRecord.setEnabled(true);
                            mReStartRecordBtn.setEnabled(true);
                        }
//                        try {
//                            if (mRenderMuxer == null) {
//                                mRenderMuxer = new MediaMuxerWrapper(".mp4");
//                                new MediaVideoBufferEncoder(mRenderMuxer, mWidth, mHeight, mFps, 0,
//                                       MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
//                                       , mMediaEncoderListener);
//                                // for mAudio capturing
//                                new MediaAudioEncoder(mRenderMuxer, mMediaEncoderListener);
//                                mRenderMuxer.prepare();
//                                mRenderMuxer.startRecording();
//                            } else {
//                                mRenderMuxer.stopRecording();
//                                mRenderMuxer = null;
//                            }
//                        } catch (final IOException e) {
//                            LogUtil.e(TAG, "startCapture:", e);
//                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private volatile boolean isRenderRecord = false;
    private MediaVideoBufferEncoder mVideoEncoder = null;
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            LogUtil.i(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoBufferEncoder) {
                mVideoEncoder = (MediaVideoBufferEncoder) encoder;
            }
            isRenderRecord = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRenderRecordBtn.setText("stop render record");
                }
            }, 0);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            LogUtil.i(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoBufferEncoder) {
                mVideoEncoder = null;
            }
            isRenderRecord = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRenderRecordBtn.setText("start render record");
                }
            }, 0);
        }
    };

    private void startRecording() throws CameraException {
        if (mICameraDevice != null) {
            mRecordStatusListener = new MyRecordStatusListener(mCameraLcdView);
            mRecordAudioListener = new MyRecordAudioListener();
            mICameraDevice.startRecording(mRecordStatusListener, mRecordAudioListener);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setText("CLOSERECORD");
                }
            }, 0);
        }

    }

    private void stopRecording() {
        try {
            if (mICameraDevice != null && mICameraDevice.isRecording()) {
                mICameraDevice.stopRecording();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRecord.setText("STARTRECORD");
                        mReStartRecordBtn.setText("PAUSERECORDING");
                    }
                }, 0);

            }
        } catch (CameraException e) {
            LogUtil.e(TAG, "stopRecording", e);
        }
    }

    private SurfaceCallback mSurfaceCallback = new SurfaceCallback() {
        @Override
        public void onSurfaceCreated(Surface surface) {
            LogUtil.i(TAG, "onSurfaceCreated");
            if (mICameraDevice != null) {
                try {
                    if (mCameraView.getSurface() != null) {
                        mICameraDevice.addSurface(mCameraView.getSurface(), false);
                    }
                } catch (CameraException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onSurfaceChanged(Surface surface, int width, int height) {
            LogUtil.i(TAG, "onSurfaceChanged");

        }

        @Override
        public void onSurfaceDestroy(Surface surface) {
            LogUtil.i(TAG, "onSurfaceDestroy");
            if (!mHadDestroy && mICameraDevice != null) {
                try {
                    mICameraDevice.removeSurface(surface);
                } catch (CameraException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onSurfaceUpdate(Surface surface) {
//            LogUtil.i(TAG, "onSurfaceUpdate");
        }
    };

    /**
     * 录像回调
     */
    private MyRecordStatusListener mRecordStatusListener;

    private class MyRecordStatusListener implements RecordStatusListener {
        private CameraLcdView cameraLcdView;

        public MyRecordStatusListener(CameraLcdView cameraLcdView) {
            this.cameraLcdView = cameraLcdView;
        }

        @Override
        public void onCameraRecordPrepared() {
            cameraLcdView.onCameraRecordPrepared();
        }

        @Override
        public void onCameraRecordStoped() {
            cameraLcdView.onCameraRecordStoped();
        }

        @Override
        public void onCameraRecordError(int code) {
            cameraLcdView.onCameraRecordError(code);
            btnRecord.setText("STARTRECORD");
            mReStartRecordBtn.setText("PAUSERECORDING");
            ToastUtils.showShort(getActivity(), "录像出错");
        }

        @Override
        public void onCameraRecordPaused() {
            cameraLcdView.onCameraRecordPaused();
        }

        @Override
        public void onCameraRecordResume() {
            cameraLcdView.onCameraRecordResume();
        }

    }

    private MyRecordAudioListener mRecordAudioListener;

    private class MyRecordAudioListener implements RecordAudioListener {

        @Override
        public void onAudioDataAvailable(byte[] audioData) {
            LogUtil.i("onAudioDataAvailable#audioData:" + audioData);
        }
    }

    CameraStatusListener mCameraStatusListener = new CameraStatusListener() {
        @Override
        public void onCameraOpened() {
            LogUtil.i(TAG, "CameraStatusListener#onCameraOpened");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCameraConnected() {
            try {
                openDisplay();
            } catch (BaseException e) {
                e.printStackTrace();
            }
            LogUtil.i(TAG, "CameraStatusListener#onCameraConnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bntCamera.setText("CLOSE");
                }
            }, 0);


            if (mICameraDevice != null) {
                try {
                    if (mCameraView.getSurface() != null) {
                        mICameraDevice.addSurface(mCameraView.getSurface(), false);
                        mIFrameCallback = new FrameCallback(mHandler, responseResult);
                        mICameraDevice.setFrameCallback(mIFrameCallback, PixelFormat
                                .PIXEL_FORMAT_NV21);

//                        mICameraDevice.startRenderCameraStream(mOverlayView,
//                                mIRenderFrameCallback, YuvFormat.FOURCC_NV12);
                    }
                } catch (CameraException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCameraDisconnected() {
            LogUtil.i(TAG, "CameraStatusListener#onCameraDisconnected");
            //close render record
            if (mMp4v2Helper != null) {
                mMp4v2Helper.stop();
                mMp4v2Helper = null;
            }
            //停止合成渲染
            try {
                if (isRenderRecord && mICameraDevice != null) {
                    mICameraDevice.stopRenderCameraStream();
                }
            } catch (CameraException e) {
                e.printStackTrace();
            }
            isRenderRecord = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bntCamera.setText("OPEN");
                    btnRecord.setText("STARTRECORD");
                    mReStartRecordBtn.setText("PAUSERECORDING");
                    mCameraLcdView.onCameraRecordStoped();
                    //close render record
                    mRenderRecordBtn.setText("start render record");
                    btnRecord.setEnabled(true);
                    mReStartRecordBtn.setEnabled(true);
                }
            }, 0);

//            if (mICameraDevice != null) {
//                try {
//                    if (mCameraView.getSurface() != null) {
//                        mICameraDevice.removeSurface(mCameraView.getSurface());
//                    }
//                } catch (CameraException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        @Override
        public void onCameraClosed() {
            LogUtil.i(TAG, "CameraStatusListener#onCameraClosed");
        }

        @Override
        public void onError(int code) {
            LogUtil.i(TAG, "CameraStatusListener#onError");
        }
    };

    private boolean isCloseDisplay = false;

    private void closeGlassDisplay() {
        isCloseDisplay = true;
        LogUtil.i(TAG, "close Glass Display");
        if (mGlassDisplay != null) {
            mGlassDisplay.stopCaptureScreen();
            mGlassDisplay = null;
        }
    }

    private FpsCounter mRenderFpsCounter = new FpsCounter();
    private long mRenderCallbackLastTime;
    private final IRenderFrameCallback mIRenderFrameCallback = new IRenderFrameCallback() {
        @Override
        public void onFrameAvailable(byte[] frame, int width, int height) {
//            mRenderFpsCounter.count();
//            long curTime = System.currentTimeMillis();
//            if ((curTime - mRenderCallbackLastTime) > SECOND_ONE) {
//                mRenderFpsCounter.update();
//                LogUtil.i(TAG, "camera render fps : " + mRenderFpsCounter.getFps());
//                mHandler.sendEmptyMessage(MSG_RENDER_VIEW);
//                mRenderCallbackLastTime = System.currentTimeMillis();
//            }
//
//            if (isRenderRecord && mVideoEncoder != null) {
//                mVideoEncoder.encode(frame);
//                mVideoEncoder.frameAvailableSoon();
//            }
            mMp4v2Helper.addFrame(new Frame(frame, 0, false, ImageFormat.NV21));
        }
    };

    private IFrameCallback mIFrameCallback;

    private static class FrameCallback implements IFrameCallback {
        private FpsCounter mFpsCounter;
        private Handler mHandler;
        private long mLastTime;
        private TextView responseResult;

        private FrameCallback(Handler handler, TextView responseResult) {
            mFpsCounter = new FpsCounter();
            this.mHandler = handler;
            this.responseResult = responseResult;
        }

        @Override
        public void onFrameAvailable(byte[] frame) {
            //TODO
            // 此处可对每帧画面进行处理
            // 根据实际需求设置fps或采集率
            // 如下注释是在线调用腾讯AI的图像分析的接口示例
            // 但显然网络延迟会给应用带来不好的体验

//            // 实例化一个认证对象，入参需要传入腾讯云账户 secretId、secretKey
//            Credential cred = new Credential("AKIDVx8TamiX0FG0f6rtzxqa6cwneJ4cuKp7", "KN9Ay2ziFXlcGqFFclkSv92i8qMejiUh");
//            // 实例化一个 http 选项
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("tiia.tencentcloudapi.com");
//            // 实例化一个 client 选项
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            // 实例化要请求产品的 client 对象
//            TiiaClient client = new TiiaClient(cred, "ap-beijing", clientProfile);
//
//            String base64 = null;
//            base64 = Base64.encodeToString(frame, Base64.DEFAULT);
//            String params = "{\"ImageBase64\":\"" + base64 + "\"}";
//            DetectLabelRequest req = DetectLabelRequest.fromJsonString(params, DetectLabelRequest.class);
//
//            DetectLabelResponse resp = null;
//            try {
//                resp = client.DetectLabel(req);
//            } catch (TencentCloudSDKException e) {
//                e.printStackTrace();
//            }
////            System.out.println(DetectLabelRequest.toJsonString(resp));
//            responseResult.setText(DetectLabelRequest.toJsonString(resp));
//            responseResult.setText(base64);
//            mFpsCounter.count();
//            long curTime = System.currentTimeMillis();
//            if ((curTime - mLastTime) > SECOND_ONE) {
//                mFpsCounter.update();
////                LogUtil.i(TAG, "camera frame fps : " + mFpsCounter.getFps());
//                mHandler.obtainMessage(MSG_UPDATE_FPS, mFpsCounter.getFps()).sendToTarget();
//                mLastTime = System.currentTimeMillis();
//            }
        }
    }

    private CameraLcdView mCameraLcdView;


    private void openDisplay() throws BaseException {
        if (mLcdClient == null) {
            mLcdClient = (ILCDClient) LLVisionGlass3SDK.getInstance().getGlass3Client
                    (IGlass3Device.Glass3DeviceClient.LCD);
        }
        if (mGlassDisplay == null) {
            mGlassDisplay = mLcdClient.getGlassDisplay(mGlass3Device);
        }
        if (mCameraLcdView != null) {
            mCameraLcdView.setGlassFovImageResource(mFovImageList.get(mCurrentFov));

            /*
            mGlassRecordTimeTv.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    mTimeMiss++;
                    chronometer.setText(FormatMiss(mTimeMiss));
                    if (mRecordingTime >= 10) {
                        try {
                            if (mICameraDevice.isRecording()) {
                                mICameraDevice.stopRecording();
                                mRecordingTime = 0;
                                mTimeMiss = 0;
                                mHandler.sendEmptyMessageDelayed(MSG_RESTART_RECORDING, 500);
                            }
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            */
            mCameraLcdView.setResolutionText(mWidth + "x" + mHeight + " " + mFps + "fps");
        }

        mGlassDisplay.createCaptureScreen(getActivity(), mCameraLcdView.getView());
        isCloseDisplay = false;
    }

    private ConnectionStatusListener mConnectionStatusListener = new ConnectionStatusListener() {
        @Override
        public void onServiceConnected(List<IGlass3Device> glass3Devices) {
            ToastUtils.showLong(getActivity(), "onServiceConnected");
            LogUtil.i(TAG, "onServiceConnected");

        }

        @Override
        public void onServiceDisconnected() {
            ToastUtils.showLong(getActivity(), "onServiceDisconnected");
            LogUtil.i(TAG, "onServiceDisconnected");

        }

        @Override
        public void onDeviceConnect(IGlass3Device device) {
            ToastUtils.showLong(getActivity(), "onDeviceConnect");
            LogUtil.i(TAG + "-CameraDevice", "onDeviceConnect(IGlass3Device device)");
            //close old camera
            if (mGlassDisplay != null) {
                mGlassDisplay.stopCaptureScreen();
                mGlassDisplay = null;
            }
            //open new camera
            mGlass3Device = device;
            if (device != null && mCameraClient != null) {
                try {
                    mICameraDevice = mCameraClient.openCamera(device, mCameraStatusListener);
                    mICameraDevice.setPreviewSize(mWidth, mHeight, mFps);
                    mCameraView.setAspectRatio(mWidth / (float) mHeight);
                    mICameraDevice.connect();
                } catch (CameraException e) {
                    LogUtil.e(TAG, e);
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onDeviceDisconnect(IGlass3Device device) {
            LogUtil.i(TAG + "-CameraDevice", "onDeviceDisconnect(IGlass3Device device)");
            if (device != null && mGlass3Device != null) {
                ToastUtils.showLong(getActivity(), "onDeviceDisconnect deviceId = " +
                        device.getDeviceId() + " mGlass3Device = " + mGlass3Device.getDeviceId());
                LogUtil.i("CameraClient", "onDeviceDisconnect deviceId = " +
                        device.getDeviceId() + " mGlass3Device = " + mGlass3Device.getDeviceId());
            }
            if (mGlassDisplay != null) {
                mGlassDisplay.stopCaptureScreen();
                mGlassDisplay = null;
            }
            if (mCameraLcdView != null) {
                mCameraLcdView.onCameraRecordStoped();
            }
            mICameraDevice = null;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mHadDestroy) {
                        btnRecord.setText("STARTRECORD");
                        mReStartRecordBtn.setText("PAUSERECORDING");
                    }
                }
            }, 500);

            mGlass3Device = null;
        }

        @Override
        public void onError(int code, String msg) {
            ToastUtils.showLong(getActivity(), "onError");
            LogUtil.i(TAG, "onError");
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup
            .OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (mICameraDevice == null) {
                ToastUtils.showLong(getActivity(), "设备未连接");
                return;
            }
            try {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.ec_level_n30:
                        mICameraDevice.setEc(ICameraBase.EC_N30);
                        break;
                    case R.id.ec_level_n20:
                        mICameraDevice.setEc(ICameraBase.EC_N20);
                        break;
                    case R.id.ec_level_n10:
                        mICameraDevice.setEc(ICameraBase.EC_N10);
                        break;
                    case R.id.ec_level_0:
                        mICameraDevice.setEc(ICameraBase.EC_0);
                        break;
                    case R.id.ec_level_10:
                        mICameraDevice.setEc(ICameraBase.EC_10);
                        break;
                    case R.id.ec_level_20:
                        mICameraDevice.setEc(ICameraBase.EC_20);
                        break;
                    case R.id.ec_level_30:
                        mICameraDevice.setEc(ICameraBase.EC_30);
                        break;
                    case R.id.ant_value_off:
                        mICameraDevice.setAntibanding(ICameraBase.ANTIBANDING_OFF);
                        break;
                    case R.id.ant_value_50hz:
                        mICameraDevice.setAntibanding(ICameraBase.ANTIBANDING_50HZ);
                        break;
                    case R.id.ant_value_60hz:
                        mICameraDevice.setAntibanding(ICameraBase.ANTIBANDING_60HZ);
                        break;
                    case R.id.ae_mode_center:
                        mICameraDevice.setAe(ICameraBase.AE_MODE_CENTER);
                        break;
                    case R.id.ae_mode_roi:
                        mICameraDevice.setAe(ICameraBase.AE_MODE_ROI);
                        break;
                    case R.id.ae_mode_spot:
                        mICameraDevice.setAe(ICameraBase.AE_MODE_SPOT);
                        break;
                    case R.id.ae_mode_all:
                        mICameraDevice.setAe(ICameraBase.AE_MODE_ALL);
                        break;
                }
            } catch (CameraException e) {
                LogUtil.e(e.getMessage());
            }

        }
    };


    /**
     * generate output file
     *
     * @param ext .mp4(.m4a for audio) or .png
     * @return return null when this app has no writing permission to external storage.
     */
    public static final File getCaptureFile(final String ext) {
        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(), DIR_NAME);
//        LogUtil.e(TAG, "path=" + dir.toString());
        dir.mkdirs();
        if (dir.canWrite()) {
            return new File(dir, getDateTimeString() + ext);
        }
        return null;
    }

    /**
     * get current date and time as String
     *
     * @return
     */
    public static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        return mDateTimeFormat.format(now.getTime());
    }

    private RecordParameter getRecordParameter() {
        if (mRecordParameter == null) {
            mRecordParameter = new RecordParameter.RecordParameterBuilder(
                    (int) (VIDEO_BPP * mWidth * mHeight * mFps),
                    AUDIO_CHANNEL_COUNT,
                    AUDIO_SAMPLING_RATE).build();
        }
        return mRecordParameter;
    }
}
