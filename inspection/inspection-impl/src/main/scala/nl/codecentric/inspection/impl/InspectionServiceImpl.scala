package nl.codecentric.inspection.impl

import cats.data.OptionT
import cats.implicits._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.jdbc.{JdbcReadSide, JdbcSession}
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import nl.codecentric.inspection.api.InspectionService

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

  override def getFrameworkAverages = ServiceCall { _ =>
    val countFuture: Future[Option[String]] = jdbcSession.withConnection { connection =>
      tryWith(connection.prepareStatement("SELECT COUNT(*) FROM votes")) { psCount =>
        tryWith(psCount.executeQuery()) { rsCount =>
          if (rsCount.next() && rsCount.getInt(1) > 0)
            Some(rsCount.getString(1))
          else
            None
        }
      }
    }

    val avgFuture: Future[Option[String]] = jdbcSession.withConnection { connection =>
      tryWith(connection.prepareStatement("SELECT AVG(score) FROM votes")) { psAvg =>
        tryWith(psAvg.executeQuery()) { rsAvg =>
          if (rsAvg.next())
            Some(rsAvg.getString(1))
          else
            None
        }
      }
    }

    val combinedOT: OptionT[Future, String] = for {
      count <- OptionT(countFuture)
      avg <- OptionT(avgFuture)
    } yield s"# $count votes resulted in average; $avg"

    combinedOT.getOrElse("No votes yet")
  }

}
