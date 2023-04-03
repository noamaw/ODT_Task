package com.noam.odt_task.data_layer

import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class AvatarRemoteDataSource @Inject constructor(private val avatarService: AvatarService){
    // suspend fun to retrieve an avatar from the web
    // we call the retrofit response. then send the response string to the regex to get the https link from it.
    suspend fun getAvatar(type : String): String {
        val res = avatarService.getAvatar(type)
        return extractUrls(res.raw().toString())[0]
    }

    // extract url link from the string
    private fun extractUrls(text: String): List<String> {
        val containedUrls: MutableList<String> = ArrayList()
        val urlRegex =
            "((https?):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)
        while (urlMatcher.find()) {
            containedUrls.add(
                text.substring(
                    urlMatcher.start(0),
                    urlMatcher.end(0)
                )
            )
        }
        Log.d("TAG", "extractUrls: found ${containedUrls.size} urls")
        return containedUrls
    }
}