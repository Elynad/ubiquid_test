package com.ubiquid.ubiquidtest

import android.content.Context

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