# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* HTTP header에서 url경로 추출 후 경로에 해당하는 파일을 클라이언트로 전송
* BufferedReader를 사용하여 InputStream을 읽고, readLine()을 통해 첫번째 줄에서 경로 추출
* 요청한 경로에 해당하는 파일을 Files를 이용하여  byte[]배열로 읽은 후 저장

### 요구사항 2 - get 방식으로 회원가입
* 요청 경로는 /user/create?userId=test&password=pw&name=kim&email=kim@kim.com
* HTTP header의 첫번째 라인에서 url을 추출한 후 유저 정보에 해당하는 파라미터 파싱(util.HttpRequestUtils 클래스 사용)
* 파싱한 데이터로 model.User 클래스에 저장

### 요구사항 3 - post 방식으로 회원가입
* 요청경로는 /user/create
* POST 전송 방식일 떄 데이터는 HTTP Body에 있음. Body은 HTTP Header의 끝을 알리는 공백라인의 다음줄
* HTTP Body에 전달되는 데이터는 GET 방식으로 데이터를 전달할 때의 이름=값 과 같음
* HTTP Body는 util.IOUtils 클래스 사용하여 읽은 후 데이터로 model.User 객체 생성

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 