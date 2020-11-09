package com.myfittinglife.photpselectuploadproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


/**
 * @Author LD
 * @Time 2020/11/3 9:54
 * @Describe 普通Adapter实现多布局
 * @odify
 */
class MyCommonAdapter(private val data: MutableList<String>,private val maxNum:Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ADD_ITEM = 1
    val PIC_ITEM = 2
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ADD_ITEM -> {
                var view =
                    LayoutInflater.from(parent.context).inflate(R.layout.add_item, parent, false)
                return AddViewHolder(view)
            }
            else -> {
                var view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
                return PicViewHolder(view)
            }
        }
    }

    /**
     * 当data.size()达到最大值时，返回data.size()(不给加号布局提供位置)
     * 否则，返回的数量+1，为了给加号布局添加位置
     */
    override fun getItemCount(): Int {
        return if(data.size<maxNum){
            data.size+1
        }else{
            data.size
        }
    }

    //设置控件数据
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //加号的布局
        if (holder is AddViewHolder) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemAddClick(position)
            }

        }
        //加载图片的布局
        else {
            Glide.with(holder.itemView.context).load(data[position])
                .into((holder as PicViewHolder).pic)
            holder.pic.setOnClickListener {
                onItemClickListener?.onItemPicClick(position)
            }
            holder.del.setOnClickListener {
                onItemClickListener?.onItemDelClick(position)
            }
        }
    }

    /**
     * 当data.size()达到最大值时，返回图片布局，
     * 否则按照逻辑：如果position + 1 == itemCount返回添加布局，不等于返回图片布局
     * (因为如果当前位置+1=itemCount，则代表它是最后一个，因为位置是从0计数的，而itemCount是从1计数)
     */
    override fun getItemViewType(position: Int): Int {

        if(data.size==maxNum){
            return PIC_ITEM
        }else{
            return if (position + 1 == itemCount) {
                ADD_ITEM
            } else {
                PIC_ITEM
            }
        }

    }

    //加号布局
    inner class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    //普通布局
    inner class PicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.ivImage)
        val del: ImageView = itemView.findViewById(R.id.ivDelete)
    }


    //设置接口回调来实现点击功能
    fun setOnMyClickListener(onClickListener: OnItemClickListener?) {
        onItemClickListener = onClickListener
    }

    interface OnItemClickListener {
        //点击增加按键
        fun onItemAddClick(position: Int)

        //点击删除按键
        fun onItemDelClick(position: Int)

        //点击图片
        fun onItemPicClick(position: Int)
    }
}