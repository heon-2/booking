package com.ssafy.data.repository
import com.ssafy.data.remote.api.BookingApi
import com.ssafy.domain.model.booking.BookingAcceptRequest
import com.ssafy.domain.model.booking.BookingAll
import javax.inject.Inject
import com.ssafy.domain.model.booking.BookingCreateRequest
import com.ssafy.domain.model.booking.BookingDetail
import com.ssafy.domain.model.booking.BookingJoinRequest
import com.ssafy.domain.model.booking.BookingModifyRequest
import com.ssafy.domain.model.booking.BookingParticipants
import com.ssafy.domain.model.booking.BookingRejectRequest
import com.ssafy.domain.model.booking.BookingStartRequest
import com.ssafy.domain.model.booking.BookingWaiting
import com.ssafy.domain.model.booking.SearchResponse
import com.ssafy.domain.repository.BookingRepository
import retrofit2.Response

class BookingRepositoryImpl @Inject constructor(
    private val bookingApi:BookingApi
) : BookingRepository {
    override suspend fun postBookingCreate(request: BookingCreateRequest): Response<Unit> {
        return bookingApi.postBookingCreate(request)
    }
    override suspend fun getAllBooking(): Response<List<BookingAll>> {
        return bookingApi.getAllBooking()
    }
    override suspend fun getEachBooking(meetingId: Long): Response<BookingDetail> {
        return bookingApi.getEachBooking(meetingId)
    }
    override suspend fun getParticipants(meetingId: Long): Response<List<BookingParticipants>> {
        return bookingApi.getParticipants(meetingId)
    }
    override suspend fun getWaitingList(meetingId: Long): Response<List<BookingWaiting>> {
        return bookingApi.getWaitingList(meetingId)
    }

    override suspend fun getSearchList(query:String,display:Int,start:Int,sort:String): Response<SearchResponse> {
        return bookingApi.getSearchList(query,display,start,sort)
    }

    override suspend fun postBookingJoin(meetingId: Long,request: BookingJoinRequest): Response<Unit> {
        return bookingApi.postBookingJoin(meetingId,request)
    }

    override suspend fun postBookingAccept(meetingId: Long,memberId:Int,request: BookingAcceptRequest): Response<Unit> {
        return bookingApi.postBookingAccept(meetingId,memberId,request)
    }

    override suspend fun postBookingReject(meetingId: Long,memberId:Int,request: BookingRejectRequest): Response<Unit> {
        return bookingApi.postBookingReject(meetingId,memberId,request)
    }
    override suspend fun postBookingStart(request: BookingStartRequest): Response<Unit> {
        return bookingApi.postBookingStart(request)
    }
    override suspend fun patchBookingDetail(request: BookingModifyRequest) : Response<Unit> {
        return bookingApi.patchBookingDetail(request)
    }
}
