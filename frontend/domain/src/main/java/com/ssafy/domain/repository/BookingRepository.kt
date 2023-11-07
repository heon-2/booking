package com.ssafy.domain.repository

import com.ssafy.domain.model.ChatCreateRequest
import com.ssafy.domain.model.booking.AllBooking
import retrofit2.Response
import com.ssafy.domain.model.booking.BookingCreateRequest
import com.ssafy.domain.model.booking.EachBooking

interface BookingRepository {
    // 북킹 생성
    suspend fun postBookingCreate(request : BookingCreateRequest) : Response<Unit>
    // 단일 북킹 조회
    suspend fun getBookingDetail() : Response<EachBooking>
    // 전체 북킹 조회
    suspend fun getBookingList() : Response<AllBooking>


}

