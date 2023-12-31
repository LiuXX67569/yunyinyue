package com.example.neteasecloudmusic.ui.model

object GlobalUserInfo {
    var pk_id: Int = 0
    var username: String? = null
    var email: String = ""
    var portrait: String? = null
    var level: Int = 0
    var introduction: String? = null
    var num_follow: Int = 0
    var follow_list: String? = null
    var followList: ArrayList<Int> = ArrayList()
    var num_fans: Int = 0
    var menu_list: String? = null
    var menuList: ArrayList<Int> = ArrayList()
    var friends: String? = null

    fun setGlobalUserInfo(userInfoBean: UserInfoBean) {
        pk_id = userInfoBean.pk_id
        username = userInfoBean.username
        email = userInfoBean.email
        portrait = userInfoBean.portrait
        level = userInfoBean.level
        introduction = userInfoBean.introduction
        num_follow = userInfoBean.num_follow
        follow_list = userInfoBean.follow_list
        followList = parseStrToListInt(follow_list)
        num_fans = userInfoBean.num_fans
        menu_list = userInfoBean.menu_list
        menuList = parseStrToListInt(menu_list)
        friends = userInfoBean.friends
    }

    private fun parseStrToListInt(Str: String?): ArrayList<Int> {
        val arrayList: ArrayList<Int> = ArrayList()
        Str?.split(",")?.forEach {
            if (it != "") {
                arrayList.add(it.toInt())
            }
        }
        return arrayList
    }

    fun parseListIntToStr(arrayList: ArrayList<Int>): String {
        var str = ""
        if (arrayList.isNotEmpty()) {
            for (i in 0 until arrayList.size) {
                str += if (i == 0) {
                    "${arrayList[i]}"
                } else {
                    ",${arrayList[i]}"
                }
            }
        }
        return str
    }

    fun getUserInfo(): UserInfoBean {
        val userInfoBean = UserInfoBean()
        GlobalUserInfo.apply {
            userInfoBean.pk_id = pk_id
            userInfoBean.username = when(username) {
                null -> ""
                else -> username
            }
            userInfoBean.email = email
//            userInfoBean.email = when(email) {
//                null -> ""
//                else -> email.toString()
//            }
            userInfoBean.portrait = when(portrait) {
                null -> ""
                else -> portrait
            }
            userInfoBean.level = level
            userInfoBean.introduction = when(introduction) {
                null -> ""
                else -> introduction
            }
            userInfoBean.num_follow = num_follow
            userInfoBean.follow_list = parseListIntToStr(followList)
            userInfoBean.num_fans = num_fans
            userInfoBean.menu_list = parseListIntToStr(menuList)
            userInfoBean.friends = when(friends) {
                null -> ""
                else -> menu_list
            }
        }
        return userInfoBean
    }

    override fun toString(): String {
        return UserInfoBean(pk_id, username, email, portrait, level, introduction, num_follow, follow_list, num_fans, menu_list, friends).toString()
    }
}