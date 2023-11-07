package com.ssafy.domain.model.booking

import com.google.gson.annotations.SerializedName

// 모임 개별 조회
data class EachBooking (
    @SerializedName("bookIsbn")
    val bookIsbn : String,
    @SerializedName("meetingTitle")
    val meetingTitle: String,
    @SerializedName("description")
    val description : String,
    @SerializedName("maxParticipants")
    val maxParticipants : Number,
    @SerializedName("hashtagList")
    val hashtagList : List<String>,
)

// 모임 전체 조회
data class AllBooking (
    @SerializedName("bookIsbn")
    val bookIsbn : String,
    @SerializedName("meetingTitle")
    val meetingTitle: String,
    @SerializedName("description")
    val description : String,
    @SerializedName("maxParticipants")
    val maxParticipants : Number,
    @SerializedName("hashtagList")
    val hashtagList : List<String>,

)