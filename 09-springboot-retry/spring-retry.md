## spring retry

在调用第三方接口或者使用mq时，会出现网络抖动，连接超时等网络异常，所以需要重试。
为了使处理更加健壮并且不太容易出现故障，后续的尝试操作，有时候会帮助失败的操作最后执行成功。
例如，由于网络故障或数据库更新中的DeadLockLoserException导致Web服务或RMI服务的远程调用
可能会在短暂等待后自行解决。 

**为了自动执行这些操作的重试，Spring Batch具有RetryOperations策略。
不过该重试功能从Spring Batch 2.2.0版本中独立出来，变成了Spring Retry模块。**

spring-retry项目实现了重试和熔断功能，目前已用于SpringBatch、Spring Integration等项目。

### 1 Spring Retry介绍
#### 依赖

```xml
 <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
```
#### RetryOperations
Spring retry是Spring提供的一种重试机制的解决方案。它内部抽象了一个RetryOperations接口，其定义如下。

```java
package org.springframework.retry;

public interface RetryOperations {
    <T, E extends Throwable> T execute(RetryCallback<T, E> var1) throws E;

    <T, E extends Throwable> T execute(RetryCallback<T, E> var1, RecoveryCallback<T> var2) throws E;

    <T, E extends Throwable> T execute(RetryCallback<T, E> var1, RetryState var2) throws E, ExhaustedRetryException;

    <T, E extends Throwable> T execute(RetryCallback<T, E> var1, RecoveryCallback<T> var2, RetryState var3) throws E;
}
```


定义了几个重载的execute()，它们之间的差别就在于RetryCallback、RecoveryCallback、RetryState，
其中核心参数是RetryCallback。

#### RetryCallback
```java
package org.springframework.retry;


public interface RetryCallback<T, E extends Throwable> {
    /**
    * 该方法的返回值就是RetryOperations的execute()的返回值，
    * RetryCallback的范型参数中定义的Throwable是中执行可重试方法时可抛出的异常，可由外部进行捕获。
    */
	T doWithRetry(RetryContext context) throws E;
}
```

#### RecoveryCallback

当RetryCallback不能再重试的时候，如果定义了RecoveryCallback，
就会调用RecoveryCallback，并以其返回结果作为execute()的返回结果。

```java
package org.springframework.retry;

public interface RecoveryCallback<T> {

	T recover(RetryContext context) throws Exception;

}
```
#### RetryContext 重试上下文
RetryCallback和RecoverCallback定义的接口方法都可以接收一个RetryContext参数，
通过它可以获取到尝试次数，也可以通过其setAttribute()和getAttribute()来传递一些信息。

Spring Retry包括有状态的重试和无状态的重试，对于有状态的重试，它主要用来提供一个用于在RetryContextCache中
保存RetryContext的Key，这样可以在多次不同的调用中应用同一个RetryContext
（无状态的重试每次发起调用都是一个全新的RetryContext，
在整个重试过程中是一个RetryContext，其不会进行保存。有状态的重试因为RetryContext是保存的，
其可以跨或不跨线程在多次execute()调用中应用同一个RetryContext）。

#### RetryTemplate

Spring Retry提供了一个RetryOperations的实现，RetryTemplate，通过它我们可以发起一些可重试的请求。
其内部的重试机制通过RetryPolicy来控制。RetryTemplate默认使用的是SimpleRetryPolicy实现，SimpleRetryPolicy只是简单的控制尝试几次，包括第一次调用。
RetryTemplate默认使用的是尝试3次的策略。

#### RetryTemplate 入门Demo

`study.wyy.springboot.retry.test.RetryTemplateTest`

#### RetryPolicy
RetryTemplate内部的重试策略是由RetryPolicy控制的。RetryPolicy的定义如下。

```java
/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.retry;

import java.io.Serializable;

public interface RetryPolicy extends Serializable {

	boolean canRetry(RetryContext context);
	
	RetryContext open(RetryContext parent);

	void close(RetryContext context);

	void registerThrowable(RetryContext context, Throwable throwable);

}
```
#### SimpleRetryPolicy
RetryTemplate内部默认时候用的是SimpleRetryPolicy。**SimpleRetryPolicy默认将对所有异常进行尝试，**
最多尝试3次。如果需要调整使用的RetryPolicy，**可以通过RetryTemplate的setRetryPolicy()进行设置**。

> 如果希望最多尝试10次，只需要传入构造参数10即可，比如下面这样。
```java
RetryPolicy retryPolicy = new SimpleRetryPolicy(10);
```
>在实际使用的过程中，可能你不会希望所有的异常都进行重试
>通过SimpleRetryPolicy的构造参数可以指定哪些异常是可以进行重试的。

```java
Map<Class<? extends Throwable>, Boolean> retryableExceptions = Maps.newHashMap();
retryableExceptions.put(IllegalStateException.class, true);
RetryPolicy retryPolicy = new SimpleRetryPolicy(10, retryableExceptions);
RetryTemplate retryTemplate = new RetryTemplate();
retryTemplate.setRetryPolicy(retryPolicy);
```

看到这里可能你会有疑问，可以进行重试的异常定义为什么使用的是Map结构，
而不是简单的通过Set或List来定义可重试的所有异常类似，
而要多一个Boolean类型的Value来定义该异常是否可重试。这样做的好处是它可以实现包含/排除的逻辑，

```java
public static void testSimpleRetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap();
        // 我们可以指定对所有的RuntimeException都是可重试的，唯独IllegalArgumentException是一个例外。
        retryableExceptions.put(RuntimeException.class, true);
        retryableExceptions.put(IllegalArgumentException.class, false);
        // 指定尝试10次
        RetryPolicy retryPolicy = new SimpleRetryPolicy(10, retryableExceptions);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        AtomicInteger counter = new AtomicInteger();
        retryTemplate.execute(retryContext -> {
            if (counter.incrementAndGet() < 3) {
                throw new IllegalStateException();
            } else if (counter.incrementAndGet() < 6) {
                // 由于IllegalArgumentException不会重试，所以最终会抛出这个异常
                throw new IllegalArgumentException();
            }
            return counter.get();
        });
    }

```

SimpleRetryPolicy在判断一个异常是否可重试时，默认会取最后一个抛出的异常。
我们通常可能在不同的业务层面包装不同的异常，
比如有些场景我们可能需要把捕获到的异常都包装为ServiceException，
比如说把一个IllegalStateException包装为BusinessException。
我们程序中定义了所有的IllegalStateException是可以进行重试的，
如果SimpleRetryPolicy直接取的最后一个抛出的异常会取到ServiceException。
这可能不是我们想要的，此时可以通过构造参数traverseCauses指定可以遍历异常栈上的每一个异常进行判断。
比如下面代码，在traverseCauses=false时，
只会在抛出IllegalStateException时尝试3次，
第四次抛出的Exception不是RuntimeException，
所以不会进行重试。指定了traverseCauses=true时第四次尝试时抛出的Exception，
再往上找时会找到IllegalArgumentException，此时又可以继续尝试，所以最终执行后counter的值会是6

```java

public void testSimpleRetryPolicy() throws Exception {
  Map<Class<? extends Throwable>, Boolean> retryableExceptions = Maps.newHashMap();
  retryableExceptions.put(RuntimeException.class, true);
  RetryPolicy retryPolicy = new SimpleRetryPolicy(10, retryableExceptions, true);
  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setRetryPolicy(retryPolicy);
  AtomicInteger counter = new AtomicInteger();
  retryTemplate.execute(retryContext -> {
    if (counter.incrementAndGet() < 3) {
      throw new IllegalStateException();
    } else if (counter.incrementAndGet() < 6) {
      try {
        throw new IllegalArgumentException();
      } catch (Exception e) {
        throw new Exception(e);
      }
    }
    return counter.get();
  });
}

```

#### AlwaysRetryPolicy
顾名思义就是一直重试，直到成功为止。
#### NeverRetryPolicy
与AlwaysRetryPolicy相对的一个极端是从不重试，NeverRetryPolicy的策略就是从不重试，但是第一次调用还是会发生的。

#### TimeoutRetryPolicy
TimeoutRetryPolicy用于在指定时间范围内进行重试，直到超时为止，默认的超时时间是1000毫秒。

#### ExceptionClassifierRetryPolicy
如果你需要基于不同的异常应用不同的重试策略怎么办呢？
ExceptionClassifierRetryPolicy可以帮你实现这样的需求。

下面的代码中我们就指定了当捕获的是IllegalStateException时将最多尝试5次，
当捕获的是IllegalArgumentException时将最多尝试4次。

```java

public static void testRetryPolicy() throws Exception {
  ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();

  Map<Class<? extends Throwable>, RetryPolicy> policyMap = Maps.newHashMap();
  policyMap.put(IllegalStateException.class, new SimpleRetryPolicy(5));
  policyMap.put(IllegalArgumentException.class, new SimpleRetryPolicy(4));
  retryPolicy.setPolicyMap(policyMap);

  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setRetryPolicy(retryPolicy);
  AtomicInteger counter = new AtomicInteger();
  retryTemplate.execute(retryContext -> {
    if (counter.incrementAndGet() < 5) {
      throw new IllegalStateException();
    } else if (counter.get() < 10) {
      throw new IllegalArgumentException();
    }
    return counter.get();
  });
}
```
> 其执行结果最终是抛出IllegalArgumentException的，
> 但是在最终抛出IllegalArgumentException时counter的值是多少呢？
> 换句话说它一共尝试了几次呢？答案是8次。
> 进行第5次尝试时不会抛出IllegalStateException，
> 而是抛出IllegalArgumentException，
> 它对于IllegalArgumentException的重试策略而言是第一次尝试，
> 之后会再尝试3次，5+3=8，所以counter的最终的值是8。

#### CircuitBreakerRetryPolicy

CircuitBreakerRetryPolicy是包含了断路器功能的RetryPolicy，
它内部默认包含了一个SimpleRetryPolicy，最多尝试3次。
在固定的时间窗口内（默认是20秒）如果底层包含的RetryPolicy的尝试次数都已经耗尽了，
则其会打开断路器，默认打开时间是5秒，在这段时间内如果还有其它请求过来就不会再进行调用了。

```java
@Test
public void testCircuitBreakerRetryPolicy() throws Exception {
  CircuitBreakerRetryPolicy retryPolicy = new CircuitBreakerRetryPolicy();
  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setRetryPolicy(retryPolicy);
  AtomicInteger counter = new AtomicInteger();
  RetryState retryState = new DefaultRetryState("key");
  for (int i=0; i<5; i++) {
    try {
      retryTemplate.execute(retryContext -> {
        System.out.println(LocalDateTime.now() + "----" + counter.get());
        TimeUnit.MILLISECONDS.sleep(100);
        if (counter.incrementAndGet() > 0) {
          throw new IllegalStateException();
        }
        return 1;
      }, null, retryState);
    } catch (Exception e) {
      
    }
  }
}
```
> CircuitBreakerRetryPolicy需要跟RetryState一起使用，上面的代码中RetryTemplate使用的是
CircuitBreakerRetryPolicy，一共调用了5次execute()，
每次调用RetryCallback都会抛出IllegalStateException，
并且会打印counter的当前值，前三次RetryCallback都是可以运行的，
之后断路器打开了，第四五次执行execute()时就不会再执行RetryCallback了，
所以你只能看到只进行了3次打印。
  
断路器默认打开的时间是5秒，5秒之后断路器又会关闭，RetryCallback又可以正常调用了。
判断断路器是否需要打开的时间窗口默认是20秒，即在20秒内所有的尝试次数都用完了，就会打开断路器。
如果在20秒内只尝试了两次（默认3次），则在新的时间窗口内尝试次数又将从0开始计算。
可以通过如下方式进行这两个时间的设置。

```java
  SimpleRetryPolicy delegate = new SimpleRetryPolicy(5);
  //底层允许最多尝试5次
  CircuitBreakerRetryPolicy retryPolicy = new CircuitBreakerRetryPolicy(delegate);
  retryPolicy.setOpenTimeout(2000);//断路器打开的时间
  retryPolicy.setResetTimeout(15000);//时间窗口

```
#### CompositeRetryPolicy
CompositeRetryPolicy可以用来组合多个RetryPolicy，
可以设置必须所有的RetryPolicy都是可以重试的时候才能进行重试，
也可以设置只要有一个RetryPolicy可以重试就可以进行重试。
默认是必须所有的RetryPolicy都可以重试才能进行重试。

下面代码中应用的就是CompositeRetryPolicy，它组合了两个RetryPolicy，
最多尝试5次的SimpleRetryPolicy和超时时间是2秒钟的TimeoutRetryPolicy，
所以它们的组合就是必须尝试次数不超过5次且尝试时间不超过2秒钟才能进行重试。
```java
public void testCompositeRetryPolicy() {
  CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
  RetryPolicy policy1 = new SimpleRetryPolicy(5);
  TimeoutRetryPolicy policy2 = new TimeoutRetryPolicy();
  policy2.setTimeout(2000);
  RetryPolicy[] policies = new RetryPolicy[]{policy1, policy2};
  compositeRetryPolicy.setPolicies(policies);

  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setRetryPolicy(compositeRetryPolicy);
  AtomicInteger counter = new AtomicInteger();
  retryTemplate.execute(retryContext -> {
    if (counter.incrementAndGet() < 10) {
      throw new IllegalStateException();
    }
    return counter.get();
  });
}
```
CompositeRetryPolicy也支持组合的RetryPolicy中只要有一个RetryPolicy满足条件就可以进行重试，
这是通过参数optimistic控制的，默认是false，改为true即可。

#### BackOffPolicy
BackOffPolicy用来定义在两次尝试之间需要间隔的时间，
RetryTemplate内部默认使用的是NoBackOffPolicy，
其在两次尝试之间不会进行任何的停顿。对于一般可重试的操作往往是基于网络进行的远程请求，
它可能由于网络波动暂时不可用，如果立马进行重试它可能还是不可用，
但是停顿一下，过一会再试可能它又恢复正常了，所以在RetryTemplate中使用BackOffPolicy往往是很有必要的。

#### FixedBackOffPolicy
FixedBackOffPolicy将在两次重试之间进行一次固定的时间间隔，默认是1秒钟，也可以通过setBackOffPeriod()进行设置。

```java
public void testFixedBackOffPolicy() {

  FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
  backOffPolicy.setBackOffPeriod(1000);
  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setBackOffPolicy(backOffPolicy);

  long t1 = System.currentTimeMillis();
  long t2 = retryTemplate.execute(retryContext -> {
    if (System.currentTimeMillis() - t1 < 1000) {
      throw new IllegalStateException();
    }
    return System.currentTimeMillis();
  });
  Assert.assertTrue(t2 - t1 > 1000);
  Assert.assertTrue(t2 - t1 < 1100);
}

```
#### ExponentialBackOffPolicy
ExponentialBackOffPolicy可以使每一次尝试的间隔时间都不一样，
它有3个重要的参数，初始间隔时间、后一次间隔时间相对于前一次间隔时间的倍数和最大的间隔时间，
它们的默认值分别是100毫秒、2.0和30秒。
```java
// 指定了初始间隔时间是1000毫秒，每次间隔时间以2倍的速率递增，最大的间隔时间是5000毫秒，它最多可以尝试10次。所以当第1次尝试失败后会间隔1秒后进行第2次尝试，
// 之后再间隔2秒进行第3次尝试，之后再间隔4秒进行第4次尝试，
public void testExponentialBackOffPolicy() {
  ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
  backOffPolicy.setInitialInterval(1000);
  backOffPolicy.setMaxInterval(5000);
  backOffPolicy.setMultiplier(2.0);
  RetryTemplate retryTemplate = new RetryTemplate();
  retryTemplate.setBackOffPolicy(backOffPolicy);
  int maxAttempts = 10;
  retryTemplate.setRetryPolicy(new SimpleRetryPolicy(maxAttempts));

  long t1 = System.currentTimeMillis();
  long t2 = retryTemplate.execute(retryContext -> {
    if (retryContext.getRetryCount() < maxAttempts-1) {//最后一次尝试会成功
      throw new IllegalStateException();
    }
    return System.currentTimeMillis();
  });
  long time = 0 + 1000 + 1000 * 2 + 1000 * 2 * 2 + 5000 * (maxAttempts - 4);
  Assert.assertTrue((t2-t1) - time < 100);
}

```

#### ExponentialRandomBackOffPolicy
ExponentialRandomBackOffPolicy的用法跟ExponentialBackOffPolicy的用法是一样的，
它继承自ExponentialBackOffPolicy，在确定间隔时间时会先按照ExponentialBackOffPolicy的方式确定一个时间间隔，
然后再随机的增加一个0-1的。比如取得的随机数是0.1即表示增加10%，
每次需要确定重试间隔时间时都会产生一个新的随机数。如果指定的初始间隔时间是100毫秒，增量倍数是2,最大间隔时间是2000毫秒，
则按照ExponentialBackOffPolicy的重试间隔是100、200、400、800,
而ExponentialRandomBackOffPolicy产生的间隔时间可能是111、256、421、980。
#### UniformRandomBackOffPolicy
UniformRandomBackOffPolicy用来每次都随机的产生一个间隔时间，默认的间隔时间是在500-1500毫秒之间。
可以通过setMinBackOffPeriod()设置最小间隔时间，通过setMaxBackOffPeriod()设置最大间隔时间。

####  监听器
RetryTemplate中可以注册一些RetryListener，它可以用来对整个Retry过程进行监听。
RetryListener的定义如下，它可以在整个Retry前、整个Retry后和每次Retry失败时进行一些操作。

```java
public interface RetryListener {

  /**
   * 在第一次尝试之前调用。如果方法的返回值是false，则不会进行尝试，反而会抛出TerminatedRetryException。
   *
   * @param <E> RetryCallback可抛出的异常类型
   * @param <T> RetryCallback的返回值类型
   * @param context 当前RetryContext.
   * @param callback 当前RetryCallback.
   * @return 如果需要继续尝试则返回true.
   */
  <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback);

  /**
   * 在最后一次尝试后调用，而不管最后一次尝试是成功的还是失败的。
   *
   * @param context 当前RetryContext.
   * @param callback 当前RetryCallback.
   * @param throwable RetryCallback抛出的最后一个异常.
   * @param <E> RetryCallback可抛出的异常类型
   * @param <T> RetryCallback的返回值类型
   */
  <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable);

  /**
   * 每一次尝试失败都会调用一次
   *
   * @param context 当前RetryContext.
   * @param callback 当前RetryCallback.
   * @param throwable RetryCallback抛出的最后一个异常.
   * @param <E> RetryCallback可抛出的异常类型
   * @param <T> RetryCallback的返回值类型
   */
  <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable);
}

```

#### 声明式的重试（使用注解）
##### @EnableRetry
开启重试
```java
@EnableRetry
@Configuration
public class RetryConfiguration {
  @Bean
  public HelloService helloService() {
    return new HelloService();
  }
}

```
这样就启用了声明式的重试机制，其会对使用了@Retryable标注的方法对应的bean创建对应的代理。
##### @Retryable
使用@Retryable标注的方法如果不特殊声明的话，默认最多可以尝试3次。
```java
public class HelloService {

  @Retryable
  public void hello(AtomicInteger counter) {
    if (counter.incrementAndGet() < 10) {
      throw new IllegalStateException();
    }
  }
}
```
@Retryable也可以加在Class上，当加在Class上时表示该bean所有的对外方法都是可以重试的。当Class上和方法上都加了@Retryable时，方法上的优先级更高。

- 默认的最大尝试次数是3次，可以通过maxAttempts属性进行自定义。
- 默认会对所有的异常进行重试，如有需要可以通过value和include属性指定需要重试的异常，也可以通过exclude属性指定不需要进行重试的异常。
- 可以通过backoff属性指定BackOffPolicy相关的信息，它对应一个@BackOff，默认使用的BackOffPolicy将每次都间隔1000毫秒，
    - 如果默认值不能满足要求可以通过@BackOff指定初始的间隔时间。
    - 可以通过@BackOff的multiplier属性指定间隔之间的倍数，默认是0,即每次都是固定的间隔时间。
    - 当指定了multiplier后可以通过maxDelay属性指定最大的间隔时间，默认是0，表示不限制，即取ExponentialBackOffPolicy的默认值30秒。
    
##### @Recover
使用注解的可重试方法，如果重试次数达到后还是继续失败的就会抛出异常，
可以通过@Recover标记同一Class中的一个方法作为RecoveryCallback。
@Recover标记的方法的返回类型必须与@Retryable标记的方法一样。方法参数可以与@Retryable标记的方法一致，也可以不带参数，带了参数就会传递过来。

@Recover标记的方法还可以选择包含一个Exception类型的参数，它对应于@Retryable标记的方法最后抛出的异常，如果需要包含异常参数该参数必须是第一个参数。当定义了多个@Recover方法时，Spring Retry将选择更精确的那一个。此时的RecoveryCallback将选择第二个helloRecover方法。
```java
@Retryable
public class HelloService {

  @Retryable(maxAttemptsExpression = "${retry.maxAttempts:5}",
          backoff = @Backoff(delayExpression = "${retry.delay:100}",
                  maxDelayExpression = "${retry.maxDelay:2000}",
                  multiplierExpression = "${retry.multiplier:2}"))
  public void hello(AtomicInteger counter) {
    if (counter.incrementAndGet() < 10) {
      throw new IllegalStateException();
    }
  }

  @Recover
  public void helloRecover(AtomicInteger counter) {
    counter.set(1000);
  }

  @Recover
  public void helloRecover(IllegalStateException e, AtomicInteger counter) {
    counter.set(2000);
  }

}
```

