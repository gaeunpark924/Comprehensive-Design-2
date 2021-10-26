package com.example.flatload

import java.io.Serializable

data class ItemList (val title:String, val category:String,
                     val address: String, val roadAddress:String,
                     val mapx:String,val mapy:String): Serializable {}