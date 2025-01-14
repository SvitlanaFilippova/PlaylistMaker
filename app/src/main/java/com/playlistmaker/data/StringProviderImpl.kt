package com.playlistmaker.data

import android.content.Context
import com.playlistmaker.domain.StringProvider

class StringProviderImpl(private val context: Context) : StringProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}