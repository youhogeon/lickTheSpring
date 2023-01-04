# 12. Resource 인터페이스

# Spring의 Resource 인터페이스

Java의 표준 java.net.URL의 클래스는 기능이 부족하다.

- classpath로부터 파일을 읽어올 수 없음
- ServletContext로 부터의 상대경로로 파일을 읽어올 수 없음
- 특수 접두사에 대해 핸들러 등록이 복잡함

Spring은 Resource 인터페이스(org.springframework.core.io.Resource)를 만들어 여러 resource들을 추상화했다.

Resource 인터페이스는 아래와 같다.

```java
public interface Resource extends InputStreamSource {
	boolean exists();

	default boolean isReadable() {
		return exists();
	}

	default boolean isOpen() {
		return false;
	}

	default boolean isFile() {
		return false;
	}

	URL getURL() throws IOException;
	URI getURI() throws IOException;
	File getFile() throws IOException;

	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	long contentLength() throws IOException;
	long lastModified() throws IOException;
	Resource createRelative(String relativePath) throws IOException;

	@Nullable
	String getFilename();

	String getDescription();

}
```

Resource 인터페이스를 구현한 구현 클래스는 아래와 같다.

- UrlResource
- ClassPathResource
- FileSystemResource
- PathResource
- ServletContextResource
- InputStreamResource
- ByteArrayResource

# Resource 사용 예시

```java
@Value("classpath:data.txt")
Resource dataResource;
```

@Value 어노테이션을 통해 Resource를 주입받을 수 있다.

```java
try {
    Resource resource = context.getResource("classpath:application.properties");
    File file = resource.getFile();
    String data = new String(Files.readAllBytes(file.toPath()));
    System.out.println(data);
} catch (IOException e) {
    System.out.println("Something wrong..");
}
```

ApplicationContext(의 상위 인터페이스인 ResourceLoader)의 getResource를 통해 Resource를 가져올 수 있다.

# PS

여기까지가 Spring framework document - core의 1장 내용이다.

[https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans)