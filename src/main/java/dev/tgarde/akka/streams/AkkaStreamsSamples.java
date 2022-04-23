package dev.tgarde.akka.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import akka.util.ByteString;
import dev.tgarde.akka.config.AkkaConfig;

import java.math.BigInteger;
import java.util.concurrent.CompletionStage;


public class AkkaStreamsSamples {

    private AkkaConfig config;
    private ActorSystem actorSystem;

    public AkkaStreamsSamples(){
        config = new AkkaConfig();
        actorSystem = config.getSystem();
    }


    public static void main(String[] args) {
        AkkaStreamsSamples sample = new AkkaStreamsSamples();
        sample.runBasicAkkaStream();
        sample.addingStreamValues();
    }

    public void addingStreamValues() {
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<BigInteger, NotUsed> addition = source.scan(BigInteger.ONE,
                (acc, next) -> acc.add(BigInteger.valueOf(next)));
        addition
                .map(val -> {
                    System.out.println(val);
                    return val;
                })
                .runWith(Sink.seq(), actorSystem);
    }

    public void runBasicAkkaStream(){
        runningForEachElement(getIntegerRangeSource())
                .thenRun(() -> System.out.print("Stream Consumed"));
    }

    public CompletionStage<Void> runningForEachElement(Source<Integer, NotUsed> source){
        return source.runForeach(i -> System.out.println(i),  actorSystem)
                .thenRun(() -> actorSystem.terminate());
    }

    public Source<Integer, NotUsed> getIntegerRangeSource(){
        return Source.range(1, 10);
    }
}
