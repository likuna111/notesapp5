package com.example.todonotesapp.model

import java.lang.StringBuilder

data class JsonResponse(val status_code:String,val message:StringBuilder, var data:List<Data> )
data class Data(val title:String ,val description:String , val author:String,val img_url: String,val blog_url:String,val published_at :String )