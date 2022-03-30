# 2022 오픈소스 SW 수업

2022-1학기 건국대학교 오픈소스 SW 수업 과제 제출용입니다.

**kuir.java**가 프로젝트의 메인 소스 코드로 사용되고,

주차별로 생성된 파일을 메인 함수의 인자값(String[] args)에 따라 객체를 생성하고 함수를 실행합니다.

## 파일 구조

```bash
├── README.md
├── .gitignore
├── data
│  ├── 떡.html
│  ├── 라면.html
│  ├── 아이스크림.html
│  ├── 초밥.html
│  └── 파스타.html
├── jars
└── src
    └── scripts
        ├── kuir.java
        ├── makeCollection.java
        ├── makeKeyword.java
        └── indexer.java
``` 

## 인코딩

**Encoding : UTF-8**

## 디렉토리 설명

**src/scrips** : .java 소스 파일이 저장되는 디렉토리


**data/*.html** : html 파일 데이터가 저장되어 있는 디렉토리


## 컴파일 명령어

### MAC

`javac -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,, src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp jars/jsoup-1.13.1.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

## 2~3주차 과제 제출본에 대하여

2~3 주차 과제 제출본은 삭제한 관계로 해당 코드를 위의 형식에 맞추어 다시 리팩토링하여 현재 파일에 반영하였습니다.
따라서 해당 내용은 makeCollection.java와 makeKeyword.java에서 확인할 수 있습니다.

