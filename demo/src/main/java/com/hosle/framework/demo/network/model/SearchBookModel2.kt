package com.hosle.framework.demo.network.model


import com.google.gson.annotations.SerializedName

data class Series(@SerializedName("id")
                  val id: String = "",
                  @SerializedName("title")
                  val title: String = "")


data class TagsItem(@SerializedName("count")
                    val count: Int = 0,
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("title")
                    val title: String = "")


data class BooksItem(@SerializedName("origin_title")
                     val originTitle: String = "",
                     @SerializedName("ebook_price")
                     val ebookPrice: String = "",
                     @SerializedName("catalog")
                     val catalog: String = "",
                     @SerializedName("translator")
                     val translator: List<String>?,
                     @SerializedName("rating")
                     val rating: Rating,
                     @SerializedName("binding")
                     val binding: String = "",
                     @SerializedName("title")
                     val title: String = "",
                     @SerializedName("author_intro")
                     val authorIntro: String = "",
                     @SerializedName("pages")
                     val pages: String = "",
                     @SerializedName("price")
                     val price: String = "",
                     @SerializedName("id")
                     val id: String = "",
                     @SerializedName("pubdate")
                     val pubdate: String = "",
                     @SerializedName("summary")
                     val summary: String = "",
                     @SerializedName("image")
                     val image: String = "",
                     @SerializedName("images")
                     val images: Images,
                     @SerializedName("author")
                     val author: List<String>?,
                     @SerializedName("alt")
                     val alt: String = "",
                     @SerializedName("url")
                     val url: String = "",
                     @SerializedName("tags")
                     val tags: List<TagsItem>?,
                     @SerializedName("alt_title")
                     val altTitle: String = "",
                     @SerializedName("series")
                     val series: Series,
                     @SerializedName("subtitle")
                     val subtitle: String = "",
                     @SerializedName("isbn13")
                     val isbn13: String = "",
                     @SerializedName("publisher")
                     val publisher: String = "",
                     @SerializedName("isbn10")
                     val isbn10: String = "",
                     @SerializedName("ebook_url")
                     val ebookUrl: String = "")


data class SearchBookModel(@SerializedName("total")
                           val total: Int = 0,
                           @SerializedName("books")
                           val books: List<BooksItem>?,
                           @SerializedName("count")
                           val count: Int = 0,
                           @SerializedName("start")
                           val start: Int = 0)


data class Rating(@SerializedName("average")
                  val average: String = "",
                  @SerializedName("min")
                  val min: Int = 0,
                  @SerializedName("max")
                  val max: Int = 0,
                  @SerializedName("numRaters")
                  val numRaters: Int = 0)


data class Images(@SerializedName("small")
                  val small: String = "",
                  @SerializedName("large")
                  val large: String = "",
                  @SerializedName("medium")
                  val medium: String = "")


