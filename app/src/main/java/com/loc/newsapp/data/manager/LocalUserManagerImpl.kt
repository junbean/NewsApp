package com.loc.newsapp.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.loc.newsapp.domain.manager.LocalUserManager
import com.loc.newsapp.util.Constants
import com.loc.newsapp.util.Constants.USER_SETTINGS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
    LocalUserManagerImpl
        - DataStore 사용해서 로컬 저장소에 데이터를 저장하고 읽는 기능을 구현
        - saveAppEntry(): 특정 데이터를 저장
        - readAppEntry(): 저장된 데이터를 읽어 Flow로 반환
            
    saveAppEntry() 함수
        - 역할
            - DataStore에 데이터를 저장
            - APP_ENTRY 키를 사용하여 true 값을 저장
            - 사용자가 OnBoarding 페이지를 읽었음을 저장
        - 구성
            - context.dataStore
                - Jetpack DataStore 인스턴스를 가져옴
            - edit
                - DataStore에 데이터를 저장할 때 사용하는 함수
                - edit 블록 내부에서 데이터를 저장하거나 업데이트 가능
            - PreferenceKeys.APP_ENTRY
                - 저장할 데이터의 키를 정의
                - booleanPreferencesKey를 사용해 생성된 키 
    readAppEntry() 함수
        - 역할
            - DataStore에서 데이터를 읽어 Flow로 반환
            - Flow를 통해 비동기적 데이터 스트림을 제공
            - 사용자가 OnBoarding 페이지를 읽었는지 판별, 읽었다면 true, 아니라면 false
        - 구성
            - context.dataStore.data
                - DataStore에서 데이터를 가져옴
                - 이 데이터는 Preferences객체로 반환
            - map 연산자
                - Preferences에서 특정 키의 값을 가져오고, 없으면 기본값(false)를 반환
    PreferenceKeys 객체
        - 역할
            - DataStore에서 사용할 키를 정의하는 객체
        - 구성
            - booleanPreferencesKey
                - DataStore에서 Boolean 타입 데이터를 저장하거나 읽을 떄 사용
            - constants.APP_ENTRY
                - 키의 이름을 정의한 상수. 예를 들어 "app_entry"
    DataStore 초기화
        - 역할
            - DataStore를 초기화하고, Context를 통해 접근할 수 있도록 확장 프로퍼티 정의
        - 구성
            - preferencesDataStore
                - Preferences 타입의 DataStore를 생성합니다
                - name = USER_SETTINGS
                    - DataStore의 파일 이름을 설정( USER_SETTINGS.preferences_pb라는 파일로 저장 )
            - val Context.dataStore
                - Context에 dataStore라는 확장 프로퍼티를 추가하여 어디서든 DataStore에 접근 가능

*/

class LocalUserManagerImpl(
    private val context: Context
) : LocalUserManager {

    override suspend fun saveAppEntry() {
        context.dataStore.edit { settings ->
            settings[PreferenceKeys.APP_ENTRY] = true
        }
    }

    override fun readAppEntry(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.APP_ENTRY] ?: false
        }
    }
}

private val readOnlyProperty = preferencesDataStore(name = USER_SETTINGS)

val Context.dataStore: DataStore<Preferences> by readOnlyProperty

private object PreferenceKeys {
    val APP_ENTRY = booleanPreferencesKey(Constants.APP_ENTRY)
}