package dev.tgarde.akka.config;

import akka.actor.ActorSystem;

public class AkkaConfig {


    private ActorSystem system;

    public AkkaConfig(){
        system = ActorSystem.create("QuickStart");
    }

    public ActorSystem getSystem() {
        return system;
    }
}
