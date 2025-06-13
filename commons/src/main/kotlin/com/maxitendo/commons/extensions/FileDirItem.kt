package com.maxitendo.commons.extensions

import android.content.Context
import com.maxitendo.commons.models.FileDirItem

fun FileDirItem.isRecycleBinPath(context: Context): Boolean {
    return path.startsWith(context.recycleBinPath)
}
