package com.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.playlistmaker.data.search.Response
import com.playlistmaker.data.search.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApiService
) : NetworkClient {


    override suspend fun doRequest(dto: Any): Response {

        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is TracksSearchRequest) {
            return Response().apply {
                resultCode = 400
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }


    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                else -> false
            }
        } else {
            false
        }
    }
}