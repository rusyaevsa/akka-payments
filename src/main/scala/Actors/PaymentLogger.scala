package Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object PaymentLogger{
  def props(implicit actorSystem: ActorSystem): Props = Props(new PaymentLogger)
  final case class MaskError(payment: String)
  final case class PayError(payOut: String, payIn: String, value: Double)
  final case class PayComplite(payOut: String, payIn: String, value: Double)
}

class PaymentLogger(implicit val actorSystem: ActorSystem) extends Actor with ActorLogging{
  import PaymentLogger._
  override def receive: Receive = {
    case MaskError(payment) =>
      log.info(s"'$payment' не соответствует маске перевода")
    case PayError(payOut, payIn, value) =>
      log.info(s"платёж от $payOut для $payIn в размере $value не выполнен")
    case PayComplite(payOut, payIn, value) =>
      log.info(s"платёж от $payOut для $payIn в размере $value выполнен")
  }
}
