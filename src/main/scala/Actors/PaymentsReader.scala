package Actors

import Actors.PaymentChecker.CheckPayment
import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.{FileIO, Source}

import java.nio.file.Path

object PaymentsReader{
  def props(checker: ActorRef)(implicit actorSystem: ActorSystem): Props = Props(new PaymentsReader(checker))
  final case object StartReader
}

class PaymentsReader(val checker: ActorRef)(implicit val actorSystem: ActorSystem) extends Actor{
  import PaymentsReader._
  val dir: String = actorSystem.settings.config.getString("akka.reader.directory")
  val mask: String = actorSystem.settings.config.getString("akka.reader.mask")

  override def receive: Receive = {
    case StartReader =>
      val files: Source[Path, NotUsed] = Directory.ls(Path.of(dir))
      files
        .map(path => path.getFileName.toString)
        .filter(name => name.matches(mask))
        .map(name => Path.of(s"$dir/$name"))
        .flatMapConcat(path => FileIO.fromPath(path))
        .map(_.utf8String.split("\n"))
        .runForeach(lstPay => lstPay.foreach(pay => checker ! CheckPayment(pay.trim)))
  }
}
