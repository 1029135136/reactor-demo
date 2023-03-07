package org.example;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * 发布订阅模式
 */
public class PublishAndSubmission {
    public static void main(String[] args) throws InterruptedException {
        SubmissionPublisher<String> publisher = new SubmissionPublisher<String>();
        Flow.Subscriber<Object> subscriber = new Flow.Subscriber<>() {
            Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                //建立订阅关系, 发送第一条数据
                subscription.request(1);
            }

            @Override
            public void onNext(Object item) {
                System.out.println("onNext: " + item);
                //处理完数据, 请求后续的条数据(背压)
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        //发布者和订阅者建立订阅关系
        publisher.subscribe(subscriber);

        publisher.submit("Hello");
        for (int i = 0; i < 100; i++) {
            publisher.submit("item: " + i);
        }
        publisher.close();
        Thread.sleep(10000);
    }
}
