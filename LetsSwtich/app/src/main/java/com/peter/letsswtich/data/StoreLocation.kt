package com.peter.letsswtich.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreLocation(
    val store: Store = Store("",""),
    val branchName: String ="",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "123"
): Parcelable

@Parcelize
data class Store(
    val storeId: String = "",
    val storeName: String ="",
    val uri: String = ""
): Parcelable