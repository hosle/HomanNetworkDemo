package com.hosle.framework.demo.network.doubansearch

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hosle.framework.demo.network.R
import com.hosle.framework.demo.network.model.BooksItem

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetworkDemo
 */
class SearchBookAdapter(val data:ArrayList<BooksItem>) : RecyclerView.Adapter<SearchBookAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_search_book,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.nameView.text = data[position].title
    }


    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val nameView = view.findViewById<TextView>(R.id.tv_book_name)
    }
}