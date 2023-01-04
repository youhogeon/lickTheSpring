# 1. Spring Framework

# 개요

- 스프링은 “세계에서 가장 인기있는” 자바 프레임워크임
- 웹 서버 프로젝트에만 적용되는 프레임워크가 아님

# 모듈식 구조

스프링은 모듈식 구조를 가짐. 따라서 개발 시 필요한 모듈만 사용할 수 있음

- Spring Framework
- Spring Boot
- Spring Data
- Spring Cloud
- Spring Security
- Spring Batch

등 특정 분야에 적합한 여러개의 모듈로 나뉘어져 있음

# 주요 패키지

[](https://mvnrepository.com/artifact/org.springframework)

Maven Repository에 org.springframework를 검색하면 다양한 스프링 패키지들이 나옴

- spring-core : 각 스프링 모듈에서 필요한 공통 클래스들
- spring-context : IoC 기능을 구현하는 ApplicationContext 등이 포함됨
- spring-beans : 스프링 빈 관련 패키지
- spring-aop : AOP(관점 지향 프로그래밍) 관련 패키지
- spring-expression : SpEL(스프링 표현언어) 관련 패키지
- spring-jcl : 로깅 관련 패키지(Jakarta Commons Logging)