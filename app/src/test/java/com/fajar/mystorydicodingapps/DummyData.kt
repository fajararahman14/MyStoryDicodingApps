package com.fajar.mystorydicodingapps

import com.fajar.mystorydicodingapps.network.story.StoryItem

object DummyData {
    fun generateDummyStoryItem() : List<StoryItem> {
        val storyList : MutableList<StoryItem> = arrayListOf()

        for (i in 0..100){
            val story = StoryItem(
                i.toString(),
                name="Story $i",
                description = "x",
                photoUrl = "y",
                createdAt = "z",
                lat = i.toFloat(),
                lon = i.toFloat()
            )
            storyList.add(story)
        }
        return storyList
    }

    fun generateDummyToken() = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWxONWZqc2E1Q0YxQ3l5WXMiLCJpYXQiOjE2ODM0NzY3MzR9.lirYaxpLZN2S9uipEB40ivoKB2VJ2rLwGHvGVfG2dPw"
}