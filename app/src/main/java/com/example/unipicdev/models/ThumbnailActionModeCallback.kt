package com.example.unipicdev.models

import androidx.appcompat.view.ActionMode

abstract class ThumbnailActionModeCallback: android.view.ActionMode.Callback {
    var isSelectable = false
}