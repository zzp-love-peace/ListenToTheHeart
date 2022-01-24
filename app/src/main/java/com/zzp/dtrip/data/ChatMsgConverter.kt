package com.zzp.dtrip.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ChatMsgConverter {

    @TypeConverter
    fun objectToString(list: List<ChatMsg>): String {
        return GsonInstance.getGson().toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String): List<ChatMsg> {
//        val listType = TypeToken<List<ChatMsg>>(){}.type
        val listType = object : TypeToken<List<ChatMsg>>() {}.type
        return GsonInstance.getGson().fromJson(json, listType)
    }


    class GsonInstance {
        companion object {
            private var instance: Gson? = null

            @Synchronized
            fun getGson(): Gson {
                instance?.let {
                    return it
                }
                return Gson().apply {
                    instance = this
                }
            }
//            val instance: GsonInstance?
//                get() {
//                    if (INSTANCE == null) {
//                        synchronized(GsonInstance::class.java) {
//                            if (INSTANCE == null) {
//                                INSTANCE = GsonInstance()
//                            }
//                        }
//                    }
//                    return INSTANCE
//                }
        }
    }


}