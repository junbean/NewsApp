package com.loc.newsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.loc.newsapp.domain.usercases.news.NewsUseCases
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases
): ViewModel() {
    val news = newsUseCases.getNews(
        sources = listOf("bbc-news", "abc-news", "al-jazeera-english")
    ).cachedIn(viewModelScope)


}