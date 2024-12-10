package com.loc.newsapp.presentation.details

import com.loc.newsapp.domain.model.Article

sealed class DetailsEvent {
    data class UpsertDeleteArticle(val article: Article) : DetailsEvent()
    object RemoveSideEffect: DetailsEvent()
}

/*
screen - ViewMode - event 관계 이해하기
    구성 요소 분석
        1. Details Screen
            - 역할 : 화면을 구성하고 사용자의 상호작용을 처리한다
            - 기능
                - 사용자가 버튼을 누를 때 이벤트를 전달
                - LazyColumn을 사용해 기사 내용 표시
                - Intent를 활용해 웹 브라우저 열기, 공유하기 등의 기능ㅇ르 제공
        2. Details ViewModel
            - 역할 : 비즈니스 로직을 처리하고 상태를 유지한다
            - 기능
                - 기사를 데이터베이스에 저장하거나 삭제하는 비동기 작업을 수행한다
                - sideEffect를 관리해 UI에 부가적인 효과를 제공한다
                - UI에서 발생하는 DetailsEvent를 처리한다
        3. Details Event
            - 역할 : UI에서 발생하는 이벤트를 추상화하고 ViewModel에서 이를 처리할 수 있도록 전달한다
            - 기능
                - UpsertDeleteArticle : 기사를 저장하거나 삭제하는 이벤트
                - RemoveSideEffect: sideEffect를 초기화하는 이벤트

    왜 event 클래스를 건드렸는데 뷰모델이 반응하는 걸까?
        - compose의 데이터 바인딩과 함수 참조에 대한 개념을 포함한다
        - 간단히 말하자면 event 함수가 DetailsEvent.UpsertDeleteArticle 이벤트를 전달했을 때, 뷰모델이 이를 감지하고 반응한다
        - 흐름 이해
            1. event함수가 뷰모델을 호출하는 방식
                - event는 DetailsScreen 컴포저블에 전달된 함수
                - 실제로는 DetailsViewModel의 onEvent 메서드를 참조하고 있다
                - 참조 코드)
                    composable(route = Route.DetailsScreen.route) {
                        val viewModel: DetailsViewModel = hiltViewModel()
                        ...
                        DetailsScreen(
                            article = article,
                            event = viewModel::onEvent,             <-- ViewModel의 onEvent를 전달
                            navigateUp = { navController.navigateUp() }
                        )
                    }
                        - DetailsScreen을 호출할 때, viewModel::onEvent가 event로 전달된다
                        - 이로 인해 DetailsScreen에서 호출되는 event 함수는 사실 DetailsViewModel의 onEvent 메서드를 참조한다
            2. onEvent 메서드 처리
                - 참조 코드)
                    fun onEvent(event: DetailsEvent) {
                        when(event) {
                            is DetailsEvent.UpsertDeleteArticle -> {
                                viewModelScope.launch {
                                    val article = newsUseCases.selectArticle(event.article.url)
                                    if (article == null) {
                                        upsertArticle(event.article)
                                    } else {
                                        deleteArticle(event.article)
                                    }
                                }
                            }
                            is DetailsEvent.RemoveSideEffect -> {
                                sideEffect = null
                            }
                        }
                    }
                        - DetailsEvent.UpsertDeleteArticle 객체가 전달되면 onEvent에서 when 문을 통해 해당 이벤트를 처리한다
                        - 비즈니스 로직(기사 저장/삭제)을 수행한다
        3. 정리
            - 중요한 점은 ViewModel은 onEvent메서드를 참조하는 함수 객체이다
            - 흐름 정리
                - composable에 DetailsScreen에 viewModel::onEvent를 전달
                - fun DetailsScreen(event: (DetailsEvent) -> Unit)으로 됨, 한마디로 event는 뷰모델의 메서드
                    - ViewModel의 onEvent메서드에 DetailsEvent의 요소를 전달하는 형태가 됨
                - DetailsEvent의 요소를 onEvent에 전달하여 상태를 전달
                - onEvent 메서드가 입력된 상태에 따른 이벤트 실행
            - 요약
                - DetailsScreen의 매개변수로 전달한건 ViewModel의 onEvent메서드
                - 해당 메서드에 DetailsEvent의 상태값을 props로 넘겨준다
                - ViweModel에서 onEvent는 해당 상태값을 통해 비즈니스 로직을 실행

        4. 이걸 사용하는 이유
            1. 상태 관리의 분리
                - UI와 비즈니스 로직을 명확히 분리하여 유지보수가 쉬움
            2. 유연한 이벤트 처리
                - DetailsEvent를 활용해 다양한 이벤트를 추상화하고 쉽게 확장 가능
            3. 컴포저블 재사용성
                - DetailsScreen은 어떤 event함수가 전달되더라도 동일한 방식으로 작동
            4. 간단한 데이터 흐름
                - compose의 함수 참조를 통해 UI에서 로직(ViewModel)으로의 데이터 흐름을 단훈화
            근데 복잡한 화면 구현에서는 안쓸거 같긴 함



*/