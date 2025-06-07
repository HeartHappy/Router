package com.hearthappy.router.core

import android.os.Bundle
import java.io.Serializable

class BundleWrapper {
    private val bundle = Bundle()

    fun putString(key: String, value: String): BundleWrapper {
        bundle.putString(key, value)
        return this
    }

    fun putInt(key: String, value: Int): BundleWrapper {
        bundle.putInt(key, value)
        return this
    }

    fun putBoolean(key: String, value: Boolean): BundleWrapper {
        bundle.putBoolean(key, value)
        return this
    }

    fun putLong(key: String, value: Long): BundleWrapper {
        bundle.putLong(key, value)
        return this
    }

    fun putFloat(key: String, value: Float): BundleWrapper {
        bundle.putFloat(key, value)
        return this
    }

    fun putDouble(key: String, value: Double): BundleWrapper {
        bundle.putDouble(key, value)
        return this
    }

    fun putSerializable(key: String, value: Serializable): BundleWrapper {
        bundle.putSerializable(key, value)
        return this
    }

    fun toBundle(): Bundle = bundle
}