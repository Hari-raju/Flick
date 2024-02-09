package com.raju.flick.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.raju.flick.R
import com.raju.flick.databinding.LayoutGridViewBinding
import com.raju.flick.utils.SquareImage

class GridImageAdapter(
    context: Context,
    resource:Int,
    private var urls:ArrayList<String>?
):ArrayAdapter<GridImageAdapter.ItemHolder>(context, resource){

    private val inflater:LayoutInflater = LayoutInflater.from(context)
    private lateinit var binding: LayoutGridViewBinding

    override fun getCount(): Int {
        return if (this.urls!=null){
            this.urls!!.size
        }
        else{
            return 0
        }
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        val holder:ItemHolder
        if(convertView==null){
            binding= LayoutGridViewBinding.inflate(inflater)
            convertView=binding.root
            holder=ItemHolder()
            holder.image=binding.gridImages
            convertView.tag=holder
        }
        else{
            holder=convertView.tag as ItemHolder
        }

        holder.image?.let {
            Glide.with(context)
                .load(urls?.get(position)!!).placeholder(R.drawable.place_holderimage)
                .error(R.drawable.error_image)
                .into(
                it
            )
        }
        return convertView
    }

    inner class ItemHolder{
        var image:SquareImage?=null
    }
}