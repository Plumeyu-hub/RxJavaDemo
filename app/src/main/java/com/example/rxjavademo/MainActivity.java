package com.example.rxjavademo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Notification;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiPredicate;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "syso";
    private String[] mDate = {"One", "Two", "Three"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxJavaBaseUse();
        //rxJavaChainUse();
        //rxJavaDelayUse();
        //rxJavaLifeUse();
        //rxJavaErrorUse();
        //rxJavaRepeatUse();
        //rxJavaThreadUse();
        //rxJavaTransformationOperatorUse();
        //rxJavaCombiningOperatorUse();
    }

    /**
     * RxJava基本使用
     */
    //一、分步骤的完整调用
    private void rxJavaBaseUse() {
        //第一步：创建观察者Observer 并 定义响应事件行为
        //采用Observer 接口
        Observer<String> observer = new Observer<String>() {
            // 默认最先调用复写的 onSubscribe（）
            // 通过复写对应方法来 响应 被观察者
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                //获取连接器 Disposable 对象
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

            //onError()和onComplete是互斥的，一次只能调用一个
            //RxJava中添加了普通观察者模式缺失的三个功能
            //①当事件处理出现异常时框架自动触发onError()方法
            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            //②RxJava中规定当不再有新的事件发出时，可以调用onComplete()方法作为标示
            // 当不再有新的事件通过被观察者发出的时候回调
            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
            //③同时Observables支持链式调用，从而避免了回调嵌套的问题。
        };

        //第二步：创建被观察者Observable
        //Observable只是生产事件
        Observable<String> observable = new Observable<String>() {
            @Override
            protected void subscribeActual(@NonNull Observer<? super String> observer) {
                //发送事件
                for (String s : mDate) {
                    observer.onNext(s);
                }
                observer.onComplete();
            }
        };

        //                Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<String>() {
        //                    @Override
        //                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
        //
        //                    }
        //                });

        //<--扩展：RxJava 提供了其他方法用于 创建被观察者对象Observable -->
        //除了create()，just()和from()同样可以创建Observable
        // 方法1：just(T...)：直接将传入的参数依次发送出来(最多只能发送10个参数)
        //just操作符也是把其他类型的对象和数据类型转化成Observable，它和from操作符很像，只是方法的参数有所差别
        // Observable observable = Observable.just("A", "B", "C");
        // 将会依次调用：
        // onNext("A");
        // onNext("B");
        // onNext("C");
        // onCompleted();

        // 方法2：from(T[])/from(Iterable<? extends T>) 直接发送 传入的数组数据
        // 将传入的数组或者Iterable拆分成Java对象依次发送
        //from操作符是把其他类型的对象和数据类型转化成Observable
        //会将数组中的数据转换为Observable对象,可以发送10个以上事件（数组形式）
        //String[] words = {"A", "B", "C"};
        //Observable observable88 = Observable.fromArray(words);
        //将会依次调用：
        //onNext("A");
        //onNext("B");
        //onNext("C");
        //onCompleted();

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

        //注意：RxJava 2.x 提供了多个函数式接口 ，用于实现简便式的观察者模式。
        //以 Consumer为例：实现简便式的观察者模式
        //        Observable.just("hello").subscribe(new Consumer<String>() {
        //            // 每次接收到Observable的事件都会调用Consumer.accept（）
        //            @Override
        //            public void accept(String s) throws Exception {
        //                System.out.println(s);
        //            }
        //        });
    }

    //三、延迟操作  即在被观察者发送事件前进行一些延迟的操作
    //delay（）  使得被观察者延迟一段时间再发送事件
    private void rxJavaDelayUse() {
        // 1. 指定延迟时间
        // 参数1 = 时间；参数2 = 时间单位
        //delay(long delay,TimeUnit unit)
        Observable.just(1, 2, 3)
                .delay(3, TimeUnit.SECONDS) // 延迟3s再发送
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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


        // 2. 指定延迟时间 & 调度器
        // 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器
        //delay(long delay,TimeUnit unit,mScheduler scheduler)

        // 3. 指定延迟时间  & 错误延迟
        // 错误延迟，即：若存在Error事件，则如常执行，执行后再抛出错误异常
        // 参数1 = 时间；参数2 = 时间单位；参数3 = 错误延迟参数
        //delay(long delay,TimeUnit unit,boolean delayError)

        // 4. 指定延迟时间 & 调度器 & 错误延迟
        // 参数1 = 时间；参数2 = 时间单位；参数3 = 线程调度器；参数4 = 错误延迟参数
        //delay(long delay,TimeUnit unit,mScheduler scheduler,boolean delayError)
        // : 指定延迟多长时间并添加调度器，错误通知可以设置是否延迟
    }


    //四、在事件的生命周期中操作
    //在事件发送 & 接收的整个生命周期过程中进行操作
    //如发送事件前的初始化、发送事件后的回调请求等
    //do（）在某个事件的生命周期中调用
    private void rxJavaLifeUse() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Throwable("发生错误了"));
            }
        })
                // 1. 当Observable每发送1次数据事件就会调用1次
                .doOnEach(new Consumer<Notification<Integer>>() {
                    @Override
                    public void accept(Notification<Integer> integerNotification) throws Exception {
                        Log.d(TAG, "doOnEach: " + integerNotification.getValue());
                    }
                })
                // 2. 执行Next事件前调用
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "doOnNext: " + integer);
                    }
                })
                // 3. 执行Next事件后调用
                .doAfterNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "doAfterNext: " + integer);
                    }
                })
                // 4. Observable正常发送事件完毕后调用
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doOnComplete: ");
                    }
                })
                // 5. Observable发送错误事件时调用
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "doOnError: " + throwable.getMessage());
                    }
                })
                // 6. 观察者订阅时调用
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        Log.e(TAG, "doOnSubscribe: ");
                    }
                })
                // 7. Observable发送事件完毕后调用，无论正常发送完毕 / 异常终止
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doAfterTerminate: ");
                    }
                })
                // 8. 最后执行
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "doFinally: ");
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

    //五、错误处理  发送事件过程中，遇到错误时的处理机制
    private void rxJavaErrorUse() {
        //①onErrorReturn()  遇到错误时，发送1个特殊事件 & 正常终止,可捕获在它之前发生的异常
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Throwable("发生错误了"));
            }
        })
                .onErrorReturn(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(@NonNull Throwable throwable) throws Exception {
                        // 捕捉错误异常
                        Log.e(TAG, "在onErrorReturn处理了错误: " + throwable.toString());

                        return 666;
                        // 发生错误事件后，发送一个"666"事件，最终正常结束
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //②onErrorResumeNext()  遇到错误时，发送1个新的Observable
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Throwable("发生错误了"));
            }
        })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
                    @Override
                    public ObservableSource<? extends Integer> apply(@NonNull Throwable throwable) throws Exception {

                        // 1. 捕捉错误异常
                        Log.e(TAG, "在onErrorReturn处理了错误: " + throwable.toString());

                        // 2. 发生错误事件后，发送一个新的被观察者 & 发送事件序列
                        return Observable.just(11, 22);

                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //onExceptionResumeNext()  遇到错误时，发送1个新的Observable
        //onExceptionResumeNext()拦截的错误 = Exception；若需拦截Throwable请用onErrorResumeNext()
        //若onExceptionResumeNext()拦截的错误 = Throwable，则会将错误传递给观察者的onError方法
        // TODO: 2020/08/13

        //③retry()  重试，即当出现错误时，让被观察者（Observable）重新发射数据
        //1、接收到 onError（）时，重新订阅 & 发送事件
        //2、Throwable 和 Exception都可拦截
        //<-- 1. retry（） -->
        // 作用：出现错误时，让被观察者重新发送数据
        // 注：若一直错误，则一直重新发送

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })
                .retry() // 遇到错误时，让被观察者重新发射数据（若一直错误，则一直重新发送
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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


        //<-- 2. retry（long time） -->
        // 作用：出现错误时，让被观察者重新发送数据（具备重试次数限制
        // 参数 = 重试次数
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })
                .retry(3) // 设置重试次数 = 3次
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //<-- 3. retry（Predicate predicate） -->
        // 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送& 持续遇到错误，则持续重试）
        // 参数 = 判断逻辑
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })
                // 拦截错误后，判断是否需要重新发送请求
                .retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(@NonNull Throwable throwable) throws Exception {
                        // 捕获异常
                        Log.e(TAG, "retry错误: " + throwable.toString());

                        //返回false = 不重新重新发送数据 & 调用观察者的onError结束
                        //返回true = 重新发送请求（若持续遇到错误，就持续重新发送）
                        return true;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //<--  4. retry（new BiPredicate<Integer, Throwable>） -->
        // 作用：出现错误后，判断是否需要重新发送数据（若需要重新发送 & 持续遇到错误，则持续重试
        // 参数 =  判断逻辑（传入当前重试次数 & 异常错误信息）
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })

                // 拦截错误后，判断是否需要重新发送请求
                .retry(new BiPredicate<Integer, Throwable>() {
                    @Override
                    public boolean test(@NonNull Integer integer, @NonNull Throwable throwable) throws Exception {
                        // 捕获异常
                        Log.e(TAG, "异常错误 =  " + throwable.toString());

                        // 获取当前重试次数
                        Log.e(TAG, "当前重试次数 =  " + integer);

                        //返回false = 不重新重新发送数据 & 调用观察者的onError结束
                        //返回true = 重新发送请求（若持续遇到错误，就持续重新发送）
                        return true;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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


        //<-- 5. retry（long time,Predicate predicate） -->
        // 作用：出现错误后，判断是否需要重新发送数据（具备重试次数限制
        // 参数 = 设置重试次数 & 判断逻辑
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })
                // 拦截错误后，判断是否需要重新发送请求
                .retry(3, new Predicate<Throwable>() {
                    @Override
                    public boolean test(@NonNull Throwable throwable) throws Exception {
                        // 捕获异常
                        Log.e(TAG, "retry错误: " + throwable.toString());

                        //返回false = 不重新重新发送数据 & 调用观察者的onError（）结束
                        //返回true = 重新发送请求（最多重新发送3次）
                        return true;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //④retryUntil（）  出现错误后，判断是否需要重新发送数据
        //若需要重新发送 & 持续遇到错误，则持续重试
        //作用类似于retry（Predicate predicate）
        //具体使用类似于retry（Predicate predicate），唯一区别：返回 true 则不重新发送数据事件

        //⑤retryWhen（）
        // 遇到错误时，将发生的错误传递给一个新的被观察者（Observable），
        // 并决定是否需要重新订阅原始被观察者（Observable）& 发送事件
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onError(new Exception("发生错误了"));
                e.onNext(3);
            }
        })
                // 遇到error事件才会回调
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {

                    @Override
                    public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                        // 参数Observable<Throwable>中的泛型 = 上游操作符抛出的异常，可通过该条件来判断异常的类型
                        // 返回Observable<?> = 新的被观察者 Observable（任意类型）
                        // 此处有两种情况：
                        // 1. 若 新的被观察者 Observable发送的事件 = Error事件，那么 原始Observable则不重新发送事件：
                        // 2. 若 新的被观察者 Observable发送的事件 = Next事件 ，那么原始的Observable则重新发送事件：
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(@NonNull Throwable throwable) throws Exception {

                                // 1. 若返回的Observable发送的事件 = Error事件，则原始的Observable不重新发送事件
                                // 该异常错误信息可在观察者中的onError（）中获得
                                return Observable.error(new Throwable("retryWhen终止啦"));

                                // 2. 若返回的Observable发送的事件 = Next事件，则原始的Observable重新发送事件（若持续遇到错误，则持续重试）
                                // return Observable.just(1);
                            }
                        });

                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应" + e.toString());
                        // 获取异常错误信息
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
    }

    //六、重复发送  重复不断地发送被观察者事件
    private void rxJavaRepeatUse() {
        //①repeat（）  无条件地、重复发送 被观察者事件，具备重载方法，可设置重复创建次数
        //②repeatWhen（）  有条件地、重复发送 被观察者事件

        // 不传入参数 = 重复发送次数 = 无限次
        //repeat（）
        // 传入参数 = 重复发送次数有限
        //repeatWhen（Integer int ）

        // 注：
        // 1. 接收到.onCompleted()事件后，触发重新订阅 & 发送
        // 2. 默认运行在一个新的线程上

        // ①repeat（）具体使用
        Observable.just(1, 2, 3, 4)
                .repeat(3) // 重复创建次数 3次
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
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

        //②repeatWhen（）具体使用
        //将原始 Observable 停止发送事件的标识（Complete（） / Error（））
        // 转换成1个 Object 类型数据传递给1个新被观察者（Observable），以此决定是否重新订阅 & 发送原来的 Observable
        //1、新被观察者（Observable）返回1个Complete / Error事件，则不重新订阅 & 发送原来的 Observable
        //2、若新被观察者（Observable）返回其余事件时，则重新订阅 & 发送原来的 Observable

        Observable.just(1, 2, 4).repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            // 在Function函数中，必须对输入的 Observable<Object>进行处理，这里我们使用的是flatMap操作符接收上游的数据
            public ObservableSource<?> apply(@NonNull Observable<Object> objectObservable) throws Exception {
                // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
                // 以此决定是否重新订阅 & 发送原来的 Observable
                // 此处有2种情况：
                // 1. 若新被观察者（Observable）返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable
                // 2. 若新被观察者（Observable）返回其余事件，则重新订阅 & 发送原来的 Observable
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Object throwable) throws Exception {

                        //新的Observable发送Complete 事件 = 原始Observable停止发送 & 不重新发送
                        // 情况1：若新被观察者（Observable）返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable
                        return Observable.empty();
                        // Observable.empty() = 发送Complete事件，但不会回调观察者的onComplete（）

                        //新的Observable发送Error 事件 = 原始Observable停止发送 & 不重新发送
                        // return Observable.error(new Throwable("不再重新订阅事件"));
                        // 返回Error事件 = 回调onError（）事件，并接收传过去的错误信息。

                        //新的Observable发送其余事件 = 原始Observable重新发送
                        // 情况2：若新被观察者（Observable）返回其余事件，则重新订阅 & 发送原来的 Observable
                        // return Observable.just(1);
                        // 仅仅是作为1个触发重新订阅被观察者的通知，发送的是什么数据并不重要，只要不是Complete（） /  Error（）事件

                    }
                });

            }
        })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应：" + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });
    }

    //线程控制（切换 / 调度 ）
    private void rxJavaThreadUse() {
        // 步骤1：创建被观察者 Observable & 发送事件
        // 在主线程创建被观察者 Observable 对象
        // 所以生产事件的线程是：主线程

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                Log.d(TAG, " 被观察者 Observable的工作线程是: " + Thread.currentThread().getName());
                // 打印验证
                emitter.onNext(1);
                emitter.onComplete();
            }
        });

        // 步骤2：创建观察者 Observer 并 定义响应事件行为
        // 在主线程创建观察者 Observer 对象
        // 所以接收 & 响应事件的线程是：主线程
        Observer<Integer> observer = new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                // 打印验证
                Log.d(TAG, "开始采用subscribe连接");
                Log.d(TAG, " 观察者 Observer的工作线程是: " + Thread.currentThread().getName());
            }

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
        };

        //<-- 使用说明 -->
        // Observable.subscribeOn（Schedulers.Thread）：指定被观察者 发送(生产)事件的线程（传入RxJava内置的线程类型）,被观察者一般放在子线程
        // Observable.observeOn（Schedulers.Thread）：指定观察者 接收 & 响应事件的线程（传入RxJava内置的线程类型）,观察者一般放在主线程

        //<-- 实例使用 -->
        // 步骤3：通过订阅（subscribe）连接观察者和被观察者
        observable.subscribeOn(Schedulers.newThread()) //1
                .observeOn(AndroidSchedulers.mainThread())//2
                .subscribe(observer); // 3. 最后再通过订阅（subscribe）连接观察者和被观察者

        //若Observable.subscribeOn（）多次指定被观察者 生产事件的线程，则只有第一次指定有效，其余的指定线程无效
        //若Observable.observeOn（）多次指定观察者 接收 & 响应事件的线程，则每次指定均有效，即每指定一次，就会进行一次线程的切换

    }

    //变换操作符
    private void rxJavaTransformationOperatorUse() {
        //Map()
        //作用：对被观察者发送的每1个事件都通过指定的函数处理，从而变换成另外一种事件
        //即，将被观察者发送的事件转换为任意的类型事件
        //应用场景：数据类型转换
        //具体使用：下面以将 使用Map（） 将事件的参数从 整型 变换成 字符串类型 为例子说明
        // 采用RxJava基于事件流的链式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {

            // 1. 被观察者发送事件 = 参数为整型 = 1、2、3
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);

            }
            // 2. 使用Map变换操作符中的Function函数对被观察者发送的事件进行统一变换：整型变换成字符串类型
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "使用 Map变换操作符 将事件" + integer + "的参数从 整型" + integer + " 变换成 字符串类型" + integer;
            }
        }).subscribe(new Consumer<String>() {

            // 3. 观察者接收事件时，是接收到变换后的事件 = 字符串类型
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
        //从上面可以看出，map() 将参数中的 Integer 类型对象转换成一个 String类型 对象后返回
        //同时，事件的参数类型也由 Integer 类型变成了 String 类型


        //FlatMap()
        //作用：将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
        //应用场景：无序的将被观察者发送的整个事件序列进行变换
        //具体使用：
        // 采用RxJava基于事件流的链式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // 采用flatMap（）变换操作符
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });//注：新合并生成的事件序列顺序是无序的，即 与旧序列发送事件的顺序无关


        //ConcatMap()
        // 作用：类似FlatMap()操作符
        //与FlatMap()的 区别在于：拆分 & 重新合并生成的事件序列 的顺序 = 被观察者旧序列生产的顺序
        // 应用场景:有序的将被观察者发送的整个事件序列进行变换
        //具体应用:采用RxJava基于事件流的链式操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }

            // 采用concatMap（）变换操作符
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });//注：新合并生成的事件序列顺序是有序的，即 严格按照旧序列发送事件的顺序


        //Buffer()
        //作用：定期从被观察者（Obervable）需要发送的事件中获取一定数量的事件 & 放到缓存区中，最终发送
        //应用场景：缓存被观察者发送的事件
        //具体应用：被观察者 需要发送5个数字
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 1) // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> stringList) {
                        //
                        Log.d(TAG, " 缓存区里的事件数量 = " + stringList.size());
                        for (Integer value : stringList) {
                            Log.d(TAG, " 事件 = " + value);
                        }
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


    }//变换操作符的主要开发需求场景 = 嵌套回调（Callback hell）

    //组合/合并操作符
    private void rxJavaCombiningOperatorUse() {
        //        //一、组合多个被观察者
        //        //concat（） / concatArray（）
        //        //作用:组合多个被观察者一起发送数据，合并后 按发送顺序串行执行
        //        //二者区别：组合被观察者的数量，即concat（）组合被观察者数量≤4个，而concatArray（）则可＞4个
        //        //具体使用：
        //        // concat（）：组合多个被观察者（≤4个）一起发送数据
        //        // 注：串行执行
        //        Observable.concat(Observable.just(1, 2, 3),
        //                Observable.just(4, 5, 6),
        //                Observable.just(7, 8, 9),
        //                Observable.just(10, 11, 12))
        //                .subscribe(new Observer<Integer>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Integer value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });
        //
        //        // concatArray（）：组合多个被观察者一起发送数据（可＞4个）
        //        // 注：串行执行
        //        Observable.concatArray(Observable.just(1, 2, 3),
        //                Observable.just(4, 5, 6),
        //                Observable.just(7, 8, 9),
        //                Observable.just(10, 11, 12),
        //                Observable.just(13, 14, 15))
        //                .subscribe(new Observer<Integer>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Integer value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });


        //        //merge（） / mergeArray（）
        //        //作用：组合多个被观察者一起发送数据，合并后 按时间线并行执行
        //        //二者区别：组合被观察者的数量，即merge（）组合被观察者数量≤4个，而mergeArray（）则可＞4个
        //        //区别上述concat（）操作符：同样是组合多个被观察者一起发送数据，但concat（）操作符合并后是按发送顺序串行执行
        //        // merge（）：组合多个被观察者（＜4个）一起发送数据
        //        // 注：合并后按照时间线并行执行
        //        Observable.merge(
        //                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
        //                Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS)) // 从2开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
        //                .subscribe(new Observer<Long>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Long value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });

        //        Observable.mergeArray(Observable.just(1, 2, 3),
        //                Observable.just(4, 5, 6),
        //                Observable.just(7, 8, 9),
        //                Observable.just(10, 11, 12),
        //                Observable.just(13, 14, 15))
        //                .subscribe(new Observer<Integer>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Integer value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });
        //
        //
        //        //concatDelayError（） / mergeDelayError（）
        //        //作用：若希望onError事件推迟到其他观察者发送事件结束后才触发
        //        //具体使用：
        //        //a. 无使用concatDelayError（）的情况
        //        Observable.concat(
        //                Observable.create(new ObservableOnSubscribe<Integer>() {
        //                    @Override
        //                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        //
        //                        emitter.onNext(1);
        //                        emitter.onNext(2);
        //                        emitter.onNext(3);
        //                        emitter.onError(new NullPointerException()); // 发送Error事件，因为无使用concatDelayError，所以第2个Observable将不会发送事件
        //                        emitter.onComplete();
        //                    }
        //                }),
        //                Observable.just(4, 5, 6))
        //                .subscribe(new Observer<Integer>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Integer value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });
        //        //b.使用了concatDelayError（）的情况
        //        Observable.concatArrayDelayError(
        //                Observable.create(new ObservableOnSubscribe<Integer>() {
        //                    @Override
        //                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        //
        //                        emitter.onNext(1);
        //                        emitter.onNext(2);
        //                        emitter.onNext(3);
        //                        emitter.onError(new NullPointerException()); // 发送Error事件，因为使用了concatDelayError，所以第2个Observable将会发送事件，等发送完毕后，再发送错误事件
        //                        emitter.onComplete();
        //                    }
        //                }),
        //                Observable.just(4, 5, 6))
        //                .subscribe(new Observer<Integer>() {
        //                    @Override
        //                    public void onSubscribe(Disposable d) {
        //
        //                    }
        //
        //                    @Override
        //                    public void onNext(Integer value) {
        //                        Log.d(TAG, "接收到了事件" + value);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Log.d(TAG, "对Error事件作出响应");
        //                    }
        //
        //                    @Override
        //                    public void onComplete() {
        //                        Log.d(TAG, "对Complete事件作出响应");
        //                    }
        //                });


        //        //二、合并多个事件  该类型的操作符主要是对多个被观察者中的事件进行合并处理。
        //        //Zip（）
        //        //作用：合并多个被观察者（Observable）发送的事件，
        //        // 生成一个新的事件序列（即组合过后的事件序列），并最终发送
        //        //特别注意：
        //        //事件组合方式 = 严格按照原先事件序列 进行对位合并
        //        //最终合并的事件数量 = 多个被观察者（Observable）中数量最少的数量
        //        //<-- 创建第1个被观察者 -->
        //        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
        //            @Override
        //            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        //                Log.d(TAG, "被观察者1发送了事件1");
        //                emitter.onNext(1);
        //                // 为了方便展示效果，所以在发送事件后加入2s的延迟
        //                Thread.sleep(1000);
        //
        //                Log.d(TAG, "被观察者1发送了事件2");
        //                emitter.onNext(2);
        //                Thread.sleep(1000);
        //
        //                Log.d(TAG, "被观察者1发送了事件3");
        //                emitter.onNext(3);
        //                Thread.sleep(1000);
        //
        //                emitter.onComplete();
        //            }
        //        }).subscribeOn(Schedulers.io()); // 设置被观察者1在工作线程1中工作
        //
        //        //<-- 创建第2个被观察者 -->
        //        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
        //            @Override
        //            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        //                Log.d(TAG, "被观察者2发送了事件A");
        //                emitter.onNext("A");
        //                Thread.sleep(1000);
        //
        //                Log.d(TAG, "被观察者2发送了事件B");
        //                emitter.onNext("B");
        //                Thread.sleep(1000);
        //
        //                Log.d(TAG, "被观察者2发送了事件C");
        //                emitter.onNext("C");
        //                Thread.sleep(1000);
        //
        //                Log.d(TAG, "被观察者2发送了事件D");
        //                emitter.onNext("D");
        //                Thread.sleep(1000);
        //
        //                emitter.onComplete();
        //            }
        //        }).subscribeOn(Schedulers.newThread());// 设置被观察者2在工作线程2中工作
        //        // 假设不作线程控制，则该两个被观察者会在同一个线程中工作，即发送事件存在先后顺序，而不是同时发送
        //
        //        //<-- 使用zip变换操作符进行事件合并 -->
        //        // 注：创建BiFunction对象传入的第3个参数 = 合并后数据的数据类型
        //        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
        //            @Override
        //            public String apply(Integer integer, String string) throws Exception {
        //                return integer + string;
        //            }
        //        }).subscribe(new Observer<String>() {
        //            @Override
        //            public void onSubscribe(Disposable d) {
        //                Log.d(TAG, "onSubscribe");
        //            }
        //
        //            @Override
        //            public void onNext(String value) {
        //                Log.d(TAG, "最终接收到的事件 =  " + value);
        //            }
        //
        //            @Override
        //            public void onError(Throwable e) {
        //                Log.d(TAG, "onError");
        //            }
        //
        //            @Override
        //            public void onComplete() {
        //                Log.d(TAG, "onComplete");
        //            }
        //        });//特别注意：
        //        //尽管被观察者2的事件D没有事件与其合并，但还是会继续发送
        //        //若在被观察者1 & 被观察者2的事件序列最后发送onComplete()事件，则被观察者2的事件D也不会发送，测试结果如下

        //        //combineLatest（）
        //        //作用:当两个Observables中的任何一个发送了数据后，
        //        //将先发送了数据的Observables的最新（最后）一个数据与另外一个Observable发送的每个数据结合，
        //        //最终基于该函数的结果发送数据
        //        //与Zip（）的区别：Zip（） = 按个数合并，即1对1合并；CombineLatest（） = 按时间合并，即在同一个时间点上合并
        //        Observable.combineLatest(
        //                Observable.just(1L, 2L, 3L), // 第1个发送数据事件的Observable
        //                Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS), // 第2个发送数据事件的Observable：从0开始发送、共发送3个数据、第1次事件延迟发送时间 = 1s、间隔时间 = 1s
        //                new BiFunction<Long, Long, Long>() {
        //                    @Override
        //                    public Long apply(Long o1, Long o2) throws Exception {
        //                        // o1 = 第1个Observable发送的最新（最后）1个数据
        //                        // o2 = 第2个Observable发送的每1个数据
        //                        Log.e(TAG, "合并的数据是： " + o1 + " " + o2);
        //                        return o1 + o2;
        //                        // 合并的逻辑 = 相加
        //                        // 即第1个Observable发送的最后1个数据 与 第2个Observable发送的每1个数据进行相加
        //                    }
        //                }).subscribe(new Consumer<Long>() {
        //            @Override
        //            public void accept(Long s) throws Exception {
        //                Log.e(TAG, "合并的结果是： " + s);
        //            }
        //        });
        //
        //        //combineLatestDelayError（）
        //        //作用:类似于concatDelayError（）/mergeDelayError（），即错误处理
        //
        //        //reduce（）
        //        //作用:把被观察者需要发送的事件聚合成1个事件 & 发送
        //        //聚合的逻辑根据需求撰写，但本质都是前2个数据聚合，然后与后1个数据继续进行聚合，依次类推
        //        Observable.just(1, 2, 3, 4)
        //                .reduce(new BiFunction<Integer, Integer, Integer>() {
        //                    // 在该复写方法中复写聚合的逻辑
        //                    @Override
        //                    public Integer apply(@NonNull Integer s1, @NonNull Integer s2) throws Exception {
        //                        Log.e(TAG, "本次计算的数据是： " + s1 + " 乘 " + s2);
        //                        return s1 * s2;
        //                        // 本次聚合的逻辑是：全部数据相乘起来
        //                        // 原理：第1次取前2个数据相乘，之后每次获取到的数据 = 返回的数据x原始下1个数据每
        //                    }
        //                }).subscribe(new Consumer<Integer>() {
        //            @Override
        //            public void accept(@NonNull Integer s) throws Exception {
        //                Log.e(TAG, "最终计算的结果是： " + s);
        //
        //            }
        //        });

    }


}