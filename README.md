# KSP Router

[中文文档](https://github.com/HeartHappy/Router/blob/master/README_CN.md "查看中文版本")

## Table of Contents
- [Configuration](#configuration)
- [Detailed Usage](#detailed-usage)
- [Advanced Usage](#advanced-usage)
- [ARouter migration to Router](#migration)

### 1. Introduction:
Use KSP technology to implement routing related functions, mainly to improve compilation performance and solve the compatibility problem between ARouter and KSP. The functions of this project mainly refer to Alibaba's ARouter. I hope Router can bring you a better experience in the project

### 2. Features
#### 1. KSP feature advantages: For details, please refer to the official document, which will not be explained in detail here
#### 2. Support standard URL jump, automatically inject parameters into the target page
#### 3. Support multiple modules
#### 4. Support interceptors (multiple interceptions, hierarchical strategies)
#### 5. Support dynamic changes of PATH and URL
#### 6. Support jump carrying parameters and target interface injection parameters
#### 7. Support configuration of transition animation
#### 8. Support new and old API jump pages to carry return values
#### 9. Support instantiation of target classes, multi-module decoupling (instantiation of subclasses of Fragment and ProvideService)
#### 10. Support starting services

<a id="configuration"></a>
### 3. Configuration
#### 1. Add in build.gradle (Project) file
```groovy
plugins{
    id "com.google.devtools.ksp" version "2.0.10-1.0.24"
    id "org.jetbrains.kotlin.jvm" version "2.0.10"
}
```
#### 2. Add remote dependencies and ksp plugins to the build.gradle (app) file
```groovy
//ksp plugin
plugins {
    id 'com.google.devtools.ksp'
}
android{
    //JAVA and jvm are set to 11 or higher
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
<a id="detailed-usage"></a>
### 4. Detailed Usage
##### 1. Add annotations
```kotlin
@Route(path ="/launcher/main") 
class MainActivity : BaseActivity<ActivityMainBinding>(){/*...*/}

@Route("/model/fragment") 
class RouterFragment : AbsBaseFragment<FragmentRouterBinding>() {/*...*/}

@Route("/service/backend") 
class RouterService : Service() {/*...*/}
```
#### 2. Start routing
```kotlin
//Path jumps to Activity
Router.build("/launcher/main").navigation()

//uri jump to Activity
val uri = Uri.parse("hearthappy://kotlin.ksp.com/model2/ui?name=Uri jump to Modules2Activity &age=18")  
Router.build(uri).navigation()

//Start the service
Router.build("/service/backend").navigation()
```
#### 3. Jump with parameters
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
#### 4. Target page injection parameters (supports lateinit, Delegates and other delay or delegation functions, automatic injection will initialize the default values)

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

#### 5. Instantiation
```kotlin
//Fragment
val fragment = Router.build("/model/fragment").getInstance() as Fragment  
val beginTransaction = supportFragmentManager.beginTransaction()  
beginTransaction.add(R.id.fragmentLayout, fragment)  
beginTransaction.commit()
```

#### 6. Logs
```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //Route execution log
        if (BuildConfig.DEBUG) Router.openLog()
    }
}
```
Add to build.gradle file
```groovy
ksp {  
    arg("enableRouterLog", "true")  //compile time log
}
```

#### 7. Add obfuscation rules
```
-keep class com.hearthappy.router.generate.interceptor.** { *; }  
-keep class com.hearthappy.router.generate.routes.** { *; }  
-keep class com.hearthappy.router.generate.path.** { *; }
```

<a id="advanced-usage"></a>
### 5. Advanced usage
#### 1. Object serialization
```kotlin
//If you need to pass in a custom object, create a class that implements the SerializationService interface and use the @Route annotation. For example:
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
#### 2. Interceptor
```kotlin
//priority: The smaller the value, the higher the priority.
@Interceptor(priority = 1, name = "RoomInterceptor") 
class RoomInterceptor : IInterceptor {  
    override fun intercept(sorter: Sorter, callback: InterceptorCallback) {  
        if (sorter.getPath() == RouterPath.CASE_ACTIVITY_FOR_RESULT) {  
            Log.d("RoomInterceptor", "intercept: Execute Interceptor 2")  
           /**
            * onContinue: Continue to execute the route and do not execute subsequent interceptors
            * onInterrupt: Interrupt the route jump, and subsequent interceptors will also be interrupted
            * If neither onContinue nor onInterrupt is used, all interceptors will be executed in order of priority level, and then the route will be started
            */
            callback.onContinue(sorter)  
        }  
        callback.onContinue(sorter)  
    }  
}
```
#### 3. Rewrite URL or Path
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
#### 4. Providing services
```kotlin
//Open Services
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
//Discover service
//Method 1:
@Autowired var helloService: HelloService? = null  

override fun onCreate(savedInstanceState: Bundle?) {  
    Router.inject(this)  
    super.onCreate(savedInstanceState)  
    helloService.sayHello("KSP Router")
}

//Method 2:
val helloService1 = Router.build(RouterPath.SERVICE_HELLO).getInstance() as HelloService  
val sayHello1 = helloService1.sayHello("KSP Router")

//Method 3:
val helloService2 = Router.getInstance(HelloService::class.java)
val sayHello2 = helloService2.sayHello("KSP Router")
```
#### 5. Processing jump results
```kotlin
//New version of api
//1. Register
private val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->  ...}

//2、Jump
activityResultLauncher.launch(Intent(this@ForResultActivity, Router.build(RouterPath.MODULES2_UI).getDestination()))  
```

```kotlin
//Legacy API
//1、Register
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}
//2、Jump
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

<a id="migration"></a>
### 6. ARouter migration to Router

#### 1. Press the shortcut keys ctrl+shift+R, search for xxx and replace it with yyy, then click Replace All
##### 1. Package path replacement table
| ARouter code (original) | Router code (after replacement) |
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

##### 2、Class name replacement table
| ARouter code (original) | Router code (after replacement) |
|------------------|---------------------|
| `Postcard` | `Sorter` |

##### 3、Example of using method replacement
| ARouter code (original) | Router code (after replacement) |
|------------------|---------------------|
| `ARouter.getInstance()` | `Router` |




#### 2、Replace instantiation with Router.getInstance(xxx) or Router.build("path").getInstance() as (Your class)
