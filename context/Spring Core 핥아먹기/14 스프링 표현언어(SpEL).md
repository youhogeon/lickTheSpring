# 14. 스프링 표현언어(SpEL)

# SpEL

SpEL(Spring Expression Language)이란 런타임에서 객체에 대한 쿼리와 조작을 지원하는 강력한 표현 언어이다.

> The Spring Expression Language (SpEL) is ting an object graph at runtime.
> 

# 사용 예시

```
public void run() {
    ExpressionParser parser = new SpelExpressionParser();
    Expression exp = parser.parseExpression("'Hello World'.concat('!')"); 
    String message = (String) exp.getValue();
    
    Expression exp2 = parser.parseExpression("'Hello World'.bytes.length"); 
    int length = (Integer) exp2.getValue();

    System.out.println(message);
    System.out.println(length);
}
```

위와 같이 ExpressionParser를 통해 SpEL을 파싱해 얻은 Expression으로부터, getValue 메서드를 통해 값을 구할 수 있다.

그러나, 저렇게 사용하는 경우 보다 아래와 같이 @Value 어노테이션이나 xml 설정 내에서 값을 계산하기 위해 사용하는 경우가 많다.

```java
public class PropertyValueTestBean {

    private String defaultLocale;

    @Value("#{ systemProperties['user.region'] }")
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale() {
        return this.defaultLocale;
    }
}
```

```java
<bean id="numberGuess" class="org.spring.samples.NumberGuess">
    <property name="randomNumber" value="#{ T(java.lang.Math).random() * 100.0 }"/>
</bean>
```

# 지원 기능

아래 코드에서 Entity 클래스는 아래 코드를 의미한다고 가정한다.

```java
class Entity {
    public String name = "Lee-Ji-Eun";
    public Date currentTime = new Date();
    public String[] hobbies = new String[] { "Singing", "Dancing", "Acting" };
    public List<String> alias = Arrays.asList("IU", "dlwlrma");
    public Map<String, String> info = Map.of("height", "161.8cm", "weight", "45kg");

		public boolean isBeautiful() { return true; }
}
```

## 리터럴 표현

```java
ExpressionParser parser = new SpelExpressionParser();

// evals to "Hello World"
String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();

double avogadrosNumber = (Double) parser.parseExpression("6.0221415E 23").getValue();

// evals to 2147483647
int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();

boolean trueValue = (Boolean) parser.parseExpression("true").getValue();

Object nullValue = parser.parseExpression("null").getValue();
```

## **속성, 배열, 목록, 맵 및 인덱서**

```java
Entity entity = new Entity();
ExpressionParser parser = new SpelExpressionParser();

int year = (Integer) parser.parseExpression("currentTime.year   1900").getValue(entity);
String secondHobby = (String) parser.parseExpression("hobbies[1]").getValue(entity);
String firstAlias = (String) parser.parseExpression("alias[0]").getValue(entity);
String height = (String) parser.parseExpression("info[height]").getValue(entity);
```

## 인라인 리스트, 맵, 배열

```java
List list = (List) parser.parseExpression("{1,2,3,4}").getValue();
Map map = (Map) parser.parseExpression("{name:{first:'Nikola',last:'Tesla'},dob:{day:10,month:'July',year:1856}}").getValue();

int[] numbers = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue();
int[][] numbers2 = (int[][]) parser.parseExpression("new int[4][5]").getValue(); //다차원 배열은 초기화 불가능
```

## 메서드

```java
String bc = parser.parseExpression("'abc'.substring(1, 3)").getValue(String.class);
boolean isBeautiful = (boolean) parser.parseExpression("isBeautiful()").getValue(entity);
```

## 연산자

관계, 논리, 수학, 할당 연산자 지원

## 타입

```java
Class dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);

Class stringClass = parser.parseExpression("T(String)").getValue(Class.class);

boolean trueValue = parser.parseExpression(
        "T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR")
        .getValue(Boolean.class);
```

## 생성자

```java
Inventor einstein = p.parseExpression(
        "tor.Inventor('Albert Einstein', 'German')")
        .getValue(Inventor.class);

// create new Inventor instance within the add() method of List
p.parseExpression(
        "Members.add(tor.Inventor(
            'Albert Einstein', 'German'))").getValue(societyContext);
```

## 변수

```java
// create an array of integers
List<Integer> primes = new ArrayList<Integer>();
primes.addAll(Arrays.asList(2,3,5,7,11,13,17));

// create parser and set variable 'primes' as the array of integers
ExpressionParser parser = new SpelExpressionParser();
EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataAccess();
context.setVariable("primes", primes);

// all prime numbers > 10 from the list (using selection ?{...})
// evaluates to [11, 13, 17]
List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
        "#primes.?[#this>10]").getValue(context);
```

## 함수

```java
public abstract class StringUtils {

    public static String reverseString(String input) {
        StringBuilder backwards = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i  ) {
            backwards.append(input.charAt(input.length() - 1 - i));
        }
        return backwards.toString();
    }
}
```

```java
ExpressionParser parser = new SpelExpressionParser();

EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
context.setVariable("reverseString",
        StringUtils.class.getDeclaredMethod("reverseString", String.class));

String helloWorldReversed = parser.parseExpression(
        "#reverseString('hello')").getValue(context, String.class);
```

## 그 외 지원기능

- 빈 참조
- 삼항 연산자
- 엘비스 연산자
- 안전탐색 연산자
- 컬랙션 선택
- 컬랙션 프로젝션
- 표현식 템플릿
-