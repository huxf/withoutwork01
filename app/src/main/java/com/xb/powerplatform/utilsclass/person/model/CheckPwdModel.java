package com.xb.powerplatform.utilsclass.person.model;

import android.content.Context;

import com.xb.powerplatform.utilsclass.base.BaseModeBackLisenter;

/**
 * Created by dell on 2017/8/2.
 */

public interface CheckPwdModel {
    /**
     * 修改密码方法
     * @param username
     * @param password
     * @param context
     * @param baseModeBackLisenter
     */
    void getCheckPwdData(String httpTag, String phone, String username, String password, Context context, BaseModeBackLisenter baseModeBackLisenter);
}
