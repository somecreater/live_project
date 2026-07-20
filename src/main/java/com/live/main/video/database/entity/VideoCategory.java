package com.live.main.video.database.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoCategory {
    GAME(
            "게임",
            "게임 플레이, 공략, 리뷰, e스포츠 관련 콘텐츠"
    ),

    MUSIC(
            "음악",
            "뮤직비디오, 라이브 공연, 커버, 연주 관련 콘텐츠"
    ),

    ENTERTAINMENT(
            "엔터테인먼트",
            "예능, 토크, 리액션, 챌린지 관련 콘텐츠"
    ),

    SPORTS(
            "스포츠",
            "스포츠 경기, 하이라이트, 분석, 운동 관련 콘텐츠"
    ),

    EDUCATION(
            "교육",
            "강의, 학습, 자격증, 지식 전달 관련 콘텐츠"
    ),

    TECH(
            "IT·기술",
            "프로그래밍, 인공지능, 소프트웨어, 전자기기 관련 콘텐츠"
    ),

    NEWS(
            "뉴스·시사",
            "정치, 사회, 국제, 시사 및 주요 뉴스 관련 콘텐츠"
    ),

    BUSINESS(
            "경제·비즈니스",
            "경제, 금융, 투자, 창업, 부동산 관련 콘텐츠"
    ),

    LIFESTYLE(
            "라이프스타일",
            "일상, 브이로그, 자기관리, 생활 정보 관련 콘텐츠"
    ),

    FOOD(
            "음식",
            "요리, 레시피, 먹방, 맛집 관련 콘텐츠"
    ),

    TRAVEL(
            "여행",
            "국내외 여행, 관광지, 숙소, 여행 정보 관련 콘텐츠"
    ),

    BEAUTY_FASHION(
            "뷰티·패션",
            "메이크업, 스킨케어, 패션, 스타일링 관련 콘텐츠"
    ),

    HEALTH_FITNESS(
            "건강·운동",
            "헬스, 홈트레이닝, 건강 관리, 운동 정보 관련 콘텐츠"
    ),

    MOVIE_ANIMATION(
            "영화·애니메이션",
            "영화, 드라마, 애니메이션, 리뷰 관련 콘텐츠"
    ),

    PETS_ANIMALS(
            "반려동물·동물",
            "반려동물 일상, 동물 정보, 야생동물 관련 콘텐츠"
    ),

    HOBBY(
            "취미",
            "공예, 사진, 캠핑, 낚시, 자동차 등 취미 관련 콘텐츠"
    ),

    KIDS(
            "키즈",
            "동요, 장난감, 아동 교육 및 어린이 대상 콘텐츠"
    ),

    ETC(
            "기타",
            "기존 카테고리에 포함되지 않는 기타 콘텐츠"
    );
    private final String name;
    private final String description;
}
