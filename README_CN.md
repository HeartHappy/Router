# KSP Router

[english](https://github.com/HeartHappy/Router/blob/master/README.md "english")

## 目录
- [配置](#三、配置)
- [使用详解](#四、使用详解)
- [高阶用法](#五、高阶用法)
- [ARouter 迁移到 Router](#六、ARouter 迁移到 Router)

### 一、简介：
使用KSP技术实现路由相关功能，主要提升编译性能，和解决ARouter与KSP兼容问题，本项目的功能主要参考阿里的ARouter。希望Router能在项目中给大家带来更好的体验

### 二、特点
#### 1、KSP特性优势：具体详见官方文档，在此不做详解
#### 2、支持标准的URL跳转，自动像目标页面注入参数
#### 3、支持多模块
#### 4、支持拦截器（多拦截，等级策略）
#### 5、支持PATH和URL的动态更改
#### 6、支持跳转携带参数和目标界面注入参数
#### 7、支持配置转场动画
#### 8、支持新老API跳转页面携带返回值
#### 9、支持目标类的实例化，多模块解耦（Fragment、ProvideService的子类实例化）
#### 10、支持启动服务


### 三、配置
#### 1、在build.gradle（Project）文件中加入
```groovy
plugins{
    id "com.google.devtools.ksp" version "2.0.10-1.0.24"
    id "org.jetbrains.kotlin.jvm" version "2.0.10"
}
```

#### 2、在 build.gradle（app） 文件中加入远程依赖和ksp插件
```groovy
//ksp插件
plugins {
    id 'com.google.devtools.ksp'
}
android{
    //JAVA和jvm设置为11或更高
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
}

dependencies {
	implementation('io.github.hearthappy:router-core:1.0.2')  
	ksp('io.github.hearthappy:router-compiler:1.0.2')
}
```

### 四、使用详解
##### 1、添加注解
```kotlin
@Route(path ="/launcher/main") 
class MainActivity : BaseActivity<ActivityMainBinding>(){/*...*/}

@Route("/model/fragment") 
class RouterFragment : AbsBaseFragment<FragmentRouterBinding>() {/*...*/}

@Route("/service/backend") 
class RouterService : Service() {/*...*/}
```
#### 2、启动路由
```kotlin
//path跳转Activity
Router.build("/launcher/main").navigation()

//uri跳转Activity
val uri = Uri.parse("hearthappy://kotlin.ksp.com/model2/ui?name=Uri jump to Modules2Activity &age=18")  
Router.build(uri).navigation()

//启动服务
 Router.build("/service/backend").navigation()
```
#### 3、携带参数跳转
```kotlin
Router.build("/case/inject")  
     .withObject("withObject", UserBean("Labubu", "987654"))  
     .withString("withString", "Labubu")  
     .withInt("withInt", 123456)  
     .withBoolean("withBoolean", true)  
     .withShort("withShort", 1234)  
     .withLong("withLong", 1234567890)  
     .withFloat("withFloat", 123.456f)  
     .withDouble("withDouble", 123.4567890)  
     .withByte("withByte", 123)  
     .withChar("withChar", 'b')  
     .withCharSequence("withCharSequence", "from charSequence")  
     .withParcelable("withParcelable", ParcelableBean("Labubu", "123456"))  
     .withSerializable("withSerializable", LoginBean("Labubu", "123456"))  
     .withBundle("withBundle", Bundle().apply { putString("withBundle", "bundle data") })  
     .withIntArray("withIntArray", intArrayOf(1, 2, 3))  
     .withByteArray("withByteArray", byteArrayOf(1, 2, 3))  
     .withShortArray("withShortArray", shortArrayOf(1, 2, 3))  
     .withCharArray("withCharArray", charArrayOf('a', 'b', 'c'))  
     .withFloatArray("withFloatArray", floatArrayOf(1f, 2f, 3f))  
     .withDoubleArray("withDoubleArray", doubleArrayOf(1.0, 2.0, 3.0))  
     .withBooleanArray("withBooleanArray", booleanArrayOf(true, false))  
     .withStringArray("withStringArray", arrayOf("a", "b", "c"))  
     .withLongArray("withLongArray", longArrayOf(1, 2, 3))  
     .withCharSequenceArray("withCharSequenceArray", arrayOf("a", "b", "c"))  
     .withParcelableArray("withParcelableArray", arrayOf(ParcelableBean("Labubu", "123456"), ParcelableBean("Alibaba", "123456")))  
     .withSparseParcelableArray("withSparseParcelableArray", SparseArray<Parcelable>().apply { put(0, ParcelableBean("Labubu", "123456")) })  
     .withParcelableArrayList("withParcelableArrayList", arrayListOf(ParcelableBean("Labubu", "123456"), ParcelableBean("Alibaba", "123456")))  
     .withIntArrayList("withIntArrayList", arrayListOf(1, 2, 3))  
     .withStringArrayList("withStringArrayList", arrayListOf("a", "b", "c"))  
     .withCharSequenceArrayList("withCharSequenceArrayList", arrayListOf("a", "b", "c"))  
     .withTransition(R.anim.window_bottom_in, R.anim.window_bottom_out)  
     .withOptionsCompat(ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, view.width, view.height))  
     .navigation()
```
#### 4、目标页面注入参数（支持lateinit、Delegates这种延迟或委托函数，自动注入会初始化默认值）

```kotlin
import java.io.Serializable

@Autowired var withObject: UserBean? = null  
  
@Autowired var withString: String?=null  
  
@Autowired var withInt: Int=-1  
  
@Autowired var withBoolean: Boolean=false  
  
@Autowired var withShort: Short = -1  
  
@Autowired var withLong: Long = -1  
  
@Autowired var withFloat: Float = -1f  
  
@Autowired var withDouble: Double = -1.0  
  
@Autowired var withChar: Char = 'a'  
  
@Autowired var withByte: Byte = -1  
  
@Autowired var withCharSequence: CharSequence?=""  
  
@Autowired var withParcelable: Parcelable? = null  
  
@Autowired var withSerializable: Serializable? = null  
  
@Autowired var withBundle: Bundle? = null  
  
//array  
@Autowired var withByteArray: ByteArray? = null  
  
@Autowired var withCharArray: CharArray? = null  
  
@Autowired var withShortArray: ShortArray? = null  
  
@Autowired var withIntArray: IntArray? = null  
  
@Autowired var withLongArray: LongArray? = null  
  
@Autowired var withFloatArray: FloatArray? = null  
  
@Autowired var withDoubleArray: DoubleArray? = null  
  
@Autowired var withBooleanArray: BooleanArray? = null  
  
@Autowired var withStringArray: Array<String>? = null  
  
@Autowired var withCharSequenceArray: Array<CharSequence>? = null  
  
@Autowired var withParcelableArray: Array<Parcelable>? = null  
  
//list  
@Autowired var withParcelableArrayList: ArrayList<Parcelable>? = null  
  
@Autowired var withSparseParcelableArray: SparseArray<out Parcelable>? = null  
  
@Autowired var withIntArrayList: ArrayList<Int>? = null  
  
@Autowired var withStringArrayList: ArrayList<String>? = null  
  
@Autowired var withCharSequenceArrayList: ArrayList<CharSequence>? = null  
  
@Autowired var helloService: HelloService? = null
```

#### 5、实例化
```kotlin
//Fragment
val fragment = Router.build("/model/fragment").getInstance() as Fragment  
val beginTransaction = supportFragmentManager.beginTransaction()  
beginTransaction.add(R.id.fragmentLayout, fragment)  
beginTransaction.commit()
```
#### 6、日志
```kotlin
class MyApp : Application() {  
    override fun onCreate() {  
        super.onCreate()  
        //路由执行日志
        if (BuildConfig.DEBUG) Router.openLog()  
    }  
}
```
添加到build.gradle文件中
```groovy
ksp {  
    arg("enableRouterLog", "true")  //编译时日志 ，默认关闭
}
```
#### 7、添加混淆规则

```
-keep class com.hearthappy.router.generate.interceptor.** { *; }  
-keep class com.hearthappy.router.generate.routes.** { *; }  
-keep class com.hearthappy.router.generate.path.** { *; }
```
### 五、高阶用法
#### 1、对象序列化
```kotlin
//如果需要传入自定义对象，请创建一个类实现SerializationService接口，并使用@Route注解。例如：
@Route(RouterPath.SERVICE_JSON)  
class JsonService:SerializationService {  
    private val gson=Gson()  
    override fun toJson(instance: Any?): String? {  
       return gson.toJson(instance)  
    }  
  
    override fun <T> fromJson(input: String?, clazz: Type?): T {  
       return gson.fromJson(input,clazz)  
    }  
}
```

#### 2、拦截器

```kotlin
//priority:值越小，优先执行。
@Interceptor(priority = 1, name = "RoomInterceptor") 
class RoomInterceptor : IInterceptor {  
    override fun intercept(sorter: Sorter, callback: InterceptorCallback) {  
        if (sorter.getPath() == RouterPath.CASE_ACTIVITY_FOR_RESULT) {  
            Log.d("RoomInterceptor", "intercept: Execute Interceptor 2")  
            /**
			* onContinue：继续执行路由、不在执行后续拦截器
			* onInterrupt：中断路由跳转， 后续拦截器也将被中断
			* onContinue和onInterrupt都不使用，则按照priority等级顺序执行所有拦截器，在启动路由
			*/
            callback.onContinue(sorter)  
        }  
        callback.onContinue(sorter)  
    }  
}
```
#### 3、重写URL或Path
```kotlin
@Route(RouterPath.SERVICE_PATH_REPLACE) 
class PathReplaceImpl : PathReplaceService {  
    override fun forString(path: String): String {  
        // Custom logic
        return path  
    }  
  
    /**  
     * test uri：hearthappy://kotlin.ksp.com/model2/ui?name=KSP Router!&age=18  
     * @param uri Uri  
     * @return Uri  
     */    
     override fun forUri(uri: Uri): Uri {  
        if (uri.path == RouterPath.MODULES2_UI) {  
            return uri.buildUpon().apply {  
                clearQuery()  
                appendQueryParameter("name", "KSP Router!")  
                appendQueryParameter("age", "30")  
            }.build()  
        }  
        return uri  
    }  
}
```
#### 4、提供服务
```kotlin
//开放服务
interface HelloService: ProviderService {  
  
    fun sayHello(name: String): String  
}

@Route(RouterPath.SERVICE_HELLO)  
class HelloServiceImpl:HelloService {  
    override fun sayHello(name: String): String {  
  
        return "Hello $name"  
    }  
}
```

```kotlin
//发现服务
//方式一：  
@Autowired var helloService: HelloService? = null  

override fun onCreate(savedInstanceState: Bundle?) {  
    Router.inject(this)  
    super.onCreate(savedInstanceState)  
    helloService.sayHello("KSP Router")
}

//方式二：  
val helloService1 = Router.build(RouterPath.SERVICE_HELLO).getInstance() as HelloService  
val sayHello1 = helloService1.sayHello("KSP Router")

//方式三：
val helloService2 = Router.getInstance(HelloService::class.java)
val sayHello2 = helloService2.sayHello("KSP Router")
```
#### 5、处理跳转结果

```kotlin
//新版api
//1、注册
private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->  ...}

//2、跳转
activityResultLauncher.launch(Intent(this@ForResultActivity, Router.build(RouterPath.MODULES2_UI).getDestination()))  
```

```kotlin
//旧版api
//1、注册
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
//2、跳转
Router.build(RouterPath.MODULES2_UI).navigation(this@ForResultActivity, 100, object : NavigationCallback {  
    override fun onFound(sorter: Sorter) {  
        Log.d(TAG, "onFound: ${sorter.getPath()}")  
    }  
  
    override fun onLost(sorter: Sorter) {  
        Log.d(TAG, "onLost: ${sorter.getPath()}")  
    }  
  
    override fun onArrival(sorter: Sorter) {  
        Log.d(TAG, "onArrival: ${sorter.getPath()}")  
    }  
  
    override fun onInterrupt(sorter: Sorter) {  
        Log.d(TAG, "onInterrupt: ${sorter.getPath()}")  
    }  
})
```

### 六、ARouter 迁移到 Router

#### 一、按下快捷键ctrl+shift+R，搜索 xxx 并替换为 yyy ,然后点击 Replace All
##### 1、包路径替换表
| ARouter代码 (原) | Router代码 (替换后) |
|------------------|---------------------|
| `com.alibaba.android.arouter.facade.annotation.Route` | `com.hearthappy.router.annotations.Route` |
| `com.alibaba.android.arouter.facade.annotation.Interceptor` | `com.hearthappy.router.annotations.Interceptor` |
| `com.alibaba.android.arouter.facade.annotation.Autowired` | `com.hearthappy.router.annotations.Autowired` |
| `com.alibaba.android.arouter.facade.template.IProvider` | `com.hearthappy.router.service.ProviderService` |
| `com.alibaba.android.arouter.facade.service.PathReplaceService` | `com.hearthappy.router.service.PathReplaceService` |
| `com.alibaba.android.arouter.facade.service.SerializationService` | `com.hearthappy.router.service.SerializationService` |
| `com.alibaba.android.arouter.facade.Postcard` | `com.hearthappy.router.launcher.Sorter` |
| `com.alibaba.android.arouter.facade.callback.InterceptorCallback` | `com.hearthappy.router.interfaces.InterceptorCallback` |
| `com.alibaba.android.arouter.facade.template.IInterceptor` | `com.hearthappy.router.interfaces.IInterceptor` |

##### 2、类名替换表
| ARouter代码 (原) | Router代码 (替换后) |
|------------------|---------------------|
| `Postcard` | `Sorter` |

##### 3、使用方法替换示例
| ARouter代码 (原) | Router代码 (替换后) |
|------------------|---------------------|
| `ARouter.getInstance()` | `Router` |




#### 二、实例化相关替换为Router.getInstance(xxx) 或 Router.build("path").getInstance() as (Your class) 
