package com.example.rxjavademo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "syso";
    private String[] mDate = {"One", "Two", "Three"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxJavaBaseUse();
    }

    /**
     * RxJava基本使用
     */
    //一、分步骤的完整调用
    private void rxJavaBaseUse() {
        //第一步：创建观察者Observer 并 定义响应事件行为
        Observer<String> observer = new Observer<String>() {
            // 默认最先调用复写的 onSubscribe（）
            // 通过复写对应方法来 响应 被观察者
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                //获取连接器 Disposable 对象
                // TODO: 2020/08/12 没输出 
                Log.d(TAG, "开始采用subscribe连接");
            }

            //onNext就相当于普通观察者模式中的update
            //在普通的观察者模式中观察者一般只会提供一个update()方法
            // 用于被观察者的状态发生变化时，用于提供给被观察者调用。
            @Override
            public void onNext(@NonNull String s) {
                //接受事件
                //接收从被观察者中返回的数据
                Log.d(TAG, "对Next事件" + s + "作出响应");
            }

            //RxJava中添加了普通观察者模式缺失的三个功能
            //①当事件处理出现异常时框架自动触发onError()方法
            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            //②RxJava中规定当不再有新的事件发出时，可以调用onComplete()方法作为标示
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
            //③同时Observables支持链式调用，从而避免了回调嵌套的问题。
        };

        //第二步：创建被观察者Observable

        Observable<String> observable = new Observable<String>() {
            @Override
            protected void subscribeActual(@NonNull Observer<? super String> observer) {
                //发送事件
                for (String s : mDate) {
                    observer.onNext(s);
                }
                //
                observer.onComplete();
            }
        };


        //        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
        //            @Override
        //            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
        //
        //            }
        //        });
        // TODO: 2020/08/12 区别 call from

        // Observable.create()方法可以创建一个Observable
        //使用crate()创建Observable需要一个OnSubscribe对象，这个对象继承Action1
        //当观察者订阅我们的Observable时，它作为一个参数传入并执行call()函数。
        //        Observable<Object> observable = Observable.create(new Observable.OnSubscribe<Object>() {
        //            @Override
        //            public void call(Subscriber<? super Object> subscriber) {
        //
        //            }
        //        });

        //除了create()，just()和from()同样可以创建Observable
        //just(T...)将传入的参数依次发送
        //just操作符也是把其他类型的对象和数据类型转化成Observable，它和from操作符很像，只是方法的参数有所差别
        //Observable observable1 = Observable.just("One", "Two", "Three");

        //from操作符是把其他类型的对象和数据类型转化成Observable
        //from(T[])/from(Iterable<? extends T>)将传入的数组或者Iterable拆分成Java对象依次发送
        //String[] parameters = {"One", "Two", "Three"};
        //Observable obs = Observable.from(parameters);

        //产生了订阅,通过订阅（subscribe）连接观察者和被观察者
        //第三步：被观察者Observable订阅观察者Observer
        // （ps:你没看错，不同于普通的观察者模式，这里是被观察者订阅观察者）
        // 前者 = 被观察者（observable）；后者 = 观察者（observer 或 subscriber）
        observable.subscribe(observer);
    }

    //二、基于事件流的链式调用
    private void rxJavaChainUse() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            // 1. 创建被观察者 & 生产事件
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            // 2. 通过通过订阅（subscribe）连接观察者和被观察者
            // 3. 创建观察者 & 定义响应事件的行为
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }
            // 默认最先调用复写的 onSubscribe（）

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "对Next事件" + value + "作出响应");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }

        });
    }

}