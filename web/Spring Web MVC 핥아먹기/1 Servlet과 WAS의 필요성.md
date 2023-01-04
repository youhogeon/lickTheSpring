# 1. Servlet과 WAS의 필요성

# HTTP 서비스를 만드는 방법

HTTP 프로토콜 아래에서 동작하는 서비스를 어떻게 구현할 수 있을까?

![Untitled](1%20Servlet%EA%B3%BC%20WAS%EC%9D%98%20%ED%95%84%EC%9A%94%EC%84%B1%2FUntitled.png)

HTTP는 TCP 프로토콜을 사용해서 (주로) 80포트에서 위와 같은 평문을 주고받는 프로토콜이다.

클라이언트가 HTTP 프로토콜에 맞추어 Request를 보내면 서버도 HTTP 프로토콜 맞추어 Response를 보낸다.

따라서 아래와 같은 동작을 하는 프로그램을 만들면 HTTP 애플리케이션을 만들 수 있다.

1. TCP 80포트에서 수신 대기 하는 소켓 생성
2. 요청이 들어오면 HTTP 프로토콜에 맞는 형식인지 확인
3. HTTP Request 패킷을 분석해서 메서드(GET, POST, …), 요청 경로, 헤더 정보, 파라미터, 요청 본문 등을 파싱
4. 메서드와 요청 경로에 맞는 함수를 호출
5. 함수에서 비즈니스 로직 처리 후 결과 반환
6. Http Response 패킷 형식에 맞추어 응답 데이터 생성
7. TCP 이용해 HTTP Response 패킷을 클라이언트에게 전달

우리의 비즈니스 코드를 실행(5번) 하기 위해 정말 많은 부가 작업이 필요하다.

따라서 이런 애플리케이션을 만든다는 것은 상상만 해도 고통스러운 일이다.

더불어, 캐싱 처리나 로깅, HTTP의 부가 기능들(keep alive 등)도 처리해야 하고, HTTP의 버전도 고려해야 한다.

# WAS란?

위와 같은 불편을 해소하기 위해 일반적으로 WAS(Web Application Server)를 도입한다.

5번을 제외한 나머지 작업을 WAS에게 위임하고, 우리는 WAS에서 요구하는 규약에 맞추어 비즈니스 로직을 처리하는 코드를 작성하면 된다.

```java
// pseudo-code 이므로 실제 작동하지 않는다.
// WASStandard라는 내가 방금 만든 가짜 규약을 지원하는 WAS는 없다.
public class MyBusinessClass extends WASStandard {

    public final String urlPattern = "/test";
    public final String method = "GET";

    @Override
    public WASResponseWrapper service(WASRequsestWrapper req) {
        //DO SOMETHING

        WASResponseWrapper res = new WASResponseWrapper();
        res.setHeader(...);
        res.setStatus(200);
        res.setBody(..);

        return res;
    }

}
```

위와 같이 코드를 작성해서 WAS에게 넘겨주면 WAS는 WAS가 요구한 규약에 따라 클라이언트에 요청이 들어오면 urlPattern 과 method가 일치하는 객체의 service 메서드를 실행해 줄 것이다.

우리는 WAS가 넘겨준 추상화된 Request 관련 정보(헤더, 본문, 요청파라미터 등)를 이용해서 비즈니스 로직을 처리하고, WAS가 정해준 규약(WASResponseWrapper)에 따라 응답 데이터에 들어갈 내용을 작성해서 WAS에 넘겨주기만 하면 된다.

# Servlet이란?

WAS를 도입함으로써 HTTP 애플리케이션을 개발하는 수고가 많이 줄었다.

하지만.. WAS를 교체해야 할 일이 생겼다면?

우리의 코드는 특정 WAS에 매우 강하게 의존하기 때문에 모든 코드를 새로운 WAS의 새로운 규약에 맞추어 뜯어 고쳐야 할 것이다.

다행히, JAVA에서는 Servlet이라는 표준화된 공통 규약을 만들어두었다.

정확히는 JavaEE의 servlet이고, 4.0.1 버전까지는 javax.servlet 패키지, 4.0.2버전 이후로는 jakarta.servlet 패키지에 담겨있다.

이제 우리는 Servlet API에 맞추어 코드를 작성하면 되고, Servlet을 지원하는 WAS(tomcat, jetty, undertow 등)를 사용하면 모든 문제가 해결된다.

# Servlet Interface, HttpServlet

```java
package jakarta.servlet;

import java.io.IOException;

public interface Servlet {

    public void init(ServletConfig config) throws ServletException;

    public ServletConfig getServletConfig();

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException;

    public String getServletInfo();

    public void destroy();

}
```

jakarta.servlet의 Servlet Interface는 위와 같이 생겼다.

따라서 저 다섯 개의 메서드를 구현해주면 우리의 Servlet이 만들어진다.

그러나, 비즈니스 로직(init, service) 외에도 다른 메서드까지 구현해야 하는 불편함이 있어서

일반적으로 이를 구현해 둔 jakarta.servlet.http.HttpServlet 추상클래스를 상속받아 사용한다.

```java
package jakarta.servlet.http;

public abstract class HttpServlet extends GenericServlet {

    public HttpServlet() {}

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        legacyHeadHandling = Boolean.parseBoolean(config.getInitParameter(LEGACY_DO_HEAD));
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = lStrings.getString("http.method_get_not_supported");
        resp.sendError(getMethodNotSupportedCode(protocol), msg);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ...
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ...
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ...
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 요청 메서드를 확인하고, 해당 메서드를 처리하는 메서드를 호출한다.
        // 예를 들어 GET 요청이 들어오면 doGet() 메서드를 호출한다.
    }

}
```

jakarta.servlet.http.HttpServlet 코드 중 일부를 발췌하고 가공했다. (실제 코드와 다름)

요청 메서드를 매번 확인하는 번거로움을 줄이기 위해

HttpServlet의 service를 구현해 요청 메서드에 따라 적절한 메서드를 호출하도록 되어있다.

따라서 우리가 GET 요청을 처리하고자 한다면 HttpServlet을 상속받고 doGet 메서드만 구현하면 된다. (나머지 메서드는 HttpServlet의 기본 구현을 따른다)