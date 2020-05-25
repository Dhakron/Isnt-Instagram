package net.abrudan.isntinstagram.util

import kotlin.math.max

class SaveViewPagerPosition() {
    fun init() {
        this.list= hashMapOf()
        this.listMax= hashMapOf()
    }
    private var list= hashMapOf<String,Int>()
    private var listMax= hashMapOf<String,Int>()

    fun getLastPosition(key:String):Int{
        return list[key]?:0
    }

    fun setLastPosition(key: String,position: Int){
        list[key] = position
    }

    fun addNewItem(key: String,max:Int){
        list[key] = 0
        listMax[key] = max
    }

    fun containItem(key:String):Boolean{
        return list.containsKey(key)&& listMax.containsKey(key)
    }

    fun getMaxPosition(key:String):Int{
        return listMax[key]!!
    }

    fun setMaxPosition(key: String,position: Int){
        listMax[key] = position
    }
}