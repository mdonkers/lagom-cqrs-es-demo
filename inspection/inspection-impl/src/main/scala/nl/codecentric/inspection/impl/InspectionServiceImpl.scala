package nl.codecentric.inspection.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import nl.codecentric.inspection.api.{InspectionForShipMessage, InspectionMessage, InspectionService}
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext

/**
  * Implementation of the InspectionService.
  */
class InspectionServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                            jdbcSession: JdbcSession,
                            readSide: ReadSide,
                            jdbcReadSide: JdbcReadSide)
                           (implicit ec: ExecutionContext)
    extends InspectionService {

  import JdbcSession.tryWith

  readSide.register[InspectionEvent](new InspectionEventProcessor(jdbcReadSide))
  val InspectionEntityId = "inspections"

  override def getAllInspections = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[InspectionEntity](InspectionEntityId)

    ref
      .ask(RequestAllInspections())
      .map(is =>
        is.map(i =>
          InspectionMessage(i.ucrn, i.dtInspection, i.employee, i.remarks)))
  }

  override def getInspectionsByEmployee(employee: String) = ServiceCall { _ =>
    // Look up the Inspection entity for the given ID.
    // Using fixed ID because here no real reason to separate this into multiple Persistent Entities
    val ref = persistentEntityRegistry.refFor[InspectionEntity](InspectionEntityId)

    ref
      .ask(RequestInspectionsByEmployeeCommand(employee))
      .map(is =>
        is.map(i =>
          InspectionMessage(i.ucrn, i.dtInspection, i.employee, i.remarks)))
  }

  override def addInspection = ServiceCall { request =>
    val ref = persistentEntityRegistry.refFor[InspectionEntity](InspectionEntityId)

    ref.ask(
      AddInspectionCommand(request.ucrn,
                           request.dtInspection,
                           request.employee,
                           request.remarks))
  }

  override def getInspectionsForShip(shipName: String) = ServiceCall { _ =>
    jdbcSession.withConnection { connection =>
      tryWith(connection.prepareStatement(
        "SELECT * FROM inspections_with_remarks_vw WHERE ship_name COLLATE UTF8_UNICODE_CI LIKE ?")) { psInspections =>
          psInspections.setString(1, "%" + shipName + "%")

          tryWith(psInspections.executeQuery()) { rsInspections =>
            var resultSet: Set[InspectionForShipMessage] = Set.empty
            while (rsInspections.next()) {
              resultSet = resultSet + InspectionForShipMessage(
                  rsInspections.getString(1),
                  Option(rsInspections.getString(2)),
                  rsInspections.getString(3),
                  new DateTime(rsInspections.getTimestamp(4).getTime),
                  Option(rsInspections.getString(5)),
                  Option(rsInspections.getString(6))
                )
            }
            resultSet
          }
      }
    }
  }

}
