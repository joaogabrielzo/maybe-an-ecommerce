package com.zo.service

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps
import scala.concurrent.duration._

trait HttpService {
    
    implicit val system: ActorSystem = ActorSystem()
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    implicit val timeout: Timeout = Timeout(3 seconds)
}
