package Actors

import Actors.PaymentLogger.{PayComplite, PayError}
import akka.actor.{Actor, ActorRef, Props}

object PaymentParticipant{
  def props(balance: Double, logger: ActorRef): Props = Props(new PaymentParticipant(balance, logger))
  final case class Payment(sign: Int, value: Double, participant: ActorRef)
  final case class StopPayment(value: Double)
}

class PaymentParticipant(var balance: Double, logger: ActorRef) extends Actor{
  import PaymentParticipant._
  override def receive: Receive = {
    case Payment(-1, value, participant) if value > balance =>
      participant ! StopPayment(value)
      logger ! PayError(self.path.name, participant.path.name, value)
    case Payment(-1, value, participant) =>
      balance -= value
      logger ! PayComplite(self.path.name, participant.path.name, value)
    case Payment(1, value, _) =>
      balance += value
    case StopPayment(value) =>
      balance -= value
  }
}
