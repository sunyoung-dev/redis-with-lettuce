# Spring-data-redis : Connection
spring-data-redis를 사용하지 않고 Lettuce로 redis 연결을 하기 위해 spring-data-redis에서는 어떻게 Connection을 관리하고 있는지 알아보자.  

### Connection 관리

1. RedisConnection은 ResourceHolder의 구현체인 RedisConnectionHolder로 관리된다. 이 holder는 RedisConnectionUtils 안에 선언된 내부 클래스이며 RedisConnection을 가져오고 등록하는 책임은 RedisConnectionUtils에 있다.  
2. TransactionalSynchronizationManager에 RedisConnectionFactory를 키로하고 RedisConnectionHolder를 값으로 등록한다. 때문에 RedisConnection을 얻으려면 키 값인 RedisConnectionFactory가 필요하다. (ResourceHolder로 관리하는 이유는 TransactionalSynchronizationManager를 이용하기 위함인 것 같다)  
3. RedisTemplate에서는 RedisConnectionFactory를 인자로 받아 RedisConnectionHolder에 등록하여 사용한다.   
4. 사용자가 신경쓸 부분은 RedisTemplate 하나이다. 사용하고 싶은 RedisURI 별로 RedisTemplate을 빈으로 등록해서 사용하면 된다. 각 RedisTemplate에는 Factory를 만들어 프로퍼티로 세팅한다.   
5. RedisTemplate은 RedisConnectionFactory를 세팅하고 가져올 수 있는 RedisAccessor를 상속 받았다. RedisTemplate에서는 RedisAccessor를 통해 얻은 Factory를 가지고 RedisConnectionUtils를 통해 RedisConnection을 다루는데, 이때 옵션에 따라 transaction, pipeline을 사용할 수도 있다.   

결국 트랜잭션을 사용하기 위해 RedisConnection을 RedisConnectionHolder로 만들어 보관했다고 볼 수 있다. Transaction을 사용하지 않아도 되는 상황이라면 Connection을 어떻게 관리해야 할까? Bean으로 만들어도 좋은 것일까?

### Connection에 관해 더 알아보기

* Application side
  * [x] Connection pool은 어떤 수요에 의해 개발되었나
  * [x] Connection pool은 왜 여러개의 connection을 보관하나
  * [x] 왜 Connection은 쓰고 닫아야 하나
  * [ ] Connection을 빈으로 생성해서 재활용해도 되나
* Redis side
  * [x] Timeout의 종류
  * [x] Connection을 어떻게 관리하나
  * [x] Redis가 Single Thread라는게 무슨 뜻인가
* Lettuce side
  * [x] 왜 Connection Pool 사용을 추천하지 않을까
  * [x] RedisURI의 timeout 값
  * [ ] Thread-safe한 Lettuce Connection의 뜻

#### Application Side

1. Connection Pool은 어떤 수요에 의해 개발되었나  

connection을 맺는 작업에 시간과 리소스가 많이 들기 때문이다. 필요할 때마다 connection 생성하는 비용을 절약하기 위해 채connection을 미리 여러개 생성해 보관해두고 필요할 때마다 꺼내서 쓴다. 음식을 먹을 때 마다 접시를 새로 사지 않는 것과 같다. connection을 쓸 때마다 connection 맺는 비용을 지불하지 않아도 이미 있는 connection을 가져다 쓸 수 있기 때문에 connection을 보관해둔다.  

2. Connection pool은 왜 여러개의 connection을 보관하나  

WAS는 Http request를 받을 때마다 Thread를 생성하고 그 스레드에서 로직이 실행된다. 이때 어플리케이션 로직에서 DB connection이 필요하다면 connection pool에서 가져다 쓴다. 여러 개의 요청이 동시에 들어올 때 지연없이 처리하기 위해 conneciton을 여러개 보관해둔다.  

3. 왜 Connection은 쓰고 닫아야하나  

DB 서버에 동시에 접속가능한 connection의 개수가 정해져있다. 처리가 끝났는데도 connection을 반납하지 않으면 DB에 계속 연결되어 connection 개수를 차지하게 된다. 이렇게 연결을 맺기만하고 반납하지 않으면 정작 새로 처리해야할 요청이 connection 수 부족으로 connection을 맺지 못해서 지연이 발생하거나 아예 멈추게 된다. 그래서 Connection을 사용하면 바로 연결을 끊어주거나 Connection Pool에 반납하여 재사용한다. (하지만 Lettuce의 connection은 Thread-safe 하게 개발되었기 때문에 한 개의 connection으로도 여러 Thread에서 사용할 수 있다)  

4. Connection을 bean으로 생성해서 재사용해도 되나  

사용해도 될 것 같다. 1. connection Pool에서 이미 connection을 재활용하고 있기 때문이고 2. 행여 connection이 끊어지더라도 lettuce의 기본 설정에 의해 auto-reconnect가 되어서 재사용은 문제 없을 것 이다. 다만 RedisURI가 다른 여러 개의 connection을 사용해야 할 때 각 connection을 빈으로 생성하기보다 connection을 관리해주는 오브젝트가 있으면 좋을 것 같긴하다. 흠🤔

#### Redis Side

1. Timeout의 종류

* timeout : client의 idle이 N초 동안 지속되면 connection을 닫는다 (0으로 주면 닫지 않는다)
* repl-timeout : 마스터 서버와 레플리카 서버 사이에 연결이 끊겼다고 인식하는 시간

2. Redis의 Connection 관리  

maxclients : 한번에 연결될 수 있는 최대 connection 수

3. Redis는 Single Thread  

한번에 하나의 명령만 처리한다. 

#### Lettuce Side

1. 왜 Connection Pool 사용을 추천하지 않을까  

Lettuce connection은 Thread-safe 해서 한 connection을 여러 Thread에서 공유할 수 있기 때문이다. Transaction을 사용하기 위해 여러 connection이 필요한 경우를 제외하고는 Connection Pool을 사용하는 이점이 없다.  

2. RedisURI에 넘겨주는 Timeout값은 무슨 값인가  

CommandTimeout으로, server의 응답시간이 N초 동안 지속되면 Exception을 발생시킨다  

---

### references

* https://github.com/spring-projects/spring-data-redis
* https://docs.oracle.com/cd/B28359_01/java.111/e10788/intro.htm#BABHFGCA
* https://lettuce.io/core/release/reference/#connection-pooling.is-connection-pooling-necessary
* https://github.com/spring-projects/spring-session/issues/789
* https://stackoverflow.com/questions/261683/what-is-the-meaning-of-the-term-thread-safe## reference
