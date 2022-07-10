package dev.tgarde.akka.streams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import akka.util.ByteString;
import dev.tgarde.akka.config.AkkaConfig;

import java.math.BigInteger;
import java.util.Arrays;
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
        sample.factorialInStream();
        sample.zipOperation();
        sample.zipWithOperation();
    }

    public void addingStreamValues() {
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<BigInteger, NotUsed> addition = source.scan(BigInteger.ZERO,
                (accumulator, next) -> accumulator.add(BigInteger.valueOf(next)));
        addition
                .map(val -> {
                    System.out.println(val);
                    return val;
                })
                .runWith(Sink.seq(), actorSystem);
    }
    public void factorialInStream() {
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<BigInteger, NotUsed> multiplication = source
                .take(5)
                .scan(BigInteger.ONE,
                (accumulator, next) -> accumulator.multiply(BigInteger.valueOf(next)));

        multiplication
                .map(val -> {
                    System.out.println(val);
                    return val;
                })
                .runWith(Sink.seq(), actorSystem);
    }

    public void zipOperation(){
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<Integer, NotUsed> source2 = getIntegerRangeSource2();

        source
                .zip(source2)
                .map(val -> {
                    val.first();
                        System.out.println(val);
                   return val;})
                .runWith(Sink.seq(), actorSystem);

    }

    public void zipAllOperation(){
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<Integer, NotUsed> source2 = getIntegerRangeSource2();

        source
                .zipAll(source2, -1 , 0)
                .map(val -> {
                    System.out.println(val);
                    return val;})
                .runWith(Sink.seq(), actorSystem);

    }

    public void zipWithOperation(){
        Source<Integer, NotUsed> source = getIntegerRangeSource();
        Source<Integer, NotUsed> source2 = getIntegerRangeSource2();

        source
                .zipWith(source2, (elem1, elem2) -> elem1*elem2)
                .map(val -> {
                    System.out.println(val);
                    return val;})
                .runWith(Sink.seq(), actorSystem);

    }

    public void runBasicAkkaStream(){
        runningForEachElement(getIntegerRangeSource())
                .thenRun(() -> System.out.print("Stream Consumed"));
    }

    public CompletionStage<Void> runningForEachElement(Source<Integer, NotUsed> source){
        return source.runForeach(System.out::println,  actorSystem)
                .thenRun(() -> actorSystem.terminate());
    }

    public Source<Integer, NotUsed> getIntegerRangeSource(){
        return Source.range(1, 10);
    }

    public Source<Integer, NotUsed> getIntegerRangeSource2(){
        return Source.range(11,20);
    }
}
