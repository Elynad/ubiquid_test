package com.ubiquid.ubiquidtest

import android.content.Context
import android.util.Log

/**
 *  This class is used to get strings from a place without context (i.e. like a ViewModel).
 *  An instance is created with a [Context], then we use its function [getString] when needed.
 */
class ResourceProvider {

    var mContext: Context? = null

    fun resourceProvider(mContext: Context?) : ResourceProvider {
        this.mContext = mContext
        return this
    }

    fun getString(resId: Int): String? {
        return mContext!!.getString(resId)
    }

    fun getString(resId: Int, vararg value: Any): String? {
        value.forEach {
            Log.d("ResourceProvider", "ITEM ${it}")
        }
        return mContext!!.getString(resId, value)
    }
}