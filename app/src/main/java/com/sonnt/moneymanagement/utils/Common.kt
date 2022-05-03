package com.sonnt.moneymanagement.utils

import com.sonnt.moneymanagement.MMApplication
import com.sonnt.moneymanagement.features.base.BaseActivity

object Common {

    fun logout() {
        if (MMApplication.currentActivity is BaseActivity) {
            //hideLoadingDialog()
        }
        //Func1Repository.newInstance().saveUserInfo(null)
        //val intent = Intent(self, LoginActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        //BitelApplication.self().startActivity(intent)
    }

}