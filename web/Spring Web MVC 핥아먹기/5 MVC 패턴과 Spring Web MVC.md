# 5. MVC 패턴과 Spring Web MVC

# MVC 패턴 개요

Servlet에서 요청을 받고 주 로직 처리는 JSP 에서 처리하다 보니 JSP의 코드가 굉장히 난잡해졌다.

뷰에 포함되어있던 비즈니스 로직을 컨트롤러 단으로 옮긴 뒤, 뷰에서는 화면 출력 처리만 담당하도록 개선할 수 있다.

(비즈니스 로직을 컨트롤러에 두지 않고, 서비스 레이어로 한번 더 분리하기도 한다.)

뷰와 컨트롤러 사이에 데이터를 주고받을 수 있도록 데이터 전송 객체(DTO; Getter Setter만 있는 단순 객체)를 사용하는데, 이를 Model이라고 한다.

Model, View, Controller가 분리된 이러한 패턴을 MVC 패턴이라고 한다.

# Spring Web에서의 MVC패턴

Spring에서는 모든 요청을 공통 컨트롤러(Dispatcher Servlet)가 받고, URL 패턴과 메서드에 따라 적절한 컨트롤러(Servlet이 아니다!)로 요청 처리를 위임한다.

![Untitled](5%20MVC%20%ED%8C%A8%ED%84%B4%EA%B3%BC%20Spring%20Web%20MVC%2FUntitled.png)

(사진에서 FrontController라고 표시된 역할을 Dispatcher Servlet이 담당한다.)

따라서 Spring Web MVC를 사용하면 더 이상 HttpServlet을 상속받아 Servlet 을 구현할 필요가 없다.

더불어, 모든 Servlet에서 처리해야 하는 공통 로직을 DispatcherServlet에 구현해 공통 처리가 가능하다.

# 의존성 추가

시리즈 제목은 Spring Web MVC이면서 아직까지 Spring을 의존성에 추가하지도 않고 있었다.

Spring Web MVC가 필요한 이유를 설명하기 위해 긴 여정을 거쳤다.

이제 아래와 같이 spring-webmvc 의존성을 추가해주자.

```java
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>6.0.3</version>
</dependency>
```

spring-webmvc가 이미 core와 context 등 Spring framework의 기본 의존성을 가지고 있기 때문에, web-mvc만 추가해도 된다.

![Untitled](5%20MVC%20%ED%8C%A8%ED%84%B4%EA%B3%BC%20Spring%20Web%20MVC%2FUntitled%201.png)

# web.xml 추가

Spring Web MVC의 Dispatcher servlet은 @Servlet 어노테이션을 가지고 있지 않다.

따라서 src/main/webapp/WEB-INF/web.xml을 다음과 같이 입력해 톰캣이 org.springframework.web.servlet.DispatcherServlet를 서블릿으로 인식할 수 있게 하자.

```java
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns="http://xmlns.jcp.org/xml/ns/javaee"
        xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                            http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd" id="HelloSpring" version="4.0">
 
    <display-name>hello-spring</display-name>
    
    <servlet>
        <servlet-name>spring-dispatcher-servlet</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring-servlet.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    
    <servlet-mapping>
        <servlet-name>spring-dispatcher-servlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
 
</web-app>
```

같은 위치에 spring-servlet.xml을 추가해, component scan 이 동작하게 만들자. (component scan을 사용하지 않고 <bean>을 이용해 컨트롤러를 빈으로 직접 등록해도 된다.)

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.lickthespring.web" />

</beans>
```

# DispatcherServlet의 동작 원리

HandlerMapping, HandlerAdapter에 대한 개념을 알면 Spring Web MVC의 가 어떻게 동작하는지 알 수 있다.

Spring에서는 요청을 처리하는 주체를 handler라고 부르는데, 이는 Controller보다 더 포괄적인 개념이다. (Controller는 handler의 한 종류이므로, 모든 handler는 controller가 아니다.)

혼란을 피하기 위해 아래 설명에서는 handler라는 용어로 통일해서 설명한다.

## HandlerMapping

Spring의 DispatcherServlet은 어떻게 우리의 handler를 알아낼 수 있을까?

Spring은 HandlerMapping을 통해 Spring에 등록된 빈들을 조회해 handler를 찾는다.

(handler는 class 객체일 수도 있고, 메서드일 수도있다.)

HandlerMapping 인터페이스를 구현한 구현체는 여러 종류가 있다. (개발자가 만들어 등록할 수도 있다.) 

- RequestMappingHandlerMapping 클래스는 @Controller 어노테이션을 가진 클래스를 인식하고,  handler로 등록해준다. (@RequestMapping 어노테이션을 가진 메서드가 handler로 등록됨)
- BeanNameUrlHandlerMapping 클래스는 스프링 빈 이름(빈 이름을 URL로 작성하면 된다)을 기반으로 handler로 등록해준다. (빈 자체가 handler로 등록됨)
- SimpleUrlHandlerMapping 클래스는 SimpleUrlHandlerMapping 객체를 반환하는 빈을 등록함으로써 사용 가능하다. map에 URL과 컨트롤러 객체를 넣고, setUrlMap(map) 메서드를 사용해 mapping 정보를 등록할 수 있다.

우리는 Spring의 여러 HandlerMapping 구현체들이 우리의 handler를 찾아 등록할 수 있도록, 그에 맞추어 handler를 만들어야 한다. (어노테이션을 붙이거나, 빈 이름을 URL로 만들거나…)

```
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (this.handlerMappings != null) {
			for (HandlerMapping mapping : this.handlerMappings) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
		}
		return null;
	}
```

위 코드는 DispatcherServlet에 구현된 코드이다.

HandlerMapping 구현체들이 담겨있는 List인 handlerMappings에 대해 반복을 돌리며, request를 처리할 수 있는 HandlerMapping을 찾는다.

엄밀히 말하자면 위 코드를 통해 알 수 있듯이, request가 들어올 때 마다 handlerMappings에 등록된 HandlerMapping 구현체들이 모든 빈을 탐색하며 처리 가능 여부를 판단하는게 맞다.

그러나 더 나은 성능을 위해, 대부분의 handlerMappings 구현체들은 생성 시점에서 모든 빈을 미리 탐색해 처리 가능한 request와 handler 쌍을 만들어둔다.

## HandlerAdaptor

HandlerMapping이 우리를 발견했다고 해서 끝이 아니다.

handler를 마음대로 구현해버리면 Spring은 그 handler를 다루지 못한다.

Spring은 개발자가 handler를 자유롭게 구현할 수 있도록 HandlerAdapter를 도입했다.

개발자는 여러 HandlerAdapter 중 하나를 선택해, 해당 Adapter가 요구하는 방식으로 handler를 구현할 수 있다.

주요 HandlerAdapter는 아래와 같다.

- HttpRequestHandler 인터페이스를 구현한 handler는 HttpRequestHandlerAdapter가 처리할 수 있다.
- @RequestMapping 어노테이션을 가진 handler는 RequestMappingHandlerAdapter가 처리할 수 있다.
- Controller 인터페이스(org.springframework.web.servlet.mvc.Controller)를 구현한 handler는 SimpleControllerHandlerAdapter가 처리할 수 있다. (Controller 어노테이션과 전혀 다른 내용이다.)

```java
@Component("/test")
public class MyController {

		public int myControllerLogic_SpringWillNotBeAbleToFindThisMethod() { ... }

}
```

위 코드는 빈 이름을 /test로 설정함으로써, BeanNameUrlHandlerMapping에 의해 handler로 인식된다.

그러나, 어떠한 HandlerAdapter도 위와 같은 클래스를 처리할 수 없기 때문에, /test로 요청이 들어오더라도 spring은 우리의 handler를 호출할 수 없다.

결국 아래와 같이 HandlerAdapter가 없다는 예외(jakarta.servlet.ServletException)가 발생할 것이다.

```java
jakarta.servlet.ServletException: No adapter for handler [com.lickthespring.web.controller.MyController@7fa8f5ae]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler
```

HttpRequestHandler 인터페이스를 구현해 HttpRequestHandlerAdapter가 우리의 handler를 호출할 수 있도록 코드를 작성해보자.

```java
@Component("/test")
public class MyController implements HttpRequestHandler {

    public void myControllerLogic() {
        System.out.println("This method is not found by Spring");
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("This method is found by Spring");
    }

}
```

HttpRequestHandler 의 handleRequest 메서드를 구현하면 HttpRequestHandlerAdapter는 /test로 요청이 들어왔을 때 handleRequest 메서드를 호출해준다.

이번에는 RequestMappingHandlerAdapter 가 우리의 handler를 호출할 수 있도록 @RequestMapping 어노테이션을 이용해 handler를 만들어보자.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    public ?????? myHandler(??????) {
        //...
    }

}
```

그런데, 어떤 파라미터를 받아야 하고 어떤 타입을 반환해야 할까?

다른 adapter와 달리 구현해야 하는 인터페이스가 없기 때문에 혼란에 빠질 수 있다.

그러나, 놀랍게도 RequestMappingHandlerAdapter는 다양한 반환 타입과 파라미터를 지원한다. 심지어 파라미터는 선택적으로 원하는 것만 순서에 상관없이 나열해 쓸 수 있다.

반환 타입은 아래 ModelAndView 섹션에서 자세히 설명하기로 하고, 일단 사용 가능한 파라미터를 알아보자.

- WebRequest, NativeWebRequest : ServletAPI 사용하지 않고 요청 파라미터 등에 엑세스
- ServletRequest, ServletResponse, HttpSession : Servlet 객체
- java.security.Principal : 인증된 사용자 정보
- org.springframework.ui.Model : 모델 객체

그 외에도 HttpEntity, HttpMethod 등의 다양한 타입의 객체를 주입받을 수 있으며, @PathVariable, @RequestParam, @RequestBody 등의 어노테이션을 이용할 수 있다.

자세한 내용은 추후에 다룬다.

```java
protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (this.handlerAdapters != null) {
			for (HandlerAdapter adapter : this.handlerAdapters) {
				if (adapter.supports(handler)) {
					return adapter;
				}
			}
		}
		throw new ServletException("No adapter for handler [" + handler +
				"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}
```

위 코드는 DispatcherServlet에 구현된 코드이다.

HandlerAdapter 구현체 List인 handlerAdapters에 대해 반복을 돌리며, handler를 처리할 수 있는 HandlerAdapter를 찾는다.

(HandlerMapping을 찾는 과정과 유사하다)

## ModelAndView

HandlerAdapter는 핸들러의 반환값을 잘 가공하여, DispatcherServlet에게 ModelAndView(org.springframework.web.servlet.ModelAndView)를 반환해주어야 한다.

ModelAndView는 Model 객체와 View 객체(View 타입 혹은 String 타입)를 가진 인터페이스이다.

```java
public class ModelAndView {

	/** View instance or view name String. */
	@Nullable
	private Object view;

	/** Model Map. */
	@Nullable
	private ModelMap model;

	/** Optional HTTP status for the response. */
	@Nullable
	private HttpStatusCode status;

	/** Indicates whether this instance has been cleared with a call to {@link #clear()}. */
	private boolean cleared = false;

	//그리고 여러 메서드들(생략)

}
```

각 Adapter들은 아래와 같이 ModelAndView를 생성해 DispatcherServlet에게 전달한다.

- SimpleControllerHandlerAdapter의 경우, handler가 직접 ModelAndView를 반환해야 한다. Adapter는 DispatcherServlet에게 handler의 반환값을 그대로 전달한다.
- HttpRequestHandlerAdapter의 경우 핸들러는 반환 없이 직접 response를 작성해야 한다. (DispatcherServlet에게 ModelAndView 객체 대신 null을 전달한다.)
- RequestMappingHandlerAdapter의 경우 handler는 다양한 반환 타입을 가질 수 있으며, adapter가 알아서 ModelAndView를 만들어준다.

마지막의 RequestMappingHandlerAdapter는 다양한 반환 타입을 ModelAndView로 변환해야 하므로, 동작 과정이 다소 복잡하다.

Handler가 ModelAndView를 반환할 수도 있다.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    public ModelAndView myHandler(...) {
        //...
    }

}
```

Handler가 View를 반환할 수도 있다. 이러한 경우 보통 핸들러의 파라미터를 통해 Model 객체를 주입받아 사용한다.

이러한 경우, RequestMappingHandlerAdapter는 View와 Model을 합쳐 ModelAndView를 생성한다.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    public View myHandler(Model model, ...) {
        //ModelAndView 대신 View만 리턴
        //Model을 사용해야 하는 경우 파라미터로 Model 객체를 받아 사용
    }

}
```

반환 타입이 String인 경우 RequestMappingHandlerAdapter는 ModelAndView의 view 필드를 String으로 설정한다.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    public String handleRequest(Model model, ...) {
        return "/WEB-INF/index.jsp";
    }

}
```

@ResponseBody 어노테이션이 추가된 경우, RequestMappingHandlerAdapter는 ModelAndView 객체 대신 null을 DispatcherServlet에게 전달한다.

대신 MessageConverter 구현체를 이용해 반환 값을 가지고 응답을 직접 생성한다.

반환 값이 String인 경우 StringHttpMessageConverter를 통해 반환 값 자체를 응답 본문으로 사용한다.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    @ResponseBody //해당 어노테이션이 있을 경우에만 MessageConverter 동작함. 그렇지 않으면 ModelAndView를 생성함.
    public String handleRequest(Model model, ...) {
        return "/WEB-INF/index.jsp";
    }

}
```

반환 값이 Object인 경우 MappingJackson2HttpMessageConverter를 통해 객체를 JSON 문자열로 만들어 응답 본문으로 사용한다.

```java
@Controller
public class MyController {

    @RequestMapping("/")
    @ResponseBody //해당 어노테이션이 있을 경우에만 MessageConverter 동작함.
    public Member handleRequest(Model model, ...) {
        return new Member(...);
    }

}
```

이 외에도 RequestMappingHandlerAdapter는, Callable<V>, HttpEntity, ResponseEntity, void 등 다양한 리턴 타입을 가질 수 있다.

# ViewResolver

DispatcherServlet이 HandlerAdapter로부터 ModelAndView를 받으면, ViewResolver를 통해 View를 렌더링한다.

단, ModelAndView 객체 대신 null을 받는 경우에는 ViewResolver가 동작하지 않는다.

기본적으로 InternalResourceViewResolver가 ViewResolver로 동작하며, InternalResourceViewResolver는 Resource를 응답으로 변환해준다.

# 정리

조금 복잡하지만, 정리하면 다음과 같다.

1. 톰캣이 DispatcherServlet을 유일한 Servlet으로 등록한다.
2. DispatcherServlet은 초기화 과정에서 HandlerMapping 구현체, HandlerAdapter 구현체, ViewResolver 구현체들을 List 형태로 등록해둔다.
3. 초기화 과정에서 ApplicationContext를 살피며, HandlerMapping 구현체들이 처리할 수 있는 handler 목록을 만들어둔다.
4. 사용자의 요청이 들어온다(모든 요청은 DispatcherServlet으로 들어온다)
5. **DispatcherServlet은 사용자의 요청을 분석(URL패턴, 메서드 등)해, 요청을 처리할 수 있는 handler를 찾는다. (2~3번 과정에서 찾은 정보 활용)**
6. **찾은 handler를 다룰 수 있는 HandlerAdapter를 찾고, adapter를 통해 handler를 호출한다.**
7. HandlerAdapter는 handler의 반환값을 이용해 ModelAndView 객체를 만들어 DispatcherServlet에게 전달한다. (혹은 null을 전달한다.)
8. DispatcherServlet은 ModelAndView객체가 있으면, ViewResolver를 이용해 응답을 생성한다.

# 참고사항

Spring Web MVC의 DispatcherServlet.properties 파일에는 기본으로 등록되는 HandlerMapping, HandlerAdapter, ViewResolver 등이 나열되어 있다.

DispatcherServlet은 이 파일의 내용을 이용해 초기화(init) 단계에서 HandlerMapping List, HandlerAdapter List 등을 만든다.

[https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-framework%2Fblob%2Fmain%2Fspring-webmvc%2Fsrc%2Fmain%2Fresources%2Forg%2Fspringframework%2Fweb%2Fservlet%2FDispatcherServlet.properties](https://github.com/spring-projects/spring-framework/blob/main/spring-webmvc/src/main/resources/org/springframework/web/servlet/DispatcherServlet.properties)