package com.faist.stockexchange.data

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("results") var barList: List<Bar>
)
