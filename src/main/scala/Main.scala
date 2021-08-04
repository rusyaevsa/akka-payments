import Actors.{PaymentChecker, PaymentLogger, PaymentsReader}
import akka.actor.ActorSystem

object Main extends App{
  implicit val actorSystem: ActorSystem = ActorSystem("Pay")
  val paymentLogger = actorSystem.actorOf(PaymentLogger.props, "Logger")
  val paymentChecker = actorSystem.actorOf(PaymentChecker.props(paymentLogger), "Checker")
  val paymentsReader = actorSystem.actorOf(PaymentsReader.props(paymentChecker), "Reader")
  paymentsReader ! PaymentsReader.StartReader
}
