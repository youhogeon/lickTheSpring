# 7. Filter와 Handler Interceptor

# 개요

사실 지금까지 만든 방명록 서비스에는 문제가 있다.

요청 본문으로 한글이 넘어오는 경우 아래와 같이 문자가 깨져서 저장된다.

![Untitled](7%20Filter%EC%99%80%20Handler%20Interceptor%2FUntitled.png)

톰캣의 문자셋 기본값은 ISO 8859-1이기 때문에 이러한 문제가 발생한다.

# 해결 방법

톰캣의 설정 변경으로 기본 문자셋을 UTF-8로 바꾸면 해결된다.

그러나, 일반적으로 UTF-8을 사용하지 아니할 이유가 없기 때문에, 애플리케이션단에서 UTF-8로 고정시키는 방법을 많이 사용한다.

HttpServletRequest의 setCharacterEncoding메서드를 사용해 다음과 같은 코드를 “모든 메서드(혹은 서블릿)”에 넣어주면 문제는 해결된다.

```java
request.setCharacterEncoding("UTF-8");
```

하지만 모든 메서드에 공통적인 코드를 넣는 것은 너무나도 비효율적이다.

# 필터를 이용한 해결

Servlet 표준에는 Filter가 포함되어 있다.

Filter는 Servlet으로 요청이 가기 “전”과 Servlet에서 응답을 생성한 “후”(응답이 클라이언트에 전달되기 직전)에 거쳐가는 레이어이다.

따라서 request, response에 대한 공통 처리가 필요할 때(문자셋 처리, 보안 처리 등) 사용한다.

더불어, Servlet(DispatcherServlet 포함) 레이어 외부에 위치해있기 때문에 일반적으로 Spring과 연계하여 사용하지 않는다.

Filter 클래스는 jakarat.servlet.Filter 인터페이스를 구현함으로써 만들 수 있다.

Servlet과 마찬가지로, web.xml 설정에 구현한 filter 클래스명을 작성해주는 방법도 있고, @WebFilter 어노테이션을 이용해 WAS가 인식하도록 만들 수도 있다.

```
package com.lickthespring.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter("/*")
public class CharsetFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {        
        request.setCharacterEncoding("UTF-8");

        chain.doFilter(request, response);
    }
    
}
```

추가로, Filter 인터페이스의 init 메서드와 destroy 메서드를 구현할 수도 있다.

init 메서드는 필터가 초기화 될 때, destroy 메서드는 필터가 제거될 때 각각 한 번씩 호출된다.

# Handler Interceptor를 이용한 해결

Handler Interceptor(이하 Interceptor)는 Filter와 비슷한 역할을 수행하지만 크게 두 가지 다른점이 있다.

- Interceptor는 Servlet 표준이 아니다. Spring Web MVC에 포함되어 있다.
- Interceptor는 DispatcherServlet이 요청을 받고 Controller로 넘기기 전과, Controller가 응답을 DispatcherServlet으로 넘긴 후에 작동한다.

![Untitled](7%20Filter%EC%99%80%20Handler%20Interceptor%2FUntitled%201.png)

(보라색 글씨는 Servlet 명세에 해당하고, 주황색 글씨는 Spring 기술이다.)

```java
package com.lickthespring.web.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CharsetInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");

        return true;
    }

}
```

preHandle 뿐 아니라 postHandle(핸들러 정상 실행 후), afterCompletion(View 렌더링 완료 후. 예외 발생시에도 호출됨) 메서드를 구현할 수도 있다. preHandle 메서드 반환값이 false이면 더 이상 다른 Interceptor나 핸들러로 요청이 넘어가지 않는다. preHandle 메서드 내에서 응답을 작성해야 한다.

추가로, Web MVC 설정을 통해 Interceptor를 등록해주어야 한다.

```java
package com.lickthespring.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lickthespring.web.filter.CharsetInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final CharsetInterceptor charsetInterceptor;

    public WebConfig(CharsetInterceptor charsetInterceptor) {
        this.charsetInterceptor = charsetInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(charsetInterceptor);
    }

}
```

참고로, Web MVC 설정을 통해 Interceptor 뿐 아니라 직접 구현한 HandlerAdaptor, HandlerMapping 등을 추가할 수도 있다.

# Filter와 Interceptor의 사용

앞에서 살펴보았듯, filter와 interceptor는 Servlet 표준 여부와 동작 시점에서 차이가 있다.

또, filter는 filter chaining을 통해 다음 필터로 request와 response 객체를 넘겨주는 구조이기 때문에, request와 response 객체를 바꿔치기 할 수 있고,

interceptor는 request, response의 setter를 통해 값을 변경할 수는 있어도 객체 자체를 바꿀 수는 없다.

더불어, filter는 주로(항상 그런것은 아니다) Spring 밖의 영역에서 동작하므로, filter 내부에서 예외 발생시 별도로 핸들링 작업이 필요하다.

반면에 interceptor는 Spring의 ExceptionHandler(추후 설명)를 통해 예외를 핸들링 할 수 있다. (단, afterCompletion은 View 렌더링 이후 호출되므로 불가능)

그렇다면 filter와 interceptor는 각각 어떤 경우에 사용해야 할까?

Filter는 주로 보안 처리(인증, 인가), 전체 요청 로깅, 응답 압축, 문자열 인코딩과 같이 Spring 혹은 MVC 로직과 무관한 작업들이 처리된다.

대표적으로, Spring Security는 Servlet Filter를 이용해 보안 관련 작업을 처리한다. 따라서 Spring Web MVC 없이 Spring Security를 단독으로 사용할 수도 있다.

반면에 Interceptor는 MVC로직에서 사용할 공통 값(User 객체 등)을 주입/가공 하는 등 MVC 로직 및 비즈니스 로직에서 공통적으로 수행되어야 할 작업들이 처리된다.