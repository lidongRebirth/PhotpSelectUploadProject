package com.myfittinglife.photpselectuploadproject

import android.content.Context
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils
import java.util.*

/**
 * @Author          LD
 * @Time            2020/11/8 16:50
 * @Describe        图片筛选过滤器,删选最小宽度、最小高度、最大大小
 * @Modify
 */
class GifSizeFilter
/**
 *
 * @param minWidth          最小宽度
 * @param minHeight         最小高度
 * @param maxSizeInBytes    最大的bytes
 */(//x or y bound size should be at least %1$dpx and file size should be no more than %2$sM
    private val mMinWidth: Int, private val mMinHeight: Int, private val mMaxSize: Int
) :
    Filter() {
    private val error_gif =
        "x or y bound size should be at least %1\$dpx and file size should be no more than %2\$sM"

    override fun constraintTypes(): HashSet<MimeType?> {
        return object : HashSet<MimeType?>() {
            init {
                add(MimeType.GIF)
            }
        }
    }

    override fun filter(
        context: Context,
        item: Item
    ): IncapableCause? {
        if (!needFiltering(context, item)) {
            return null
        }
        val size =
            PhotoMetadataUtils.getBitmapBound(context.contentResolver, item.contentUri)
        return if (size.x < mMinWidth || size.y < mMinHeight || item.size > mMaxSize) {
            IncapableCause(
                IncapableCause.DIALOG,
                context.getString(
                    R.string.error_gif,
                    mMinWidth,
                    PhotoMetadataUtils.getSizeInMB(mMaxSize.toLong()).toString()
                )
            )
        } else null
    }

}