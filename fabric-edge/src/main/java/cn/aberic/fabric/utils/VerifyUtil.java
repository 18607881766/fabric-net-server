/*
 * Copyright (c) 2018. Aberic - aberic@qq.com - All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.aberic.fabric.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * 作者：Aberic on 2018/6/17 18:07
 * 邮箱：abericyang@gmail.com
 */
public class VerifyUtil {

    /**
     * 判断字符串类型
     *
     * @param str 字符串
     *
     * @return 0-string；1-JsonObject；2、JsonArray
     */
    public static int isJSONValid(String str) {
        try {
            JSONObject.parseObject(str);
            return 1;
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(str);
                return 2;
            } catch (JSONException ex1) {
                return 0;
            }
        }
    }

}
