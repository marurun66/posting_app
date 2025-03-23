# 📱 RESTful API 기반 포스팅 앱

안드로이드 스튜디오로 개발한 **JWT 인증 기반 SNS 포스팅 앱**입니다.  
Retrofit2를 통해 서버와 통신하며, 사용자 인증, 글 작성, 이미지 업로드, 좋아요 기능 등을 구현했습니다.

---

## ✅ 프로젝트 개요

- **플랫폼**: Android (Java)
- **목적**: Retrofit을 활용한 RESTful API 연동 및 JWT 인증 흐름 구현
- **기능**: 회원가입, 로그인, 포스팅 작성, 이미지 업로드, 피드 조회, 좋아요

---

## 🧩 핵심 기능

| 기능 | 설명 |
|------|------|
| 회원가입 / 로그인 | 이메일 + 비밀번호 기반 로그인 (JWT 토큰 발급 및 저장) |
| JWT 인증 처리 | SharedPreferences를 이용해 토큰 저장, 헤더에 자동 포함 |
| 글 작성 | 텍스트 + 이미지 등록 |
| 이미지 업로드 | AWS S3에 저장 (파일명 중복 방지 처리: 유저ID + 타임스탬프) |
| 피드 조회 | 본인 포스트 + 팔로잉 유저의 포스트 병합 조회 |
| 좋아요 기능 | 게시물에 좋아요 추가 및 상태 반영 |
| 이미지 로딩 | Glide 라이브러리 사용 |

---

## 🔧 사용 기술 및 라이브러리

| 분류 | 기술 |
|------|------|
| Language | Java |
| IDE | Android Studio |
| REST 통신 | Retrofit2 |
| 인증 | JWT |
| DB (서버) | MySQL |
| 스토리지 | AWS S3 |
| 이미지 처리 | Glide |
| 로컬 저장소 | SharedPreferences, EncryptedSharedPreferences |
| 테스트 | Postman |
| 버전 관리 | Git, GitHub |

---

## 📷 시연 화면

[> (여기에 시연 영상 링크나 GIF 삽입)](https://www.canva.com/design/DAGih3G66cQ/8mNU0r8-Nq5eUtZrjr1Z-A/watch?utm_content=DAGih3G66cQ&utm_campaign=share_your_design&utm_medium=link2&utm_source=shareyourdesignpanel)

---

## 📝 느낀 점

이번 프로젝트를 통해 **실제 앱과 서버 간의 인증 흐름**, 그리고 **파일 업로드 및 보안처리 로직**을 경험할 수 있었습니다.  
Retrofit과 JWT를 활용하면서 **네트워크 통신과 인증의 흐름을 명확히 이해**할 수 있었고,  
특히 S3 업로드 로직과 피드 데이터 병합 처리 부분에서 많은 고민과 성장을 했습니다.

단순한 기능 구현을 넘어서, **실제 서비스에 적용 가능한 구조와 흐름**을 고려하며 개발할 수 있었던 의미 있는 프로젝트였습니다.

---

## 📂 프로젝트 구조 예시

