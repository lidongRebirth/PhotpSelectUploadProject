package com.myfittinglife.photpselectuploadproject

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.permissionx.guolindev.PermissionX
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*

/**
@Author LD
@Time 2020/11/4 10:10
@Describe 仿朋友圈照片选择
@Modify
 */
class MainActivity : AppCompatActivity(), MyCommonAdapter.OnItemClickListener {

    private val REQUEST_CODE_CHOOSE = 300 //照片选择回调
    private var mSelectedObtainPathResult = mutableListOf<String>()

    //最大能上传的照片数
    private val maxNum = 6

    //有缺陷
    //private var myCommonAdapter = MyCommonAdapter2(mSelectedObtainPathResult,maxNum)

    private var myCommonAdapter = MyCommonAdapter(mSelectedObtainPathResult, maxNum)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

        initRecyclerview()

    }

    /**
     * 首次进入申请权限
     */
    private fun checkPermission() {
        PermissionX.init(this)
            .permissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope, deniedList, beforeRequest ->
                scope.showRequestReasonDialog(deniedList, "即将重新申请的权限是必须依赖的权限,否则无法更新应用", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "确定", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "所有权限都已经通过", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Recyclerview相关配置
     */
    private fun initRecyclerview() {
        mRecyclerview.layoutManager = GridLayoutManager(this, 3)
        mRecyclerview.adapter = myCommonAdapter
        myCommonAdapter.setOnMyClickListener(this)
    }

    /**
     * 选择图片
     */
    private fun selectPhoto(num: Int) {
        Matisse.from(this) //  .choose(MimeType.ofAll(),false)               //false表示不能同时选照片和视频
            .choose(MimeType.ofImage()) //选择类,日后单独配置
            //拍照需要的两个（写完这个就会有照相那个图标了）
            .capture(true)
            .captureStrategy(
                CaptureStrategy(
                    true,
                    "com.myfittinglife.photpselectuploadproject.fileprovider",
                    "test"
                )
            ) //最后文件存储地址：Pictures/test
            .countable(true) //选择时是否计数
            .maxSelectable(num) //最大可选择数
            .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K)) //过滤器   5M大小
//                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size)) //每个图片方格的大小
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) //选择方向
            .thumbnailScale(0.85f) //刚进入图片选择页面后图片的清晰度
            .imageEngine(GlideEngine())//图片引擎

            .theme(R.style.Matisse_Dracula)                                 //主题这里使用默认的
            .originalEnable(true) //原图按钮
            .forResult(REQUEST_CODE_CHOOSE) //请求码

    }

    /**
     * 选择照片后的回调
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //获取到的地址只是相对地址，但可以给图片加载，若要上传图片，需获取真实本地地址
            mSelectedObtainPathResult.addAll(Matisse.obtainPathResult(data)) //真实地址
            Log.i("ceshi", "onActivityResult:获取到的地址为: " + mSelectedObtainPathResult[0])

            myCommonAdapter.notifyDataSetChanged()

        }
    }

    override fun onItemAddClick(position: Int) {
        selectPhoto(maxNum - mSelectedObtainPathResult.size)
    }

    override fun onItemDelClick(position: Int) {
        mSelectedObtainPathResult.removeAt(position)
        myCommonAdapter.notifyDataSetChanged()
    }

    //TODO 日后可增加图片放大缩小查看的功能
    override fun onItemPicClick(position: Int) {
        Toast.makeText(this, "点击图片", Toast.LENGTH_SHORT).show()
    }

}