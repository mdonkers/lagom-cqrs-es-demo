package nl.codecentric.inspection.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.CircuitBreaker.PerNode
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

trait InspectionService extends Service {

  def getInspectionsForShip(shipName: String): ServiceCall[NotUsed, Set[InspectionForShipMessage]]

  def getAllInspections: ServiceCall[NotUsed, Set[InspectionMessage]]

  def getInspectionsByEmployee(employee: String): ServiceCall[NotUsed, Set[InspectionMessage]]

  def addInspection: ServiceCall[InspectionMessage, Done]

  override final def descriptor = {
    import Service._
    named("inspection")
      .withCalls(
        pathCall("/api/inspection/for-ship/:shipName", getInspectionsForShip _),
        pathCall("/api/inspection", getAllInspections _),
        restCall(Method.GET, "/api/inspection/by-employee/:employee", getInspectionsByEmployee _),
        restCall(Method.POST, "/api/inspection", addInspection _)
      )
      .withAutoAcl(true)
      .withCircuitBreaker(PerNode)
  }
}

case class InspectionMessage(ucrn: String,
                             dtInspection: Option[DateTime] = None,
                             employee: Option[String] = None,
                             remarks: Option[String] = None)

object InspectionMessage {
  implicit val format: Format[InspectionMessage] = Json.format[InspectionMessage]
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
