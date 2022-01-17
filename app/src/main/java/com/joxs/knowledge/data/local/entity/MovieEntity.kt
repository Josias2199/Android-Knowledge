package com.joxs.knowledge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    @SerializedName("id")
    @Expose
    val id: Int,

    @SerializedName("popularity")
    @Expose
    val popularity: Double,

    @SerializedName("vote_count")
    @Expose
    val voteCount: Int,

    @SerializedName("video")
    @Expose
    val video: Boolean,

    @SerializedName("poster_path")
    @Expose
    val posterPath: String,

    @SerializedName("adult")
    @Expose
    val adult: Boolean,

    @SerializedName("backdrop_path")
    @Expose
    val backdropPath: String,

    @SerializedName("original_language")
    @Expose
    val originalLanguage: String,

    @SerializedName("original_title")
    @Expose
    val originalTitle: String,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("vote_average")
    @Expose
    val voteAverage: Double,

    @SerializedName("overview")
    @Expose
    val overview: String,

    @SerializedName("release_date")
    @Expose
    val releaseDate: String,
) : Serializable

/*@PrimaryKey
@SerializedName("id")
@Expose
private val id = 0

@SerializedName("popularity")
@Expose
private val popularity = 0.0

@SerializedName("vote_count")
@Expose
private val voteCount = 0

@SerializedName("video")
@Expose
private val video = false

@SerializedName("poster_path")
@Expose
private val posterPath: String? = null

@SerializedName("adult")
@Expose
private val adult = false

@SerializedName("backdrop_path")
@Expose
private val backdropPath: String? = null

@SerializedName("original_language")
@Expose
private val originalLanguage: String? = null

@SerializedName("original_title")
@Expose
private val originalTitle: String? = null

@SerializedName("title")
@Expose
private val title: String? = null

@SerializedName("vote_average")
@Expose
private val voteAverage = 0.0

@SerializedName("overview")
@Expose
private val overview: String? = null

@SerializedName("release_date")
@Expose
private val releaseDate: String? = null*/