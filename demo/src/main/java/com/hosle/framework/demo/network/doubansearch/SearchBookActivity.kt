package com.hosle.framework.demo.network.doubansearch

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.hosle.framework.demo.network.R
import com.hosle.framework.demo.network.data.doubansearch.SearchBookTask
import com.hosle.framework.demo.network.model.BooksItem
import com.hosle.framework.demo.network.model.SearchBookModel
import kotlinx.android.synthetic.main.activity_search_book.*

/**
 * Created by tanjiahao on 2018/6/21
 * Original Project HomanNetworkDemo
 */
class SearchBookActivity : Activity() {

    val data = ArrayList<BooksItem>()
    val searchBookTask = SearchBookTask()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_book)

        recycler_view_search_book.apply {
            layoutManager = LinearLayoutManager(this@SearchBookActivity, LinearLayoutManager.VERTICAL, false)
            adapter = SearchBookAdapter(data)
        }

        requestData()
    }

    private fun onReqSuccess(result: SearchBookModel) {
        data.clear()
        data.addAll(result.books ?: emptyList())

        recycler_view_search_book.adapter.notifyDataSetChanged()
    }

    private fun requestData() {
        searchBookTask.doGetData(onSuccessCallback = { model -> onReqSuccess(model) }, onFailureCallback = { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() })
    }


}