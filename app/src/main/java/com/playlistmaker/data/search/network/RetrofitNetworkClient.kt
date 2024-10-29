package com.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.playlistmaker.data.search.Response
import com.playlistmaker.data.search.TracksSearchRequest

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesService: ITunesApiService
) : NetworkClient {


    override fun doRequest(dto: Any): Response {
        if (dto is TracksSearchRequest) {
            if (!isConnected()) {
                return Response().apply { resultCode = -1 }
            } else try {
                val resp = iTunesService.search(dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply {
                    resultCode = resp.code()
                }
            } catch (e: Exception) {
                return Response().apply {
                    resultCode = 400
                }
            }
        } else return Response().apply {
            resultCode = 400
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