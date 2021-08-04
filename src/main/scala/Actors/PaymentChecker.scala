package Actors

import Actors.PaymentLogger.MaskError
import Actors.PaymentParticipant.Payment
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.util.matching.Regex

object PaymentChecker{
  def props(logger: ActorRef)(implicit actorSystem: ActorSystem): Props = Props(new PaymentChecker(logger))
  final case class CheckPayment(payment: String)
}

class PaymentChecker(logger: ActorRef)(implicit val actorSystem: ActorSystem) extends Actor{
  import PaymentChecker._
  val mask: Regex = actorSystem.settings.config.getString("akka.checker.mask").r
  val balance: Double = actorSystem.settings.config.getDouble("akka.checker.balance")

  override def receive: Receive = {
    case CheckPayment(mask(nameOut, nameIn, value)) =>
      val pay = value.toDouble
      val participantOut = context.child(nameOut).getOrElse(context.actorOf(PaymentParticipant.props(balance, logger), nameOut))
      val participantIn = context.child(nameIn).getOrElse(context.actorOf(PaymentParticipant.props(balance, logger), nameIn))
      participantOut ! Payment(-1, pay, participantIn)
      participantIn ! Payment(1, pay, participantOut)
    case CheckPayment(payment) => logger ! MaskError(payment)
  }
}
