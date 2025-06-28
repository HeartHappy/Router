package com.hearthappy.common_api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableBean(val username:String,val password:String) : Parcelable