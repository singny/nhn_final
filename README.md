# web-application-server
## 1. HTTP/1.1 의 Host 헤더를 해석하세요.
- 구현여부 : O
- a.com:9090 / b.com:9090
- 관련파일<br/>
  src/main/java/full/server/RequestProcessor.java

## 2. 다음 사항을 설정 파일로 관리하세요.
- 구현여부 : O
- 관련파일<br/>
  src/main/resources/config.json<br/>
  src/main/java/full/server/ServerConfig.java

## 3. 403, 404, 500 오류를 처리합니다.
- 구현여부 : O
- 관련파일<br/>
  src/main/java/full/server/RequestProcessor.java<br/>
  src/main/resources/config.json

## 4. 다음과 같은 보안 규칙을 둡니다.
- 구현여부 : △<br/>
  예, http://localhost:8000/../../../../etc/passwd -> url에 ..이 들어가면 403이 호출되도록 하였으나 브라우저에서 ../을 자동으로 삭제 후 요청을 보내 실현하지 못했습니다.
- 예시 url(exe) : localhost:9090/test.exe
- 관련파일<br/>
  src/main/java/full/server/RequestProcessor.java

## 5. logback 프레임워크 http://logback.qos.ch/를 이용하여 다음의 로깅 작업을 합니다.
- 구현여부 : O
- 경고를 info, warn, error로 분리
- 관련파일<br/>
  pom.xml<br/>
  src/main/resources/logback.xml

## 6. 간단한 WAS 를 구현합니다.
- 구현여부 : O
- 예시 url : localhost:9090/Hello?name=nhn
- 관련파일<br/>
  src/main/java/full/interfaces/*<br/>
  src/main/java/full/service/Hello.java

## 7. 현재 시각을 출력하는 SimpleServlet 구현체를 작성하세요.
- 구현여부 : O
- 예시 url : localhost:9090/CurrentTime
- 관련파일<br/>
  src/main/java/full/interfaces/*<br/>
  src/main/java/full/service/CurrentTime.java
  
## 8. 앞에서 구현한 여러 스펙을 검증하는 테스트 케이스를 JUnit4 를 이용해서 작성하세요.
- 구현여부 : O
- 관련파일<br/>
  src/test/java/Misson1.java
  src/test/java/Misson2.java
  src/test/java/Misson3And4.java
  src/test/java/Misson5.java
  src/test/java/Misson6And7.java
