package nl.codecentric.inspection.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.CircuitBreaker.PerNode
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

trait InspectionService extends Service {

  def getInspectionsForShip(shipName: String): ServiceCall[NotUsed, Set[InspectionForShipMessage]]

  def storeFrameworkVote: ServiceCall[StoreFrameworkVoteMessage, Done]

  def getFrameworkVotes(framework: String): ServiceCall[NotUsed, String]

  override final def descriptor = {
    import Service._
    named("inspection")
      .withCalls(
        pathCall("/api/inspection/for-ship/:shipName", getInspectionsForShip _),
        restCall(Method.GET, "/api/framework-vote/:framework", getFrameworkVotes _),
        restCall(Method.POST, "/api/framework-vote", storeFrameworkVote _)
      )
      .withAutoAcl(true)
      .withCircuitBreaker(PerNode)
  }
}

case class StoreFrameworkVoteMessage(framework: String,
                                     score: Int,
                                     comment: Option[String] = None)

object StoreFrameworkVoteMessage {

  /**
    * Format for converting messages to and from JSON.
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[StoreFrameworkVoteMessage] = Json.format[StoreFrameworkVoteMessage]
}

case class InspectionForShipMessage(shipName: String,
                                    shipCategory: Option[String],
                                    ucrn: String,
                                    dtInspection: DateTime,
                                    employee: Option[String],
                                    remarks: Option[String])

object InspectionForShipMessage {
  implicit val format: Format[InspectionForShipMessage] = Json.format[InspectionForShipMessage]
}
