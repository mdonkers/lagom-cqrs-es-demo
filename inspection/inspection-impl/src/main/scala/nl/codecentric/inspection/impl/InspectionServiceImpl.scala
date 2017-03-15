package nl.codecentric.inspection.impl

import cats.data.OptionT
import cats.implicits._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import nl.codecentric.inspection.api.{InspectionForShipMessage, InspectionService}
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

/**
  * Implementation of the InspectionService.
  */
class InspectionServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, jdbcSession: JdbcSession, readSide: ReadSide, jdbcReadSide: JdbcReadSide)
                           (implicit ec: ExecutionContext)
  extends InspectionService {

  import JdbcSession.tryWith

  readSide.register[InspectionEvent](new InspectionEventProcessor(jdbcReadSide))


  override def storeFrameworkVote = ServiceCall { request =>
    // Look up the Inspection entity for the given ID.
    val ref = persistentEntityRegistry.refFor[InspectionEntity](request.framework)

    ref.ask(AddFrameworkVote(request.score, request.comment))
  }

  override def getFrameworkVotes(framework: String) = ServiceCall { _ =>
    // Look up the Inspection entity for the given ID.
    val ref = persistentEntityRegistry.refFor[InspectionEntity](framework)

    // Ask the entity the Hello command.
    ref.ask(FrameworkVoting())
  }

  override def getInspectionsForShip(shipName: String) = ServiceCall { _ =>
    jdbcSession.withConnection { connection =>
      tryWith(connection.prepareStatement("SELECT * FROM inspections_with_remarks_vw WHERE ship_name COLLATE UTF8_UNICODE_CI LIKE ?")) { psInspections =>
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
              Option(rsInspections.getString(6)))
          }
          resultSet
        }
      }
    }
  }

}
