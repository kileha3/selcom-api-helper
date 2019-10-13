package com.furahitechpay.furahitechpay.util

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

open class BaseFragment: BottomSheetDialogFragment() {

    private val runOnAttach = Vector<Runnable>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isCancelable = false
        val runnableList = runOnAttach.iterator()
        while (runnableList.hasNext()) {
            val current = runnableList.next()
            current.run()
            runnableList.remove()
        }
    }


    fun runOnUiThread(runnable: Runnable) {
        if (activity != null) {
            activity!!.runOnUiThread(runnable)
        } else {
            runOnAttach.add(runnable)
        }
    }
}