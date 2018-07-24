# HomanNetwork

***

Author hosle
	
hosle@163.com

Created in : 24th July 2018

***

# Abstract

简单介绍Android网络模块引入Rx－Retrofit后，网络请求任务的新建、删除等管理操作方法。主要分为环境引用Retrofit、RxAndroid依赖库、网络请求任务类的创建、I／O数据实体的创建和任务仓库对网络任务的管理等四部分。

# v2 版本

## 1 Feature
* 基于"谁调用谁维护"的原则，删除单例仓库统一管理模式。
* 新增lambda高级函数作为异步结果回调。
* 优化url/form参数设置方式，支持每次请求前刷新同一task实例的公共参数。
* 以kotlin拓展函数方式实现多任务管理。


## 2 实现libnetwork.v2.RxHttpTask实例

创建**com.hosle.framework.libnetwork.v2.RxHttpTask**的子类，并实现对应方法

### 2.1 重写完整URL参数
将接口url 赋值给父类的url属性

### 2.2 设置URL和FORM参数

* 逐个设置参数

```
fun addURLParam(name: String, value: Any) {
}

fun addFormParam(name: String, value: Any) {
}
```

* 以Map方式传入所有参数

```
fun addURLParams(params: Map<String, Any>) {
}

fun addFormParams(params: Map<String, Any>) {
}
```

### 2.3 定义Retrofit Service 接口

定义请求方式、接口Path、返回的Observable类型等。

### 2.4 配置公共参数（建议配置在业务层的Task基类）

实现 

`override fun resetCommonFormParams(): Map<String, Any>?`

和

`override fun resetCommonUrlParams(): Map<String, Any>?`

方法


### 2.5 配置Cookie仓库（建议配置在业务层的Task基类）

实现createCookieStore()方法。

配置宿主的cookieStore

E.g.

```java
@Override
public CookieJar createCookieStore() {
    return new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
                return null;
        }
    };
}

```



### 2.6 参考例子

SearchBookTask.kt

```
import com.hosle.framework.libnetwork.v2.RxHttpTask

class SearchBookTask : RxHttpTask<SearchBookTask.RtfService,SearchBookModel>() {

    override var url: String = "https://api.douban.com/v2/book/search"

    init {
//        addURLParam("q", "python")
    }

    override fun doRequestObservable(): Observable<SearchBookModel> {
        return createService(url)
                .executeGet(getAllUrlParams())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    interface RtfService {
//        @FormUrlEncoded
//        @POST("v2/book/search")
//        fun executePost(@QueryMap(encoded = true) allQueries: Map<String, Any>,
//                        @FieldMap allFields: Map<String, Any>): Observable<SearchBookModel>

        @GET("v2/book/search")
        fun executeGet(@QueryMap(encoded = true) allQueries: Map<String, String>): Observable<SearchBookModel>
    }

    override fun resetCommonFormParams(): Map<String, Any>? {
        return hashMapOf<String, Any>("q" to "python")
    }
    
    override fun resetCommonUrlParams(): Map<String, Any>? {
        return null
    }


    override fun createCookieStore(): CookieJar {
        return object : CookieJar {
            override fun saveFromResponse(url: HttpUrl?, cookies: List<Cookie>?) {

            }

            override fun loadForRequest(url: HttpUrl?): List<Cookie> {
                return emptyList<Cookie>()
            }

        }
    }
}
```

### 3.6 Extra: 添加对通用错误码的处理

在继承 `com.hosle.framework.libnetwork.v2.RxHttpTask` 的业务父类中，重写高级函数的两个执行方法，代理实现其中的`onSuccessCallback` 函数

参考如下例子，在父类中，对未登录的异常码统一处理

```
override fun doGetData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((BaseResponseModel<M>) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {
        val commonOnSuccess  = object :((BaseResponseModel<M>) -> Unit){
            override fun invoke(result: BaseResponseModel<M>) {
                if(ERR_NUM_NOT_LOGIN == result.errno){
                    LoginManager.getInstance().logout(result.errmsg)
                }else {
                    onSuccessCallback?.invoke(result)
                }
            }
        }
        return super.doGetData(onStartCallback, onFinishCallback, commonOnSuccess, onFailureCallback)
    }

    override fun doPostData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((BaseResponseModel<M>) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {
        val commonOnSuccess  = object :((BaseResponseModel<M>) -> Unit){
            override fun invoke(result: BaseResponseModel<M>) {
                if(ERR_NUM_NOT_LOGIN == result.errno){
                    LoginManager.getInstance().logout(result.errmsg)
                }else {
                    onSuccessCallback?.invoke(result)
                }
            }
        }
        return super.doPostData(onStartCallback, onFinishCallback, commonOnSuccess, onFailureCallback)
    }
```


## 4 启动RxTask请求

### 4.1 调用方法

目前默认提供常用的get／post方法，支持interface回调，也支持Lambda回调

GET 请求

```
    override fun doGetData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((M) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {

    override fun doGetData(listener: OnSubscriberListener<M>): Subscription {

```

POST 请求

```
    override fun doPostData(onStartCallback: (() -> Unit)?, onFinishCallback: (() -> Unit)?, onSuccessCallback: ((M) -> Unit)?, onFailureCallback: ((Throwable) -> Unit)?): Subscription {

    override fun doPostData(listener: OnSubscriberListener<M>): Subscription {

```

### 4.2 参考例子

MainActivity.kt

```
class SearchBookActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
	    requestData()
	}
	private fun requestData() {
        searchBookTask.doGetData(
        	onSuccessCallback = { model -> onReqSuccess(model) }, 
        	onFailureCallback = { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() })
    }
}
```

## 5 取消网络请求

调用rxtask实例的cancel()方法，可以取消网络请求。

```
override fun cancel() {}
```

## 6 RxTask拓展多任务管理方法

每一个task实例，可以执行拓展方法，简单实现几种常用的多任务管理。

```java
    /**
     * 创建并激活2个task
     * @param task2
     * @param listener
     * @param <T>
     */
    fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiTasks(task2:T, listener: OnSubscriberListener<Any>){


    /**
     * 创建并激活2个task,同步获取结果
     * @param task2
     * @param customFunc2 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2:T,task3:T? = null,customFunc2:Func2<in Any?,in Any?,out Any>,customFunc3:Func3<in Any?,in Any?,in Any?,out Any>,listener: OnSubscriberListener<Any>){


    /**
     * 创建并激活3个task,同步获取结果
     * @param task2
     * @param task3
     * @param customfunc3 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2:T,task3:T,customFunc3:Func3<in Any?,in Any?,in Any?,out Any>,listener: OnSubscriberListener<Any>){


    /**
     * 创建并激活2个task,1个自定义observable事件，同步获取结果
     * @param task2
     * @param observable
     * @param customFunc3 实现合并所有结果的接口
     * @param listener 回调
     * @param <T>
     */
   fun <S,M,T:RxHttpTask<*,*>> RxHttpTask<S,M>.activateMultiSyncTasks(task2: T,observable: Observable<*>,customFunc3: Func3<in Any?, in Any?, in Any?, out Any>,listener: OnSubscriberListener<Any>){


    /**
     * 轮询任务
     * @param interval 相等间隔时间
     * @param maxRetries 循环最大次数
     * @param func1 中断轮询约束函数
     * @param subscriber 回调
     * @param <T>
     */
    fun <S,M> RxHttpTask<S,M>.activatePeriodicTask(interval:Long,maxRetries:Int,func1:Func1<M,Boolean>,subscriber: Subscriber<M>){


    /**
     * 轮询任务
     * @param task 同上
     * @param interval 间隔的时间组，支持每次触发间隔不同时间
     * @param maxRetries 同上
     * @param func1 同上
     * @param subscriber 同上
     * @param <T>
     */
    fun <S,M> RxHttpTask<S,M>.activatePeriodicTask(interval:LongArray,maxRetries:Int,func1:Func1<M,Boolean>,subscriber: Subscriber<M>){
```



# v1 版本


## 1	创建I／O数据实体Model与Service

首先，像以往一样按照api接口的数据结构，定义一个数据实体Model。然后，以Interface的方式，定义一个Rx－Retrofit的Service。
该Service定义了对api接口的请求方法，并以标注的形式定义api的URL、Query参数和Form参数。方法执行后，Retrofit会返回一个RxJava 的Obervable对象，泛型指定了数据实体Model的具体类型。

E.g

```java
public interface RxRtfService {

    @FormUrlEncoded
    
    @POST("{path}")
    
    Observable<ShopDishSuggestModel> executeSugTask(
    
    @Path("path") String path,
    
    @QueryMap Map<String,String> commonQueries,
    
    @FieldMap Map<String,String> commonFields,
    
    @Query("request_time") String request_time
    
    );
    
    }
```

特别需要注意的是，该文件如一般Model文件一样，不可以做混淆！

## 2.	实现RxHttpTask的子类

RxHttpTask是网络请求执行的抽象类，其主要工作为创建Retrofit、OkHttpClient的实例、对外提供Http请求参数的设置方法。Retrofit的Service类型则以泛型的方式传入。
实现RxHttpTask的子类，需要重写两个方法。一个是Service的实例创建方法，另一个是以RxJava的方式执行网络请求工作。

### 2.1	配置Cookie仓库

实现createCookieStore()方法。

配置宿主的cookieStore

E.g.

```java
@Override
public CookieJar createCookieStore() {
    return new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
                return null;
        }
    };
}

```

### 2.2 实现具体的网络请求方法

重写doRequestData(final OnSubscriberListener listener)方法。该方法创建Retrofit的Service实例，使用RxJava的方式指定线程执行策略、二次加工Response数据，最后返回一个Subscription。具体工作如下：

1. createService()：创建Retrofit的Service。
2. executeSugTask()：执行Service中定义的接口方法，Retrofit返回的Service实例承担RxJava中的Observable角色。
3. map()：RxJava操作符。二次加工Response返回的数据实体。该实体已经被Retrofit完成了Gson－Java类的转换。
4. subscribeOn()：RxJava方法。指定网络请求以及数据加工工作在IO线程执行。
5. unsubscribeOn()：RxJava方法。将当前工作线程切出IO线程。
6. observeOn()：RxJava方法。指定控件对数据的使用在UI主线程执行。
7. subscribe()：RxJava方法。创建带有listener的Subscriber实例，并执行subscribe方法。


E.g.

```java
@Override

public Subscription doRequestData(final OnSubscriberListener listener) {

return createService(URL)

	.executeSugTask(getUrlParams(), getFormParams())
            
            .map(modifyModel)
            
            .subscribeOn(Schedulers.io())
            
            .unsubscribeOn(Schedulers.io())
            
            .observeOn(AndroidSchedulers.mainThread())
            
            .subscribe(new CommonSubscriber<ShopDishSuggestModel>(listener));
            
}

```


### 2.3 需要在网络返回时获取到原始json string 的网络请求方法：

提供了重载方法 createService(String apiString, ResponseBodyStringListener listener)，listener为业务层实现。
注：该方法不会受Gson解析失败的影响。
E.g

```

@Override

    public Subscription doRequestData(OnSubscriberListener listener) {
        return createService(NetworkAPIs.BASE_HTTP_URL, responseBodyStringListener)
                .getAnnouncements(getFormParams())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new KnightCommonSubscriber<AnnouncementModel>(listener));
    }

```

### 2.4	创建操作符方法

对Response返回的Model执行二次加工操作。当Response返回的数据与UI控件上使用的要求不完全一致时，可以在RxJava通知Subscriber以前，对Model进行二次加工。RxJava的操作符类型非常丰富和灵活，也是RxJava的精髓之一。总的来说，操作符方法对Retrofit给出的Model实例进行线性处理，然后返回一个实例。新实例类型可以与以前保持一致，也可以是一个全新的类型。

E.g. 

```java
public Func1<ShopDishSuggestModel, ShopDishSuggestModel> modifyModel =

		new Func1<ShopDishSuggestModel, ShopDishSuggestModel>() {
        
        @Override
        
        public ShopDishSuggestModel call(ShopDishSuggestModel model) {
            
            //添加textInputBox中的关键词到Model中
            
            return model;
            
            }
            
};
```

## 3.	使用TasksRepository创建并管理网络任务

### 3.1 利用TasksRepository创建简单任务

以上工作完成后，网络请求能力已经就绪了。在最外层还引入了一个TasksRespository的单例，用来管理所有的网络请求。通过TasksRespository创建一个网络任务并启动，还需要传入一个Subscriber所使用的OnSubscriberListener。
OnSubscriberListener负责的工作类似于一个网络数据返回后的回调，它包含了onStart()、 onFinish()、 onSuccess() 和onFailure()四个方法。
其中onSuccess返回的就是我们最终想要的网络请求结果。方法体内执行具体的UI控件操作.


E.g.

```java

mTask = new SuggestionTask(getContext(), mParams, wd, getShopId());

TasksRepository.getInstance()

	.buildTask(mTask)
	
	.activateTask(new OnSubscriberListener<ShopDishSuggestModel>() {
	
	@Override
	
	public void onStart() {
	
	}
	
	@Override
	
	public void onFinish() {
	
	}
	
	@Override
	
	public void onSuccess(ShopDishSuggestModel model) {
	
	//使用model
	
	}
	
	@Override
	
	public void onFailure(Throwable t) {
	
	}
	
	});
	
}
```

### TaskRepository 支持多种任务管理

```java
    /**
     * 新建单一的task,删除相同的旧实例
     * @param task
     * @param <T>
     * @return
     */
    public <T extends RxHttpTask> TasksDataSource buildTask(@NonNull T task);

    public <T extends RxHttpTask> void saveTask(@NonNull T task);
    
    /**
     * 新建一个task,保留以前相同的任务实例
     * @param task
     * @param <T>
     * @return
     */
    public <T extends RxHttpTask> TasksDataSource buildTaskNonUnique(@NonNull T task);

    public <T extends RxHttpTask> void saveTaskNonUnique(@NonNull T task);

    /**
     * 创建并激活2个task
     * @param task1
     * @param task2
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiTasks(T task1, T task2, OnSubscriberListener listener);

    /**
     * 创建并激活2个task,同步获取结果
     * @param task1
     * @param task2
     * @param customFunc2 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, Func2 customFunc2, OnSubscriberListener listener);

    /**
     * 创建并激活3个task,同步获取结果
     * @param task1
     * @param task2
     * @param task3
     * @param customfunc3 实现合并所有结果的接口
     * @param listener
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, T task3, Func3 customfunc3, OnSubscriberListener listener);

    /**
     * 创建并激活2个task,1个自定义observable事件，同步获取结果
     * @param task1
     * @param task2
     * @param observable
     * @param customFunc3 实现合并所有结果的接口
     * @param listener 回调
     * @param <T>
     */
    public <T extends RxHttpTask> void activateMultiSyncTasks(T task1, T task2, Observable observable, Func3 customFunc3, OnSubscriberListener listener);


    /**
     * 轮询任务
     * @param task 网络任务实例
     * @param interval 相等间隔时间
     * @param maxRetries 循环最大次数
     * @param func1 中断轮询约束函数
     * @param subscriber 回调
     * @param <T>
     */
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber);

    /**
     * 轮询任务
     * @param task 同上
     * @param interval 间隔的时间组，支持每次触发间隔不同时间
     * @param maxRetries 同上
     * @param func1 同上
     * @param subscriber 同上
     * @param <T>
     */
    public <T extends RxHttpTask> void activatePeriodicTask(T task, final long[] interval, final int maxRetries, Func1<Object, Boolean> func1, final Subscriber subscriber);
    
    public void activateTask(OnSubscriberListener listener);

    public void deleteTask(@NonNull RxHttpTask task);

   
```