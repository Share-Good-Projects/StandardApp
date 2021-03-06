package com.app.standard.modle.rxjava;

import com.app.standard.util.RxJavaUtil;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public class RxBus {
    // 主题
    private final FlowableProcessor<Object> bus;

    public static RxBus getDefault() {
        return RxBusHolder.sInstance;
    }

    private static class RxBusHolder {
        private static final RxBus sInstance = new RxBus();
    }

    // PublishSubject只会把在订阅发生的时间点之后来自原始Flowable的数据发射给观察者
    private RxBus() {
        bus = PublishProcessor.create().toSerialized();//包装成线程安全的
    }


    // 提供了一个新的事件
    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者

    /**
     * @param eventType 该数据类型的 Clazz
     */
    public <T> Flowable<T> toFlowAble(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    // 封装默认订阅

    /**
     * @param eventType 该数据类型的 Clazz
     */
    public <T> Disposable toDefaultFlowAble(Class<T> eventType, Consumer<T> action) {
        return bus.ofType(eventType).compose(RxJavaUtil.<T>rxSchedulerHelper()).subscribe(action);
    }
}
