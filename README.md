<div align=center><h2> 🦼 FlatRoad : 이동제약자를 위한 안전한 길찾기 서비스  </h2></div>

<p align="center">
 <img src="https://user-images.githubusercontent.com/51811995/172270591-a6d569f9-ec6c-4d3a-b294-ad94ad63752e.PNG">
</p>

**🏆 건국대학교 SW경진대회 [장려상](https://drive.google.com/uc?id=1nyhV6eyY6XOo0Oe5MBGB31CmLT02O0b-)**

**👨‍💻 2021 공개SW개발자대회 [결선 진출작](http://bypub.kr/ebook/oss2021-1/index.html#p=56)**

> **Konkuk University  smartICTconvergence**</br>
> 팀원 : 안예림, 서민영, 박가은, 고유나</br>

## 목차 
1. [소개](#소개)
2. [기술 스택](#기술-스택)
3. [프로젝트 설명](#프로젝트-설명) 
4. [설계](#설계)
5. [관리 및 히스토리](#관리-및-히스토리)
6. [시연 영상](#시연-영상)
7. [라이선스](#라이선스)

## 소개
> 휠체어를 사용하거나 **거동이 힘든 사용자**가 가고자 하는 **경로의 위험요소**를 미리 알 수 있도록 도와주는 길찾기 서비스입니다.</br>

### 💡 볼라드란?
인도나 잔디밭에 자동차가 들어가지 못하도록 설치한 장애물

#### 📌 학습시킨 YOLOv5 모델로 볼라드를 탐지한 결과 
<p>
<img src="https://user-images.githubusercontent.com/51811995/151401877-ee101c0e-5c0d-47c5-8cb1-85f52374427d.png" width=400 >
</p>

### 💡 길찾기 Process

<p>
<img src="https://user-images.githubusercontent.com/51811995/172279061-67503814-ecb4-45ec-9809-2fa61ce49de2.PNG" width=800>
</p>



## 기술 스택
- FlatRoad에 사용한 주요 기술입니다

|API/SDK|<img src="https://user-images.githubusercontent.com/51811995/171873874-dccaa355-dbae-49bc-8616-d050771d265d.png" width=100>|<img src="https://user-images.githubusercontent.com/51811995/171874163-d1244766-1842-4b1b-b452-b56f027f8122.png" width=100>|<img src="https://user-images.githubusercontent.com/51811995/171875950-d140cb27-66c5-437a-bf98-bd7b26f2f657.png" width=100>|<img src="https://user-images.githubusercontent.com/51811995/171876187-e17cd3b1-9c6b-4c3b-a6aa-ec850d303906.png" width=100>|<img src="https://user-images.githubusercontent.com/51811995/171866274-1c37bc33-7099-460c-bcce-2b832ecd2189.png" width=100>|
|:---:|:---:|:---:|:---:|:---:|:---:|
|설명|Naver Maps|Kakao Roadview|Kakao Speech|Tmap Direction|Grabzit(화면캡처)|

|파트|개발 환경|
|:---:|:---:|
|앱|<img src="https://img.shields.io/badge/android-3DDC84?style=for-the-badge&logo=android&logoColor=white"> <img src="https://img.shields.io/badge/kotiln-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">|
|서버|<img src="https://img.shields.io/badge/node.js-339933?style=for-the-badge&logo=node.js&logoColor=white"> <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">|
|객체 탐지 모델|<img src="https://img.shields.io/badge/yolo-00FFFF?style=for-the-badge&logo=yolo&logoColor=black"> <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white">|
|데이터베이스|<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">|


## 프로젝트 설명
### Preview
|<img src="https://user-images.githubusercontent.com/51811995/173180071-8db47337-d8eb-4c1b-b2b8-c2e79e9994d2.gif" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173180049-75aefff1-c82d-4f59-bafe-872ab729c81b.gif" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173181366-08bf9429-90a9-49fd-b982-2003954b268b.gif" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173181420-97f9c9c7-d6c5-4e99-bfec-874ad630e509.gif" width=230>|
|:---:|:---:|:---:|:---:|
|지도 화면 내 위험요소|경로 찾기|경로 찾기 결과|위험요소 탐지|
|<img src="https://user-images.githubusercontent.com/51811995/173181498-71725200-83c6-46c2-9fa0-29c670266e93.gif" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173182360-687b73cd-f09f-4f27-a692-7dd055186c3f.png" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173182019-51e362c1-923b-4036-af0d-0e10a2502d51.jpg" width=230>|<img src="https://user-images.githubusercontent.com/51811995/173181978-c87b3095-68c9-4a70-a9b6-73e72b85bb18.jpg" width=230>|
|위험요소 정보 추출|커뮤니티|음성 안내|위험요소 등록|

### 1. 지도 🔍
- 지도 상단에 위험요소 버튼을 클릭하면 화면 내의 위험요소 위치가 마커로 표시됩니다.
- 마커를 클릭하면 위험요소에 대한 설명과 주소 정보가 제공됩니다.

### 2. 길찾기 🔍
- 길찾기 경로에 해당하는 로드뷰를 불러와 볼라드 객체를 탐지합니다.
- Roadview/Direction/Grabzit API를 사용하고 객체 탐지 모델로 YOLOv5 을 사용합니다.

<details markdown="1">
<summary>더보기</summary>

#### 사용법

1. 앱에서 장소를 검색해 출발지와 목적지를 찾는다.
2. 대로우선, 최단거리, 계단제외 중에서 경로를 선택한다.
3. 지도 화면에 길찾기 경로가 polyline으로 표시된다.
4. 객체 탐지 모델 결과는 느낌표 마커로, DB 데이터는 초록색 마커로 표시한다.
5. 마커 클릭시 각각 로드뷰 화면과 위험요소 정보 화면으로 이동해 정보를 확인한다.

</details>

### 3. 음성 안내 🔍
- 길찾기를 위해 필요한 출발지, 도착지, 경로 옵션을 **음성**으로 입력해 서버로 전송합니다.
- 경로를 전송 받고 위치 트래킹이 켜진 상태에서 위험요소와 3m 이내로 가까워지면 **음성으로 직선거리**를 알려줍니다. 

### 4. 위험요소 등록 🔍
- 사용자가 위험요소 사진을 찍어 서버의 데이터베이스에 추가하는 기능입니다.
- 등록한 정보와 이미지는 길찾기, 지도 기능을 통해 사용자에게 제공합니다.

### 5. 커뮤니티 🔍
- 로그인을 통해 글을 쓸 수 있고 다른 사람의 글은 수정/삭제할 수 없습니다.
- 게시글의 제목, 날짜, 작성자를 확인할 수 있습니다.

## 설계
- 전체 시스템 설계도 입니다.
<p align='center'>
<img src="https://user-images.githubusercontent.com/51811995/151384670-aabc5f23-33b0-4265-9a5e-299888c9fcf0.PNG" width=600>
</p>

## 시연 영상
- 모든 기능 시연이 담긴 [영상](https://www.youtube.com/watch?v=tuVqONCSgxg) 으로 약 5분 정도 소요됩니다.
  
## 관리 및 히스토리
### 관리
- 구글 Drive와 Meets로 매주 회의를 진행하며 협업하였습니다.
<p align="center">
 <img src="https://user-images.githubusercontent.com/51811995/151388607-3c4e6cad-2669-4ec4-b9f3-274e2898b95d.png" width="600">
</p>

### 히스토리
- 21.04.07 - 아이디어 제안서 제출 🌱

- 21.04.30 - 길찾기 API 연동

- 21.05.26 - 데이터셋 구축 & YOLOv5 학습

- 21.06.10 - 로드뷰, 화면 캡처 API 연동

- 21.06.23 - 앱, 서버에서 필수 기능 구현 완료

- 21.06.24~09.01 - 방학 동안 자율적으로 작업

- 21.09.16 - **`공개SW개발자대회`** 결선 진출 ✔

- 21.10.01 - **`커뮤니티`** 기능 추가 🧍‍♀️🧍‍♂️

- 21.10.15 - 볼라드 데이터 추가 & **`YOLOv5 재학습`** ✨

- 21.10.22 - **`음성 안내`** 기능 추가 📳

- 21.11.26 - 건국대학교 **`SW경진대회`** 장려 수상 🏆


## 라이선스
- 앱은 Apache-2.0 License 서버는 GPL-3.0 License를 사용합니다. 자세한 사항은 [LICENSE](https://github.com/gaeunpark924/ComprehensiveDesign2/blob/main/LICENSE)를 따릅니다.
