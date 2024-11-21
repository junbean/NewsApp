package com.loc.newsapp.domain

import com.loc.newsapp.domain.usercases.ReadAppEntry
import com.loc.newsapp.domain.usercases.SaveAppEntry

data class AppEntryUseCases(
    val readAppEntry: ReadAppEntry,
    val saveAppEntry: SaveAppEntry
)
