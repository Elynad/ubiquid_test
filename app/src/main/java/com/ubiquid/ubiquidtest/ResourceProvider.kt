package com.ubiquid.ubiquidtest

import android.content.Context

/**
 *  This class is used to get strings from a place without context (i.e. like a ViewModel).
 *  An instance is created with a [Context], then we use its function [getString] when needed.
 */
class ResourceProvider {

    private var mContext: Context? = null

    fun resourceProvider(mContext: Context?) : ResourceProvider {
        this.mContext = mContext
        return this
    }

    fun getString(resId: Int): String? {
        return mContext!!.getString(resId)
    }

    fun getString(resId: Int, value: String?): String? {
        return mContext!!.getString(resId, value)
    }
}