# 终极测试规则：保持App包名下的所有内容不被混淆
-keep class com.sunwithcat.nekochat.data.model.** { *; }
-keep class com.sunwithcat.nekochat.data.remote.** { *; }
-keep class com.sunwithcat.nekochat.data.repository.** { *; }
-keep class com.sunwithcat.nekochat.ui.chat.** { *; }

# 使用R8全模式，对未保留的类剥离通用签名。挂起函数被包装在使用类型参数的continuation中。
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# 如果不保留，R8完整模式将从返回类型中剥离通用签名。
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# 在R8全模式下，对未保留的类剥离通用签名。
-keep,allowobfuscation,allowshrinking class retrofit2.Response